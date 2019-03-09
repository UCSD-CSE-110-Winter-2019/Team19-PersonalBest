package com.example.team19.personalbest;

import android.content.Intent;

import com.example.team19.personalbest.fitness.MainScreen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class ProgressEncouragementTest {

    private MainScreen activity;
    private Goal testGoal;

    @Before
    public void setup() {
        Intent intent = new Intent(RuntimeEnvironment.application, MainScreen.class);
        activity = Robolectric.buildActivity(MainScreen.class, intent).create().get();
        testGoal = new Goal(activity, null);
    }

    @Test
    public void testProgressEncouragement() {
        // test encouragement not shown when have friends
        testGoal.setSteps(20000);
        testGoal.checkProgress(9999, false);

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNull(latestToast);

        // test encouragement not shown when progress not enough
        testGoal.setSteps(10000);
        testGoal.checkProgress(9999, true);
        latestToast = ShadowToast.getTextOfLatestToast();
        assertNull(latestToast);

        // test encouragement shown when no friends and progress enough
        testGoal.setSteps(20000);
        testGoal.checkProgress(9999,true);
        latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
        assertEquals("You have doubled you steps compared to yesterday\n" +
                "Keep up the good work!", latestToast);
    }
}