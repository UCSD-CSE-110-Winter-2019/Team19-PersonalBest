package com.example.txwu.personalbest.fitness;

import com.example.txwu.personalbest.WalkActivity;

import java.util.Calendar;
import java.util.Date;

public class MockWalkAdapter implements FitnessService {
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final String TAG = "FitAdapterForWalk";
    StepTracker stepTracker;
    WalkActivity activity;
    Walk walk;

    private float distance = 0;
    private int steps = 0;

    /**
     * Constructor, creates a mock fit adapter
     * @param activity - the activity to be tested using this adapter
     */
    public MockWalkAdapter(WalkActivity activity) {
        this.activity = activity;
        walk = activity.walk;
    }

    @Override
    public void setup() {
        stepTracker = new StepTracker(this);
        stepTracker.addObserver(activity);
        startRecording();
    }

    /**
     * This method does nothing
     */
    private void startRecording() {}

    /**
     * This method updates the step count in the activity
     * @param stepTracker - the timer used to notify the activity
     */
    @Override
    public void updateStepCount(final StepTracker stepTracker) {
        // get current time
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);

        // set endtime to walk
        long endTime = cal.getTimeInMillis();
        walk.setEndTime(endTime);

        // increment by 50 steps every 2 seconds
        stepTracker.update(steps);
        steps += 50;

        updateDistance();
        updateSpeed();
    }

    /**
     * This method updates the distance in the activity
     */
    public void updateDistance() {

        // walk 60 meters every 2 seconds
        activity.setDistance(distance );
        distance += 60;
    }

    /**
     * This method updates the speed in the activity
     */
    public void updateSpeed() {

        // speed is always 30 m/s
        activity.setSpeed(30);
    }

    @Override
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }

    @Override
    public void cancel() {
        stepTracker.cancel();
    }
}
