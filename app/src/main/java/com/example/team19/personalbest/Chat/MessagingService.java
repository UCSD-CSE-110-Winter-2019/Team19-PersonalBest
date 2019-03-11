package com.example.team19.personalbest.Chat;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.example.team19.personalbest.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String click_action = remoteMessage.getNotification().getClickAction();
        String from_user_id = remoteMessage.getData().get("from_user_id");

        // foreground notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("New Message")
                    .setContentText("You've received a new Message");

        int notificationId = (int )System.currentTimeMillis();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, builder.build());
    }
}
