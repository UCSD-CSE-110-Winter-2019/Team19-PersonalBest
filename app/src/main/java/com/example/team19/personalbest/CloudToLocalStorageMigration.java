package com.example.team19.personalbest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.team19.personalbest.fitness.HistoryClient;
import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class CloudToLocalStorageMigration {
    Activity activity;
    private static final String TAG = "C2LSM";
    private boolean ran = false;

    public CloudToLocalStorageMigration(Activity activity){
        this.activity = activity;
    }

    public void MigrateData(Runnable runnable) {
        MigrateSteps();
        MigrateIntentional();
        MigrateGoal();
        MigratePersonInfo(runnable);
    }

    public void MigratePersonInfo(final Runnable runnable) {
        Log.d(TAG, "Migrating Personal Info");
        SharedPreferences sharedPreferences = activity.getSharedPreferences(InitActivity.PACKAGE_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        // FIXME(phil): callback hell
        Cloud.get("Personal Info", "accepted_terms_and_privacy", new CloudCallback() {
            @Override
            public void onData(DataSnapshot d) {
                Boolean b = d.getValue(Boolean.class);
                editor.putBoolean("accepted_terms_and_privacy", b);
                editor.apply();
                Cloud.get("Personal Info", "user_height", new CloudCallback() {
                    @Override
                    public void onData(DataSnapshot d) {
                        long height = d.getValue(Long.class);
                        editor.putLong("user_height", height);
                        editor.apply();
                        Cloud.get("Personal Info", "user_measurement_unit", new CloudCallback() {
                            @Override
                            public void onData(DataSnapshot d) {
                                editor.putString("user_measurement_unit", d.getValue(String.class));
                                editor.apply();
                                if (!ran) {
                                    runnable.run();
                                    ran = true;
                                }
                            }
                        });
                    }
                });
            }
        });
        // NOTE(phil): If the callbacks do not get called, probably find a better way
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (!ran) {
                            runnable.run();
                            ran = true;
                        }
                    }
                },
                1000);
    }

    public void MigrateSteps(){
        Log.d(TAG, "Migrating steps");
        Cloud.getAll("Steps", new CloudCallback() {
            @Override
            public void onData(DataSnapshot childSnapshot) {
                // store in sharedpref
                String value = childSnapshot.getValue(Long.class).toString();
                String key = childSnapshot.getKey();
                Log.d(TAG, "Value is: " + value + " | Key is: " + key);
                SharedPreferences sharedPreferences = activity.getSharedPreferences("Steps", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(key, Integer.parseInt(value));
                editor.apply();
            }
        });
    }

    public void MigrateIntentional(){
        Log.d(TAG, "Migrating Intentional");
        Cloud.getAll("Intentional", new CloudCallback() {
            @Override
            public void onData(DataSnapshot childSnapshot) {
                // store in sharedpref
                // entering data manually -> long, while commands -> string
                String value = childSnapshot.getValue(Long.class).toString();
                String key = childSnapshot.getKey();
                Log.d(TAG, "Value is: " + value + " | Key is: " + key);
                SharedPreferences sharedPreferences = activity.getSharedPreferences("Intentional", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(key, Integer.parseInt(value));
                editor.apply();
            }
        });
    }

    public void MigrateGoal(){
        Log.d(TAG, "Migrating Goal");
        Cloud.get("Goal", "default_goal", new CloudCallback() {
            @Override
            public void onData(DataSnapshot d) {
                // store in sharedpref
                //SimpleDateFormat date = new SimpleDateFormat(s);
                long goal = d.getValue(Long.class);
                Log.d("AUTH", "" + goal);
                SharedPreferences sharedPreferences = activity.getSharedPreferences("Goal", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("default_goal", (int) goal);
                editor.apply();
            }
        });
    }

}
