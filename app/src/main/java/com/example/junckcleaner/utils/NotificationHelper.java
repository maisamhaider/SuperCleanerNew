package com.example.junckcleaner.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private NotificationManager manager;

    Context context;

    public NotificationHelper(Context ctx) {
        this.context = ctx;
    }

    //step 2
    public NotificationCompat.Builder notifyBuilder(String channelId,
                                                    String title,
                                                    String body,
                                                    Integer icon,
                                                    Uri sound,
                                                    int streamType) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                channelId)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (sound != null && streamType != -1) {
            builder.setSound(sound, streamType);

        } else if (sound != null) {
            builder.setSound(sound);
        }
        return builder;

    }

    //step 1
    public void createNotificationChannel(String channelId,
                                          String channelName,
                                          String channelDescription,
                                          int importance) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the s upport library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }


    //step 3
    public void notify(int id,/* //step 4. call 3rd step */ NotificationCompat.Builder notification) {
        getManager().notify(id, notification.build());
    }


    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}