package com.example.txwu.personalbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

<<<<<<< HEAD
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
=======
public class WalkActivity extends AppCompatActivity {
>>>>>>> 4da31d8eb34be914c0d7650281f174f363ec602b

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
<<<<<<< HEAD

        // create new Walk object using current time
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long time = cal.getTimeInMillis();
        walk = new Walk(time);

        fitnessService = FitAdapterForWalk.getInstance(this);
    }

    public void setSteps(long step) {
        walk.setSteps((int) step);
    }
    public void setDistance(long distance) {
        walk.setDistance(distance);
    }
    public void setSpeed(long speed) {
        walk.setSpeed(speed);
    }


    @Override
    public void update(Observable o, Object arg) {
        final long steps = (long)arg;
        setSteps(steps);
=======
>>>>>>> 4da31d8eb34be914c0d7650281f174f363ec602b
    }
}
