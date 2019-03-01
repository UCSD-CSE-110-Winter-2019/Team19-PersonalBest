package com.example.team19.personalbest;

import android.app.Activity;
import android.content.Intent;

import com.example.team19.personalbest.fitness.HistoryClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;

import org.junit.Before;
import org.junit.Test;
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

        // check goals
        for (int i = 0; i < 7; i++) {
            assertEquals(goals[i], (int) (goalSet.getEntryForIndex(i).getY()));
        }

        // check incidental
        for (int i = 0; i < 7; i++) {
            assertEquals(incidental[i], (int)(stepSet.getEntryForIndex(i).getYVals())[0]);
        }

        // check intentional
        for (int i = 0; i < 7; i++) {
            assertEquals(intentional[i], (int)(stepSet.getEntryForIndex(i).getYVals())[1]);
        }
    }
}

class MockHistory extends HistoryClient {

    public MockHistory(long timeMillis, Activity activity) {
        super(timeMillis, activity);
    }

    @Override
    public int[] getGoals() {

        return new int[]{5000, 5000, 6000, 6500, 6500, 7000, 9000};
    }

    @Override
    public int[] getIntentional() {

        return new int[]{2000, 2000, 2000, 4000, 3000, 1000, 0};
    }

    @Override
    public int[] getIncidentals() {
        return new int[]{2000, 3000, 4500, 1000, 3400, 10000, 8000};
    }
}