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
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

public class Goal {
    private Activity activity;
    private Timer t;
    private TimerTask goalTask;
    private long steps;

    public Goal(Activity activity, final Date endSubGoalDate) {
        this.activity = activity;
        goalTask = new TimerTask() {
            @Override
            public void run() {
                Date currentTime = Calendar.getInstance().getTime();
                if (!checkIfDailyGoalShown("subgoal1") && endSubGoalDate.after(currentTime)) {
                    showMeetSubGoal();
                    setDailyGoalShown("subgoal1");
                }
            }
        };
        t = new Timer();
        t.schedule(goalTask, 0, 2000);
    }

    public void showMeetGoal(long goal) {
        if (!checkIfDailyGoalShown("goal") && this.steps >= goal) {
            Toast.makeText(activity, "Congratulations for meeting your goal of " + String.valueOf(goal) + " steps!", Toast.LENGTH_SHORT).show();
            setDailyGoalShown("goal");
            try {
                Button button = activity.findViewById(R.id.button_change_goal);
                button.performClick();
            }
            catch (Exception e) {
                Log.d("Goal Met", "Exception when prompting to set new goal");
            }
        }
    }

    public boolean checkIfDailyGoalShown(String type) {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        SharedPreferences sharedPreferences = activity.getSharedPreferences("PersonalBest", MODE_PRIVATE);
        return sharedPreferences.getBoolean(date+type, false);
    }

    public void setDailyGoalShown(String type) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("PersonalBest", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        editor.putBoolean(date+type, true);

        String previousDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date(System.currentTimeMillis()-24*60*60*1000));
        editor.remove(previousDate+type);

        editor.apply();
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    public void showMeetSubGoal() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "TODO put subgoal here!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void show500StepsSubGoal(){
        Toast.makeText(activity, "You have reached another 500 steps!", Toast.LENGTH_SHORT).show();
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
