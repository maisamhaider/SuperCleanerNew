package com.example.junckcleaner.views.activities;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.adapters.AppsAdapter;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.AdClosed;
import com.example.junckcleaner.interfaces.SendData;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class BatterySaverActivity extends BaseActivity implements SendData, TrueFalse, AdClosed {
    ConstraintLayout cl_battery_scanning, cl_battery_calculated, cl_hibernating, cl_hibernate;
    View layout_battery_finished;

    ImageView imageView_back_1, imageView_battery_select, imageView_back_finished, imageViewHibernating;
    ImageView imageView_battery_select_click;

    TextView textView_battery_apps_1, textView_button,
            textView_protect_now, textView_cool_now, textView_battery_last_text,
            textViewScanningPercentage, appCompatTextView7;

    RecyclerView recyclerView_background_apps;

    Utils utils;
    AppPreferences preferences;
    boolean appsSelectedAll = true;
    List<Drawable> icons = new ArrayList<>();

    LottieAnimationView lottie_battery_saving;
    boolean scanning = true;
    AppsAdapter appsAdapter;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_saver);
        utils = new Utils(this);
        preferences = new AppPreferences(this);
        Intent intent = getIntent();
        if (intent != null && intent.getIntExtra(MyAnnotations.ID, 0) != 0) {
            NotificationManager n = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            n.cancel(intent.getIntExtra(MyAnnotations.ID, 0));
        }
        imageView_back_1 = findViewById(R.id.imageView_back_1);
        imageView_battery_select = findViewById(R.id.imageView_battery_select);
        imageView_battery_select_click = findViewById(R.id.imageView_battery_select_click);
        imageView_back_finished = findViewById(R.id.imageView_back_finished);
        imageViewHibernating = findViewById(R.id.imageViewHibernating);

        textView_battery_apps_1 = findViewById(R.id.textView_battery_apps_1);
        textView_button = findViewById(R.id.textView_button);
        textView_battery_last_text = findViewById(R.id.textView_battery_last_text);

        textView_protect_now = findViewById(R.id.textView_protect_now);
        textView_cool_now = findViewById(R.id.textView_cool_now);

        cl_battery_scanning = findViewById(R.id.cl_battery_scanning);
        cl_battery_calculated = findViewById(R.id.cl_battery_calculated);
        cl_hibernate = findViewById(R.id.cl_hibernate);
        cl_hibernating = findViewById(R.id.cl_hibernating);
        layout_battery_finished = findViewById(R.id.layout_battery_finished);

        lottie_battery_saving = findViewById(R.id.lottie_battery_saving);

        recyclerView_background_apps = findViewById(R.id.recyclerView_virus);
        textViewScanningPercentage = findViewById(R.id.textViewScanningPercentage);
        appCompatTextView7 = findViewById(R.id.appCompatTextView7);
        ProgressBar progress = findViewById(R.id.progress);


        progress.setMax(100);
        imageView_back_1.setOnClickListener(v -> {
            startActivity(new Intent(BatterySaverActivity.this, ActivityMain.class));
            finish();
        });


        calendar = Calendar.getInstance();
        if (calendar.getTimeInMillis() > preferences.getLong(MyAnnotations.POWER_SAVED_LAST_TIME,
                0)) {
            scanning(progress);

        } else {
            scanning = false;
            cl_battery_scanning.setVisibility(View.GONE);
            layout_battery_finished.setVisibility(View.VISIBLE);
        }

        imageView_back_finished.setOnClickListener(v -> {
            startActivity(new Intent(BatterySaverActivity.this, ActivityMain.class));
            finish();
        });
        textView_protect_now.setOnClickListener(v -> {
            startActivity(new Intent(BatterySaverActivity.this, AntivirusActivity.class));
            finish();
        });
        textView_cool_now.setOnClickListener(v -> {
            startActivity(new Intent(BatterySaverActivity.this, CpuCoolerActivity.class));
            finish();
        });
        refreshAd(findViewById(R.id.fl_adplaceholder));

    }

    public void scanning(ProgressBar progress) {
        progressAnimation(1, 100, progress);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            appsAdapter = new AppsAdapter(this, MyAnnotations.BATTERY_SAVER);
            appsAdapter.setSendData(this);
            appsAdapter.setTrueFalse(this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView_background_apps.setLayoutManager(linearLayoutManager);
            recyclerView_background_apps.setAdapter(appsAdapter);
            if (runningTask() != null) {
                appsAdapter.setAdapterApps(runningTask());
                appsAdapter.setKillingApps(runningTask());
                appsAdapter.submitList(runningTask());
            }
            runOnUiThread(() -> {
                cl_battery_scanning.setVisibility(View.GONE);
                cl_battery_calculated.setVisibility(View.VISIBLE);
                calculated();
            });
        }, 6000);
    }

    public void calculated() {
        scanning = false;
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_cardinal_1));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_alizarin_crimson_2));
        textView_battery_apps_1.setText(runningTask().size() + " " + textView_battery_apps_1.getText().toString());
        //        textView_button.setText("Accelerate " + backgroundApp.size() + " Apps");


        imageView_battery_select_click.setOnClickListener(v -> {
            if (appsSelectedAll) {
                imageView_battery_select.setImageResource(R.drawable.ic_undone_rectangle);
                appsAdapter.clearList();
                appsSelectedAll = false;

            } else {
                appsAdapter.selectAll();
                appsSelectedAll = true;
                imageView_battery_select.setImageResource(R.drawable.ic_blue_checked);
            }
        });

        cl_hibernate.setOnClickListener(v -> {
            if (!appsAdapter.getKillingApps().isEmpty()) {
                cl_battery_calculated.setVisibility(View.GONE);
                cl_hibernating.setVisibility(View.VISIBLE);
                lottie_battery_saving.playAnimation();
                hibernateAppsDownAnimation(appsAdapter.getKillingApps().size(), 0);
                hibernatingImageChanging();
                hibernating();
            } else {
                Toast.makeText(this, "Please select at least one app", Toast.LENGTH_SHORT).show();
            }

        });


    }

    public void hibernating() {
        scanning = true;
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_main));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));
        ActivityManager am = (ActivityManager)
                getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (!appsAdapter.getKillingApps().isEmpty()) {
                for (String s : appsAdapter.getKillingApps()) {
                    AtomicBoolean donNotKill = new AtomicBoolean(false);
                    if (preferences.getStringSet(MyAnnotations.PROTECTED_APPS) != null) {
                        if (preferences.getStringSet(MyAnnotations.PROTECTED_APPS)
                                .contains(s)) {
                            donNotKill.set(true);
                        }
                    } else if (preferences.getStringSet(MyAnnotations.IGNORE_APPS) != null) {
                        if (preferences.getStringSet(MyAnnotations.IGNORE_APPS)
                                .contains(s)) {
                            donNotKill.set(true);
                        }
                    }
                    if (!donNotKill.get()) {
                        am.killBackgroundProcesses(s);
                    }
                }
            }
        });


    }

    List<String> runningTask() {
        List<String> sendingList = new ArrayList<>();
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        manager.getRunningTasks(100);
        List<ActivityManager.RunningAppProcessInfo> list = manager.getRunningAppProcesses();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            UsageStatsManager usage = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -50);
            long time = calendar.getTimeInMillis();
            List<UsageStats> stats = usage.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    time, System.currentTimeMillis());
            try {
                if (!stats.isEmpty()) {
                    for (UsageStats app : stats) {
                        if (!app.getPackageName().equals(getPackageName())) {
                            icons.add((Drawable) utils.appInfo(app.getPackageName(), MyAnnotations.APP_ICON));
                            sendingList.add(app.getPackageName());
                        }
                    }
                    return sendingList;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                if (!list.isEmpty()) {
                    for (ActivityManager.RunningAppProcessInfo taskInfo : list) {
                        if (!taskInfo.processName.equals(getPackageName())) {
                            icons.add((Drawable) utils.appInfo(taskInfo.processName, MyAnnotations.APP_ICON));
                            sendingList.add(taskInfo.processName);
                        }
                    }
                    return sendingList;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sendingList;
    }

    public void hibernateAppsDownAnimation(int from, int to) {
        @SuppressLint("Recycle")
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(7000);
        animator.addUpdateListener(animation -> {

            int value = (int) animation.getAnimatedValue();
            new Handler(Looper.getMainLooper()).post(() ->
                    textView_battery_last_text.setText("Hibernated " + value + "/" + from + " App(s)"));
            if (value == 0) {
                showInterstitialActivity(this);
            }
        });
        animator.start();

    }

    public void hibernatingImageChanging() {

        @SuppressLint("Recycle")
        ValueAnimator animator = ValueAnimator.ofInt(1, icons.size());
        animator.setDuration(6500);
        animator.addUpdateListener(animation -> {

            int value = (int) animation.getAnimatedValue();
            if (value == icons.size()) {
                imageViewHibernating.setVisibility(View.GONE);
            }
            new Handler().postDelayed(() -> {
                if (!isDestroyed()) {

                    Glide.with(BatterySaverActivity.this).load(icons.get(value - 1))
                            .into(imageViewHibernating);
                }
            }, 90);
        });
        animator.start();

    }

    public void progressAnimation(int from, int to, ProgressBar progress) {
        @SuppressLint("Recycle")
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(6000);
        animator.addUpdateListener(animation -> {

            int value = (int) animation.getAnimatedValue();
            new Handler(Looper.getMainLooper()).post(() -> {
                textViewScanningPercentage.setText(value + "%");
                progress.setProgress(value);
            });
        });
        animator.start();

    }

    @Override
    public void onBackPressed() {
        if (!scanning) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (calendar.getTimeInMillis() < preferences.getLong(MyAnnotations.POWER_SAVED_LAST_TIME,
                0)) {
            scanning = false;
        }
    }

    @Override
    public void data(String data) {
        if (runningTask().size() != appsAdapter.getKillingApps().size()) {
            appsSelectedAll = false;
            imageView_battery_select.setImageResource(R.drawable.ic_undone_rectangle);
        }
    }

    @Override
    public void isTrue(boolean appsSelectedAll) {
        if (appsSelectedAll) {
            imageView_battery_select.setImageResource(R.drawable.ic_done_rectangle);
            this.appsSelectedAll = true;

        } else {
            this.appsSelectedAll = false;
            imageView_battery_select.setImageResource(R.drawable.ic_undone_rectangle);
        }
    }

    @Override
    public void addDismissed(boolean closed) {
        hibernatingFinish();
    }

    @Override
    public void addFailed(boolean closed) {
        hibernatingFinish();
    }

    public void hibernatingFinish() {
        lottie_battery_saving.cancelAnimation();
        cl_hibernating.setVisibility(View.GONE);
        layout_battery_finished.setVisibility(View.VISIBLE);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_main));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, utils.randomValue(4, 9));
        preferences.addLong(MyAnnotations.POWER_SAVED_LAST_TIME, calendar.getTimeInMillis());
        appCompatTextView7.setText(appsAdapter.getKillingApps() + " battery draining apps hibernated");
        scanning = false;
    }
}