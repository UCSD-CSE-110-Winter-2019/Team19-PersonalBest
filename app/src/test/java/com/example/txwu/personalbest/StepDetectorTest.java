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
        float[] values = {10.0f, 10.0f, 10.0f};
        assertEquals(stepDetector.update(values), true);
    }

}