package com.example.team19.personalbest.fitness;

import androidx.annotation.NonNull;
import android.util.Log;

import com.example.team19.personalbest.WalkActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FitAdapterForWalk implements FitnessService {

    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final String TAG = "FitAdapterForWalk";
    private GoogleSignInAccount lastSignedInAccount;
    private StepTracker stepTracker;
    private WalkActivity activity;
    private Walk walk;

    /**
     * Constructor
     * @param activity - walk activity
     */
    public FitAdapterForWalk(WalkActivity activity) {
        this.activity = activity;
        walk = activity.walk;
    }

    public void setup() {

        lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        stepTracker = new StepTracker(this);
        stepTracker.addObserver(activity);
        startRecording();
    }

    private void startRecording() {
        if (lastSignedInAccount == null) {
            return;
        }
        try {
            Fitness.getSensorsClient(activity, lastSignedInAccount)
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
        subScribeDistance();
    }

    /**
     * make subscription to steps
     */
    public void subScribeSteps() {
        try {
            Fitness.getRecordingClient(activity, lastSignedInAccount)
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

    /**
     * make subscription to distance
     */
    public void subScribeDistance() {
        try {
            Fitness.getRecordingClient(activity, lastSignedInAccount)
                    .subscribe(DataType.TYPE_DISTANCE_DELTA)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "Successfully subscribed distance!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, e.getMessage());
                        }
                    });
        }
        catch (Exception e) {

        }
    }

    /**
     * update step count using start and end time
     * @param stepTracker - the stepTracker used to notify activity
     */
    public void updateStepCount(final StepTracker stepTracker) {
        if (lastSignedInAccount == null) {
            return;
        }

        // get current time
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);

        // get the start and end time, set endtime to walk
        long endTime = cal.getTimeInMillis();
        long startTime = walk.StartTime();
        walk.setEndTime(endTime);

        Log.d("RUAAA", "Start: " + startTime + "      End: " + endTime);
        try {
            DataReadRequest readRequest =
                    new DataReadRequest.Builder()
                            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                            .bucketByTime(1, TimeUnit.DAYS)
                            .build();

            Fitness.getHistoryClient(activity, lastSignedInAccount)
                    .readData(readRequest)
                    .addOnSuccessListener(
                            new OnSuccessListener<DataReadResponse>() {
                                @Override
                                public void onSuccess(DataReadResponse dataReadResponse) {
                                    List<Bucket> dataSets = dataReadResponse.getBuckets();
                                    List<DataPoint> dataPoints = dataSets.get(0)
                                            .getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)
                                            .getDataPoints();

                                    int total = dataPoints.isEmpty()
                                            ? 0
                                            : dataPoints.get(0).getValue(Field.FIELD_STEPS).asInt();

                                    stepTracker.update(total);
                                    Log.d(TAG, "Got newest step for walk: " + total);
                                }
                            }
                    )
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "There was a problem getting the step count.", e);
                                }
                            });
        }
        catch (Exception e) {

        }
        updateDistance();
        updateSpeed();
    }

    /**
     * update distance on activity using start and end time of walk
     */
    public void updateDistance() {

        if (lastSignedInAccount == null) {
            return;
        }

        long endTime = walk.EndTime();
        long startTime = walk.StartTime();
        try {
            DataReadRequest readRequest =
                    new DataReadRequest.Builder()
                            .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                            .bucketByTime(1, TimeUnit.DAYS)
                            .build();

            Fitness.getHistoryClient(activity, lastSignedInAccount)
                    .readData(readRequest)
                    .addOnSuccessListener(
                            new OnSuccessListener<DataReadResponse>() {
                                @Override
                                public void onSuccess(DataReadResponse dataReadResponse) {
                                    List<Bucket> dataSets = dataReadResponse.getBuckets();
                                    List<DataPoint> dataPoints = dataSets.get(0)
                                            .getDataSet(DataType.AGGREGATE_DISTANCE_DELTA)
                                            .getDataPoints();

                                    Log.d(TAG, "" + dataPoints.isEmpty());
                                    float total = dataPoints.isEmpty()
                                            ? 0
                                            : dataPoints.get(0).getValue(Field.FIELD_DISTANCE).asFloat();

                                    activity.setDistance(total);
                                    Log.d(TAG, "Got newest distance in meters for walk: " + total);
                                }
                            }
                    )
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "There was a problem getting the distance.", e);
                                }
                            });
        }
        catch (Exception e) {

        }
    }

    /**
     * update speed on activity using start and end time of walk
     */
    public void updateSpeed() {

        if (lastSignedInAccount == null) {
            return;
        }

        //get start and end time
        long startTime = walk.StartTime();
        long endTime = walk.EndTime();

        try {
            DataReadRequest readRequest =
                    new DataReadRequest.Builder()
                            .aggregate(DataType.TYPE_SPEED, DataType.AGGREGATE_SPEED_SUMMARY)
                            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                            .bucketByTime(1, TimeUnit.DAYS)
                            .build();

            Fitness.getHistoryClient(activity, lastSignedInAccount)
                    .readData(readRequest)
                    .addOnSuccessListener(
                            new OnSuccessListener<DataReadResponse>() {
                                @Override
                                public void onSuccess(DataReadResponse dataReadResponse) {
                                    List<Bucket> dataSets = dataReadResponse.getBuckets();
                                    List<DataPoint> dataPoints = dataSets.get(0)
                                            .getDataSet(DataType.AGGREGATE_SPEED_SUMMARY)
                                            .getDataPoints();

                                    float total = dataPoints.isEmpty()
                                            ? 0
                                            : dataPoints.get(0).getValue(Field.FIELD_AVERAGE).asFloat();

                                    activity.setSpeed(total);
                                    Log.d(TAG, "Got newest speed in m/s for walk: " + total);
                                }
                            }
                    )
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "There was a problem getting the speed.", e);
                                }
                            });
        }
        catch (Exception e) {

        }
    }

    @Override
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }

    public static FitnessService getInstance(WalkActivity walkActivity) {
        if (walkActivity.fitnessServiceKey.equals("TEST_SERVICE"))
            return new MockWalkAdapter(walkActivity);
        else
            return new FitAdapterForWalk(walkActivity);
    }

    @Override
    public void cancel() {
        stepTracker.cancel();
    }
}
