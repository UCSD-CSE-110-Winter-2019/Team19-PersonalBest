package com.example.team19.personalbest.fitness;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class StepTracker extends Observable {
    private Timer t;
    private TimerTask updateTemperature;

    public StepTracker(final FitnessService fitnessService) {
        final StepTracker self = this;
        updateTemperature = new TimerTask() {
            @Override
            public void run() {
                setChanged();
                fitnessService.updateStepCount(self);
            }
        };
        t = new Timer();
        t.schedule(updateTemperature, 0, 2000);
    }

    public void update(long total) {
        notifyObservers(total);
    }

    /**
     * Cancel the timer
     */
    public void cancel() {

        t.cancel();
        t.purge();
    }
}
