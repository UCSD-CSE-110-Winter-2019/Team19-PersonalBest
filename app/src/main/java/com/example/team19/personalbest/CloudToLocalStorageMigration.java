package com.example.team19.personalbest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.team19.personalbest.fitness.HistoryClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class CloudToLocalStorageMigration {
    Activity activity;
    private static final String TAG = "C2LSM";

    public CloudToLocalStorageMigration(Activity activity){
        this.activity = activity;
    }

    public void MigrateData(){
        MigrateSteps();
        MigrateIntentional();
        MigrateGoal();
    }

    public void MigrateSteps(){
        Log.d(TAG, "Migrating steps");
        Cloud.getAll("Steps", new CloudStorageCallback() {
            @Override
            public void onData(String s, String t) {
                // store in sharedpref
                //SimpleDateFormat date = new SimpleDateFormat(s);
                SharedPreferences sharedPreferences = activity.getSharedPreferences("Steps", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(s, Integer.parseInt(t));
                editor.apply();
            }
        });
    }

    public void MigrateIntentional(){
        Log.d(TAG, "Migrating Intentional");
        Cloud.getAll("Intentional", new CloudStorageCallback() {
            @Override
            public void onData(String s, String t) {
                // store in sharedpref
                //SimpleDateFormat date = new SimpleDateFormat(s);
                SharedPreferences sharedPreferences = activity.getSharedPreferences("Intentional", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(s, Integer.parseInt(t));
                editor.apply();
            }
        });
    }

    public void MigrateGoal(){
        Log.d(TAG, "Migrating Goal");
        Cloud.get("Goal", "default_goal", new CloudCallback() {
            @Override
            public void onData(String s) {
                // store in sharedpref
                //SimpleDateFormat date = new SimpleDateFormat(s);
                SharedPreferences sharedPreferences = activity.getSharedPreferences("Goal", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("default_goal", Integer.parseInt(s));
                editor.apply();
            }
        });
    }

}
