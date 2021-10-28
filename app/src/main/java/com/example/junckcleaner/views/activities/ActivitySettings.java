package com.example.junckcleaner.views.activities;

import static com.example.junckcleaner.annotations.MyAnnotations.APP_INSTALL;
import static com.example.junckcleaner.annotations.MyAnnotations.APP_UN_INSTALL;
import static com.example.junckcleaner.annotations.MyAnnotations.BATTERY_SAVER;
import static com.example.junckcleaner.annotations.MyAnnotations.CPU_COOLER_REMINDER;
import static com.example.junckcleaner.annotations.MyAnnotations.JUNK_REMINDER_FREQUENCY;
import static com.example.junckcleaner.annotations.MyAnnotations.PHONE_BOOST_REMINDER;
import static com.example.junckcleaner.annotations.MyAnnotations.REAL_TIME_PROTECTION;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.junckcleaner.BuildConfig;
import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.services.ServiceReminder;
import com.example.junckcleaner.utils.Utils;

import java.util.concurrent.atomic.AtomicReference;

public class ActivitySettings extends AppCompatActivity {


    private AppPreferences preferences;

    TextView textView7;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = new AppPreferences(this);
        utils = new Utils(this);

        ConstraintLayout cl_junk_reminder_frequency,

                cl_create_shortcut,
                cl_application_protected,
                cl_dnd,
                cl_ignore_list;

        ImageView
                imageVIew_phone_booster_reminder,
                imageView_cpu_cooler_reminder,
                imageView_battery_saver,
                imageView_app_install,
                imageView_app_uninstall,
                imageView_rtp;


        cl_junk_reminder_frequency = findViewById(R.id.cl_junk_reminder_frequency);
         cl_create_shortcut = findViewById(R.id.cl_create_shortcut);
        cl_application_protected = findViewById(R.id.cl_application_protected);
        cl_dnd = findViewById(R.id.cl_dnd);
        cl_ignore_list = findViewById(R.id.cl_ignor_list);

        imageVIew_phone_booster_reminder = findViewById(R.id.imageVIew_phone_booster_reminder);
        imageView_cpu_cooler_reminder = findViewById(R.id.imageView_cpu_cooler_reminder);
        imageView_battery_saver = findViewById(R.id.imageView_battery_saver);
        imageView_app_install = findViewById(R.id.imageView_app_install);
        imageView_app_uninstall = findViewById(R.id.imageView_app_uninstall);
        imageView_rtp = findViewById(R.id.imageView_rtp);

        findViewById(R.id.imageView_back).setOnClickListener(v -> finish());

        textView7 = findViewById(R.id.textView7);


        // check preferences
        if (preferences.getBoolean(PHONE_BOOST_REMINDER, false)) {
            imageVIew_phone_booster_reminder.setImageResource(R.drawable.ic_toggle_on);
        } else {
            imageVIew_phone_booster_reminder.setImageResource(R.drawable.ic_toggle_off);
        }
        //
        if (preferences.getBoolean(CPU_COOLER_REMINDER, false)) {
            imageView_cpu_cooler_reminder.setImageResource(R.drawable.ic_toggle_on);
        } else {
            imageView_cpu_cooler_reminder.setImageResource(R.drawable.ic_toggle_off);
        }
        //
        if (preferences.getBoolean(BATTERY_SAVER, false)) {
            imageView_battery_saver.setImageResource(R.drawable.ic_toggle_on);
        } else {
            imageView_battery_saver.setImageResource(R.drawable.ic_toggle_off);
        }
        //
        if (preferences.getBoolean(APP_INSTALL, false)) {
            imageView_app_install.setImageResource(R.drawable.ic_toggle_on);
        } else {
            imageView_app_install.setImageResource(R.drawable.ic_toggle_off);
        }

        if (preferences.getBoolean(APP_UN_INSTALL, false)) {
            imageView_app_uninstall.setImageResource(R.drawable.ic_toggle_on);
        } else {
            imageView_app_uninstall.setImageResource(R.drawable.ic_toggle_off);
        }
        if (preferences.getBoolean(REAL_TIME_PROTECTION, false)) {
            imageView_rtp.setImageResource(R.drawable.ic_toggle_on);
        } else {
            imageView_rtp.setImageResource(R.drawable.ic_toggle_off);
        }


        cl_junk_reminder_frequency.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    reqOverLay();
                } else {
                    junkReminderFrequency();
                }
            } else {
                junkReminderFrequency();
            }

        });

        cl_create_shortcut.setOnClickListener(v -> {
            addShortcut(this);
        });
        cl_application_protected.setOnClickListener(v -> {
            startActivity(new Intent(ActivitySettings.this, ActivityApplicationProtected.class));

        });
        cl_dnd.setOnClickListener(v -> {
            startActivity(new Intent(ActivitySettings.this, ActivityDND.class));

        });
        cl_ignore_list.setOnClickListener(v -> {
            startActivity(new Intent(ActivitySettings.this, ActivityIgnoredList.class));

        });


        imageVIew_phone_booster_reminder.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    reqOverLay();
                } else if (hasUsageStatsPermission()) {
                    reqUsageStats();
                } else {
                    if (preferences.getBoolean(PHONE_BOOST_REMINDER, false)) {
                        preferences.addBoolean(PHONE_BOOST_REMINDER, false);
                        imageVIew_phone_booster_reminder.setImageResource(R.drawable.ic_toggle_off);
                        stopService();
                    } else {
                        preferences.addBoolean(PHONE_BOOST_REMINDER, true);
                        imageVIew_phone_booster_reminder.setImageResource(R.drawable.ic_toggle_on);
                        startService();
                    }
                }
            } else {
                if (preferences.getBoolean(PHONE_BOOST_REMINDER, false)) {
                    preferences.addBoolean(PHONE_BOOST_REMINDER, false);
                    imageVIew_phone_booster_reminder.setImageResource(R.drawable.ic_toggle_off);
                    stopService();
                } else {
                    preferences.addBoolean(PHONE_BOOST_REMINDER, true);
                    imageVIew_phone_booster_reminder.setImageResource(R.drawable.ic_toggle_on);
                    startService();
                }
            }


        });


        imageView_cpu_cooler_reminder.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    reqOverLay();
                } else if (hasUsageStatsPermission()) {
                    reqUsageStats();
                } else {

                    if (preferences.getBoolean(CPU_COOLER_REMINDER, false)) {
                        imageView_cpu_cooler_reminder.setImageResource(R.drawable.ic_toggle_off);
                        preferences.addBoolean(CPU_COOLER_REMINDER, false);
                        stopService();
                    } else {
                        imageView_cpu_cooler_reminder.setImageResource(R.drawable.ic_toggle_on);
                        preferences.addBoolean(CPU_COOLER_REMINDER, true);
                        startService();
                    }
                }
            } else {

                if (preferences.getBoolean(CPU_COOLER_REMINDER, false)) {
                    imageView_cpu_cooler_reminder.setImageResource(R.drawable.ic_toggle_off);
                    preferences.addBoolean(CPU_COOLER_REMINDER, false);
                    stopService();
                } else {
                    imageView_cpu_cooler_reminder.setImageResource(R.drawable.ic_toggle_on);
                    preferences.addBoolean(CPU_COOLER_REMINDER, true);
                    startService();
                }
            }

        });
        imageView_battery_saver.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    reqOverLay();
                } else if (hasUsageStatsPermission()) {
                    reqUsageStats();
                } else {
                    if (preferences.getBoolean(BATTERY_SAVER, false)) {
                        preferences.addBoolean(BATTERY_SAVER, false);
                        imageView_battery_saver.setImageResource(R.drawable.ic_toggle_off);
                        stopService();
                    } else {
                        preferences.addBoolean(BATTERY_SAVER, true);
                        imageView_battery_saver.setImageResource(R.drawable.ic_toggle_on);
                        startService();
                    }
                }
            } else {

                if (preferences.getBoolean(BATTERY_SAVER, false)) {
                    preferences.addBoolean(BATTERY_SAVER, false);
                    imageView_battery_saver.setImageResource(R.drawable.ic_toggle_off);
                    stopService();
                } else {
                    preferences.addBoolean(BATTERY_SAVER, true);
                    imageView_battery_saver.setImageResource(R.drawable.ic_toggle_on);
                    startService();
                }
            }
        });
        imageView_app_install.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    reqOverLay();
                } else if (hasUsageStatsPermission()) {
                    reqUsageStats();

                } else {
                    if (preferences.getBoolean(APP_INSTALL, false)) {
                        preferences.addBoolean(APP_INSTALL, false);
                        imageView_app_install.setImageResource(R.drawable.ic_toggle_off);
                        stopService();
                    } else {
                        preferences.addBoolean(APP_INSTALL, true);
                        imageView_app_install.setImageResource(R.drawable.ic_toggle_on);
                        startService();
                    }
                }
            } else {
                if (preferences.getBoolean(APP_INSTALL, false)) {
                    preferences.addBoolean(APP_INSTALL, false);
                    imageView_app_install.setImageResource(R.drawable.ic_toggle_off);
                    stopService();
                } else {
                    preferences.addBoolean(APP_INSTALL, true);
                    imageView_app_install.setImageResource(R.drawable.ic_toggle_on);
                    startService();
                }
            }
        });
        imageView_app_uninstall.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    reqOverLay();
                } else if (hasUsageStatsPermission()) {
                    reqUsageStats();

                } else {
                    if (preferences.getBoolean(APP_UN_INSTALL, false)) {
                        preferences.addBoolean(APP_UN_INSTALL, false);
                        imageView_app_uninstall.setImageResource(R.drawable.ic_toggle_off);
                        stopService();
                    } else {
                        preferences.addBoolean(APP_UN_INSTALL, true);
                        imageView_app_uninstall.setImageResource(R.drawable.ic_toggle_on);
                        startService();
                    }
                }
            } else {
                if (preferences.getBoolean(APP_UN_INSTALL, false)) {
                    preferences.addBoolean(APP_UN_INSTALL, false);
                    imageView_app_uninstall.setImageResource(R.drawable.ic_toggle_off);
                    stopService();
                } else {
                    preferences.addBoolean(APP_UN_INSTALL, true);
                    imageView_app_uninstall.setImageResource(R.drawable.ic_toggle_on);
                    startService();
                }
            }

        });
        imageView_rtp.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    reqOverLay();
                } else if (hasUsageStatsPermission()) {
                    reqUsageStats();

                } else {
                    if (preferences.getBoolean(REAL_TIME_PROTECTION, false)) {
                        preferences.addBoolean(REAL_TIME_PROTECTION, false);
                        imageView_rtp.setImageResource(R.drawable.ic_toggle_off);
                        stopService();
                    } else {
                        imageView_rtp.setImageResource(R.drawable.ic_toggle_on);
                        preferences.addBoolean(REAL_TIME_PROTECTION, true);
                        startService();
                    }
                }
            } else {
                if (preferences.getBoolean(REAL_TIME_PROTECTION, false)) {
                    preferences.addBoolean(REAL_TIME_PROTECTION, false);
                    imageView_rtp.setImageResource(R.drawable.ic_toggle_off);
                    stopService();
                } else {
                    imageView_rtp.setImageResource(R.drawable.ic_toggle_on);
                    preferences.addBoolean(REAL_TIME_PROTECTION, true);
                    startService();
                }
            }
        });
    }

    private void junkReminderFrequency() {
        AtomicReference<String> witchRadio = new AtomicReference<>();

        View view = LayoutInflater.from(this).inflate(R.layout.layout_junk_remide_frequency_dialog,
                null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView textView_button = view.findViewById(R.id.textView_button);
        LinearLayout llRadioEveryday = view.findViewById(R.id.ll_radio_everyday);
        LinearLayout llRadioEvery3day = view.findViewById(R.id.ll_radio_every3day);
        LinearLayout llRadioEvery7day = view.findViewById(R.id.ll_radio_every7day);
        LinearLayout llRadioNeverRemind = view.findViewById(R.id.ll_radio_never_remind);

        ImageView imageViewRadEveryday = view.findViewById(R.id.imageView_rad_everyday);
        ImageView imageViewRadEvery3day = view.findViewById(R.id.imageView_rad_every3day);
        ImageView imageViewRadEvery7day = view.findViewById(R.id.imageView_rad_every7day);
        ImageView imageViewRadNeverRemind = view.findViewById(R.id.imageView_rad_never_remind);

        if (preferences.getString(JUNK_REMINDER_FREQUENCY, MyAnnotations.NEVER_REMIND) != null &&
                !preferences.getString(JUNK_REMINDER_FREQUENCY, MyAnnotations.NEVER_REMIND).isEmpty()) {
            witchRadio.set(preferences.getString(JUNK_REMINDER_FREQUENCY, MyAnnotations.NEVER_REMIND));

            switch (witchRadio.get()) {
                case MyAnnotations.EVERY_DAY:
                    junkReminderRatio(imageViewRadEveryday,
                            imageViewRadEvery3day,
                            imageViewRadEvery7day,
                            imageViewRadNeverRemind
                    );
                    break;
                case MyAnnotations.EVERY_3_DAY:
                    junkReminderRatio(imageViewRadEvery3day,
                            imageViewRadEvery7day,
                            imageViewRadNeverRemind,
                            imageViewRadEveryday);
                    break;
                case MyAnnotations.EVERY_7_DAY:
                    junkReminderRatio(imageViewRadEvery7day,
                            imageViewRadNeverRemind,
                            imageViewRadEveryday,
                            imageViewRadEvery3day);

                    break;
                case MyAnnotations.NEVER_REMIND:
                    junkReminderRatio(imageViewRadNeverRemind,
                            imageViewRadEveryday,
                            imageViewRadEvery3day,
                            imageViewRadEvery7day);
                    break;
            }

        } else {
            witchRadio.set(MyAnnotations.EVERY_DAY);
            junkReminderRatio(imageViewRadEveryday,
                    imageViewRadEvery3day,
                    imageViewRadEvery7day,
                    imageViewRadNeverRemind
            );
        }

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        textView_button.setOnClickListener(v1 ->
                {
                    preferences.addString(JUNK_REMINDER_FREQUENCY, witchRadio.get());
                    switch (witchRadio.get()) {
                        case MyAnnotations.NEVER_REMIND:
                            textView7.setText("Never remind");
                            stopService();
                            break;
                        case MyAnnotations.EVERY_DAY:
                            textView7.setText("Everyday");
                            break;
                        case MyAnnotations.EVERY_3_DAY:
                            textView7.setText("Every 3 day");
                            break;
                        case MyAnnotations.EVERY_7_DAY:
                            textView7.setText("Every 7 day");
                            break;
                    }
                    startService();
                    dialog.dismiss();
                }
        );

        llRadioEveryday.setOnClickListener(v1 -> {
            junkReminderRatio(imageViewRadEveryday,
                    imageViewRadEvery3day,
                    imageViewRadEvery7day,
                    imageViewRadNeverRemind
            );
            witchRadio.set(MyAnnotations.EVERY_DAY);
        });
        llRadioEvery3day.setOnClickListener(v1 -> {
            junkReminderRatio(imageViewRadEvery3day,
                    imageViewRadEvery7day,
                    imageViewRadNeverRemind,
                    imageViewRadEveryday);
            witchRadio.set(MyAnnotations.EVERY_3_DAY);

        });
        llRadioEvery7day.setOnClickListener(v1 -> {
            junkReminderRatio(imageViewRadEvery7day,
                    imageViewRadNeverRemind,
                    imageViewRadEveryday,
                    imageViewRadEvery3day);
            witchRadio.set(MyAnnotations.EVERY_7_DAY);

        });
        llRadioNeverRemind.setOnClickListener(v1 -> {
            junkReminderRatio(imageViewRadNeverRemind,
                    imageViewRadEveryday,
                    imageViewRadEvery3day,
                    imageViewRadEvery7day);
            witchRadio.set(MyAnnotations.NEVER_REMIND);

        });
    }

    public void junkReminderRatio(ImageView select,
                                  ImageView uncheck1,
                                  ImageView uncheck2,
                                  ImageView uncheck3) {
        select.setImageResource(R.drawable.ic_radio_on);
        uncheck1.setImageResource(R.drawable.ic_radio_off);
        uncheck2.setImageResource(R.drawable.ic_radio_off);
        uncheck3.setImageResource(R.drawable.ic_radio_off);

    }

//    private void addShortcut(String activityName, Activity activity) {
//        if (Build.VERSION.SDK_INT < 26) {
//
//        } else {
//            if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
//
//                ShortcutInfoCompat shortcutInfo = new
//                        ShortcutInfoCompat.Builder(getApplicationContext(), "#1")
//                        .setIntent(new Intent(this,
//                                activity.getClass()).setAction(Intent.CATEGORY_LAUNCHER)) // !!! intent's action must be set on oreo
//                        .setShortLabel(activityName)
//                        .setIcon(IconCompat.createWithResource(this,
//                                R.drawable.ic_logo))
//                        .build();
//                ShortcutManagerCompat.requestPinShortcut(getApplicationContext(), shortcutInfo, null);
//            }
//        }
//
//
//    }

    private void setCreateShortCut() {
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        Intent intent2 = new Intent();
        intent2.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        intent2.putExtra(Intent.EXTRA_SHORTCUT_NAME, getApplicationContext().getString(R.string.app_name));
        intent2.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_logo));
        intent2.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(intent2);

    }

    public void addShortcut(Context mContext) {
        if (Build.VERSION.SDK_INT < 26) {
            setCreateShortCut();
            return;
        }
        ShortcutManager shortcutManager = mContext.getApplicationContext().getSystemService(ShortcutManager.class);
        if (shortcutManager.isRequestPinShortcutSupported()) {
            Intent intent3 = new Intent(getApplicationContext(), ActivityMain.class);
            intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent3.setAction(Intent.ACTION_MAIN);
            ShortcutInfo build = new ShortcutInfo.Builder(getApplicationContext(), ActivityMain.class.getName())
                    .setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_logo))
                    .setIntent(intent3).setShortLabel(getApplicationContext().getString(R.string.app_name)).build();
            shortcutManager.requestPinShortcut(build, PendingIntent.getBroadcast(getApplicationContext(), 0,
                    shortcutManager.createShortcutResultIntent(build), PendingIntent.FLAG_IMMUTABLE).getIntentSender());
        }
    }

    public void stopService() {
        if (preferences.getString(JUNK_REMINDER_FREQUENCY, MyAnnotations.NEVER_REMIND).equals(MyAnnotations.NEVER_REMIND) &&
                !preferences.getBoolean(PHONE_BOOST_REMINDER, false) &&
                !preferences.getBoolean(CPU_COOLER_REMINDER, false) &&
                !preferences.getBoolean(BATTERY_SAVER, false) &&
                !preferences.getBoolean(APP_INSTALL, false) &&
                !preferences.getBoolean(APP_UN_INSTALL, false) &&
                !preferences.getBoolean(REAL_TIME_PROTECTION, false)) {

            stopService(new Intent(this, ServiceReminder.class));
        }
    }

    public void startService() {
        if (!preferences.getString(JUNK_REMINDER_FREQUENCY, MyAnnotations.NEVER_REMIND).equals(MyAnnotations.NEVER_REMIND) ||
                preferences.getBoolean(PHONE_BOOST_REMINDER, false) ||
                preferences.getBoolean(CPU_COOLER_REMINDER, false) ||
                preferences.getBoolean(BATTERY_SAVER, false) ||
                preferences.getBoolean(APP_INSTALL, false) ||
                preferences.getBoolean(APP_UN_INSTALL, false) ||
                preferences.getBoolean(REAL_TIME_PROTECTION, false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, ServiceReminder.class));
            } else {
                startService(new Intent(this, ServiceReminder.class));
            }
        }
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode != AppOpsManager.MODE_ALLOWED;
    }

    public void reqOverLay() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void reqUsageStats() {
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }


//    public void openDirectory(String packageName, String allPath,
//                              String folder1, String folder2,
//                              String folder3) {
//
//        String path = Environment.getExternalStorageDirectory() + "/Android/data/" + allPath;
//        File file = new File(path);
//        String startDir = null, secondDir, finalDirPath;
//
//        if (file.exists()) {
//            if (folder1 != null) {
//                startDir = "Android%2Fdata%2F" + packageName + "%2F" + folder1;
//
//            }
//            if (folder2 != null) {
//                startDir = "Android%2Fdata%2F" + packageName + "%2F" + folder1 + "%2F" + folder2;
//
//            }
//            if (folder3 != null) {
//                startDir = "Android%2Fdata%2F" + packageName + "%2F" + folder1 + "%2F" + folder2 + "%2F" + folder3;
//
//            }
//        }
//
//        StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
//
//        Intent intent = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
//            }
//        }
//
//
//        String uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
//
//        String scheme = uri.toString();
//
//        Log.d("TAG", "INITIAL_URI scheme: " + scheme);
//
//        scheme = scheme.replace("/root/", "/document/");
//
//        finalDirPath = scheme + "%3A" + startDir;
//
//        uri = finalDirPath;
//
//        intent.putExtra("", uri);
//        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
//
//        Log.d("TAG", "uri: " + uri.toString());
//
//        try {
//            openSomeActivityForResult(intent);
//        } catch (ActivityNotFoundException ignored) {
//
//        }
//    }
////
//    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//
//                    String uri = result.getData().getStringExtra("android.provider.extra.INITIAL_URI");
//
//                    getContentResolver().takePersistableUriPermission(Uri.parse(uri), FLAG_GRANT_READ_URI_PERMISSION);
//
//                    // these are my SharedPerfernce values for remembering the path
//
//
//                    // save any boolean in pref if user given the right path so we can use the path
//                    // in future and avoid to ask permission more than one time
//
//                    if (Build.VERSION.SDK_INT >= 29) {
//                        // uri is the path which we've saved in our shared pref
//                        DocumentFile fromTreeUri = DocumentFile.fromTreeUri(this, Uri.parse(uri));
//                        DocumentFile[] documentFiles = fromTreeUri.listFiles();
//
//
//                        for (int i = 0; i < documentFiles.length; i++) {
//                            documentFiles[i].getUri().toString(); //uri of the document
//                        }
//                    }
//                }
//
//            });
//
//    public void openSomeActivityForResult(Intent intent) {
//        someActivityResultLauncher.launch(intent);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        startService();
        stopService();
        switch (preferences.getString(JUNK_REMINDER_FREQUENCY, MyAnnotations.NEVER_REMIND)) {
            case MyAnnotations.NEVER_REMIND:
                textView7.setText("Never remind");
                break;
            case MyAnnotations.EVERY_DAY:
                textView7.setText("Everyday");
                break;
            case MyAnnotations.EVERY_3_DAY:
                textView7.setText("Every 3 day");
                break;
            case MyAnnotations.EVERY_7_DAY:
                textView7.setText("Every 7 day");
                break;
        }
    }
}