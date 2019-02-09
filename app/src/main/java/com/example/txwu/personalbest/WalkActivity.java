package com.example.txwu.personalbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        fitnessService.setup();
    }

    public void setSteps(long step) {
        walk.setSteps((int) step);
        TextView step_count = findViewById(R.id.walk_step_counter);
        step_count.setText(String.format("%d", walk.getSteps()));
    }
    public void setDistance(long distance) {
        walk.setDistance(distance);
        TextView dist_count = findViewById(R.id.walk_distance);
        dist_count.setText(String.format("%f", walk.getDistance()/1600));
    }
    public void setSpeed(long speed) {
        walk.setSpeed(speed);
    }


    @Override
    public void update(Observable o, Object arg) {
        final long steps = (long)arg;
        setSteps(steps);
    }
}
