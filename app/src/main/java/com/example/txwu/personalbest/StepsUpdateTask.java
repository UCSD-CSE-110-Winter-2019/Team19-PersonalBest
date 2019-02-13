package com.example.txwu.personalbest;

import android.app.Activity;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

public class StepsUpdateTask extends Observable {
    private Timer t;
    private TimerTask timer;

    public StepsUpdateTask(final Activity activity) {
        timer = new TimerTask() {
            @Override
            public void run() {
                setChanged();
                SharedPreferences sharedPreferences = activity.getSharedPreferences("PersonalBest", MODE_PRIVATE);
                           
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                notifyObservers(sharedPreferences.getInt(date, 0));
            }
        };
        t = new Timer();
        t.schedule(timer, 0, 1000);
    }
}
