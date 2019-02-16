package com.example.txwu.personalbest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.txwu.personalbest.fitness.GoogleFitAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class StepService extends Service {
    Context context = this;
    public StepService() {
    }

    final class MyThread implements Runnable {
        int startId;

        public MyThread(int startId) {
            this.startId = startId;
        }

        @Override
        public void run() {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(StepService.this);

            if (account == null) {
                // not signed in, use sensor API
                SensorCollector sensorCollector = new SensorCollector(context);
                System.out.println("sensorcollector created");
                sensorCollector.setup();
            }

            else {
                // signed in, use Google Fit
                GoogleFitAdapter adapter = new GoogleFitAdapter(StepService.this);
                adapter.setup();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(StepService.this,"Service Started!", Toast.LENGTH_SHORT).show();
        Thread thread = new Thread(new MyThread(startId));
        System.out.println("about to start new thread");
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        Toast.makeText(StepService.this, "Service stopped!", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}