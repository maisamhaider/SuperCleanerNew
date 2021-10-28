package com.example.junckcleaner.views.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
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

public class CpuCoolerActivity extends BaseActivity implements SendData, TrueFalse, AdClosed {

    ConstraintLayout cl_cpuCooler_scanning, cl_cpuCooler_calculated, cl_cooling, cl_cool;
    View layout_cpuCooler_finished;

    ImageView imageView_back_1, imageView_cpuCooler_select, imageView_back_finished;
    ImageView imageView_cpuCooler_select_click;

    TextView textView_cpuCooler_temp, textView_cooling_temp,
            textView_protect_now, textView_clean_now;

    RecyclerView recyclerView_background_apps;

    Utils utils;
    AppPreferences preferences;
    List<String> backgroundApp = new ArrayList<>();
    boolean appsSelectedAll = true;

    LottieAnimationView lottie_cpu_temp, lottie_cpu_snow;
    boolean scanning = true;
    AppsAdapter appsAdapter;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_cooler);
        Intent intent = getIntent();
        if (intent != null && intent.getIntExtra(MyAnnotations.ID, 0) != 0) {

            NotificationManager n = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            n.cancel(intent.getIntExtra(MyAnnotations.ID, 0));
        }
        utils = new Utils(this);
        preferences = new AppPreferences(this);

        imageView_back_1 = findViewById(R.id.imageView_back_1);
        imageView_cpuCooler_select = findViewById(R.id.imageView_cpuCooler_select);
        imageView_cpuCooler_select_click = findViewById(R.id.imageView_cpuCooler_select_click);
        imageView_back_finished = findViewById(R.id.imageView_back_finished);

        textView_cpuCooler_temp = findViewById(R.id.textView_cpuCooler_temp);
        textView_cooling_temp = findViewById(R.id.textView_cooling_temp);

        textView_protect_now = findViewById(R.id.textView_protect_now);
        textView_clean_now = findViewById(R.id.textView_clean_now);

        cl_cpuCooler_scanning = findViewById(R.id.cl_cpuCooler_scanning);
        cl_cpuCooler_calculated = findViewById(R.id.cl_cpuCooler_calculated);
        cl_cool = findViewById(R.id.cl_cool);
        cl_cooling = findViewById(R.id.cl_cooling);
        layout_cpuCooler_finished = findViewById(R.id.layout_cpuCooler_finished);

        lottie_cpu_temp = findViewById(R.id.lottie_cpu_temp);
        lottie_cpu_snow = findViewById(R.id.lottie_cpu_snow);

        recyclerView_background_apps = findViewById(R.id.recyclerView_virus);

        imageView_back_1.setOnClickListener(v -> {
            startActivity(new Intent(CpuCoolerActivity.this, ActivityMain.class));
            finish();
        });
        calendar = Calendar.getInstance();
        if (calendar.getTimeInMillis() > preferences.getLong(MyAnnotations.COOL_LAST_CLEANED_TIME,
                0)) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_main));
            scanning();

        } else {
            cl_cpuCooler_scanning.setVisibility(View.GONE);
            layout_cpuCooler_finished.setVisibility(View.VISIBLE);
        }

        imageView_back_finished.setOnClickListener(v -> {
            startActivity(new Intent(CpuCoolerActivity.this, ActivityMain.class));
            finish();
        });
        textView_protect_now.setOnClickListener(v -> {
            startActivity(new Intent(this, AntivirusActivity.class));
        });
        textView_clean_now.setOnClickListener(v -> {
            startActivity(new Intent(CpuCoolerActivity.this, JunkActivity.class));
            finish();
        });

        refreshAd(findViewById(R.id.fl_adplaceholder));
    }

    public void scanning() {
        if (!runningTask().isEmpty()) {
            backgroundApp.addAll(runningTask());
        }

        new Handler(Looper.getMainLooper()).post(() -> {

            runOnUiThread(() -> {

                new Handler(Looper.getMainLooper()).postDelayed(() -> {

                    runOnUiThread(() -> {
                        cl_cpuCooler_scanning.setVisibility(View.GONE);
                        cl_cpuCooler_calculated.setVisibility(View.VISIBLE);
                        calculated();
                    });
                }, 6000);

            });

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

    public void calculated() {

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_mojo_1));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_mojo_1));

        textView_cpuCooler_temp.setText(String.valueOf(utils.randomValue(20, 50)));

        appsAdapter = new AppsAdapter(this, MyAnnotations.COOLER);
        appsAdapter.setSendData(this);
        appsAdapter.setTrueFalse(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_background_apps.setLayoutManager(linearLayoutManager);
        recyclerView_background_apps.setAdapter(appsAdapter);

        if (!runningTask().isEmpty()) {
            appsAdapter.setAdapterApps(runningTask());
            appsAdapter.setKillingApps(runningTask());
            appsAdapter.submitList(runningTask());

        }
        imageView_cpuCooler_select_click.setOnClickListener(v -> {
            if (appsSelectedAll) {
                imageView_cpuCooler_select.setImageResource(R.drawable.ic_undone_rectangle);
                appsAdapter.clearList();
                appsSelectedAll = false;

            } else {
                appsAdapter.selectAll();
                appsSelectedAll = true;
                imageView_cpuCooler_select.setImageResource(R.drawable.ic_done_rectangle);
            }
        });
        scanning = false;

        cl_cool.setOnClickListener(v -> {
            if (!appsAdapter.getKillingApps().isEmpty()) {
                cooling();

            } else {
                Toast.makeText(this, "select at least one app", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void cooling() {
        scanning = true;
        cl_cpuCooler_calculated.setVisibility(View.GONE);
        cl_cooling.setVisibility(View.VISIBLE);
        coolingValueDownAnimation(Integer.parseInt(textView_cpuCooler_temp.getText().toString()),
                0);
        coolingBackgroundAnimation(ContextCompat.getColor(this, R.color.color_mojo_1),
                ContextCompat.getColor(this, R.color.color_main));
        lottie_cpu_temp.playAnimation();
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                lottie_cpu_snow.playAnimation(), 2000);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ActivityManager am = (ActivityManager)
                        getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
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
            }
        });
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showInterstitialActivity(this);
        }, 7000);


    }

    public void coolingBackgroundAnimation(int from, int to) {
        @SuppressLint("Recycle")
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setDuration(6000);
        animator.addUpdateListener(animation -> {

            Integer value = (Integer) animation.getAnimatedValue();
            cl_cooling.setBackgroundColor(value);
            getWindow().setStatusBarColor(value);
            getWindow().setNavigationBarColor(value);

        });
        animator.start();

    }

    public void coolingValueDownAnimation(int from, int to) {
        @SuppressLint("Recycle")
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(6000);
        animator.addUpdateListener(animation -> {

            int value = (int) animation.getAnimatedValue();
            textView_cooling_temp.setText(String.valueOf(value));

        });
        animator.start();

    }

    @Override
    public void onBackPressed() {
        if (!scanning)
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (calendar.getTimeInMillis() < preferences.getLong(MyAnnotations.COOL_LAST_CLEANED_TIME,
                0)) {
            scanning = false;
        }
    }

    @Override
    public void data(String data) {
        if (backgroundApp.size() != appsAdapter.getKillingApps().size()) {
            appsSelectedAll = false;
            imageView_cpuCooler_select.setImageResource(R.drawable.ic_undone_rectangle);
        }
    }

    @Override
    public void isTrue(boolean appsSelectedAll) {
        if (appsSelectedAll) {
            imageView_cpuCooler_select.setImageResource(R.drawable.ic_done_rectangle);
            this.appsSelectedAll = true;

        } else {
            this.appsSelectedAll = false;
            imageView_cpuCooler_select.setImageResource(R.drawable.ic_undone_rectangle);
        }
    }

    @Override
    public void addDismissed(boolean closed) {
        cpuFinish();
    }

    @Override
    public void addFailed(boolean closed) {
        cpuFinish();
    }

    public void cpuFinish() {
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        lottie_cpu_temp.cancelAnimation();
        lottie_cpu_snow.cancelAnimation();
        cl_cooling.setVisibility(View.GONE);
        layout_cpuCooler_finished.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, utils.randomValue(4,9));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_main));
        preferences.addLong(MyAnnotations.COOL_LAST_CLEANED_TIME,
                calendar.getTimeInMillis());
        scanning = false;
    }
}