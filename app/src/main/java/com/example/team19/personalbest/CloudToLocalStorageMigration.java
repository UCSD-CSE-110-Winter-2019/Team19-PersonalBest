package com.example.team19.personalbest;

import android.app.Activity;
import android.content.SharedPreferences;

import com.example.team19.personalbest.fitness.HistoryClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class CloudToLocalStorageMigration {
    Activity activity;

    public CloudToLocalStorageMigration(Activity activity){
        this.activity = activity;
    }

    public void MigrateData(){
        MigrateSteps();
    }

    public void MigrateSteps(){
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
}
