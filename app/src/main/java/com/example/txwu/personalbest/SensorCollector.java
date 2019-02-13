package com.example.txwu.personalbest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.widget.Toast;

import com.example.txwu.personalbest.fitness.FitnessService;
import com.example.txwu.personalbest.fitness.StepTracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SensorCollector implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelSensor;
    private int steps;
    private Context context;
    private StepDetector stepDetector;

    public SensorCollector(Context context){
        this.context = context;
        this.stepDetector = new StepDetector();
    }

    public void setup() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        SharedPreferences sharedPreferences = context.getSharedPreferences("PersonalBest", MODE_PRIVATE);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        steps = sharedPreferences.getInt(date, 0);

        accelSensor =  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent event) {
        if (stepDetector.update(event)) {
            steps++;

            SharedPreferences sharedPreferences = context.getSharedPreferences("PersonalBest", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            editor.putInt(date, steps);

            editor.apply();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
