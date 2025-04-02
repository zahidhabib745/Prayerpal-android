package com.project.prayerpal;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class NotificationScheduler {

    private static final String CHANNEL_ID = "Prayer";
    private static AlarmManager alarmManager;
    private static int num;

    public static void scheduleNotification(Context context, long delayMillis, int prayerReference) {

        createNotificationChannel(context);
        num = prayerReference;

        Log.d("habib", "createNotificationChannel method ran");

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d("habib", "alarm manager created");

        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.setAction("com.project.prayerpal.NOTIFICATION_ACTION");

        notificationIntent.putExtra("notification_id", prayerReference);
        notificationIntent.putExtra("channel_id", CHANNEL_ID);

        Log.d("habib", "extras added to intent");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Log.d("habib", "Pending intent created");

        //long timePrayerTakesPlace = System.currentTimeMillis() + delayMillis;//Added 10 seconds to ensure the alarm has enough time to schedule before the prayer time passes
        Log.d("habib", "timePrayerTakesPlace created");

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, delayMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d("habib", "alarm manager set");
    }

    public static void createNotificationChannel(Context context) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationManager notificationManager= context.getSystemService(NotificationManager.class);
            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);

            if(channel == null){

                channel = new NotificationChannel(CHANNEL_ID, "Prayer alert", NotificationManager.IMPORTANCE_DEFAULT);

                notificationManager.createNotificationChannel(channel);

                Log.d("habib", "Created the notification channel");
            }

            Log.d("habib", "Notification channel already created");
        }else{

            Log.d("habib", "if statement for notification version check didn't pass");
        }

        Log.d("habib", "createNotificationChannel ended");
    }

    public static void cancelNotifications(Context context){

        Log.d("habib", "entered cancel notification method");


        Intent intent = new Intent(context, NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        intent.putExtra("notification_id", num);
        intent.putExtra("channel_id", CHANNEL_ID);

        if(alarmManager == null){

            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);

        Log.d("habib", "cancelled notification");

       /*
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.cancel(0);
        notificationManager.cancel(1);
        notificationManager.cancel(2);
        notificationManager.cancel(3);
        notificationManager.cancel(4);

        */
    }
}
