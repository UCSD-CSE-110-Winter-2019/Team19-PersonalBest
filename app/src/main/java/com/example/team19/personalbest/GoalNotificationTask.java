package com.example.team19.personalbest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.team19.personalbest.fitness.MainScreen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

public class GoalNotificationTask extends TimerTask {

    private static final String CHANNEL_ID = "GOAL_NOTIFICATION_CHANNEL_ID";

    private StepService service;

    public GoalNotificationTask(StepService service){
        super();
        this.service = service;
    }

    @Override
    public void run(){
        SharedPreferences sharedPreferences = service.getSharedPreferences("PersonalBest", MODE_PRIVATE);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        int totalSteps = sharedPreferences.getInt(date, 0);

        sharedPreferences = service.getSharedPreferences("Goal", MODE_PRIVATE);
        int goal = sharedPreferences.getInt(date, 5000);


        if (totalSteps < goal) return;
        
        sharedPreferences = service.getSharedPreferences("GoalPushNotification", MODE_PRIVATE);

        if (date.equals(sharedPreferences.getString("date", ""))) return;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("date", date);
        editor.apply();
        
        createNotificationChannel();

        Intent intent = new Intent(service, MainScreen.class);
        intent.putExtra("mainScreenFragment", "updateGoalFragment");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Personal Best Goal Met!")
        .setContentText("Congratulations on meeting your goal of " + goal + " steps!\nTap to set a new goal!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(service);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(new Date().getDate() , builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Goal Notification";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = service.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
