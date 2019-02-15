package com.example.txwu.personalbest;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.txwu.personalbest.fitness.FitAdapterForWalk;
import com.example.txwu.personalbest.fitness.FitnessService;
import com.example.txwu.personalbest.fitness.Walk;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class WalkActivity extends AppCompatActivity implements Observer {

    public static boolean isRunning = false;
    public String fitnessServiceKey = "FIT_FOR_WALK";
    private static final String TAG = "WalkActivity";
    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    public Walk walk;
    private FitnessService fitnessService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);

        // get service key if specified
        Intent intent = getIntent();
        if (intent.hasExtra(FITNESS_SERVICE_KEY))
            fitnessServiceKey = intent.getStringExtra(FITNESS_SERVICE_KEY);

        // create new Walk object using current time
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long time = cal.getTimeInMillis();
        walk = new Walk(time);

        fitnessService = FitAdapterForWalk.getInstance(this);
        request_location_permission();

        Button end = findViewById(R.id.end_walk);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endWalk();
            }
        });
        fitnessService.setup();

        isRunning = true;
        Log.d(TAG, "Walk started.");
    }

    /**
     * Sets the step count on view and walk
     * @param step - step taken
     */
    public void setSteps(long step) {
        walk.setSteps((int) step);
        TextView step_count = findViewById(R.id.walk_step_counter);
        TextView time = findViewById(R.id.walk_time);
        long seconds = (walk.EndTime() - walk.StartTime())/1000;
        long minutes = seconds/60;
        long hours = minutes/60;
        seconds = seconds%60;
        minutes = minutes%60;

        time.setText(String.format(Locale.US,"%d:%02d:%02d", hours, minutes, seconds));
        step_count.setText(String.format(Locale.US,"%d", walk.getSteps()));
    }

    /**
     * Sets the distance on view and walk
     * @param distance - distance covered
     */
    public void setDistance(float distance) {
        walk.setDistance(distance);
        TextView dist_count = findViewById(R.id.walk_distance);
        dist_count.setText(String.format(Locale.US,"%.2f", walk.getDistance()/1600));
    }

    /**
     * Sets the speed of walk
     * @param speed - average speed
     */
    public void setSpeed(float speed) {
        walk.setSpeed(speed);

        //convert m/s to MPH
        speed = (speed*3600)/1600;
        String speedFormat = String.format(Locale.US, "%.1f", speed);
        TextView speedView = findViewById(R.id.walk_speed);
        speedView.setText(speedFormat);
    }

    @Override
    public void update(Observable o, Object arg) {
        final long steps = (long)arg;
        setSteps(steps);
    }

    /**
     * Requests permission to access user location
     */
    public void request_location_permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    System.identityHashCode(this) & 0xFFFF);
        }
    }

    /**
     * End walk activity
     */
    private void endWalk() {
        float speed = walk.getSpeed();

        //convert m/s to MPH
        speed = (speed*3600)/1600;

        fitnessService.cancel();
        TextView step_count = findViewById(R.id.walk_step_counter);
        TextView time = findViewById(R.id.walk_time);
        Toast.makeText(this,
                String.format(Locale.US,
                "Walk ended with %s steps in %s." +
                        "\nAverage speed is %.1f mph.", step_count.getText().toString(),
                        time.getText().toString(), speed),
                Toast.LENGTH_LONG).show();

        Log.d(TAG, "Walk finished.");

        isRunning = false;
        saveWalk(new Date());
        finish();
    }

    /**
     * Get the SharedPreferences used for intentional walks
     * @return - intentional walk sharedPreferences
     */
    public SharedPreferences outputSharedPreferences() {
        return getSharedPreferences("Intentional", MODE_PRIVATE);
    }

    /**
     * Save the number of steps of the walk in the history
     * @param date - the current date
     */
    public void saveWalk(Date date) {

        SharedPreferences sharedPreferences = outputSharedPreferences();
        String dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(date);
        int steps = walk.getSteps();
        int prevSteps = sharedPreferences.getInt(dateString, 0);

        steps += prevSteps;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(dateString, steps);
        editor.apply();
    }
}
