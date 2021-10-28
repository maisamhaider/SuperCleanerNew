package com.example.junckcleaner.services;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.models.ModelNoty;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NotificationChecker extends NotificationListenerService {

    AppPreferences appPreferences;
    Set<String> appSet;
    Utils utils;

    @Override
    public void onCreate() {
        super.onCreate();
        appPreferences = new AppPreferences(this);
        utils = new Utils(this);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onNotificationPosted(StatusBarNotification noty) {

        appSet = appPreferences.getStringSet(MyAnnotations.NOTY_APPS);
        if (appSet != null && !appSet.isEmpty()) {
            if (appSet.contains(noty.getPackageName())) {
                cancelNotification(noty.getKey());
                List<ModelNoty> list = new ArrayList<>();
                Notification notification = noty.getNotification();
                Bundle extras = null;
                extras = notification.extras;

                if (appPreferences.getNoty(MyAnnotations.NOTIFICATIONS) != null) {
                    list = appPreferences.getNoty(MyAnnotations.NOTIFICATIONS);
                    if (extras != null) {
                        String title = extras.get("android.title").toString();
                        String body = extras.get("android.text").toString();
                        String icon = noty.getPackageName();
                        ModelNoty modelNoty = new ModelNoty(title, body, icon);
                        modelNoty.setId(noty.getId());
                        list.add(modelNoty);
                        appPreferences.setNoty(MyAnnotations.NOTIFICATIONS, list);

                    }

                } else {
                    if (extras != null) {
                        String title = extras.get("android.title").toString();
                        String body = extras.get("android.text").toString();
                        String icon = noty.getPackageName();
                        ModelNoty modelNoty = new ModelNoty(title, body, icon);
                        modelNoty.setId(noty.getId());
                        list.add(modelNoty);
                        appPreferences.setNoty(MyAnnotations.NOTIFICATIONS, list);

                    }

                }
            }
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }


}
