package com.example.txwu.personalbest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class WalkActivityTest {
    // uses MockWalkAdapter
    private static final String TEST_SERVICE = "TEST_SERVICE";

    private WalkActivity activity;
    private TextView textSteps;
    private TextView textDistance;
    private Button stop;

    @Before
    public void setUp() {
        Intent intent = new Intent(RuntimeEnvironment.application, WalkActivity.class);
        intent.putExtra(WalkActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        activity = Robolectric.buildActivity(WalkActivity.class, intent).create().get();

        textSteps = activity.findViewById(R.id.walk_step_counter);
        textDistance = activity.findViewById(R.id.walk_distance);
        stop = activity.findViewById(R.id.end_walk);
    }

    @Test
    public void testUpdateInformation() throws Exception {

        // test 0 at beginning
        assertEquals(0, Integer.parseInt(textSteps.getText().toString()));
        assertEquals("0.00", textDistance.getText().toString());

        // wait for 6 seconds
        Thread.sleep(6200);
        assertEquals("150", textSteps.getText().toString());
        assertEquals(String.format("%.2f", 180.0/1600.0), textDistance.getText().toString());
    }

    @Test
    public void testToastCreated() throws Exception {

        // wait for 6 seconds
        Thread.sleep(6200);

        // click Stop Walk
        stop.performClick();
        float speed = ((float)(30 * 3600)/1600);

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
        assertEquals(String.format("Walk ended with %d steps in 0:00:%02d." +
                        "\nAverage speed is %.1f mph.", 150, 6, speed),
                latestToast);
    }
}