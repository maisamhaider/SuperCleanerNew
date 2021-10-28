package com.example.junckcleaner.views.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.junckcleaner.R;
import com.example.junckcleaner.adapters.JunkAdapter1;
import com.example.junckcleaner.adapters.JunkAdapter2;
import com.example.junckcleaner.adapters.JunkAdapter3;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.AdClosed;
import com.example.junckcleaner.interfaces.ApkInterface;
import com.example.junckcleaner.interfaces.SelectAll;
import com.example.junckcleaner.interfaces.SendData;
import com.example.junckcleaner.interfaces.SysInterface;
import com.example.junckcleaner.interfaces.UserAppInterface;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utilities;
import com.example.junckcleaner.utils.Utils;
import com.example.junckcleaner.viewmodel.ViewModelApps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class JunkActivity extends BaseActivity implements ApkInterface, SysInterface,
        UserAppInterface, SendData, SelectAll, AdClosed {
    ApkInterface apkInterface;
    SysInterface sysInterface;
    UserAppInterface userAppInterface;
    ViewModelApps viewModelApps;
    ConstraintLayout cl_calculating_super, cl_calculating, cl_apksRecyclerView, cl_apksHead,
            cl_systemRecyclerView, cl_sys_head, cl_usrRecyclerView, cl_userHead;
    ScrollView scrollView_junk_calculated;
    View layout_junk_finished;

    TextView textView_filePath, textView_junk_size, textView_junk_prefix,
            textView_progress, textView_apks_size, textView_systemCache_size,
            textView_userApps_size, textView_boost_now, textView_save_now;
    ProgressBar progressBar3;

    ImageView imageView_download,
            imageView_system_files,
            imageView_useless_apks,
            imageView_apks_upDown,
            imageView_systemCache_upDown,
            imageView_userApps_upDown,
            imageView_apks_select,
            imageView_systemCache_select,
            imageView_userApps_select,
            imageView_back_finished,
            imageView_back_1;
    ImageView imageView_apks_upDown_click, imageView_system_upDown_click, imageView_user_upDown_click;
    ImageView imageView_apks_select_click, imageView_systemCache_select_click, imageView_userApps_select_click;

    LottieAnimationView lottie_download,
            lottie_system_files,
            lottie_useless_apks;

    RecyclerView recyclerView_apks, recyclerView_system_apps, recyclerView_user_apps;

    Utilities utilities;
    Utils utils;

    AppPreferences preferences;
    boolean apksExpended = false;
    boolean systemApksExpanded = false;
    boolean userAppsExpanded = false;

    boolean apksSelectedAll = true;
    boolean systemSelectedAll = true;
    boolean userSelectedAll = true;

    boolean scanning = false;

    JunkAdapter1 apksFileAdapter;
    JunkAdapter2 systemAppsAdapter;
    JunkAdapter3 userAppsAdapter;
    List<String> apksList = new ArrayList<>();
    List<String> systemAppsList = new ArrayList<>();
    List<String> userAppsList = new ArrayList<>();

    float total = 0;
    float apkTotal = 0;
    float sysTotal = 0;
    float userTotal = 0;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_junk);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        Intent intent = getIntent();
        if (intent != null && intent.getIntExtra(MyAnnotations.ID, 0) != 0) {

            NotificationManager n = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            n.cancel(intent.getIntExtra(MyAnnotations.ID, 0));
        }
        setApkInterface(this);
        setSysInterface(this);
        setUserAppInterface(this);

        preferences = new AppPreferences(this);
        utilities = new Utilities();
        utils = new Utils(this);

        cl_apksHead = findViewById(R.id.cl_apksHead);
        cl_apksRecyclerView = findViewById(R.id.cl_apksRecyclerView);
        cl_systemRecyclerView = findViewById(R.id.cl_systemRecyclerView);
        cl_usrRecyclerView = findViewById(R.id.cl_usrRecyclerView);
        cl_userHead = findViewById(R.id.cl_userHead);
        cl_sys_head = findViewById(R.id.cl_sys_head);
        cl_calculating_super = findViewById(R.id.cl_calculating_super);
        cl_calculating = findViewById(R.id.cl_calculating);
        scrollView_junk_calculated = findViewById(R.id.scrollView_junk_calculated);

        layout_junk_finished = findViewById(R.id.layout_junk_finished);


        recyclerView_apks = findViewById(R.id.recyclerView_apks);
        recyclerView_system_apps = findViewById(R.id.recyclerView_system_apps);
        recyclerView_user_apps = findViewById(R.id.recyclerView_user_apps);


        textView_apks_size = findViewById(R.id.textView_apks_size);
        textView_systemCache_size = findViewById(R.id.textView_systemCache_size);
        textView_userApps_size = findViewById(R.id.textView_userApps_size);
        textView_junk_size = findViewById(R.id.textView_junk_size);
        textView_junk_prefix = findViewById(R.id.textView_junk_prefix);
        textView_filePath = findViewById(R.id.textView_filePath);
        textView_progress = findViewById(R.id.textView_progress);
        textView_boost_now = findViewById(R.id.textView_boost_now);
        textView_save_now = findViewById(R.id.textView_save_now);

        progressBar3 = findViewById(R.id.progressBar3);

        imageView_apks_upDown_click = findViewById(R.id.imageView_apks_upDown_click);
        imageView_system_upDown_click = findViewById(R.id.imageView_system_upDown_click);
        imageView_user_upDown_click = findViewById(R.id.imageView_user_upDown_click);


        imageView_apks_upDown = findViewById(R.id.imageView_apks_upDown);
        imageView_systemCache_upDown = findViewById(R.id.imageView_systemCache_upDown);
        imageView_userApps_upDown = findViewById(R.id.imageView_userApps_upDown);

        imageView_useless_apks = findViewById(R.id.imageView_useless_apks);
        imageView_system_files = findViewById(R.id.imageView_system_files);
        imageView_download = findViewById(R.id.imageView_download);

        imageView_apks_select_click = findViewById(R.id.imageView_apks_select_click);
        imageView_systemCache_select_click = findViewById(R.id.imageView_systemCache_select_click);
        imageView_userApps_select_click = findViewById(R.id.imageView_userApps_select_click);

        imageView_apks_select = findViewById(R.id.imageView_apks_select);
        imageView_systemCache_select = findViewById(R.id.imageView_systemCache_select);
        imageView_userApps_select = findViewById(R.id.imageView_userApps_select);

        imageView_back_finished = findViewById(R.id.imageView_back_finished);
        imageView_back_1 = findViewById(R.id.imageView_back_1);

        lottie_useless_apks = findViewById(R.id.lottie_useless_apks);
        lottie_system_files = findViewById(R.id.lottie_system_files);
        lottie_download = findViewById(R.id.lottie_download);
        calendar = Calendar.getInstance();


        viewModelApps = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(ViewModelApps.class);


        if (preferences.getLong(MyAnnotations.JUNK_LAST_CLEANED_TIME, 0)
                > calendar.getTimeInMillis()) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_main));
            cl_calculating_super.setVisibility(View.GONE);
            layout_junk_finished.setVisibility(View.VISIBLE);
            scanning = false;
            //mean we need to wait for next clean
        } else {
            scanning = true;
            layout_junk_finished.setVisibility(View.GONE);
            scrollView_junk_calculated.setVisibility(View.GONE);
            cl_calculating.setVisibility(View.VISIBLE);
// finally change the color
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_cardinal_1));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_mojo_1));

            viewModelApps.getUserApps().observe(this, apks -> {
                if (apks != null && !apks.isEmpty()) {
                    for (String app : apks) {
                        if (!verifyInstallerId(JunkActivity.this, app)) {
                            // not play store app
                            if (utils.getCache(app) != 0) {
                                apksList.add(app);
                                total += utils.getCache(app);
                                apkTotal += utils.getCache(app);
                            }
                        }
                    }
                }
                try {
                    setApksRecyclerView(apksList);
                    junkCalculating();
                } catch (Exception e) {

                }

            });
            viewModelApps.getSystemApps().observe(this, apks -> {
                if (apks != null && !apks.isEmpty()) {
                    for (String app : apks) {
                        if (utils.getCache(app) != 0) {
                            systemAppsList.add(app);
                            total += utils.getCache(app);
                            sysTotal += utils.getCache(app);
                        }
                    }
                }
                try {
                    setSystemAppsRecyclerView(systemAppsList);
                } catch (Exception e) {

                }

            });
            viewModelApps.getUserApps().observe(this, apks -> {

                if (apks != null && !apks.isEmpty()) {
                    for (String app : apks) {
                        if (verifyInstallerId(this, app)) {
                            if (utils.getCache(app) != 0) {
                                userAppsList.add(app);
                                total += total + utils.getCache(app);
                                userTotal += utils.getCache(app);

                            }
                        }
                    }

                }
                try {
                    setUserAppsRecyclerView(userAppsList);
                } catch (Exception e) {

                }
            });

        }


        // recycler views visibilities settings
        progressBar3.setOnClickListener(v -> {
            if (!scanning) {
                scanning = true;
                textView_progress.setText("Cleaning");
                int from = ContextCompat.getColor(this, R.color.color_cardinal_1);
                int to = ContextCompat.getColor(this, R.color.color_main);

                setBackgroundColorAnimation(from, to);

                cleanProgressAnimation();
                float size = Float.parseFloat(textView_junk_size.getText().toString());
                if (size > 0f) {
                    setSizeAnimation((int) size, 0);
                }
            }
        });

        imageView_back_finished.setOnClickListener(v ->
        {
            finish();
        });

        imageView_back_1.setOnClickListener(v -> {
            if (!scanning) {
                finish();
            }
        });

        textView_boost_now.setOnClickListener(v -> {
            startActivity(new Intent(JunkActivity.this, BoostActivity.class));
            finish();
        });

        textView_save_now.setOnClickListener(v -> {
            startActivity(new Intent(this, BatterySaverActivity.class));
            finish();
        });

        imageView_apks_upDown_click.setOnClickListener(v -> {

            if (!apksExpended) {
                imageView_apks_upDown.setImageResource(R.drawable.ic_arrow_up);
                cl_apksHead.setBackgroundResource(R.drawable.shape_stroke_above_curved_white_opacity_10);
                cl_apksRecyclerView.setVisibility(View.VISIBLE);
                apksExpended = true;
            } else {
                imageView_apks_upDown.setImageResource(R.drawable.ic_arrow_down);
                cl_apksHead.setBackgroundResource(R.drawable.shape_stroke_curved_white_opacity_10);
                cl_apksRecyclerView.setVisibility(View.GONE);
                apksExpended = false;
            }

        });
        imageView_system_upDown_click.setOnClickListener(v -> {

            if (!systemApksExpanded) {
                imageView_systemCache_upDown.setImageResource(R.drawable.ic_arrow_up);
                cl_sys_head.setBackgroundResource(R.drawable.shape_stroke_above_curved_white_opacity_10);
                cl_systemRecyclerView.setVisibility(View.VISIBLE);
                systemApksExpanded = true;
            } else {
                imageView_systemCache_upDown.setImageResource(R.drawable.ic_arrow_down);
                cl_apksHead.setBackgroundResource(R.drawable.shape_stroke_curved_white_opacity_10);
                cl_systemRecyclerView.setVisibility(View.GONE);
                systemApksExpanded = false;
            }

        });
        imageView_user_upDown_click.setOnClickListener(v -> {

            if (!userAppsExpanded) {
                imageView_userApps_upDown.setImageResource(R.drawable.ic_arrow_up);
                cl_userHead.setBackgroundResource(R.drawable.shape_stroke_above_curved_white_opacity_10);
                cl_usrRecyclerView.setVisibility(View.VISIBLE);
                userAppsExpanded = true;
            } else {
                imageView_userApps_upDown.setImageResource(R.drawable.ic_arrow_down);
                cl_userHead.setBackgroundResource(R.drawable.shape_stroke_curved_white_opacity_10);
                cl_usrRecyclerView.setVisibility(View.GONE);
                userAppsExpanded = false;
            }

        });
        try {

            refreshAd(findViewById(R.id.fl_adplaceholder));
        } catch (Exception e) {
            Log.e("refreshAd", e.getMessage());
        }

    }

    boolean verifyInstallerId(Context context, String packageName) {


        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));

        // The package name of the app that has installed your app

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            try {
                final InstallSourceInfo installer = context.getPackageManager().getInstallSourceInfo(packageName);
                installer.getInitiatingPackageName();
                installer.getInstallingPackageName();
                return validInstallers.contains(installer.getInitiatingPackageName()) ||
                        validInstallers.contains(installer.getInstallingPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            String installer1 = context.getPackageManager().getInstallerPackageName(packageName);

            return installer1 != null && validInstallers.contains(installer1);
        }
        return false;
    }

    public void imagesDoneVisibility(boolean visible, int num) {
        if (visible) {
            if (num == 1) {
                imageView_useless_apks.setVisibility(View.VISIBLE);
            } else if (num == 2) {
                imageView_system_files.setVisibility(View.VISIBLE);

            } else {
                imageView_download.setVisibility(View.VISIBLE);
            }

        } else {
            if (num == 1) {
                imageView_useless_apks.setVisibility(View.INVISIBLE);

            } else if (num == 2) {
                imageView_system_files.setVisibility(View.INVISIBLE);

            } else {
                imageView_download.setVisibility(View.INVISIBLE);

            }
        }

    }

    public void lottieVisibility(boolean visible, int num) {
        if (visible) {
            if (num == 1) {
                lottie_useless_apks.setVisibility(View.VISIBLE);
            } else if (num == 2) {
                lottie_system_files.setVisibility(View.VISIBLE);

            } else {
                lottie_download.setVisibility(View.VISIBLE);
            }

        } else {
            if (num == 1) {
                lottie_useless_apks.setVisibility(View.INVISIBLE);

            } else if (num == 2) {
                lottie_system_files.setVisibility(View.INVISIBLE);

            } else {
                lottie_download.setVisibility(View.INVISIBLE);

            }
        }

    }


    public void setApksRecyclerView(List<String> list) {
        LinearLayoutManager lm1 = new LinearLayoutManager(this);
        lm1.setOrientation(LinearLayoutManager.VERTICAL);
        List<String> list1 = new ArrayList<>(list);
        List<String> list2 = new ArrayList<>(list);
        apksFileAdapter = new JunkAdapter1(JunkActivity.this, list1, list2);
        recyclerView_apks.setLayoutManager(lm1);
        recyclerView_apks.setAdapter(apksFileAdapter);
        apksFileAdapter.setSendData(this);
        apksFileAdapter.setSelectAll(this);
        apksFileAdapter.submitList(list);
        if (list.isEmpty()) {
            imageView_apks_select.setImageResource(R.drawable.ic_undone_rectangle);
        } else {
            imageView_apks_select.setImageResource(R.drawable.ic_done_rectangle);

        }
        imageView_apks_select_click.setOnClickListener(v -> {
            if (apksSelectedAll) {
                imageView_apks_select.setImageResource(R.drawable.ic_undone_rectangle);
                apksFileAdapter.clearList();
                apksSelectedAll = false;

            } else {
                apksSelectedAll = true;
                apksFileAdapter.selectAll();
                imageView_apks_select.setImageResource(R.drawable.ic_done_rectangle);
            }
        });

        if (apksList.isEmpty()) {
            imageView_apks_upDown.setVisibility(View.GONE);
            imageView_apks_select.setVisibility(View.GONE);
            imageView_apks_upDown_click.setVisibility(View.GONE);
            imageView_apks_select_click.setVisibility(View.GONE);
            textView_apks_size.setVisibility(View.GONE);
        } else {
            imageView_apks_upDown.setVisibility(View.VISIBLE);
            imageView_apks_select.setVisibility(View.VISIBLE);
            imageView_apks_upDown_click.setVisibility(View.VISIBLE);
            imageView_apks_select_click.setVisibility(View.VISIBLE);
            textView_apks_size.setVisibility(View.VISIBLE);

        }
    }

    public void setSystemAppsRecyclerView(List<String> list) {
        LinearLayoutManager lm1 = new LinearLayoutManager(this);
        lm1.setOrientation(LinearLayoutManager.VERTICAL);
        List<String> list1 = new ArrayList<>(list);
        List<String> list2 = new ArrayList<>(list);
        systemAppsAdapter = new JunkAdapter2(JunkActivity.this, list1, list2);
        recyclerView_system_apps.setLayoutManager(lm1);
        recyclerView_system_apps.setAdapter(systemAppsAdapter);
        systemAppsAdapter.setSendData(this);
        systemAppsAdapter.setSelectAll(this);
        systemAppsAdapter.submitList(list);
        if (!list.isEmpty()) {
            imageView_systemCache_select.setImageResource(R.drawable.ic_done_rectangle);
        }

        imageView_systemCache_select_click.setOnClickListener(v -> {
            if (!systemSelectedAll) {
                systemSelectedAll = true;
                imageView_systemCache_select.setImageResource(R.drawable.ic_done_rectangle);
                systemAppsAdapter.selectAll();

            } else {
                systemAppsAdapter.clearList();
                systemSelectedAll = false;
                imageView_systemCache_select.setImageResource(R.drawable.ic_undone_rectangle);
            }

        });

        if (systemAppsList.isEmpty()) {


            imageView_systemCache_upDown.setVisibility(View.GONE);
            imageView_systemCache_select.setVisibility(View.GONE);
            imageView_system_upDown_click.setVisibility(View.GONE);
            imageView_systemCache_select_click.setVisibility(View.GONE);
            textView_systemCache_size.setVisibility(View.GONE);
        } else {
            imageView_systemCache_upDown.setVisibility(View.VISIBLE);
            imageView_systemCache_select.setVisibility(View.VISIBLE);
            imageView_system_upDown_click.setVisibility(View.VISIBLE);
            imageView_systemCache_select_click.setVisibility(View.VISIBLE);
            textView_systemCache_size.setVisibility(View.VISIBLE);

        }
    }

    public void setUserAppsRecyclerView(List<String> list) {
        LinearLayoutManager lm3 = new LinearLayoutManager(this);
        lm3.setOrientation(LinearLayoutManager.VERTICAL);
        List<String> list1 = new ArrayList<>(list);
        List<String> list2 = new ArrayList<>(list);
        userAppsAdapter = new JunkAdapter3(JunkActivity.this, list1, list2);
        recyclerView_user_apps.setLayoutManager(lm3);
        recyclerView_user_apps.setAdapter(userAppsAdapter);
        userAppsAdapter.setSendData(this);
        userAppsAdapter.setSelectAll(this);
        userAppsAdapter.submitList(list);
        if (!list.isEmpty()) {
            imageView_userApps_select.setImageResource(R.drawable.ic_done_rectangle);
        }

        imageView_userApps_select_click.setOnClickListener(v -> {
            if (!userSelectedAll) {
                imageView_userApps_select.setImageResource(R.drawable.ic_done_rectangle);
                userAppsAdapter.selectAll();
                userSelectedAll = true;
            } else {
                userAppsAdapter.clearList();
                userSelectedAll = false;
                imageView_userApps_select.setImageResource(R.drawable.ic_undone_rectangle);
            }
        });


        if (userAppsList.isEmpty()) {
            imageView_user_upDown_click.setVisibility(View.GONE);
            imageView_userApps_upDown.setVisibility(View.GONE);
            textView_userApps_size.setVisibility(View.GONE);
            imageView_userApps_select.setVisibility(View.GONE);
            imageView_userApps_select_click.setVisibility(View.GONE);


        } else {
            imageView_user_upDown_click.setVisibility(View.VISIBLE);
            imageView_userApps_upDown.setVisibility(View.VISIBLE);
            textView_userApps_size.setVisibility(View.VISIBLE);
            imageView_userApps_select.setVisibility(View.VISIBLE);
            imageView_userApps_select_click.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void systemAppStart() {

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler();
        executor.execute(() -> {
            setSizeAnimation(utils.getCalculatedDataSizeMB(apkTotal),
                    utils.getCalculatedDataSizeMB(apkTotal + sysTotal));
            handler.post(() -> {
                setAppPathAnimation(systemAppsList, true, systemAppsList.isEmpty());

            });
        });
    }

    public void userAppStart() {
        Executor executor2 = Executors.newSingleThreadExecutor();
        Handler handler2 = new Handler();
        executor2.execute(() -> {
//            userAppsList.addAll(runningTask(false));
            setSizeAnimation(utils.getCalculatedDataSizeMB(apkTotal),
                    utils.getCalculatedDataSizeMB(apkTotal + sysTotal + userTotal));
            handler2.post(() -> {
                setAppPathAnimation(userAppsList, false, !userAppsList.isEmpty());
                scanning = false;
            });
        });
    }

    public void junkCalculating() {
        //pre execute
        progressBar3.setMax(100);
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler();
        executor.execute(() -> {

            setApksPathAnimation(apksList, apksList.isEmpty());
            setSizeAnimation(0.0f, utils.getCalculatedDataSizeMB(total));
            setProgressAnimation(0, 100);

            handler.post(() -> {
            });
        });

    }

    private void setProgressAnimation(int start, int setLevel) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofInt(start, setLevel);
                animator.setInterpolator(new LinearInterpolator());
                animator.setStartDelay(0);
                animator.setDuration(15000);
                animator.addUpdateListener(valueAnimator -> {

                    int value = (int) valueAnimator.getAnimatedValue();
                    progressBar3.setProgress(value);

                    if (value == 33) {
                        imagesDoneVisibility(true, 1);
                        lottieVisibility(false, 1);
                    }
                    if (value == 66) {
                        imagesDoneVisibility(true, 2);
                        lottieVisibility(false, 2);
                    }
                    if (value == 97) {
                        imagesDoneVisibility(true, 3);
                        lottieVisibility(false, 3);
                    }

                    if (value == 100) {
                        scanning = false;

                        cl_calculating.setVisibility(View.INVISIBLE);
                        scrollView_junk_calculated.setVisibility(View.VISIBLE);
                        textView_filePath.setVisibility(View.GONE);

                        textView_junk_size.setText(String.format("%.2f", utils.getCalculatedDataSizeMB(
                                (total))));
                        textView_apks_size.setText(Formatter.formatFileSize(JunkActivity.this, (long) apkTotal));
                        textView_systemCache_size.setText(Formatter.formatFileSize(JunkActivity.this, (long) sysTotal));
                        textView_userApps_size.setText(Formatter.formatFileSize(JunkActivity.this, (long) userTotal));
                        textView_progress.setText("Clean (" + String.format("%.2f", utils.getCalculatedDataSizeMB(
                                (apkTotal + sysTotal + userTotal))) + ") MB");
                    }
                });
                animator.start();
            }
        });

    }

    private void cleanProgressAnimation() {
        ValueAnimator animator = ValueAnimator.ofInt(100, 0);
        animator.setInterpolator(new LinearInterpolator());
        animator.setStartDelay(0);
        animator.setDuration(5000);
        animator.addUpdateListener(valueAnimator -> {

            int value = (int) valueAnimator.getAnimatedValue();
            progressBar3.setProgress(value);
            if (value == 0) {
                showInterstitialActivity(this);
            }
        });
        animator.start();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void setBackgroundColorAnimation(int from, int to) {
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(5000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.addUpdateListener(valueAnimator -> {

            Integer value = (Integer) valueAnimator.getAnimatedValue();
            cl_calculating_super.setBackgroundColor(value);
            getWindow().setStatusBarColor(value);
            getWindow().setNavigationBarColor(value);


        });
        animator.start();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void setSizeAnimation(float start, float setLevel) {
        new Handler(Looper.getMainLooper()).post(() -> {
            ValueAnimator animator = ValueAnimator.ofFloat(start, setLevel);
            animator.setDuration(5000);
            animator.addUpdateListener(valueAnimator -> {

                float value = (float) valueAnimator.getAnimatedValue();

                textView_junk_size.setText(/*value + ".0"*/String.format("%.1f", value)
                );
            });
            animator.start();
        });

    }

    @SuppressLint("DefaultLocale")
    private void setApksPathAnimation(List<String> list, boolean empty) {
        new Handler(Looper.getMainLooper()).post(() -> {
            ValueAnimator animator = ValueAnimator.ofInt(0, list.size());
            animator.setDuration(5000);
            animator.addUpdateListener(valueAnimator -> {

                int value = (int) valueAnimator.getAnimatedValue();
                if (value == 0) {
                    textView_filePath.setText("Scanning " + utils.appInfo(list.get(value), MyAnnotations.APP_NAME));
                } else {
                    textView_filePath.setText("Scanning " + utils.appInfo(list.get(value - 1), MyAnnotations.APP_NAME));
                }
                if (value == list.size() - 1) {
                    apkInterface.done(true);
                }
            });
            if (empty) {
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                        apkInterface.done(true), 3000);
            } else {
                animator.start();
            }
        });

    }


    private void setAppPathAnimation(List<String> list, boolean sys, boolean empty) {
        new Handler(Looper.getMainLooper()).post(() -> {
            ValueAnimator animator = ValueAnimator.ofInt(0, list.size());
            animator.setStartDelay(0);
            animator.setDuration(5000);
            animator.addUpdateListener(valueAnimator -> {

                int value = (int) valueAnimator.getAnimatedValue();
                if (value != list.size()) {


                    if (sys) {
                        sysInterface.sysDone(true);
                    } else {
                        userAppInterface.userDone(true);
                    }
                    textView_filePath.setText("Scanning " + list.get(value));
                }
            });
            if (!empty) {
                animator.start();

            } else {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (sys) {
                        sysInterface.sysDone(true);
                    } else {
                        userAppInterface.userDone(true);
                    }
                }, 3000);
            }
        });

    }


    public void setApkInterface(ApkInterface apkInterface) {
        this.apkInterface = apkInterface;
    }

    public void setSysInterface(SysInterface sysInterface) {
        this.sysInterface = sysInterface;
    }

    public void setUserAppInterface(UserAppInterface userAppInterface) {
        this.userAppInterface = userAppInterface;
    }

    @Override
    public void onBackPressed() {
        if (!scanning) {
            super.onBackPressed();
        }
    }

    @Override
    public void done(boolean flag) {
        if (flag) {
            systemAppStart();
        }
    }

    @Override
    public void sysDone(boolean flag) {
        if (flag) {
            userAppStart();
        }
    }

    @Override
    public void userDone(boolean flag) {
        if (flag) {
        }
    }


    @Override
    public void data(String data) {
    }

    @Override
    public void selectAll(boolean isSelectAll, String type) {
        if (type.equals("apk")) {
            if (isSelectAll) {
                imageView_apks_select.setImageResource(R.drawable.ic_done_rectangle);
                apksSelectedAll = true;
            } else {
                apksSelectedAll = false;
                imageView_apks_select.setImageResource(R.drawable.ic_undone_rectangle);
            }
        } else if (type.equals("sys")) {
            if (isSelectAll) {
                imageView_systemCache_select.setImageResource(R.drawable.ic_done_rectangle);
                systemSelectedAll = true;
            } else {
                systemSelectedAll = false;
                imageView_systemCache_select.setImageResource(R.drawable.ic_undone_rectangle);
            }
        } else {
            if (isSelectAll) {
                imageView_userApps_select.setImageResource(R.drawable.ic_done_rectangle);
                userSelectedAll = true;
            } else {
                userSelectedAll = false;
                imageView_userApps_select.setImageResource(R.drawable.ic_undone_rectangle);
            }
        }
    }

    @Override
    public void addDismissed(boolean closed) {
        scanningFinish();
    }

    @Override
    public void addFailed(boolean closed) {
        scanningFinish();
    }

    public void scanningFinish() {
        scanning = false;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, utils.randomValue(4, 10));
        preferences.addLong(MyAnnotations.JUNK_LAST_CLEANED_TIME, calendar.getTimeInMillis());
        layout_junk_finished.setVisibility(View.VISIBLE);
        cl_calculating_super.setVisibility(View.GONE);
        getWindow().setNavigationBarColor(ContextCompat.getColor(JunkActivity.this, R.color.white));
    }
}