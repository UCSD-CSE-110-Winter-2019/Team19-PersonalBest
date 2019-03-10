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
public class FriendHistoryTest {
    private FriendHistoryActivity activity;
    private MockFriendHistory history;

    @Before
    public void setup() {
        Intent intent = new Intent(RuntimeEnvironment.application, FriendHistoryActivity.class);
        activity = Robolectric.buildActivity(FriendHistoryActivity.class, intent).create().get();
        history = new MockFriendHistory(null, 0);
        activity.setHistoryClient(history);
    }

    @Test
    public void testBarChartDrawnCorrectly() {
        BarChart chart = activity.getChart();
        BarDataSet stepSet = (BarDataSet) chart.getData().getDataSetByLabel("Steps", false);

        int[] steps = history.getSteps();

        try {
            // check goals
            for (int i = 0; i < 28; i++) {
                assertEquals(steps[i], (int) (stepSet.getEntryForIndex(i).getY()));
            }
        }
        catch (IndexOutOfBoundsException e) {
            fail("The chart has less than 28 days data");
        }
    }
}

class MockFriendHistory extends FriendHistoryClient {

    public MockFriendHistory(Users user, long timeMillis) {
        super(user, timeMillis);
    }

    @Override
    public int[] getSteps() {

        return new int[]{5000, 5000, 6000, 6500, 6500, 7000, 9000,
                10000, 10000, 10000, 10000, 10000, 10000, 10000,
                10000, 11000, 11000, 11000, 11500, 11500, 11500,
                11500, 11500, 11500, 11500, 11500, 11500, 11500};
    }

}