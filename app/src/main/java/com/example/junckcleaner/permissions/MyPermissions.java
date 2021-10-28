package com.example.junckcleaner.permissions;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.junckcleaner.annotations.ManifestPermissions;
import com.example.junckcleaner.annotations.RequestCodes;

public class MyPermissions {

    private static final int REQ = 1111;

    Context context;

    public MyPermissions(Context context) {
        this.context = context;
    }

    public boolean checkPermission() {
        for (String permission : ManifestPermissions.REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context,
                    permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;

    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions((Activity) context,
                ManifestPermissions.REQUIRED_PERMISSIONS,
                RequestCodes.REQUEST_CODE_PERMISSIONS);

    }

    public boolean checkDNDPermission() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return notificationManager.isNotificationPolicyAccessGranted();
        }
        return false;
    }

    public void requestDNDPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
        }
    }

    public boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager)
                context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public boolean checkAppUsagePermission() {

        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(),
                    0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName);
            return (mode != AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }

    }

    public void requestAppUsagePermission() {
        context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
    }


}
