package com.example.junckcleaner.services;

import static com.example.junckcleaner.views.activities.AntivirusActivity.__dangerousAppFound;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WorkerAntivirus extends Worker {


    private final AppPreferences __preferences;
    private Set<String> __set;
    private final Context __context;
    private final Set<String> __resultSet;

    public WorkerAntivirus(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.__context = context;
        __preferences = new AppPreferences(context);
        __set = new HashSet<>();
        __set = __preferences.getStringSet(MyAnnotations.ANTIVIRUS_APPS);
        __resultSet = new HashSet<>();
        __preferences.adStringSet(MyAnnotations.DANGEROUS_APP, __resultSet);


    }

    @NonNull
    @Override
    public Result doWork() {
        scan();
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();

    }

    public void scan() {
        PackageManager packageManager = getApplicationContext().getPackageManager();

        if (__set != null && !__set.isEmpty()) {
            for (String app : __set) {
                boolean permissionFound = false;
                String[] permissions = new String[]{};
                try {
                    permissions = packageManager.getPackageInfo(app, PackageManager.GET_PERMISSIONS)
                            .requestedPermissions;

                    if (permissions.length != 0) {
                        for (String permission : permissions) {
                            if (!permissionFound) {
                                if (dangerousPermissions().contains(permission)) {
                                    permissionFound = true;
                                    __dangerousAppFound = true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (permissionFound) {
                    __resultSet.add(app);
                    __preferences.adStringSet(MyAnnotations.DANGEROUS_APP, __resultSet);
                }

            }
        }

    }


    public ArrayList<String> dangerousPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.GET_ACCOUNTS);
        permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        permissions.add(Manifest.permission.READ_PHONE_NUMBERS);

        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        return permissions;
    }


}
