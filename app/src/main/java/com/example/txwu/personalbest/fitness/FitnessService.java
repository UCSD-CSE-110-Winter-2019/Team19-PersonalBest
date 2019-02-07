package com.example.txwu.personalbest.fitness;

public interface FitnessService {
    int getRequestCode();
    void setup();
    void updateStepCount(StepTracker self);
}
