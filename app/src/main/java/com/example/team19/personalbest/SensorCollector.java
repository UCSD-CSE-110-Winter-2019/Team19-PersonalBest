package com.example.team19.personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            boolean update = stepDetector.update(event.values);
            Log.i("STEPS", "x:" + event.values[0] + " y:" + event.values[1] + " z:" + event.values[1] + " update:" + update + "\n");
            if (update) {
                steps++;

                SharedPreferences sharedPreferences = context.getSharedPreferences("PersonalBest", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                editor.putInt(date, steps);

                editor.apply();
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
