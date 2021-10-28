package com.example.junckcleaner.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.services.AppLockService;


public class AlarmReceiver extends BroadcastReceiver {
    AppPreferences appPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        appPreferences = new AppPreferences(context);
        if (appPreferences.getBoolean(MyAnnotations.IS_LOCKED, false)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, AppLockService.class));
            } else {
                context.startService(new Intent(context, AppLockService.class));
            }
        }

    }
}
