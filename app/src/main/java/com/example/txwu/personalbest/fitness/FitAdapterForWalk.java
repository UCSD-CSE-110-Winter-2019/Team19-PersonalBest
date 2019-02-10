package com.example.txwu.personalbest.fitness;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.txwu.personalbest.WalkActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
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
    StepTracker stepTracker;
    WalkActivity activity;
    Walk walk;

    /**
     * Constructor
     * @param activity - walk activity
     */
    public FitAdapterForWalk(WalkActivity activity) {
        this.activity = activity;
        walk = activity.walk;
    }

    public void setup() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_CUMULATIVE, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_SPEED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_SPEED_SUMMARY, FitnessOptions.ACCESS_READ)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    fitnessOptions);
        } else {
            stepTracker = new StepTracker(this);
            stepTracker.addObserver(activity);
            startRecording();
        }
    }

    private void startRecording() {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return;
        }

        Fitness.getSensorsClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
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

        subScribeSteps();
        subScribeDistance();
        //subScribeSpeed();
    }

    /**
     * make subscription to steps
     */
    public void subScribeSteps() {
        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
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

    /**
     * make subscription to distance
     */
    public void subScribeDistance() {
        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
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

    /**
     * make subscription to speed
     */
    public void subScribeSpeed() {
        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .subscribe(DataType.TYPE_SPEED)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed speed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                });

        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .subscribe(DataType.AGGREGATE_SPEED_SUMMARY)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed speed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing speed.");
                    }
                });
    }


    /**
     * update step count using start and end time
     * @param stepTracker - the stepTracker used to notify activity
     */
    public void updateStepCount(final StepTracker stepTracker) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
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
                                List<DataPoint> dataPoints= dataSets.get(0)
                                        .getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)
                                        .getDataPoints();

                                int total = dataPoints.isEmpty()
                                        ?0
                                        :dataPoints.get(0).getValue(Field.FIELD_STEPS).asInt();

                                stepTracker.update(total);
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
        updateDistance();
        updateSpeed();
    }

    /**
     * update distance on activity using start and end time of walk
     */
    public void updateDistance() {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return;
        }

        long endTime = walk.EndTime();
        long startTime = walk.StartTime();

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
                                List<DataPoint> dataPoints= dataSets.get(0)
                                        .getDataSet(DataType.AGGREGATE_DISTANCE_DELTA)
                                        .getDataPoints();

                                Log.d(TAG, "" + dataPoints.isEmpty());
                                float total = dataPoints.isEmpty()
                                        ?0
                                        :dataPoints.get(0).getValue(Field.FIELD_DISTANCE).asFloat();

                                activity.setDistance(total);
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

    /**
     * update speed on activity using start and end time of walk
     */
    public void updateSpeed() {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return;
        }

        //get start and end time
        long startTime = walk.StartTime();
        long endTime = walk.EndTime();

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
                                List<DataPoint> dataPoints= dataSets.get(0)
                                        .getDataSet(DataType.AGGREGATE_SPEED_SUMMARY)
                                        .getDataPoints();

                                float total = dataPoints.isEmpty()
                                        ?0
                                        :dataPoints.get(0).getValue(Field.FIELD_AVERAGE).asFloat();

                                activity.setSpeed(total);
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

    @Override
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }

    public static FitAdapterForWalk getInstance(WalkActivity walkActivity) {
        if (walkActivity.fitnessServiceKey.equals("FIT_FOW_WALK"))
            return new FitAdapterForWalk(walkActivity);

        else
            return new FitAdapterForWalk(walkActivity);
    }

    @Override
    public void cancel() {
        stepTracker.cancel();
    }
}
