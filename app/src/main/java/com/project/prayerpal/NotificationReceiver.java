package com.project.prayerpal;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    private String channel_id;
    private int notification_id;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("habib", "onRecieve entered");

        channel_id = intent.getStringExtra("channel_id");
        notification_id = intent.getIntExtra("notification_id", 0);

        this.context = context;

        showNotification(context, notification_id);
    }

    private void showNotification(Context context, int notification_id) {

        //add conditional code that sends a notification based on the type of prayer that has arrived.

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_id)
                .setContentTitle("Prayer alert").setContentText("prayer has arrived")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(android.R.drawable.ic_dialog_alert);

        Notification notification = builder.build();
        Log.d("habib", "built notification");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Log.d("habib", "notification manager created");

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d("habib", "notification permission is not granted");
            return;
        }

        notificationManager.notify(notification_id, notification);
        Log.d("habib", "notification manager is now notifying the user");
    }
}
