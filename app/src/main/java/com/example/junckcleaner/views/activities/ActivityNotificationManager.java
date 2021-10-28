package com.example.junckcleaner.views.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.views.fragments.FragmentNotificationManager;

public class ActivityNotificationManager extends AppCompatActivity {
    AppPreferences appPreferences;
    FrameLayout container;
    ImageView imageViewApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_manager);
        appPreferences = new AppPreferences(this);
        container = findViewById(R.id.container);
        imageViewApps = findViewById(R.id.imageViewApps);
        findViewById(R.id.imageView_back).setOnClickListener(v -> {
            finish();
        });
        findViewById(R.id.textView_enable).setOnClickListener(v -> {

            dialog();
        });
        imageViewApps.setOnClickListener(v -> {
            startActivity(new Intent(this, ActivityNotyApps.class));
        });
    }


    public void frag(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, null).commit();

    }

    public void dialog() {
        View view = LayoutInflater.from(ActivityNotificationManager.this).inflate(R.layout.layout_notification_needed_dialog,
                null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityNotificationManager.this)
                .setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView_grant = view.findViewById(R.id.textView_grant);
        textView_grant.setOnClickListener(v1 -> {
            if (!appPreferences.getBoolean(MyAnnotations.NOTIFICATION_MANAGEMENT, false)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (weHaveNotNotificationListenerPermission()) {
                        enableNotificationServiceSettingMethod();
                    }
                } else {
                    appPreferences.addBoolean(MyAnnotations.NOTIFICATION_MANAGEMENT, true);
                    container.setVisibility(View.VISIBLE);
                    imageViewApps.setVisibility(View.VISIBLE);
                }
            }
            alertDialog.dismiss();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void enableNotificationServiceSettingMethod() {
        if (weHaveNotNotificationListenerPermission()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        }
    }

    public boolean weHaveNotNotificationListenerPermission() {
        boolean perm = false;
        for (String service : NotificationManagerCompat.getEnabledListenerPackages(this)) {
            if (service.equals(getPackageName()))
                perm = true;
        }
        return !perm;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (weHaveNotNotificationListenerPermission()) {
                container.setVisibility(View.GONE);
                imageViewApps.setVisibility(View.GONE);
            } else {
                container.setVisibility(View.VISIBLE);
                imageViewApps.setVisibility(View.VISIBLE);

            }
        } else {
            if (appPreferences.getBoolean(MyAnnotations.NOTIFICATION_MANAGEMENT, false)) {
                container.setVisibility(View.VISIBLE);
                imageViewApps.setVisibility(View.VISIBLE);
            } else {
                container.setVisibility(View.GONE);
                imageViewApps.setVisibility(View.GONE);
            }
        }

        frag(new FragmentNotificationManager());
    }
}