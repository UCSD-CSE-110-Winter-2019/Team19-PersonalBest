package com.example.team19.personalbest;

public class StepDetector {
    private float prev_x, prev_y, prev_z;
    private float threshold = 5;

    public StepDetector() {
        prev_x = 0;
        prev_y = 0;
        prev_z = 0;
    }

    private boolean movementDetected(float a, float prev_a) {
        return (a != prev_a) && (Math.abs(a-prev_a) > threshold);
    }

    public boolean update(float[] values) {
        float x = values[0];
        float y = values[1];
        float z = values[2];
        boolean shouldUpdate = movementDetected(x,prev_x) || movementDetected(y,prev_y) || movementDetected(z,prev_z);
        prev_x = x;
        prev_y = y;
        prev_z = z;
        return shouldUpdate;
    }
}