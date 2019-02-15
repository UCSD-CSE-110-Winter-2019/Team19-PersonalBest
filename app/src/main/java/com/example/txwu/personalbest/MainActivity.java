package com.example.txwu.personalbest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.txwu.personalbest.fitness.FitnessService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {
    private String fitnessServiceKey = "GOOGLE_FIT";

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    private TextView textSteps;
    //private FitnessService fitnessService;
    private Goal goal;
    private int goalSteps = 10;
    private int stepsPrev;
    private int stepsSubgoal = 500;

    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("com.exmaple.txwu.personalbest", MODE_PRIVATE);
        Intent i;
        if (!prefs.getBoolean("accepted_terms_and_privacy", false) || prefs.getLong("user_height", 0) == 0) {
            i = new Intent(getApplicationContext(), InitActivity.class);
            startActivity(i);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button startWalk = (Button)findViewById(R.id.button);
        startWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWalkActivity();
            }
        });

        textSteps = findViewById(R.id.textSteps);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        goal = new Goal(this, cal.getTime());

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date());
        SharedPreferences sharedPreferences =getSharedPreferences("PersonalBest", MODE_PRIVATE);
        stepsPrev = sharedPreferences.getInt(date + "stepsPrev", 0);

        requestSignInAndPermission();

        Intent intent = new Intent(this, StepService.class);
        startService(intent);

        StepsUpdateTask stepsUpdateTask = new StepsUpdateTask(this);
        stepsUpdateTask.addObserver(this);
    }

    private boolean isFirstTimeOpenApp = true;

    @Override
    public void update(Observable o, Object arg) {
        final int steps = (int) arg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                goal.setSteps(steps);
                goal.showMeetGoal(goalSteps);
                textSteps.setText(String.valueOf(steps));
                if (steps >= stepsPrev + stepsSubgoal) {
                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date());
                    SharedPreferences sharedPreferences =
                            getSharedPreferences("PersonalBest", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(date + "stepsPrev", (int) steps);
                    editor.apply();
                }
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

    /**
     * Starts the WalkActivity
     */
    public void launchWalkActivity() {
        Intent intent = new Intent(this, WalkActivity.class);
        startActivity(intent);
    }

    private void requestSignInAndPermission() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_CUMULATIVE, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_SPEED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_SPEED_SUMMARY, FitnessOptions.ACCESS_READ)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    System.identityHashCode(this) & 0xFFFF,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        }
    }

    public void enterNewGoal(View view) {
        DialogFragment enterNewGoalDialogFragment = new EnterNewGoalDialogFragment();
        enterNewGoalDialogFragment.show(getSupportFragmentManager(), "new goal");
    }
}
