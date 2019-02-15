package com.example.txwu.personalbest.fitness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.txwu.personalbest.EnterNewGoalDialogFragment;
import com.example.txwu.personalbest.Goal;
import com.example.txwu.personalbest.R;
import com.example.txwu.personalbest.StepService;
import com.example.txwu.personalbest.StepsUpdateTask;
import com.example.txwu.personalbest.WalkActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class MainScreen extends AppCompatActivity implements Observer, EnterNewGoalDialogFragment.NoticeDialogListener {
    private String fitnessServiceKey = "GOOGLE_FIT";

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    private TextView textSteps;
    //private FitnessService fitnessService;
    private Goal goal;
    private int goalSteps;
    private int stepsPrev;
    private int stepsSubgoal = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Button startWalk = (Button)findViewById(R.id.button_start_walk);
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

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date());
        SharedPreferences sharedPreferences =getSharedPreferences("PersonalBest", MODE_PRIVATE);
        stepsPrev = sharedPreferences.getInt(date + "stepsPrev", 0);

        Intent intent = new Intent(this, StepService.class);
        startService(intent);

        TextView goalView = findViewById(R.id.text_goal);
        goalView.setText(String.valueOf(goalSteps));

        StepsUpdateTask stepsUpdateTask = new StepsUpdateTask(this);
        stepsUpdateTask.addObserver(this);
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
        final int steps = (int) arg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                goal.setSteps(steps);
                goal.showMeetGoal(goalSteps);
                textSteps.setText(String.valueOf(steps));
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date());

                if (steps >= stepsPrev + stepsSubgoal) {
                    SharedPreferences sharedPreferences =
                            getSharedPreferences("PersonalBest", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(date + "stepsPrev", (int) steps);
                    editor.apply();
                }

                SharedPreferences sharedPreferences = getSharedPreferences("Steps", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences.edit();
                editor2.putInt(date, steps);
            }
        });
    }
    
    private void updateGoal() {
        goalSteps = goal.getGoal();
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

}
