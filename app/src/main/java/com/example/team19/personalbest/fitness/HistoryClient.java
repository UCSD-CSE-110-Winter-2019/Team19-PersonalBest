package com.example.team19.personalbest.fitness;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryClient {
    public static final long MILLIS_A_DAY = 86400000;

    private long currentTimeMillis;
    private Activity activity;
    private Date[] dateOfMonth;
    public HistoryClient(long timeMillis, Activity activity) {
        currentTimeMillis = timeMillis;
        this.activity = activity;
        // initialize dates for the week
        dateOfMonth = new Date[28];

        long timeDif = 0;
        for (int i = 27; i >=0 ; i--) {
            dateOfMonth[i] = new Date(currentTimeMillis - timeDif);
            timeDif += MILLIS_A_DAY;
        }
    }

    /**
     * Get the formatted date strings of the most recent 28 days
     * @return - array of formatted date strings
     */
    public String[] getDateFormat() {
        String[] dateFormat = new String[dateOfMonth.length];
        for (int i = 0; i < dateOfMonth.length; i++) {
            dateFormat[i] = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(dateOfMonth[i]);
        }

        return dateFormat;
    }

    /**
     * Get the steps for the most recent 28 days
     * @return - array of steps for most recent 28 days
     */
    public int[] getSteps() {

        SharedPreferences stepHistory = activity.getSharedPreferences("Steps", Context.MODE_PRIVATE);
        String[] dates = getDateFormat();
        int[] steps = new int[dateOfMonth.length];
        for (int i = 0; i < dateOfMonth.length; i++) {
            steps[i] = stepHistory.getInt(dates[i], 0);
        }

        return steps;
    }

    /**
     * Get the goals for the most recent 28 days
     * @return - array of goals for most recent 28 days
     */
    public int[] getGoals() {

        SharedPreferences goalHistory = activity.getSharedPreferences("Goal", Context.MODE_PRIVATE);
        String[] dates = getDateFormat();
        int[] goals = new int[dateOfMonth.length];
        for (int i = 0; i < dateOfMonth.length; i++) {
            goals[i] = goalHistory.getInt(dates[i], 0);
        }
        return goals;
    }

    /**
     * Get the intentional walks of most recent 28 days
     * @return - array of intentional walks for each day
     */
    public int[] getIntentional() {

        SharedPreferences intentionalHistory = activity.getSharedPreferences("Intentional", Context.MODE_PRIVATE);
        String[] dates = getDateFormat();
        int[] intentionals = new int[dateOfMonth.length];
        for (int i = 0; i < dateOfMonth.length; i++) {
            intentionals[i] = intentionalHistory.getInt(dates[i], 0);
        }
        return intentionals;
    }

    /**
     * Get incidental walks for the most recent 28 days, this is the difference of step and intentional
     * @return - array of incidental walks of the most recent 28 days
     */
    public int[] getIncidentals() {
        int[] intentionals = getIntentional();
        int[] steps = getSteps();
        int[] incidentals = new int[dateOfMonth.length];
        for (int i = 0; i < dateOfMonth.length; i++) {
            incidentals[i] = steps[i] - intentionals[i];
        }
        return incidentals;
    }

    /**
     * Get the time millis for the first day
     * @return - time millis for first day
     */
    public long getFirstDayTimeMillis() {
        return currentTimeMillis - 27 * MILLIS_A_DAY;
    }
}
