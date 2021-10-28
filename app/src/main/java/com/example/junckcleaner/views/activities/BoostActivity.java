package com.example.junckcleaner.views.activities;

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
import com.example.junckcleaner.permissions.MyPermissions;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class BoostActivity extends BaseActivity implements SendData, TrueFalse, AdClosed {
    ConstraintLayout cl_boost_scanning, cl_boost_calculated, cl_boosting, cl_boost;
    View layout_boost_finished;

    ImageView imageView_back_1, imageView_boost_select, imageView_back_finished;
    ImageView imageView_boost_select_click;

    TextView textView_boost_apps, textView_boost_ram, appCompatTextView16, textView_button,
            textView_protect_now, textView_cool_now, textView_boosting_last_text;

    RecyclerView recyclerView_background_apps;

    Utils utils;
    AppPreferences preferences;
    List<String> backgroundApp = new ArrayList<>();
    boolean appsSelectedAll = true;

    LottieAnimationView lottie_phone_booster;
    boolean scanning = true;
    MyPermissions permissions;
    AppsAdapter appsAdapter;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boost);
        Intent intent = getIntent();
        if (intent != null && intent.getIntExtra(MyAnnotations.ID, 0) != 0) {

            NotificationManager n = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            n.cancel(intent.getIntExtra(MyAnnotations.ID, 0));
        }
        utils = new Utils(this);
        permissions = new MyPermissions(this);
        preferences = new AppPreferences(this);

        imageView_back_1 = findViewById(R.id.imageView_back_1);
        imageView_boost_select = findViewById(R.id.imageView_select);
        imageView_boost_select_click = findViewById(R.id.imageView_select_click);
        imageView_back_finished = findViewById(R.id.imageView_back_finished);

        textView_boost_apps = findViewById(R.id.textView_boost_apps);
        textView_boost_ram = findViewById(R.id.textView_boost_ram);
        appCompatTextView16 = findViewById(R.id.appCompatTextView16);
        textView_button = findViewById(R.id.textView_button);
        textView_boosting_last_text = findViewById(R.id.textView_boosting_last_text);

        textView_protect_now = findViewById(R.id.textView_protect_now);
        textView_cool_now = findViewById(R.id.textView_cool_now);

        cl_boost_scanning = findViewById(R.id.cl_boost_scanning);
        cl_boost_calculated = findViewById(R.id.cl_boost_calculated);
        cl_boost = findViewById(R.id.cl_boost);
        cl_boosting = findViewById(R.id.cl_boosting);
        layout_boost_finished = findViewById(R.id.layout_boost_finished);

        lottie_phone_booster = findViewById(R.id.lottie_phone_booster);

        recyclerView_background_apps = findViewById(R.id.recyclerView_virus);

        imageView_back_1.setOnClickListener(v -> {
            startActivity(new Intent(BoostActivity.this, ActivityMain.class));
            finish();
        });

        //viewModel
        if (!runningTask().isEmpty()) {
            backgroundApp.addAll(runningTask());
        }


        calendar = Calendar.getInstance();
        if (calendar.getTimeInMillis() > preferences.getLong(MyAnnotations.BOOST_LAST_CLEANED_TIME,
                0)) {
            scanning();

        } else {
            scanning = false;
            cl_boost_scanning.setVisibility(View.GONE);
            layout_boost_finished.setVisibility(View.VISIBLE);
        }

        imageView_back_finished.setOnClickListener(v -> {
            startActivity(new Intent(BoostActivity.this, ActivityMain.class));
            finish();
        });
        textView_protect_now.setOnClickListener(v -> {
            startActivity(new Intent(this, AntivirusActivity.class));
        });
        textView_cool_now.setOnClickListener(v -> {
            startActivity(new Intent(BoostActivity.this, CpuCoolerActivity.class));
            finish();
        });
        refreshAd(findViewById(R.id.fl_adplaceholder));

    }

    public void scanning() {
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            runOnUiThread(() -> {
                cl_boost_scanning.setVisibility(View.GONE);
                cl_boost_calculated.setVisibility(View.VISIBLE);
                calculated();
            });
        }, 5000);
    }

    public void calculated() {
        scanning = false;
        textView_boost_apps.setText(String.valueOf(backgroundApp.size()));
        appCompatTextView16.setText(backgroundApp.size() + " "
                + appCompatTextView16.getText().toString());
        textView_button.setText("Accelerate " + backgroundApp.size() + " Apps");
        ActivityManager activityManager = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        String totalRam = utils.getDataSizeWithPrefix((float) memoryInfo.totalMem);
        String usedRam = utils.getDataSizeWithPrefix((float) (memoryInfo.totalMem
                - memoryInfo.availMem));

        textView_boost_ram.setText("Ram " + usedRam + "/" + totalRam);
        appsAdapter = new AppsAdapter(this, MyAnnotations.BOOST);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_background_apps.setLayoutManager(linearLayoutManager);
        recyclerView_background_apps.setAdapter(appsAdapter);
        appsAdapter.setSendData(this);
        appsAdapter.setTrueFalse(this);
        if (!runningTask().isEmpty()) {
            appsAdapter.setAdapterApps(runningTask());
            appsAdapter.setKillingApps(runningTask());
            appsAdapter.submitList(runningTask());
        }


        imageView_boost_select_click.setOnClickListener(v -> {
            if (appsSelectedAll) {
                imageView_boost_select.setImageResource(R.drawable.ic_undone_rectangle);
                appsAdapter.clearList();
                appsSelectedAll = false;

            } else {
                appsAdapter.selectAll();
                appsSelectedAll = true;
                imageView_boost_select.setImageResource(R.drawable.ic_blue_checked);
            }
        });

        cl_boost.setOnClickListener(v -> {
            if (appsAdapter.getKillingApps().isEmpty()) {
                Toast.makeText(this, "Select at least one app", Toast.LENGTH_SHORT).show();
            } else {
                cl_boost_calculated.setVisibility(View.GONE);
                cl_boosting.setVisibility(View.VISIBLE);
                lottie_phone_booster.playAnimation();
                boostAppsDownAnimation(appsAdapter.getKillingApps().size(), 0);
                boosting();
            }

        });


    }

    public void boosting() {
        scanning = true;
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
        new Handler(Looper.getMainLooper()).postDelayed(() -> runOnUiThread(() -> {
            showInterstitialActivity(this);
        }), 7000);

    }

    public void boostAppsDownAnimation(int from, int to) {
        @SuppressLint("Recycle")
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(6000);
        animator.addUpdateListener(animation -> {

            int value = (int) animation.getAnimatedValue();
            textView_boosting_last_text.setText(value + " of " + from + " Apps Stopped");

        });
        animator.start();
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


    @Override
    public void onBackPressed() {
        if (!scanning) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (calendar.getTimeInMillis() < preferences.getLong(MyAnnotations.BOOST_LAST_CLEANED_TIME,
                0)) {
            scanning = false;
        }
    }

    @Override
    public void data(String data) {
        if (backgroundApp.size() != appsAdapter.getKillingApps().size()) {
            appsSelectedAll = false;
            imageView_boost_select.setImageResource(R.drawable.ic_undone_rectangle);

        }
        if (appsAdapter.getKillingApps().isEmpty()) {
            textView_button.setText("Accelerate " + "0" + " Apps");

        } else {
            textView_button.setText("Accelerate " + appsAdapter.getKillingApps().size() + " Apps");

        }
    }

    @Override
    public void isTrue(boolean appsSelectedAll) {
        if (appsSelectedAll) {
            imageView_boost_select.setImageResource(R.drawable.ic_blue_checked);
            this.appsSelectedAll = true;

        } else {
            this.appsSelectedAll = false;
            imageView_boost_select.setImageResource(R.drawable.ic_undone_rectangle);
        }
    }

    @Override
    public void addDismissed(boolean closed) {
        boostFinish();
    }

    @Override
    public void addFailed(boolean closed) {
        boostFinish();
    }

    public void boostFinish() {
        lottie_phone_booster.cancelAnimation();
        cl_boosting.setVisibility(View.GONE);
        layout_boost_finished.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, utils.randomValue(3,8));
        preferences.addLong(MyAnnotations.BOOST_LAST_CLEANED_TIME,
                calendar.getTimeInMillis());
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,
                R.color.white));
        scanning = false;
    }
}