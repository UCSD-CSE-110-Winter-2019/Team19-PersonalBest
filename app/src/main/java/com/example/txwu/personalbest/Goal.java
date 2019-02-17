package com.example.txwu.personalbest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.txwu.personalbest.fitness.MainScreen;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

public class Goal {
    private Activity activity;
    private long steps;

    public Goal(Activity activity, final Date endSubGoalDate) {
        this.activity = activity;
    }

    public void showMeetGoal(long goal) {
        if (!checkIfDailyGoalShown("goal") && this.steps >= goal) {
            setDailyGoalShown("goal");
            try {
                Button button = activity.findViewById(R.id.button_change_goal);
                button.performClick();
            }
            catch (Exception e) {
                Log.d("Goal Met", "Exception when prompting to set new goal");
            }
            Toast.makeText(activity, "Congratulations for meeting your goal of " + String.valueOf(goal)
                    + " steps!", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY,
                    new Date(System.currentTimeMillis() + MainScreen.timedif).getHours());
            SharedPreferences sharedPreferences = activity.getSharedPreferences("PersonalBest", MODE_PRIVATE);
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US)
                    .format(new Date(System.currentTimeMillis() + MainScreen.timedif));
            if (cal.get(Calendar.HOUR_OF_DAY) >= 20 && !sharedPreferences.getBoolean(date+"subgoal", false)) {
                if (this.steps >= goal*0.8) {
                    Toast.makeText(activity, "You have achieved over 80% of your goal.\n" +
                            "Try meet your goal today", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(date + "subgoal", true);
                    editor.apply();
                }
            }
        }
    }

    /**
     * Shows the goal met or sub goal met encouragement from yesterday if it was not shown
     */
    public void showMeetGoalYesterday() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("Goal", MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = activity.getSharedPreferences("PersonalBest", MODE_PRIVATE);
        SharedPreferences sharedPreferences3 = activity.getSharedPreferences("Steps", MODE_PRIVATE);

        // yesterday's date
        String previousDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US)
                .format(new Date(System.currentTimeMillis() + MainScreen.timedif - 24*60*60*1000));

        // yesterday's goal
        int goalYesterday = sharedPreferences.getInt(previousDate, -1);
        SharedPreferences.Editor editor = sharedPreferences2.edit();

        if (!sharedPreferences2.getBoolean(previousDate + "goal", false)) {

            // yesterday's step
            int stepYesterday = sharedPreferences3.getInt(previousDate, -1);
            if (stepYesterday >= goalYesterday) {
                Toast.makeText(activity, "Congratulations for meeting your goal of "
                        + String.valueOf(goalYesterday) + " steps yesterday!", Toast.LENGTH_SHORT).show();
            }
            else if (stepYesterday >= (0.8 * goalYesterday)) {
                if (!sharedPreferences2.getBoolean(previousDate + "subgoal", false))
                Toast.makeText(activity, "You accomplished over 80% of your goal yesterday, keep up the good work!",
                        Toast.LENGTH_SHORT).show();
            }

            editor.putBoolean(previousDate+"goal", true);
            editor.apply();
        }
    }

    public boolean checkIfDailyGoalShown(String type) {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US)
                .format(new Date(System.currentTimeMillis() + MainScreen.timedif));
        SharedPreferences sharedPreferences = activity.getSharedPreferences("PersonalBest", MODE_PRIVATE);
        return sharedPreferences.getBoolean(date+type, false);
    }

    public void setDailyGoalShown(String type) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("PersonalBest", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US)
                .format(new Date(System.currentTimeMillis() + MainScreen.timedif));
        editor.putBoolean(date+type, true);

        editor.apply();
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    /**
     * return a newly suggested goal using the current goal
     * @param currentGoal - the current Goal
     * @return
     */
    public static int suggestNextGoal(int currentGoal) {
        return currentGoal + 500;
    }

    /**
     * get the current goal used
     * @return the current goal
     */
    public int getGoal() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("Goal", MODE_PRIVATE);
        int goal = sharedPreferences.getInt("default_goal", 5000);
        return goal;
    }

    /**
     * Get goal for a day that has past
     * @param date - the date to get the goal
     * @return the goal on date
     */
    public int getGoal(String date) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("Goal", MODE_PRIVATE);
        int goal = sharedPreferences.getInt(date, 5000);
        return goal;
    }

    /**
     * Sets a new goal
     * @param newGoal - the new goal
     */
    public void setGoal(int newGoal) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("Goal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("default_goal", newGoal);
        editor.apply();
    }

    /**
     * Sets the goal of a particular date
     * @param newGoal - the new goal
     * @param date - date to be set
     */
    public void setGoal(int newGoal, String date) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("Goal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(date, newGoal);
        editor.apply();
    }
}
