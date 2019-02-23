package com.example.team19.personalbest.fitness;

public interface FitnessService {
    int getRequestCode();
    void setup();
    void updateStepCount(StepTracker self);
    void cancel();
}
