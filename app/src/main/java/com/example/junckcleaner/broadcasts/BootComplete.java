package com.example.junckcleaner.broadcasts;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.services.AppLockService;
import com.example.junckcleaner.services.ServiceSmartCharge;


public class BootComplete extends BroadcastReceiver {
    AppPreferences appPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        appPreferences = new AppPreferences(context);
        if (appPreferences.getBoolean(MyAnnotations.IS_LOCKED, false)) {
            start(context, new Intent(context, AppLockService.class));
        }
        /*-------alarm setting after boot again--------*/
        if (appPreferences.getBoolean(MyAnnotations.IS_LOCKED, false)) {
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);

            }
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int interval = (86400 * 1000) / 4;
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        }
        if (appPreferences.getBoolean(MyAnnotations.SMART_CHARGE_ENABLED, false)
                || appPreferences.getBoolean(MyAnnotations.CHARGING_FINISHED_SWITCH, false)) {
            startServiceSmart(context);
        }


    }

    public void startServiceSmart(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ServiceSmartCharge.class));
        } else {
            context.startService(new Intent(context, ServiceSmartCharge.class));
        }
    }


    public void start(Context context, Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
