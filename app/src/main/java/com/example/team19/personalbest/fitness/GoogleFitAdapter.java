package com.example.team19.personalbest.fitness;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.team19.personalbest.Cloud;
import com.example.team19.personalbest.StepService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GoogleFitAdapter{

    private Timer t;
    private TimerTask task;

    private final String TAG = "GoogleFitAdapter";
    private StepService service;
    private GoogleSignInAccount lastSignedInAccount;
    private int lastStep = 0;

    public GoogleFitAdapter(StepService service) {
        this.service = service;
    }

    public void setup() {

        lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(service);
        startRecording();
    }

    private void startRecording() {
        if (lastSignedInAccount == null) {
            return;
        }
        try {
            Fitness.getSensorsClient(service, lastSignedInAccount)
                    .findDataSources(
                            new DataSourcesRequest.Builder()
                                    .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
                                    .setDataSourceTypes(DataSource.TYPE_RAW)
                                    .build())
                    .addOnSuccessListener(
                            new OnSuccessListener<List<DataSource>>() {
                                @Override
                                public void onSuccess(List<DataSource> dataSources) {
                                    Log.i(TAG, "Num Data source: " + dataSources.size());
                                    for (DataSource dataSource : dataSources) {
                                        Log.i(TAG, "Data source type: " + dataSource.getDataType().getName() + "\n");

                                        // Let's register a listener to receive Activity data!
                                        Log.i(TAG, "Data source for LOCATION_SAMPLE found!  Registering.");
                                        //registerFitnessDataListener(dataSource, DataType.TYPE_LOCATION_SAMPLE);
                                    }

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "failed", e);
                                }
                            });
        }
        catch (Exception e) {

        }

        subScribeSteps();

        task = new TimerTask() {
            @Override
            public void run() {
                GoogleFitAdapter.this.updateStepCount();
            }
        };
        t = new Timer();
        t.schedule(task, 0, 1000);
    }

    /**
     * make subscription to steps
     */
    public void subScribeSteps() {
        try {
            Fitness.getRecordingClient(service, lastSignedInAccount)
                    .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "Successfully subscribed steps!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "There was a problem subscribing steps.");
                        }
                    });
        }
        catch (Exception e) {

        }
    }

    public void updateStepCount() {
        if (lastSignedInAccount == null) {
            return;
        }

        Fitness.getHistoryClient(service, lastSignedInAccount)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                Log.d(TAG, dataSet.toString());
                                int total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                SharedPreferences sharedPreferences =
                                        service.getSharedPreferences("PersonalBest", Context.MODE_PRIVATE);
                                Log.d(TAG, "Total steps: " + total);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(date, total);
                                editor.apply();
                                if (lastStep != total) {
                                    Cloud.set("PersonalBest", date, total);
                                    lastStep = total;
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "There was a problem getting the step count.", e);
                            }
                        });
    }

}
