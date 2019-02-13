package com.example.txwu.personalbest;
/*
 * Citation
 * Link: https://github.com/bagilevi/android-pedometer/blob/master/src/name/bagi/levente/pedometer/StepDetector.java
 * Title: StepDetector
 * Date captured: Feb 8 2019
 * Source used: Copying code for the accelerometer to detect steps.
 * Github handle: CreaturePhil
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Arrays;

public class StepDetector {
    private float   mLimit = 10;
    private float   mLastValues[] = new float[3*2];
    private float   mScale[] = new float[2];
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private float   mLastDiff[] = new float[3*2];
    private int mLastMatch = -1;

    private float mPrevValues[] = new float[3];

    public StepDetector() {
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    public boolean update(float[] values) {
        if (mPrevValues[0] == values[0] && mPrevValues[1] == values[1] && mPrevValues[2] == values[2]) {
            return false;
        }

        mPrevValues = Arrays.copyOf(values, values.length);

        boolean update = false;
        synchronized (this) {
            float vSum = 0;
            for (int i=0 ; i<3 ; i++) {
                final float v = mYOffset + values[i] * mScale[1];
                vSum += v;
            }
            int k = 0;
            float v = vSum / 3;

            float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
            if (direction == - mLastDirections[k]) {
                // Direction changed
                int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                mLastExtremes[extType][k] = mLastValues[k];
                float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                if (diff > mLimit) {
                    boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                    boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                    boolean isNotContra = (mLastMatch != 1 - extType);

                    if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                        update = true;
                        mLastMatch = extType;
                    }
                    else {
                        mLastMatch = -1;
                    }
                }
                mLastDiff[k] = diff;
            }
            mLastDirections[k] = direction;
            mLastValues[k] = v;
        }
        return update;
    }
}