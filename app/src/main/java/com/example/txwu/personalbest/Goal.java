package com.example.txwu.personalbest;

import android.app.Activity;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

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
                if (endSubGoalDate.after(currentTime)) {
                    showMeetSubGoal();
                }
            }
        };
        t = new Timer();
        t.schedule(goalTask, 0, 2000);
    }

    public void showMeetGoal(long goal) {
        if (this.steps >= goal) {
            Toast.makeText(activity, "Congratulations for meeting your goal of " + String.valueOf(goal) + " steps!", Toast.LENGTH_SHORT).show();
        }
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
}
