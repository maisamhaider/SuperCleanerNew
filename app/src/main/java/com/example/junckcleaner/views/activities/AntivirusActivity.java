package com.example.junckcleaner.views.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.adapters.AdapterAntivirus;
import com.example.junckcleaner.adapters.AppsAdapter;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.duplicatenew.models.FileDetails;
import com.example.junckcleaner.duplicatenew.utils.DuplicatePreferences;
import com.example.junckcleaner.duplicatenew.utils.Functions;
import com.example.junckcleaner.interfaces.AdClosed;
import com.example.junckcleaner.interfaces.DeleteVirusInterface;
import com.example.junckcleaner.interfaces.SelectAll;
import com.example.junckcleaner.interfaces.SendData;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.services.WorkerAntivirus;
import com.example.junckcleaner.utils.Utils;
import com.example.junckcleaner.viewmodel.ViewModelApps;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AntivirusActivity extends BaseActivity implements SendData,
        TrueFalse, AdClosed, DeleteVirusInterface, SelectAll {
    View layout_antivirus_scanning, layout_antivirus_no_thread,
            layout_antivirus_result,
            layout_antivirus_finished;

    ConstraintLayout cl_inDanger, cl_virus_found, cl_apps, cl_virus;
    ConstraintLayout cl_virus_not_found, cl_danger_apps_not_found;
    TextView textView_antivirus_scan, textView_no_thread_found, textView_check, textView_kill_virus;
    TextView textView_anti_main, textView_anti_result_header,
            textView_anti_result_second,
            textView_warning_apps, textView_virus_found, last_message, textView_save_now,
            textView_boost_now, textViewSelectedVirus;


    ImageView imageView_anti_back_finished, imageView_select, imageView_select_click;

    LottieAnimationView lottie_virus,
            lottie_security,
            lottie_privacy;
    RecyclerView recyclerView, recyclerView_virus;
    AppsAdapter __appsAdapter;
    AdapterAntivirus __adapterAntivirus;
    AppPreferences __preferences;

    Utils __utils;
    boolean _isScanning = true,
            _isSkippedAll = false,
            _isChecked = false,
            _virusFound = false;
    public static boolean __dangerousAppFound = false;
    ViewModelApps __viewModelApps;
    List<String> __apps;
    String _app = "";
    String _lastMessage = "";
    List<String> _virus = new ArrayList<>();
    boolean _isOneSkipped = false;
    boolean _isVirusSkipped = false;
    boolean _isAppsSkipped = false;
    boolean _isAllChecked = false;
    boolean _isVirusChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        Intent intent = getIntent();
        if (intent != null && intent.getIntExtra(MyAnnotations.ID, 0) != 0) {

            NotificationManager n = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            n.cancel(intent.getIntExtra(MyAnnotations.ID, 0));
        }
        __utils = new Utils(this);
        __preferences = new AppPreferences(this);
        __viewModelApps = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ViewModelApps.class);

        __apps = new ArrayList<>();

        textViewSelectedVirus = findViewById(R.id.textViewSelectedVirus);
        imageView_select_click = findViewById(R.id.imageView_select_click);
        imageView_select = findViewById(R.id.imageView_select);


        layout_antivirus_scanning = findViewById(R.id.layout_antivirus_scanning);
        layout_antivirus_no_thread = findViewById(R.id.layout_antivirus_no_thread);
        layout_antivirus_result = findViewById(R.id.layout_antivirus_result);
        layout_antivirus_finished = findViewById(R.id.layout_antivirus_finished);

        cl_apps = findViewById(R.id.cl_apps);
        cl_virus = findViewById(R.id.cl_virus);
        cl_inDanger = findViewById(R.id.cl_inDanger);
        cl_virus_found = findViewById(R.id.cl_virus_found);


        cl_virus_not_found = findViewById(R.id.cl_virus_not_found);
        cl_danger_apps_not_found = findViewById(R.id.cl_danger_apps_not_found);

        textView_antivirus_scan = findViewById(R.id.textView_antivirus_scan);

        textView_anti_result_header = findViewById(R.id.textView_anti_result_header);
        textView_anti_result_second = findViewById(R.id.textView_anti_result_second);
        textView_virus_found = findViewById(R.id.textView_virus_found);
        textView_warning_apps = findViewById(R.id.textView_warning_apps);
        textView_anti_main = findViewById(R.id.textView_anti_main);
        last_message = findViewById(R.id.appCompatTextView7);

        textView_no_thread_found = findViewById(R.id.textView_no_thread_found);
        textView_check = findViewById(R.id.textView_check);
        textView_kill_virus = findViewById(R.id.textView_kill_virus);
        imageView_anti_back_finished = findViewById(R.id.imageView_back_finished);
        textView_save_now = findViewById(R.id.textView_save_now);
        textView_boost_now = findViewById(R.id.textView_boost_now);

        lottie_virus = findViewById(R.id.lottie_virus);
        lottie_security = findViewById(R.id.lottie_security);
        lottie_privacy = findViewById(R.id.lottie_privacy);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView_virus = findViewById(R.id.recyclerView_virus);


        imageView_anti_back_finished.setOnClickListener(v -> finish());

        imageView_select_click.setOnClickListener(view -> {
            if (!_isAllChecked) {
                _isAllChecked = true;
                __adapterAntivirus.checkAll();
                imageView_select.setImageResource(R.drawable.ic_done_rectangle);
            } else {
                _isAllChecked = false;
                __adapterAntivirus.unCheckAll();
                imageView_select.setImageResource(R.drawable.ic_undone_rectangle);

            }
        });

        textView_check.setOnClickListener(v -> {
            _isChecked = true;
            cl_virus_not_found.setVisibility(View.GONE);
            cl_virus_found.setVisibility(View.GONE);
            cl_apps.setVisibility(View.VISIBLE);
            cl_inDanger.setVisibility(View.GONE);
            textView_anti_main.setText("Skip All");
            textView_anti_result_header.setText("Apps In Dangerous");
            textView_anti_result_second.setText("Some applications use dangerous permissions");
            __appsAdapter = new AppsAdapter(this, MyAnnotations.ANTIVIRUS);
            __appsAdapter.setSendData(this);
            __appsAdapter.setTrueFalse(this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(__appsAdapter);
            if (__preferences.getStringSet(MyAnnotations.DANGEROUS_APP) != null
                    && !__preferences.getStringSet(MyAnnotations.DANGEROUS_APP).isEmpty()) {
                List<String> list = new ArrayList<>(__preferences.getStringSet(MyAnnotations.DANGEROUS_APP));
                __appsAdapter.submitList(list);
            }


        });
        textView_kill_virus.setOnClickListener(v -> {
            _isChecked = true;
            cl_danger_apps_not_found.setVisibility(View.GONE);
            cl_virus.setVisibility(View.VISIBLE);
            cl_virus_found.setVisibility(View.GONE);
            cl_inDanger.setVisibility(View.GONE);
            textView_anti_main.setText("Skip All");
            textView_anti_result_header.setText("Dangerous Files");
            textView_anti_result_second.setText("These Files may have virus");
            __adapterAntivirus = new AdapterAntivirus(this, this);
            List<String> newVirus = new ArrayList<>(_virus);
            __adapterAntivirus.setAllVirus(newVirus);
            __adapterAntivirus.setSelectAll(this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView_virus.setLayoutManager(linearLayoutManager);
            recyclerView_virus.setAdapter(__adapterAntivirus);
            __adapterAntivirus.submitList(_virus);


        });

        findViewById(R.id.textView_skip).setOnClickListener(v -> {
            _isAppsSkipped = true;
            if (_isOneSkipped) {
                showInterstitialActivity(this);
                _lastMessage = "Skipped";
            } else {
                _isOneSkipped = true;
                cl_inDanger.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.textView_skip_virus).setOnClickListener(v -> {
            _isVirusSkipped = true;
            if (_isOneSkipped) {
                showInterstitialActivity(this);
                _lastMessage = "Skipped";
            } else {
                _isOneSkipped = true;
                cl_virus_found.setVisibility(View.GONE);
            }
        });
        textView_anti_main.setOnClickListener(v -> {
            if (_isVirusChecked) {
                __adapterAntivirus.getDeleteVirus();

                View view = LayoutInflater.from(this).inflate(R.layout.layout_file_delete_dialog, null, false);
                TextView textView_cancel = view.findViewById(R.id.textView_cancel);
                TextView textView_delete = view.findViewById(R.id.textView_delete);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view).setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();

                textView_delete.setOnClickListener(view1 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        for (String virus : __adapterAntivirus.getDeleteVirus()) {
                            deleteVirus(virus);
                            _virus.remove(virus);

                        }
                    } else {
                        for (String virus : __adapterAntivirus.getDeleteVirus()) {

                            File file = new File(virus);
                            if (file.exists()) {
                                file.delete();
                                _virus.remove(virus);
                            }
                        }


                    }
                    textViewSelectedVirus.setText("No virus selected");
                    imageView_select.setImageResource(R.drawable.ic_undone_rectangle);
                    __adapterAntivirus.submitList(_virus);
                    __adapterAntivirus.setAllVirus(new ArrayList<>(_virus));
                    __adapterAntivirus.setDeleteVirus(new ArrayList<>());
                    __adapterAntivirus.notifyDataSetChanged();
                    dialog.dismiss();

                    if (_virus.isEmpty()) {
                        onBackPressed();
                        _isVirusChecked = false;
                        textView_anti_main.setText("Skip All");
                    }
                });
                textView_cancel.setOnClickListener(view12 -> dialog.dismiss());

            } else {
                _isSkippedAll = true;
                if (_isChecked) {
                    _lastMessage = "Skipped All";
                    showInterstitialActivity(this);
                } else {
                    layout_antivirus_no_thread.setVisibility(View.VISIBLE);
                    layout_antivirus_result.setVisibility(View.GONE);
                    textView_no_thread_found.setText("Skipped All");
                    getWindow().setStatusBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.color_main));
                    getWindow().setNavigationBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.color_third));
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        _lastMessage = "Skipped All";
                        showInterstitialActivity(this);

                    }, 3000);
                }
            }

        });


        findViewById(R.id.imageView_back_no_result).setOnClickListener(v -> onBackPressed());

        scanning_();

        textView_save_now.setOnClickListener(view -> {
            startActivity(new Intent(this, BatterySaverActivity.class));
            finish();
        });
        textView_boost_now.setOnClickListener(view -> {
            startActivity(new Intent(this, BoostActivity.class));
            finish();
        });

        refreshAd(findViewById(R.id.fl_adplaceholder));
    }

    @SuppressLint("SetTextI18n")
    public void scanning_() {
        //Collect Threads and apps from background.
        //check each app permissions if danger permission are given add that app to danger list

        //animating
        scanningTextAnimation();
        scanningBackgroundAnimation();
        appObserver();
        new Handler(Looper.getMainLooper()).postDelayed(this::startWorker, 4000);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(this::collectVirus);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (!_virusFound) {
                _isOneSkipped = true;
                cl_virus_found.setVisibility(View.GONE);
                cl_virus_not_found.setVisibility(View.VISIBLE);
            } else {
                textView_virus_found.setText(_virus.size() + " Virus Found");

            }
            if (!__dangerousAppFound) {
                _isOneSkipped = true;
                cl_inDanger.setVisibility(View.GONE);
                cl_danger_apps_not_found.setVisibility(View.VISIBLE);
            }

            int s = 0;
            if (__preferences.getStringSet(MyAnnotations.DANGEROUS_APP) != null) {
                s = __preferences.getStringSet(MyAnnotations.DANGEROUS_APP).size();
            }

            textView_anti_result_second.setText(_virus.size() + s + " issues found");

            if (__dangerousAppFound || _virusFound) {

                layout_antivirus_scanning.setVisibility(View.GONE);
                layout_antivirus_result.setVisibility(View.VISIBLE);
                getWindow().setStatusBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.color_cardinal_1));
                getWindow().setNavigationBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.color_cardinal_1));

                if (__preferences.getStringSet(MyAnnotations.DANGEROUS_APP) != null
                        && !__preferences.getStringSet(MyAnnotations.DANGEROUS_APP).isEmpty()) {
                    List<String> list = new ArrayList<>(__preferences.getStringSet(MyAnnotations.DANGEROUS_APP));
                    textView_warning_apps.setText(list.size() + " dangerous apps");
                }
            } else {
                getWindow().setStatusBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.color_main));
                getWindow().setNavigationBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.color_third));
                layout_antivirus_scanning.setVisibility(View.GONE);
                layout_antivirus_no_thread.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    layout_antivirus_finished.setVisibility(View.VISIBLE);
                    getWindow().setNavigationBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.white));

                }, 3000);
            }


        }, 30000);

    }

    public void scanningTextAnimation() {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(30000);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) valueAnimator.getAnimatedValue();
            long time = animation.getCurrentPlayTime();

            textView_antivirus_scan.setText(value + "%");
            if (value >= 60) {
                lottie_virus.setVisibility(View.INVISIBLE); //stop virus blinking animation
                lottie_security.setVisibility(View.VISIBLE); //show security flaws animation
            }
            if (value >= 80) {
                lottie_virus.setVisibility(View.INVISIBLE);//stop virus blinking animation
                lottie_security.setVisibility(View.INVISIBLE); //stop security flaws animation
                lottie_privacy.setVisibility(View.VISIBLE);//show show privacy animation


            }
            if (value == 100) {
                lottie_virus.setVisibility(View.INVISIBLE);//stop virus blinking animation
                lottie_security.setVisibility(View.INVISIBLE);//stop virus blinking animation
                lottie_privacy.setVisibility(View.INVISIBLE); //stop privacy animation

                _isScanning = false;

            }
        });

        valueAnimator.start();
    }

    //
    public void scanningBackgroundAnimation() {
        ValueAnimator blueToRed = scanningBackgroundBlueToRedAnimation();
        ValueAnimator redToBlue = scanningBackgroundRedToBlueAnimation();
        blueToRed.start();

        // blue-red-blue-red-blue
        new Handler(Looper.getMainLooper()).postDelayed(redToBlue::start, 6000);
        new Handler(Looper.getMainLooper()).postDelayed(blueToRed::start, 12000);
        new Handler(Looper.getMainLooper()).postDelayed(redToBlue::start, 18000);

    }

    public ValueAnimator scanningBackgroundBlueToRedAnimation() {
        int from = ContextCompat.getColor(this, R.color.color_main);
        int to = ContextCompat.getColor(this, R.color.color_cardinal_1);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.setDuration(6000);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            layout_antivirus_scanning.setBackgroundColor(value);
            getWindow().setStatusBarColor(value);
            getWindow().setNavigationBarColor(value);
        });

        return valueAnimator;

    }

    public ValueAnimator scanningBackgroundRedToBlueAnimation() {
        int from = ContextCompat.getColor(this, R.color.color_cardinal_1);
        int to = ContextCompat.getColor(this, R.color.color_main);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.setDuration(6000);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            layout_antivirus_scanning.setBackgroundColor(value);
            getWindow().setStatusBarColor(value);
            getWindow().setNavigationBarColor(value);
        });
        return valueAnimator;

    }

    @Override
    public void onBackPressed() {
        if (!_isScanning) {
            if (!_isSkippedAll) {
                if (_isChecked) {
                    _isChecked = false;
                    cl_apps.setVisibility(View.GONE);
                    cl_virus.setVisibility(View.GONE);
                    cl_inDanger.setVisibility(View.VISIBLE);
                    textView_anti_main.setText("Skip All");
                    textView_anti_result_header.setText("InDanger");

                    if (_isVirusSkipped) {
                        cl_virus_found.setVisibility(View.GONE);
                    } else {
                        cl_virus_found.setVisibility(View.VISIBLE);
                    }

                    if (_isAppsSkipped) {
                        cl_inDanger.setVisibility(View.GONE);
                    } else {
                        cl_inDanger.setVisibility(View.VISIBLE);
                    }


                    if (_virus.isEmpty()) {
                        _isOneSkipped = true;
                        cl_virus_found.setVisibility(View.GONE);
                        cl_virus_not_found.setVisibility(View.VISIBLE);
                    } else {
                        textView_virus_found.setText(_virus.size() + " Virus Found");
                    }

                    int s = 0;
                    if (__preferences.getStringSet(MyAnnotations.DANGEROUS_APP) != null) {
                        s = __preferences.getStringSet(MyAnnotations.DANGEROUS_APP).size();
                        textView_warning_apps.setText(s + " dangerous apps");
                    } else {
                        _isOneSkipped = true;
                        cl_inDanger.setVisibility(View.GONE);
                        cl_danger_apps_not_found.setVisibility(View.VISIBLE);
                    }

                    textView_anti_result_second.setText(_virus.size() + s + " issues found");

                } else {
                    finish();

                }
            } else {
                finish();
            }

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void data(String data) {
        if (!data.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View layout = LayoutInflater.from(this).inflate(R.layout.layout_antivirus_un_install_dialog, null);
            builder.setView(layout).setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            LinearLayout linearLayout = layout.findViewById(R.id.ll_thread_permission_container);

            ImageView imageViewAppIcon = layout.findViewById(R.id.imageView_app_icon);
            TextView textViewAppName = layout.findViewById(R.id.textView_app_name);
            TextView textViewAppVersion = layout.findViewById(R.id.textView_app_version);
            TextView textViewSize = layout.findViewById(R.id.textView_size);
            TextView textViewDate = layout.findViewById(R.id.textView_date);

            Glide.with(this).load((Drawable) __utils.appInfo(data, MyAnnotations.APP_ICON))
                    .into(imageViewAppIcon);
            textViewAppName.setText(__utils.appInfo(data, MyAnnotations.APP_NAME));
            textViewAppVersion.setText("Version: " + __utils.appInfo(data, MyAnnotations.APP_VERSION));

            try {
                textViewSize.setText("Size: " + Formatter.formatFileSize(this, __utils.appSize(data)));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            // app install date
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(__utils.getAppTime(data, MyAnnotations.INSTALLATION));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            String date = simpleDateFormat.format(calendar.getTime());
            textViewDate.setText("Date: " + date);


            layout.findViewById(R.id.textView_un_ins).setOnClickListener(v -> {
                unInstallApp(data);
                dialog.dismiss();
            });
            layout.findViewById(R.id.textView_cancel).setOnClickListener(v -> {
                dialog.dismiss();
            });


            String[] permissions = new String[]{};
            try {
                permissions = getPackageManager().getPackageInfo(data, PackageManager.GET_PERMISSIONS)
                        .requestedPermissions;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (permissions.length != 0) {
                boolean storage = false;
                boolean location = false;
                for (String permission : permissions) {
                    View itemLayout = null;
                    if (dangerousPermissions().contains(permission)) {
                        HashMap<String, Pair<String, String>> map = getPermissionsWithText(permission);
                        if (map != null) {
                            String permissionHeading = "";
                            String permissionText = "";
                            if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)
                                    || permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                                    permission.equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                                if (!location) {
                                    itemLayout = LayoutInflater.from(this).inflate(R.layout.layout_thread_permission_item, null);
                                    permissionHeading = map.get(Manifest.permission.ACCESS_FINE_LOCATION).first;
                                    permissionText = map.get(Manifest.permission.ACCESS_FINE_LOCATION).second;
                                    location = true;
                                }


                            } else if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    || permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                    permission.equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                                if (!storage) {
                                    itemLayout = LayoutInflater.from(this).inflate(R.layout.layout_thread_permission_item, null);
                                    permissionHeading = map.get(Manifest.permission.READ_EXTERNAL_STORAGE).first;
                                    permissionText = map.get(Manifest.permission.READ_EXTERNAL_STORAGE).second;
                                    storage = true;
                                }

                            } else {
                                itemLayout = LayoutInflater.from(this).inflate(R.layout.layout_thread_permission_item, null);
                                permissionHeading = map.get(permission).first;
                                permissionText = map.get(permission).second;
                            }
                            if (itemLayout != null) {

                                TextView textView1 = itemLayout.findViewById(R.id.textView1);
                                TextView textView2 = itemLayout.findViewById(R.id.textView5);

                                textView1.setText(permissionHeading);
                                textView2.setText(permissionText);
                                linearLayout.addView(itemLayout);
                            }

                        }

                    }

                }
            }


            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void appObserver() {
        __viewModelApps.getUserApps().observe(this, strings -> {
            if (strings != null && !strings.isEmpty()) {
                __apps.addAll(strings);
            }
        });
    }

    public void startWorker() {

        Set<String> list = new HashSet<>(__apps);

        __preferences.adStringSet(MyAnnotations.ANTIVIRUS_APPS, list);

        WorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(WorkerAntivirus.class)
                        .build();
        WorkManager
                .getInstance(this)
                .enqueue(uploadWorkRequest);
    }

    public void unInstallApp(String app) {
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + app));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        _app = app;
        openSomeActivityForResult(intent);
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

    public HashMap<String, Pair<String, String>> getPermissionsWithText(String permission) {
        HashMap<String, Pair<String, String>> map = new HashMap<>();
        Pair<String, String> pair;
        switch (permission) {

            case Manifest.permission.CAMERA:
                pair = new Pair<>("Access camera", "This Application can take (picture and video) and is " +
                        "risk for your privacy");
                map.put(Manifest.permission.CAMERA, pair);
                return map;
            case Manifest.permission.RECORD_AUDIO:
                pair = new Pair<>("Can record audio", "This Application can record audio and is " +
                        "risk for your privacy");
                map.put(Manifest.permission.RECORD_AUDIO, pair);
                return map;

            case Manifest.permission.GET_ACCOUNTS:
                pair = new Pair<>("Can get accounts", "This Application can get account info and is " +
                        "risk for your credential");
                map.put(Manifest.permission.GET_ACCOUNTS, pair);
                return map;
            case Manifest.permission.MANAGE_EXTERNAL_STORAGE:
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                pair = new Pair<>("Can manage files", "This Application can access files and is " +
                        "risk for your privacy");
                map.put(Manifest.permission.READ_EXTERNAL_STORAGE, pair);
                return map;
            case Manifest.permission.SEND_SMS:
                pair = new Pair<>("Can send sms", "This Application can send sms and is " +
                        "risk for your privacy");
                map.put(Manifest.permission.SEND_SMS, pair);
                return map;
            case Manifest.permission.READ_SMS:
                pair = new Pair<>("Can read sms", "This Application can read sms and is " +
                        "risk for your privacy");
                map.put(Manifest.permission.READ_SMS, pair);
                return map;
            case Manifest.permission.RECEIVE_SMS:
                pair = new Pair<>("Can receive sms", "This Application can receive sms and is " +
                        "risk for your privacy");
                map.put(Manifest.permission.RECEIVE_SMS, pair);
                return map;
            case Manifest.permission.PROCESS_OUTGOING_CALLS:
                pair = new Pair<>("Process calls", "This Application can process calls and is " +
                        "risk for your privacy");
                map.put(Manifest.permission.PROCESS_OUTGOING_CALLS, pair);
                return map;
            case Manifest.permission.READ_PHONE_NUMBERS:
                pair = new Pair<>("Read phone numbers", "This Application can read phone numbers and is " +
                        "risk for your credential");
                map.put(Manifest.permission.READ_PHONE_NUMBERS, pair);
                return map;
            case Manifest.permission.ACCESS_COARSE_LOCATION:
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_BACKGROUND_LOCATION:
                pair = new Pair<>("Location shared", "This Application can access its exact position and is " +
                        "risk for your privacy");
                map.put(Manifest.permission.ACCESS_FINE_LOCATION, pair);
                return map;
        }

        return map;
    }

    public void collectVirus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Cursor queryDocuments;
            Uri uri = MediaStore.Files.getContentUri("external");
            String[] projection = new String[]{"_data", "_display_name", "mime_type", "_size",
                    "datetaken", "date_added"};

            String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";

            String[] selectionArgs = new String[]{"application/x-msdos-program"};


            queryDocuments = this.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);

            if (queryDocuments != null && queryDocuments.moveToFirst()) {
                do {
                    @SuppressLint("Range") String string =
                            queryDocuments.getString(queryDocuments.getColumnIndex("_data"));
                    if (new File(string).exists()) {
                        long length = new File(string).length();
//                    String readableFileSize = Formatter.formatFileSize(this, length);
                        FileDetails fileDetails = new FileDetails();

//                    fileDetails.setFileSize(length);
                        fileDetails.setFileName(new File(string).getName());
                        fileDetails.setFilePath(string);
//                    fileDetails.setFileSizeStr(readableFileSize);
                        if (fileDetails.getFileName().toLowerCase().contains(".exe")) {
                            _virusFound = true;
                            _virus.add(fileDetails.getFilePath());
                        }
                    }

                } while (queryDocuments.moveToNext());
                queryDocuments.close();
            }
        } else {
            getViruses(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            __preferences.getStringSet(MyAnnotations.DANGEROUS_APP).remove(_app);
                            List<String> list = new ArrayList<>(__preferences.getStringSet(MyAnnotations.DANGEROUS_APP));
                            __appsAdapter.submitList(list);
                        } else {
                        }
                        _app = "";
                    });

    public void openSomeActivityForResult(Intent intent) {

        someActivityResultLauncher.launch(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        __dangerousAppFound = false;
    }

    @Override
    public void isTrue(boolean isTrue) {

    }

    @Override
    public void addDismissed(boolean closed) {
        finishAntivirus();
    }

    @Override
    public void addFailed(boolean closed) {
        finishAntivirus();
    }

    public void finishAntivirus() {
        switch (_lastMessage) {
            case "Skipped All":
                layout_antivirus_result.setVisibility(View.INVISIBLE);
                layout_antivirus_finished.setVisibility(View.VISIBLE);
                last_message.setText("Skipped All");

                getWindow().setStatusBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.color_main));
                getWindow().setNavigationBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.color_divider));
                break;
            case "Skipped":
                layout_antivirus_result.setVisibility(View.GONE);
                layout_antivirus_finished.setVisibility(View.VISIBLE);

                last_message.setText("Skipped");
                getWindow().setStatusBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.color_main));
                getWindow().setNavigationBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.color_divider));
                break;
            case "Apps Resolved":
                layout_antivirus_finished.setVisibility(View.VISIBLE);
                layout_antivirus_no_thread.setVisibility(View.GONE);
                getWindow().setNavigationBarColor(ContextCompat.getColor(AntivirusActivity.this, R.color.white));
                last_message.setText("Resolved");
                break;
        }
    }

    private void deleteVirus(List<String> fileToDeleted) {
        for (int i = 0; i < fileToDeleted.size(); i++) {
            String fileDetails = fileToDeleted.get(i);
            try {
                Log.e("deletePhotosByPosition", "---asdfasd-----" + fileDetails);
                deleteFile(this, fileDetails);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteVirus(String fileToDeleted) {
        try {
            deleteFile(this, fileToDeleted);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    public static void deleteFile(Context context, String path) {
        File file = new File(path);
        List<Boolean> checkSDCardFile = new ArrayList();
        if (Functions.getSDCardPath(context) != null) {
            try {
                DocumentFile documentFile =
                        DocumentFile.fromTreeUri(context,
                                Uri.parse(DuplicatePreferences.getStorageAccessFrameWorkURIPermission(context)));
                String[] parts = file.getPath().split("\\/");
                Log.i("TAG", "deleteFile:parts " + parts);
                for (int j = 0; j < parts.length; j++) {
                    Log.i("TAG", "deleteFile:j= " + j);
                    checkSDCardFile.add(parts[j].equals(documentFile.getName()));
                    if (parts[j].equals(documentFile.getName())) {
                        for (int i = 3; i < parts.length; i++) {
                            if (documentFile != null) {
                                Log.i("TAG", "deleteFile:i= " + i);
                                documentFile = documentFile.findFile(parts[i]);
                            }
                        }
                        if (documentFile != null) {
                            documentFile.delete();
                        }
                    }
                }
                if (!checkSDCardFile.contains(Boolean.TRUE)) {
                    normalFunctionForDeleteFile(context, file);
                    return;
                }
                return;
            } catch (Exception e) {
                return;
            }
        }
        normalFunctionForDeleteFile(context, file);
    }

    private static void normalFunctionForDeleteFile(Context context, File file) {
        File f = new File(file.getAbsolutePath());
        if (!f.exists()) {
            return;
        }
        deleteFileFromMediaStore(context.getContentResolver(), f);
    }

    private static void deleteFileFromMediaStore(ContentResolver contentResolver, File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        try {
            Uri uri = MediaStore.Files.getContentUri("external");
            if (contentResolver.delete(uri, "_data=?", new String[]{canonicalPath}) == 0) {
                if (!file.getAbsolutePath().equals(canonicalPath)) {
                    contentResolver.delete(uri, "_data=?", new String[]{file.getAbsolutePath()});
                }
            }
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (RuntimeException e3) {
            e3.printStackTrace();
        }
    }

    public List<String> getViruses(String path) {
        File fold = new File(path);
        File[] mlist = fold.listFiles();
        File[] mFilelist = fold.listFiles(new ExeFilter());
        if (mlist != null) {
            for (File f : mlist) {
                if (f.isDirectory()) {
                    List<String> fList = getViruses(f.getAbsolutePath());
                    _virus.addAll(fList);
                }
            }
        }
        if (mFilelist != null) {
            for (File f : mFilelist) {

                if (f.getPath().contains(".exe")) {
                    _virus.add(f.getPath());
                    _virusFound = true;
                }

            }
        }
        return _virus;
    }

    @Override
    public void listener(boolean onVirusIsSelected) {
        _isVirusChecked = onVirusIsSelected;
        if (onVirusIsSelected) {
            textView_anti_main.setText("Delete");
            String size = String.valueOf(__adapterAntivirus.getDeleteVirus().size());
            if (Integer.parseInt(size) == 1) {
                textViewSelectedVirus.setText(size + " virus is selected");
            } else {
                textViewSelectedVirus.setText(size + " viruses are selected");
            }
        } else {
            textView_anti_main.setText("Skip All");
            textViewSelectedVirus.setText("No virus is selected");

        }

    }

    @Override
    public void selectAll(boolean isSelectAll, String size) {
        _isAllChecked = isSelectAll;
        if (isSelectAll) {
            imageView_select.setImageResource(R.drawable.ic_done_rectangle);
            if (Integer.parseInt(size) == 1) {
                textViewSelectedVirus.setText(size + " virus is selected");
            } else {
                textViewSelectedVirus.setText(size + " viruses are selected");
            }
            textView_anti_main.setText("Delete");
            _isVirusChecked = true;
        } else {
            imageView_select.setImageResource(R.drawable.ic_undone_rectangle);
            if (Integer.parseInt(size) == 0) {
                textViewSelectedVirus.setText("No virus is selected");
                textView_anti_main.setText("Skip All");
                _isVirusChecked = true;
            }

        }

    }


    public static class ExeFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getPath();
            return (path.endsWith(".exe"));
        }
    }

}