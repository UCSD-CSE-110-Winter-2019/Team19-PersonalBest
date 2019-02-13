package com.example.txwu.personalbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.txwu.personalbest.fitness.GoogleFitAdapter;
import com.example.txwu.personalbest.fitness.SensorAdapter;
import com.example.txwu.personalbest.fitness.FitnessService;
import com.example.txwu.personalbest.fitness.FitnessServiceFactory;
import com.example.txwu.personalbest.fitness.StepTracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer{
    private String fitnessServiceKey = "GOOGLE_FIT";

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    private TextView textSteps;
    private TextView textTest;
    private FitnessService fitnessService;
    private Goal goal;
    private int goalSteps = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        textSteps = findViewById(R.id.textSteps);

        goal = new Goal(this, new Date());

        Intent intent = new Intent(this, StepService2.class);
        startService(intent);

        StepsUpdateTask stepsUpdateTask = new StepsUpdateTask(this);
        stepsUpdateTask.addObserver(this);

/*       if (stepSensor == null) {
            FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
                @Override
                public FitnessService create(MainActivity activity) {
                    return new SensorAdapter(activity);
                }
            });
        } else {*/
            /*FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
                @Override
                public FitnessService create(MainActivity activity) {
                    return new GoogleFitAdapter(activity);
                }
            });*/
        //}

        //fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        //fitnessService.setup();
    }

    private boolean isFirstTimeOpenApp = true;

    @Override
    public void update(Observable o, Object arg) {
        final int steps = (int) arg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(steps);
                goal.setSteps(steps);
                goal.showMeetGoal(goalSteps);
                textSteps.setText(String.valueOf(steps));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
