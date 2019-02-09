package com.example.txwu.personalbest.fitness;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.txwu.personalbest.MainActivity;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SENSOR_SERVICE;

public class SensorAdapter extends Service implements FitnessService, SensorEventListener {
    private MainActivity activity;

    public SensorAdapter(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getRequestCode() {
        return 0;
    }

    SensorManager sensorManager;
    Sensor motionSensor;
    Sensor accelSensor;
    TriggerEventListener listener;
    long steps;

    /* https://github.com/bagilevi/android-pedometer/blob/master/src/name/bagi/levente/pedometer/StepDetector.java */
    private float   mLimit = 10;
    private float   mLastValues[] = new float[3*2];
    private float   mScale[] = new float[2];
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private float   mLastDiff[] = new float[3*2];
    private int mLastMatch = -1;
    /* https://github.com/bagilevi/android-pedometer/blob/master/src/name/bagi/levente/pedometer/StepDetector.java */

    @Override
    public void setup() {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // TODO(phil): remove this
        for (Sensor s : list) {
            System.out.println(s.getName());
        }

        if (stepSensor == null) {
            Toast.makeText(activity, "No Step Counter Sensor!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "We have Step Counter Sensor!", Toast.LENGTH_SHORT).show();
        }

        motionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);

/*        if (motionSensor == null) {
            Toast.makeText(activity, "No Motion Sensor!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "We have Motion Sensor!", Toast.LENGTH_SHORT).show();
        }*/

        SharedPreferences sharedPreferences = activity.getSharedPreferences("PersonalBest", MODE_PRIVATE);
        steps = sharedPreferences.getInt("steps", 0);

        listener = new TriggerEventListener() {
            @Override
            public void onTrigger(TriggerEvent triggerEvent) {
                Toast.makeText(activity, "trigger!", Toast.LENGTH_SHORT).show();
                System.out.println(triggerEvent.values[0]);
                steps++;
            }
        };

        sensorManager.requestTriggerSensor(listener, motionSensor);

        accelSensor =  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        pedometerSetup();
        activity.update(null, steps);
    }

    @Override
    public void updateStepCount(StepTracker self) {
        sensorManager.requestTriggerSensor(listener, motionSensor);
        self.update(steps);
    }

    private void pedometerSetup() {
        /* https://github.com/bagilevi/android-pedometer/blob/master/src/name/bagi/levente/pedometer/StepDetector.java */
        int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        /* https://github.com/bagilevi/android-pedometer/blob/master/src/name/bagi/levente/pedometer/StepDetector.java */
    }

    private boolean pedometerUpdate(SensorEvent event) {
        boolean update = false;
        /* https://github.com/bagilevi/android-pedometer/blob/master/src/name/bagi/levente/pedometer/StepDetector.java */
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
            }
            else {
                int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
                if (j == 1) {
                    float vSum = 0;
                    for (int i=0 ; i<3 ; i++) {
                        final float v = mYOffset + event.values[i] * mScale[j];
                        vSum += v;
                    }
                    int k = 0;
                    float v = vSum / 3;

                    float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                    if (direction == - mLastDirections[k]) {
                        // Direction changed
                        int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                        mLastExtremes[extType][k] = mLastValues[k];
                        float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                        if (diff > mLimit) {

                            boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                            boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                            boolean isNotContra = (mLastMatch != 1 - extType);

                            if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                                update = true;
                                mLastMatch = extType;
                            }
                            else {
                                mLastMatch = -1;
                            }
                        }
                        mLastDiff[k] = diff;
                    }
                    mLastDirections[k] = direction;
                    mLastValues[k] = v;
                }
            }
        }
        return update;
        /* https://github.com/bagilevi/android-pedometer/blob/master/src/name/bagi/levente/pedometer/StepDetector.java */
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (pedometerUpdate(event)) {
            steps++;

            SharedPreferences sharedPreferences = activity.getSharedPreferences("PersonalBest", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("steps", (int) steps);

            editor.apply();

            activity.update(null, steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private final LocalBinder mBinder = new LocalBinder();
    protected Handler handler;
    protected Toast mToast;

    public class LocalBinder extends Binder {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
            }
        });
        return android.app.Service.START_STICKY;
    }

    @Override
    public void cancel() {}

}
