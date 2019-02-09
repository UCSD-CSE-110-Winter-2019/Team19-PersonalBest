package com.example.txwu.personalbest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.txwu.personalbest.fitness.FitAdapterForWalk;
import com.example.txwu.personalbest.fitness.FitnessService;
import com.example.txwu.personalbest.fitness.FitnessServiceFactory;
import com.example.txwu.personalbest.fitness.GoogleFitAdapter;
import com.example.txwu.personalbest.fitness.Walk;

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class WalkActivity extends AppCompatActivity implements Observer {

    public String fitnessServiceKey = "FIT_FOR_WALK";
    private static final String TAG = "WalkActivity";

    public Walk walk;
    private FitnessService fitnessService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);

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
        seconds = seconds%60;

        time.setText(String.format("%d:%d", minutes, seconds));
        step_count.setText(String.format("%d", walk.getSteps()));
    }

    /**
     * Sets the distance on view and walk
     * @param distance - distance covered
     */
    public void setDistance(float distance) {
        walk.setDistance(distance);
        TextView dist_count = findViewById(R.id.walk_distance);
        dist_count.setText(String.format("%.2f", walk.getDistance()/1600));
    }

    /**
     * Sets the speed of walk
     * @param speed - average speed
     */
    public void setSpeed(float speed) {
        walk.setSpeed(speed);
    }

    @Override
    public void update(Observable o, Object arg) {
        final long steps = (long)arg;
        setSteps(steps);
    }

    /**
     * Requests permission ot access user location
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
    public void endWalk() {
        fitnessService.cancel();
        finish();
    }
}
