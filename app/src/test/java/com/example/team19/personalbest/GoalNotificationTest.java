package com.example.team19.personalbest;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.team19.personalbest.fitness.MainScreen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class GoalNotificationTest {

    private StepService service;
    private GoalNotificationTask task;

    private ShadowNotificationManager manager;

    @Before
    public void setup() {

        Intent intent = new Intent(RuntimeEnvironment.application, MainScreen.class);
        service = Robolectric.buildService(StepService.class, intent).create().get();

        task = new GoalNotificationTask(service);

        manager = Shadows.shadowOf((NotificationManager)
                RuntimeEnvironment.application.getSystemService(Context.NOTIFICATION_SERVICE));
    }

    @Test
    public void testGoalNotification() {

        // goal not accomplished; no notification given
        setCurrentGoals(20000);
        setCurrentSteps(10000);
        task.run();

        assertNull(getLatestNotification());

        setCurrentGoals(20000);
        setCurrentSteps(15000);
        task.run();

        assertNull(getLatestNotification());

        setCurrentGoals(20000);
        setCurrentSteps(19999);
        task.run();

        assertNull(getLatestNotification());

        setCurrentGoals(1001);
        setCurrentSteps(1000);
        task.run();

        assertNull(getLatestNotification());

        // test when goal met
        int goal = 10000;
        setCurrentGoals(goal);
        setCurrentSteps(20000);
        task.run();

        ShadowNotification notification = getLatestNotification();

        assertNotNull(notification);
        assertEquals(notification.getContentTitle().toString(), "Personal Best Goal Met!");
        assertEquals(notification.getContentText().toString(),
                "Congratulations on meeting your goal of " + goal + " steps!\nTap to set a new goal!");


    }

    private void setCurrentSteps(int steps) {

        final String key = new SimpleDateFormat("dd-MM-yyyy")
                .format(new Date());

        final SharedPreferences.Editor editor =
                service.getSharedPreferences("PersonalBest", MODE_PRIVATE).edit();

        editor.putInt(key, steps);
        editor.apply();
    }

    private void setCurrentGoals(int steps) {
        final String key = new SimpleDateFormat("dd-MM-yyyy")
                .format(new Date());

        final SharedPreferences.Editor editor =
                service.getSharedPreferences("Goal", MODE_PRIVATE).edit();

        editor.putInt(key, steps);
        editor.apply();
    }

    private ShadowNotification getLatestNotification() {

        final List<Notification> notifications = manager.getAllNotifications();
        if (notifications.isEmpty()) return null;

        return Shadows.shadowOf(notifications.get(0));
    }
}