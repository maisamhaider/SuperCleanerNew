package com.example.junckcleaner.views.fragments;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.junckcleaner.BuildConfig;
import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.views.activities.ActivityAppsManagments;
import com.example.junckcleaner.views.activities.ActivityCreatePattern;
import com.example.junckcleaner.views.activities.ActivityCreatePin;
import com.example.junckcleaner.views.activities.ActivityDuplicateFileScanner;
import com.example.junckcleaner.views.activities.ActivityNotificationManager;
import com.example.junckcleaner.views.activities.ActivitySmartCharge;
import com.example.junckcleaner.views.activities.GameBoosterActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToolbarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToolbarFragment extends Fragment {


    private AppPreferences appPreferences;

    public ToolbarFragment() {
        // Required empty public constructor
    }

    public static ToolbarFragment newInstance() {

        return new ToolbarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_toolbar, container, false);
        appPreferences = new AppPreferences(getContext());
        ConstraintLayout cl_tool_deep_clean,
                cl_tool_game_boost,
                cl_tool_app_management,
                cl_tool_notification_manager,
                cl_tool_app_lock,
                cl_tool_smart_charge;
        cl_tool_deep_clean = root.findViewById(R.id.cl_tool_deep_clean);
        cl_tool_game_boost = root.findViewById(R.id.cl_tool_game_boost);
        cl_tool_app_management = root.findViewById(R.id.cl_tool_app_management);
        cl_tool_notification_manager = root.findViewById(R.id.cl_tool_notification_manager);
        cl_tool_app_lock = root.findViewById(R.id.cl_tool_app_lock);
        cl_tool_smart_charge = root.findViewById(R.id.cl_tool_smart_charge);

        cl_tool_deep_clean.setOnClickListener(v -> {
            if (checkPermission()) {
                startActivity(new Intent(getContext(), ActivityDuplicateFileScanner.class));

            } else {
                try {
                    showPermissionDialog();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });
        cl_tool_game_boost.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), GameBoosterActivity.class));
        });
        cl_tool_app_management.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ActivityAppsManagments.class));
        });

        cl_tool_notification_manager.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ActivityNotificationManager.class));
        });
        cl_tool_app_lock.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(getActivity())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(intent);
                } else if (!hasUsageStatsPermission()) {

                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)));

                } else {
                    goToAppLocker();
                }

            } else {
                goToAppLocker();
            }

        });

        cl_tool_smart_charge.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(getActivity())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(intent);
                } else if (!checkSystemWritePermission()) {
                    manageWriteSettingsPermission();
                } else {
                    startActivity(new Intent(getContext(), ActivitySmartCharge.class));

                }
            } else {
                startActivity(new Intent(getContext(), ActivitySmartCharge.class));
            }
        });


        return root;
    }

    private boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(getActivity());
        }
        return retVal;
    }

    private void manageWriteSettingsPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        startActivity(intent);
    }


    public void goToAppLocker() {
        if (!appPreferences.getBoolean(MyAnnotations.IS_LOCKED, false)) {
            startActivity(new Intent(getActivity(), ActivityCreatePattern.class)
                    .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PATTERN));

        } else {
            if (appPreferences.getBoolean(MyAnnotations.PATTERN_ENABLED, true)) {

                startActivity(new Intent(getActivity(), ActivityCreatePattern.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.DRAW_PATTERN));

            } else {

                startActivity(new Intent(getActivity(), ActivityCreatePin.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.ENTER_PIN));

            }
        }
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getContext().getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getContext().getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public boolean checkPermission() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        } else {
        int write = ContextCompat.checkSelfPermission(getContext(),
                WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(getContext(),
                READ_EXTERNAL_STORAGE);

        return write == PackageManager.PERMISSION_GRANTED &&
                read == PackageManager.PERMISSION_GRANTED;
//        }
    }

    public void showPermissionDialog() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//
//            try {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageName()})));
//                startActivityForResult(intent, 2000);
//            } catch (Exception e) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivityForResult(intent, 2000);
//
//            }
//
//        } else
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 333);
    }

}