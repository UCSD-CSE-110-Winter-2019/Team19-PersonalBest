package com.example.team19.personalbest.fitness;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryClient {
    public static final long millisADay = 86400000;

    private long currentTimeMillis;
    private Activity activity;
    private Date[] dateOfWeek;
    public HistoryClient(long timeMillis, Activity activity) {
        currentTimeMillis = timeMillis;
        this.activity = activity;
        // initialize dates for the week
        dateOfWeek = new Date[7];

        long timeDif = 0;
        for (int i = 6; i >=0 ; i--) {
            dateOfWeek[i] = new Date(currentTimeMillis - timeDif);
            timeDif += millisADay;
        }
    }

    /**
     * Get the formatted date strings of the current week
     * @return - array of formatted date strings
     */
    public String[] getDateFormat() {
        String[] dateFormat = new String[dateOfWeek.length];
        for (int i = 0; i < dateOfWeek.length; i++) {
            dateFormat[i] = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(dateOfWeek[i]);
        }

        return dateFormat;
    }

    /**
     * Get the steps for the current week
     * @return - array of steps for current week
     */
    public int[] getStepsForWeek() {

        SharedPreferences stepHistory = activity.getSharedPreferences("Steps", Context.MODE_PRIVATE);
        String[] dates = getDateFormat();
        int[] steps = new int[dateOfWeek.length];
        for (int i = 0; i < dateOfWeek.length; i++) {
            steps[i] = stepHistory.getInt(dates[i], 0);
        }

        return steps;
    }

    /**
     * Get the goals for the current week
     * @return - array of goals for current week
     */
    public int[] getGoalsForWeek() {

        SharedPreferences goalHistory = activity.getSharedPreferences("Goal", Context.MODE_PRIVATE);
        String[] dates = getDateFormat();
        int[] goals = new int[dateOfWeek.length];
        for (int i = 0; i < dateOfWeek.length; i++) {
            goals[i] = goalHistory.getInt(dates[i], 0);
        }
        return goals;
    }

    /**
     * Get the intentional walks of current week
     * @return - array of intentional walks for each day
     */
    public int[] getIntentional() {

        SharedPreferences intentionalHistory = activity.getSharedPreferences("Intentional", Context.MODE_PRIVATE);
        String[] dates = getDateFormat();
        int[] intentionals = new int[dateOfWeek.length];
        for (int i = 0; i < dateOfWeek.length; i++) {
            intentionals[i] = intentionalHistory.getInt(dates[i], 0);
        }
        return intentionals;
    }

    /**
     * Get incidental walks for the current week, this is the difference of step and intentional
     * @return - array of incidental walks of the week
     */
    public int[] getIncidentals() {
        int[] intentionals = getIntentional();
        int[] steps = getStepsForWeek();
        int[] incidentals = new int[dateOfWeek.length];
        for (int i = 0; i < dateOfWeek.length; i++) {
            incidentals[i] = steps[i] - intentionals[i];
        }
        return incidentals;
    }

    /**
     * Get the time millis for the first day
     * @return - time millis for first day
     */
    public long getFirstDayTimeMillis() {
        return currentTimeMillis - 6 * millisADay;
    }
}
