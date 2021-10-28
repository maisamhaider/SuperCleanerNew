package com.example.junckcleaner.views.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.junckcleaner.R;
import com.example.junckcleaner.adapters.GameBoosterAdapter;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.SendData;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.permissions.MyPermissions;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameBoosterActivity extends AppCompatActivity implements SendData, TrueFalse {
    ConstraintLayout cl_game_boosting;
    LottieAnimationView lottie;
    MyPermissions permissions;
    SwitchCompat switch_dnd;
    ConstraintLayout cl_createShortCut;
    private Set<String> strings = new HashSet<>();
    AppPreferences preferences;
    boolean checked = false;
    RecyclerView recyclerView;
    private Utils utils;
    CircularProgressBar prog_ram;
    TextView textView_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_booster);

        utils = new Utils(this);

        prog_ram = findViewById(R.id.prog_ram);
        textView_progress = findViewById(R.id.textView_progress);
        recyclerView = findViewById(R.id.recyclerView);
        cl_game_boosting = findViewById(R.id.cl_game_boosting);
        lottie = findViewById(R.id.lottie);
        switch_dnd = findViewById(R.id.switch_dnd);
        cl_createShortCut = findViewById(R.id.cl_createShortCut);

        permissions = new MyPermissions(this);
        preferences = new AppPreferences(this);
        findViewById(R.id.imageView_back).setOnClickListener(v -> finish());


        cl_createShortCut.setOnClickListener(v -> {
            addShortcut(this);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cl_game_boosting.setVisibility(View.GONE);
        lottie.cancelAnimation();
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        ActivityManager activityManager = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        float prog = utils.getPercentage((float) memoryInfo.totalMem,
                (float) (memoryInfo.totalMem - memoryInfo.availMem));

        if (prog > 16) {
            prog_ram.setProgress(prog - 16f);

        } else {
            prog_ram.setProgress(prog);
        }
        textView_progress.setText(String.format("%.0f", prog) + "%");
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5,
                GridLayoutManager.VERTICAL, false);
        GameBoosterAdapter adapter = new GameBoosterAdapter(this, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        List<String> list = new ArrayList<>();

        if (preferences.getStringSet(MyAnnotations.GAME_BOOSTER_APPS) != null) {
            list.addAll(preferences.getStringSet(MyAnnotations.GAME_BOOSTER_APPS));
        }
        list.add("add");
        adapter.submitList(list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch_dnd.setChecked(preferences.getBoolean(MyAnnotations.DND_PERMISSION, false));
//            preferences.addBoolean(MyAnnotations.DND_PERMISSION, switch_dnd.isChecked());

//            String appUsage = !permissions.checkAppUsagePermission() ? "\n->App Usage" : "";
            String dnd = permissions.checkAppUsagePermission() ? "\n->Do not Disturb" : "";
            switch_dnd.setOnClickListener(v -> {
                if (!checked) {
                    if (!permissions.checkDNDPermission() || permissions.checkAppUsagePermission()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Permissions")
                                .setMessage("Grant permission for an ultimate gaming experience " + dnd
                                        /*appUsage*/)
                                .setPositiveButton("Allow", (dialog, which) -> {

                                    openSomeActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));

                                    openSomeActivityForResult(new
                                            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                                    dialog.dismiss();

                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.setOnDismissListener(dialog1 -> {
                            switch_dnd.setChecked(false);
                        });
                    } else {
                        preferences.addBoolean(MyAnnotations.DND_PERMISSION, true);
                        checked = true;
                    }
                } else {
                    preferences.addBoolean(MyAnnotations.DND_PERMISSION, false);
                    checked = false;

                }
            });
        } else {
            switch_dnd.setVisibility(View.GONE);
        }
    }

    @Override
    public void data(String data) {
        cl_game_boosting.setVisibility(View.VISIBLE);
        lottie.playAnimation();
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(data);
            if (intent != null) {
                startActivity(intent);
            }
        }, 6500);

    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    preferences.addBoolean(MyAnnotations.DND_PERMISSION, permissions.checkDNDPermission());
                    switch_dnd.setChecked(result.getResultCode() == Activity.RESULT_OK);

                }
            });

    public void openSomeActivityForResult(Intent intent) {
        someActivityResultLauncher.launch(intent);
    }

    private void setCreateShortCut() {
        Intent intent = new Intent(getApplicationContext(), GameBoosterActivity.class);
        Intent intent2 = new Intent();
        intent2.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        intent2.putExtra(Intent.EXTRA_SHORTCUT_NAME, getApplicationContext().getString(R.string.game_booster));
        intent2.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_game_booster_logo));
        intent2.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(intent2);


    }

    public void addShortcut(Context mContext) {
        if (Build.VERSION.SDK_INT < 26) {
            setCreateShortCut();
            return;
        }
        ShortcutManager shortcutManager = mContext.getSystemService(ShortcutManager.class);
        if (shortcutManager.isRequestPinShortcutSupported()) {
            Intent intent3 = new Intent(mContext, GameBoosterActivity.class);
            intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent3.setAction(Intent.ACTION_MAIN);
            ShortcutInfo build = new ShortcutInfo.Builder(mContext, GameBoosterActivity.class.getName())
                    .setIcon(Icon.createWithResource(mContext, R.drawable.ic_game_booster_logo))
                    .setIntent(intent3).setShortLabel(mContext.getString(R.string.app_name)).build();
            shortcutManager.requestPinShortcut(build, PendingIntent.getBroadcast(mContext, 0,
                    shortcutManager.createShortcutResultIntent(build), PendingIntent.FLAG_IMMUTABLE).getIntentSender());
        }
    }

    @Override
    public void isTrue(boolean isTrue) {
        if (isTrue) {
            startActivity(new Intent(GameBoosterActivity.this,
                    ActivitySelectGameBoosterApps.class));
        }
    }
}