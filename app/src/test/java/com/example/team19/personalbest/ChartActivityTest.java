package com.example.team19.personalbest;

import android.app.Activity;
import android.content.Intent;

import com.example.team19.personalbest.fitness.HistoryClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class ChartActivityTest {

    private ChartActivity activity;
    private MockHistory history;

    @Before
    public void setup() {
        Intent intent = new Intent(RuntimeEnvironment.application, ChartActivity.class);
        activity = Robolectric.buildActivity(ChartActivity.class, intent).create().get();
        history = new MockHistory(0, activity);
        activity.setHistoryClient(history);
    }

    @Test
    public void testBarChartDrawnCorrectly() {
        BarChart chart = activity.getChart();
        BarDataSet goalSet = (BarDataSet) chart.getData().getDataSetByLabel("Goal", false);
        BarDataSet stepSet = (BarDataSet) chart.getData().getDataSetByLabel("Steps", false);

        int[] goals = history.getGoals();
        int[] incidental = history.getIncidentals();
        int[] intentional = history.getIntentional();

        try {
            // check goals
            for (int i = 0; i < 28; i++) {
                assertEquals(goals[i], (int) (goalSet.getEntryForIndex(i).getY()));
            }

            // check incidental
            for (int i = 0; i < 28; i++) {
                assertEquals(incidental[i], (int) (stepSet.getEntryForIndex(i).getYVals())[0]);
            }

            // check intentional
            for (int i = 0; i < 28; i++) {
                assertEquals(intentional[i], (int) (stepSet.getEntryForIndex(i).getYVals())[1]);
            }
        }
        catch (IndexOutOfBoundsException e) {
            fail("The chart has less than 28 days data");
        }
    }
}

class MockHistory extends HistoryClient {

    public MockHistory(long timeMillis, Activity activity) {
        super(timeMillis, activity);
    }

    @Override
    public int[] getGoals() {

        return new int[]{5000, 5000, 6000, 6500, 6500, 7000, 9000,
                         10000, 10000, 10000, 10000, 10000, 10000, 10000,
                         10000, 11000, 11000, 11000, 11500, 11500, 11500,
                         11500, 11500, 11500, 11500, 11500, 11500, 11500};
    }

    @Override
    public int[] getIntentional() {

        return new int[]{2000, 2000, 2000, 4000, 3000, 1000, 1000,
                         1000, 1000, 1000, 1000, 1000, 1000, 1000,
                         4000, 2000, 1000, 3000, 3000, 2000, 5000,
                         5000, 1000, 2000, 2000, 3000, 3000, 1000};
    }

    @Override
    public int[] getIncidentals() {
        return new int[]{2000, 3000, 4500, 1000, 3400, 10000, 8000,
                         8235, 2345, 2345, 8345, 1845, 7629, 8347,
                         6000, 6000, 6000, 8000, 3499, 3457, 7235,
                         2835, 9245, 7258, 9235, 7285, 8345, 8174};
    }
}