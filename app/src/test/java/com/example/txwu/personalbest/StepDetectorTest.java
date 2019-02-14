package com.example.txwu.personalbest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StepDetectorTest {
    private StepDetector stepDetector;

    @Before
    public void setup() {
        stepDetector = new StepDetector();
    }

    @Test
    public void updateFail() {
        float[] values = {0.0f, 0.0f, 0.0f};
        assertEquals(stepDetector.update(values), false);
    }

    @Test
    public void updateSuccess() {
        float[] values = new float[3];
        values[0] = 0; values[1] = 0; values[2] = 0; stepDetector.update(values);
        values[0] = 0; values[1] = 0; values[2] = 1; stepDetector.update(values);
        values[0] = 0; values[1] = 0; values[2] = 2; stepDetector.update(values);
        values[0] = 0; values[1] = 0; values[2] = 3; stepDetector.update(values);
        values[0] = 0; values[1] = 0; values[2] = 4; stepDetector.update(values);
        values[0] = 0; values[1] = 0; values[2] = 5; stepDetector.update(values);
        values[0] = 0; values[1] = 0; values[2] = 6; stepDetector.update(values);
        values[0] = 0; values[1] = 0; values[2] = 7; stepDetector.update(values);
        values[0] = 0; values[1] = 0; values[2] = 8; stepDetector.update(values);
        values[0] = 0; values[1] = 0; values[2] = 9; stepDetector.update(values);
        values[0] = 0; values[1] = 1; values[2] = 0; stepDetector.update(values);
        values[0] = 0; values[1] = 1; values[2] = 1;
        assertEquals(stepDetector.update(values), true);
    }

}