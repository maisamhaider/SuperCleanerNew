package com.example.junckcleaner.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.services.AppLockService;
import com.example.junckcleaner.services.ServiceSmartCharge;

public class BroadcastServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        AppPreferences appPreferences = new AppPreferences(context);

        switch (action) {
            case MyAnnotations.START_LOCKER_SERVICE:
                if (appPreferences.getBoolean(MyAnnotations.IS_LOCKED, false)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(new Intent(context, AppLockService.class));
                    } else {
                        context.startService(new Intent(context, AppLockService.class));
                    }
                }
                break;
            case MyAnnotations.START_SMART_SERVICE:
                if (appPreferences.getBoolean(MyAnnotations.SMART_CHARGE_ENABLED, false)
                        || appPreferences.getBoolean(MyAnnotations.CHARGING_FINISHED_SWITCH, false)) {
                    startServiceSmart(context);
                }
                break;
            case MyAnnotations.START_SERVICE_REMINDER:

                break;
        }
    }

    public void startServiceSmart(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ServiceSmartCharge.class));
        } else {
            context.startService(new Intent(context, ServiceSmartCharge.class));
        }
    }
}
