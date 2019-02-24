package com.example.team19.personalbest.fitness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team19.personalbest.Auth;
import com.example.team19.personalbest.ChartActivity;
import com.example.team19.personalbest.Cloud;
import com.example.team19.personalbest.CloudToLocalStorageMigration;
import com.example.team19.personalbest.EnterNewGoalDialogFragment;
import com.example.team19.personalbest.Goal;
import com.example.team19.personalbest.R;
import com.example.team19.personalbest.StepService;
import com.example.team19.personalbest.StepsUpdateTask;
import com.example.team19.personalbest.WalkActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class MainScreen extends AppCompatActivity implements Observer, EnterNewGoalDialogFragment.NoticeDialogListener {

    private static final String TAG = "MainScreen";

    private TextView textSteps;
    private Goal goal;
    private int goalSteps;
    private int stepsPrev;
    private int stepsSubgoal = 500;
    private int mockStep;
    public static long timedif = 0;
    private Auth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        final Button startWalk = (Button)findViewById(R.id.button_start_walk);
        startWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWalkActivity();
            }
        });

        Button changeGoal = (Button)findViewById(R.id.button_change_goal);
        changeGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterNewGoal(v);
            }
        });

        textSteps = findViewById(R.id.textSteps);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        goal = new Goal(this, cal.getTime());
        goalSteps = goal.getGoal();

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US)
                .format(new Date(System.currentTimeMillis() + timedif));

        Intent intent = new Intent(this, StepService.class);
        startService(intent);

        TextView goalView = findViewById(R.id.text_goal);
        goalView.setText(String.valueOf(goalSteps));

        StepsUpdateTask stepsUpdateTask = new StepsUpdateTask(this);
        stepsUpdateTask.addObserver(this);

        SharedPreferences stepsPref = getSharedPreferences("Steps", MODE_PRIVATE);
        goal.setSteps(stepsPref.getInt(date, -1));

        // the update button checks for encouragements
        Button update = findViewById(R.id.button_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForEncouragement();
            }
        });

        Button mock = findViewById(R.id.button_mock500);
        mock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mockStep+=500;
            }
        });

        Button stop_mock = findViewById(R.id.button_stop_mock);
        stop_mock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mockStep = 0;
                timedif = 0;
            }
        });

        final EditText mock_time = findViewById(R.id.mock_time);
        Button minus = findViewById(R.id.button_minus);
        Button plus = findViewById(R.id.button_plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    long dif = Long.parseLong(mock_time.getText().toString());
                    timedif += dif;
                }
                catch (Exception e) {

                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    long dif = Long.parseLong(mock_time.getText().toString());
                    timedif -= dif;
                }
                catch (Exception e) {

                }
            }
        });

        Button stats = findViewById(R.id.button_statistics);
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, ChartActivity.class);
                startActivity(intent);
            }
        });
        checkForEncouragement();

        mAuth = new Auth(this);
        mAuth.signIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mAuth.handleActivityResult(requestCode, resultCode, data);
    }

    /**
     * Starts the WalkActivity
     */
    public void launchWalkActivity() {
        Intent intent = new Intent(this, WalkActivity.class);
        startActivity(intent);
    }

    public void enterNewGoal(View view) {
        DialogFragment enterNewGoalDialogFragment = new EnterNewGoalDialogFragment();
        enterNewGoalDialogFragment.show(getSupportFragmentManager(), "new goal");
    }


    @Override
    public void update(Observable o, Object arg) {
        final int steps = mockStep > 0?mockStep:(int) arg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                goal.setSteps(steps);
                textSteps.setText(String.valueOf(steps));
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US)
                        .format(new Date(System.currentTimeMillis() + timedif));

                SharedPreferences sharedPreferences = getSharedPreferences("Steps", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences.edit();
                editor2.putInt(date, steps);
                editor2.apply();
                if (stepsPrev != steps) {
                    Cloud.set("Steps", date, steps);
                    stepsPrev = steps;
                }
            }
        });

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US)
                .format(new Date(System.currentTimeMillis() + timedif));

        SharedPreferences sharedPreferences = getSharedPreferences("Goal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getInt(date, -1) == -1) {
            editor.putInt(date, goalSteps);
            editor.apply();
        }
    }

    /**
     * updates the goal
     */
    private void updateGoal() {

        Log.d(TAG, "Updating Goal Steps");
        SharedPreferences sharedPreferences = getSharedPreferences("Goal", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int prevIncreasedGoal = sharedPreferences.getInt("prevIncrease", 5000);
        goalSteps = goal.getGoal();

        if (goalSteps >= prevIncreasedGoal + 2000) {
            Toast.makeText(this, "Your daily goal steps have increased by over 2000.\nGood Job!",
                    Toast.LENGTH_SHORT).show();
            editor.putInt("prevIncrease", goalSteps);
            editor.apply();
        }

        TextView goalView = findViewById(R.id.text_goal);
        goalView.setText(String.valueOf(goalSteps));
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
        updateGoal();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        updateGoal();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "MainScreen Resumed");
        checkForEncouragement();
        super.onResume();
    }

    /**
     * Checks for whether encouragements need to be shown
     */
    private void checkForEncouragement() {
        goal.showMeetGoal(goalSteps);
        goal.showMeetGoalYesterday();
    }
}
