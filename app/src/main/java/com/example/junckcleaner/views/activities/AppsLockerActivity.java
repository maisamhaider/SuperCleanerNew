package com.example.junckcleaner.views.activities;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.junckcleaner.R;
import com.example.junckcleaner.adapters.AppLockerAdapter;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.broadcasts.AlarmReceiver;
import com.example.junckcleaner.interfaces.SendData;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.services.AppLockService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppsLockerActivity extends AppCompatActivity implements SendData {
    private AppPreferences preferences;
    TextView textViewApp;
    AppLockerAdapter adapter2;
    List<String> list;
    boolean buttonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_locker);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));

        preferences = new AppPreferences(this);
        RecyclerView recyclerView_other_app = findViewById(R.id.recyclerView_other_app);
        findViewById(R.id.imageView_back).setOnClickListener(v ->
                startActivity(new Intent(AppsLockerActivity.this, ActivityMain.class)
                        .addFlags(FLAG_ACTIVITY_CLEAR_TOP)));

        findViewById(R.id.textView_locker_main_setting).setOnClickListener(v -> {
                    buttonClicked = true;
                    startActivity(new Intent(AppsLockerActivity.this,
                            ActivityLockSettings.class));
                }
        );


        adapter2 = new AppLockerAdapter(this, this);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_other_app.setLayoutManager(linearLayoutManager1);
        recyclerView_other_app.setAdapter(adapter2);
        loadDialog();

        startService();

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void startService() {
        /*too much important don't miss it*/
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, AppLockService.class));

        } else {
            startService(new Intent(this, AppLockService.class));
        }


        try {
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                pendingIntent = PendingIntent.getBroadcast(this, 999,
                        alarmIntent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getBroadcast(this, 999,
                        alarmIntent, 0);
            }
            int interval = (86400 * 1000) / 4;
            if (manager != null) {
                manager.cancel(pendingIntent);
            }
            if (manager != null) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval,
                        pendingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void data(String data) {
        textViewApp.setText("Locked " + data + " Apps");
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            if (preferences.getStringSet(MyAnnotations.ALL_APPS) != null &&
                    !preferences.getStringSet(MyAnnotations.ALL_APPS).isEmpty()) {
                list = new ArrayList<>();
                list.addAll(preferences.getStringSet(MyAnnotations.ALL_APPS));
            }
            handler.post(() -> {
                if (!list.isEmpty()) {
                    adapter2.submitList(list);
                }
            });
        });

        textViewApp = findViewById(R.id.textView_main_locked_app);
        if (preferences.getStringSet(MyAnnotations.APPS_SET) != null) {
            textViewApp.setText("Locked " + preferences.getStringSet(MyAnnotations.APPS_SET).size()
                    + " Apps");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!buttonClicked) {
            finish();
        }
    }

    public void loadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_loading_dialog, null,
                false);

        builder.setView(view).setCancelable(true);

    }


}