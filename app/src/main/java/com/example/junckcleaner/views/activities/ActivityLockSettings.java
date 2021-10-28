package com.example.junckcleaner.views.activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.annotations.RequestCodes;
import com.example.junckcleaner.interfaces.SendData;
import com.example.junckcleaner.permissions.MyPermissions;
import com.example.junckcleaner.prefrences.AppPreferences;

import java.util.concurrent.atomic.AtomicReference;

public class ActivityLockSettings extends AppCompatActivity implements SendData {

    ConstraintLayout cl_selfie_main, cl_take_pic, cl_selfie, cl_review_intruders, cl_changePassword,
            cl_changePattern, cl_fingerPrint,
            cl_newAlert, cl_chang_quest, cl_pattern_enable,
            cl_auto_lock, cl_vib_fb, cl_security_email;
    boolean clicked1 = false;

    ImageView imageView_selfie_switch3;
    ImageView imageView_finger_print_switch2;
    ImageView imageView_new_alert_switch2;
    ImageView imageView_pattern_enable_switch;
    ImageView imageView_vib_switch2;
    AppPreferences appPreferences;

    TextView textView_auto_lock_small, textView_attempts, textVIewEmail;
    AtomicReference<String> period = new AtomicReference<>();
    AtomicReference<String> attempts = new AtomicReference<>();
    MyPermissions permissions;
    boolean buttonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_settings);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white_opacity_70));

        appPreferences = new AppPreferences(this);
        permissions = new MyPermissions(this);


        cl_selfie_main = findViewById(R.id.cl_selfie_main);
        cl_take_pic = findViewById(R.id.cl_take_pic);
        cl_selfie = findViewById(R.id.cl_selfie);
        cl_review_intruders = findViewById(R.id.cl_review_intruders);
        cl_changePassword = findViewById(R.id.cl_changePassword);
        cl_changePattern = findViewById(R.id.cl_changePattern);
        cl_fingerPrint = findViewById(R.id.cl_fingerPrint);
        cl_newAlert = findViewById(R.id.cl_newAlert);
        cl_chang_quest = findViewById(R.id.cl_chang_quest);
        cl_pattern_enable = findViewById(R.id.cl_pattern_enable);
        cl_auto_lock = findViewById(R.id.cl_auto_lock);
        cl_vib_fb = findViewById(R.id.cl_vib_fb);
        cl_security_email = findViewById(R.id.cl_security_email);
        textVIewEmail = findViewById(R.id.textVIewEmail);


        imageView_selfie_switch3 = findViewById(R.id.imageView_selfie_switch3);
        imageView_finger_print_switch2 = findViewById(R.id.imageView_finger_print_switch2);
        imageView_new_alert_switch2 = findViewById(R.id.imageView_new_alert_switch2);
        imageView_pattern_enable_switch = findViewById(R.id.imageView_pattern_enable_switch);
        imageView_vib_switch2 = findViewById(R.id.imageView_vib_switch2);

        textView_auto_lock_small = findViewById(R.id.textView_auto_lock_small);
        textView_attempts = findViewById(R.id.textView_attempts);


        ImageView imageView_intruder_selfie_up2 = findViewById(R.id.imageView_intruder_selfie_up2);

        if (!appPreferences.getString(MyAnnotations.SECURITY_EMAIL, "").equals("")) {
            textVIewEmail.setText(appPreferences.getString(MyAnnotations.SECURITY_EMAIL, ""));
        }
        if (checkFingerPrintsAvailable()) {
//            cl_fingerPrint.setVisibility(View.GONE);
        } else {

        }

        setViewVisibility(false);
        checkSwitchesPreferences();
        checkAutoLockPreferences();
        checkTakePicturePreferences();
        cl_selfie_main.setOnClickListener(v -> {
            if (!clicked1) {
                clicked1 = true;
                setViewVisibility(true);
                imageView_intruder_selfie_up2.setRotation(180f);
            } else {
                setViewVisibility(false);
                imageView_intruder_selfie_up2.setRotation(360f);
                clicked1 = false;
            }
        });
        cl_selfie.setOnClickListener(v -> {

            if (!permissions.checkPermission()) {
                permissions.requestPermissions();
            } else if (!checkPermission()) {
                requestStorage();
            } else {
                if (!appPreferences.getBoolean(MyAnnotations.TAKE_SELFIE, false)) {
                    appPreferences.addBoolean(MyAnnotations.TAKE_SELFIE, true);
                    imageView_selfie_switch3.setImageResource(R.drawable.ic_toggle_on);
                } else {
                    imageView_selfie_switch3.setImageResource(R.drawable.ic_toggle_off);
                    appPreferences.addBoolean(MyAnnotations.TAKE_SELFIE, false);
                }
            }


        });
        cl_take_pic.setOnClickListener(v -> {
            takePictureAttemptsDialog();

        });

        cl_review_intruders.setOnClickListener(v -> {
            buttonClicked = true;
            startActivity(new Intent(ActivityLockSettings.this, ActivityIntrudersRecord.class));
        });
        cl_changePassword.setOnClickListener(v -> {

            buttonClicked = true;

            if (appPreferences.getBoolean(MyAnnotations.PIN_IS_CREATED, false)) {
                startActivity(new Intent(ActivityLockSettings.this, ActivitySetSecurityQuestion.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CHANGE_PIN));
            } else {
                startActivity(new Intent(ActivityLockSettings.this, ActivitySetSecurityQuestion.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PIN));
            }
        });
        cl_changePattern.setOnClickListener(v -> {
            buttonClicked = true;


            if (appPreferences.getBoolean(MyAnnotations.PATTERN_IS_CREATED, false)) {
                startActivity(new Intent(ActivityLockSettings.this, ActivitySetSecurityQuestion.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CHANGE_PATTERN));
            } else {
                startActivity(new Intent(ActivityLockSettings.this, ActivitySetSecurityQuestion.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PATTERN));
            }
        });

        cl_fingerPrint.setOnClickListener(v -> {

            if (checkFingerPrintsAvailable()) {
                buttonClicked = true;
                if (!appPreferences.getBoolean(MyAnnotations.FINGER_PRINT, false)) {
                    appPreferences.addBoolean(MyAnnotations.FINGER_PRINT, true);
                    imageView_finger_print_switch2.setImageResource(R.drawable.ic_toggle_on);
                } else {
                    imageView_finger_print_switch2.setImageResource(R.drawable.ic_toggle_off);
                    appPreferences.addBoolean(MyAnnotations.FINGER_PRINT, false);
                }
            } else {
                Toast.makeText(this, "Unable to enable fingerprint", Toast.LENGTH_SHORT).show();
            }
        });
        cl_newAlert.setOnClickListener(v -> {
            buttonClicked = true;

            if (!appPreferences.getBoolean(MyAnnotations.NEW_ALERT, false)) {
                appPreferences.addBoolean(MyAnnotations.NEW_ALERT, true);
                imageView_new_alert_switch2.setImageResource(R.drawable.ic_toggle_on);
            } else {
                imageView_new_alert_switch2.setImageResource(R.drawable.ic_toggle_off);
                appPreferences.addBoolean(MyAnnotations.NEW_ALERT, false);
            }
        });
        cl_chang_quest.setOnClickListener(v -> {
            buttonClicked = true;

            startActivity(new Intent(ActivityLockSettings.this, ActivitySetSecurityQuestion.class)
                    .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, "")
                    .putExtra(MyAnnotations.IN_APP, true));


        });

        cl_pattern_enable.setOnClickListener(v -> {
            buttonClicked = true;
            if (!appPreferences.getBoolean(MyAnnotations.PATTERN_ENABLED, false)) {

                if (!appPreferences.getBoolean(MyAnnotations.PATTERN_IS_CREATED, false)) {
                    startActivity(new Intent(ActivityLockSettings.this, ActivityCreatePattern.class)
                            .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PATTERN));
                } else {
                    imageView_pattern_enable_switch.setImageResource(R.drawable.ic_toggle_on);
                    appPreferences.addBoolean(MyAnnotations.PATTERN_ENABLED, true);
                }

            } else {
                if (!appPreferences.getBoolean(MyAnnotations.PIN_IS_CREATED, false)) {
                    startActivity(new Intent(ActivityLockSettings.this, ActivityCreatePin.class)
                            .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PIN));
                } else {
                    imageView_pattern_enable_switch.setImageResource(R.drawable.ic_toggle_off);
                    appPreferences.addBoolean(MyAnnotations.PATTERN_ENABLED, false);
                }
            }
        });
        cl_auto_lock.setOnClickListener(v -> {
            autoLockDialog();
        });


        cl_vib_fb.setOnClickListener(v -> {

            if (!appPreferences.getBoolean(MyAnnotations.VIB_FEEDBACK, false)) {
                appPreferences.addBoolean(MyAnnotations.VIB_FEEDBACK, true);
                imageView_vib_switch2.setImageResource(R.drawable.ic_toggle_on);
            } else {
                appPreferences.addBoolean(MyAnnotations.VIB_FEEDBACK, false);
                imageView_vib_switch2.setImageResource(R.drawable.ic_toggle_off);
            }
        });
        cl_security_email.setOnClickListener(v -> {
            buttonClicked = true;

            startActivity(new Intent(ActivityLockSettings.this, ActivityAddSecurityEmail.class)
                    .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, ""));
        });
        findViewById(R.id.imageView_back).setOnClickListener(v -> {
            finish();
        });

    }

    public boolean checkPermission() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        } else {
        int write = ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);

        return write == PackageManager.PERMISSION_GRANTED &&
                read == PackageManager.PERMISSION_GRANTED;
//        }
    }

    public void requestStorage() {
        ActivityCompat.requestPermissions(this,
                new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                RequestCodes.REQUEST_CODE_PERMISSIONS);
    }

    private void takePictureAttemptsDialog() {
        AtomicReference<String> attempt = new AtomicReference<>(MyAnnotations.TIMES_3);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_take_pic_dialog,
                null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view);

        LinearLayout lc_radio_3 = view.findViewById(R.id.lc_radio_3);
        LinearLayout lc_radio_4 = view.findViewById(R.id.lc_radio_4);
        LinearLayout lc_radio_5 = view.findViewById(R.id.lc_radio_5);
        LinearLayout lc_radio_10 = view.findViewById(R.id.lc_radio_10);
        LinearLayout lc_radio_15 = view.findViewById(R.id.lc_radio_15);

        ImageView imageView_rad_3 = view.findViewById(R.id.imageView_rad_3);
        ImageView imageView_rad_4 = view.findViewById(R.id.imageView_rad_4);
        ImageView imageView_rad_5 = view.findViewById(R.id.imageView_rad_5);
        ImageView imageView_rad_10 = view.findViewById(R.id.imageView_rad_10);
        ImageView imageView_rad_15 = view.findViewById(R.id.imageView_rad_15);


        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view.findViewById(R.id.textView_button).setOnClickListener(v1 -> {
            textView_attempts.setText(attempts.get());
            appPreferences.addString(MyAnnotations.WRONG_ATTEMPTS, attempt.get());
            dialog.dismiss();

        });
        switch (appPreferences.getString(MyAnnotations.WRONG_ATTEMPTS, "")) {
            case MyAnnotations.TIMES_3:
                changeRatioImages5(imageView_rad_3,
                        imageView_rad_4,
                        imageView_rad_5,
                        imageView_rad_10,
                        imageView_rad_15);
                break;
            case MyAnnotations.TIMES_4:
                changeRatioImages5(imageView_rad_4,
                        imageView_rad_3,
                        imageView_rad_5,
                        imageView_rad_10,
                        imageView_rad_15);
                break;
            case MyAnnotations.TIMES_5:
                changeRatioImages5(imageView_rad_5,
                        imageView_rad_3,
                        imageView_rad_4,
                        imageView_rad_10,
                        imageView_rad_15);
                break;
            case MyAnnotations.TIMES_10:
                changeRatioImages5(imageView_rad_10,
                        imageView_rad_3,
                        imageView_rad_4,
                        imageView_rad_5,
                        imageView_rad_15);
                break;
            case MyAnnotations.TIMES_15:
                changeRatioImages5(imageView_rad_15,
                        imageView_rad_3,
                        imageView_rad_4,
                        imageView_rad_5,
                        imageView_rad_10);
                break;
            default:
                changeRatioImages5(imageView_rad_3,
                        imageView_rad_4,
                        imageView_rad_5,
                        imageView_rad_10,
                        imageView_rad_15);
                break;
        }

        lc_radio_3.setOnClickListener(view1 -> {
            changeRatioImages5(imageView_rad_3,
                    imageView_rad_4,
                    imageView_rad_5,
                    imageView_rad_10,
                    imageView_rad_15);

            attempt.set(MyAnnotations.TIMES_3);
            attempts.set("3 Attempts");
        });
        lc_radio_4.setOnClickListener(view14 -> {
            changeRatioImages5(imageView_rad_4,
                    imageView_rad_3,
                    imageView_rad_5,
                    imageView_rad_10,
                    imageView_rad_15);
            attempt.set(MyAnnotations.TIMES_4);
            attempts.set("4 Attempts");

        });
        lc_radio_5.setOnClickListener(view13 -> {
            changeRatioImages5(imageView_rad_5,
                    imageView_rad_3,
                    imageView_rad_4,
                    imageView_rad_10,
                    imageView_rad_15);
            attempt.set(MyAnnotations.TIMES_5);
            attempts.set("5 Attempts");

        });
        lc_radio_10.setOnClickListener(view12 -> {
            changeRatioImages5(imageView_rad_10,
                    imageView_rad_3,
                    imageView_rad_4,
                    imageView_rad_5,
                    imageView_rad_15);
            attempt.set(MyAnnotations.TIMES_10);
            attempts.set("10 Attempts");

        });
        lc_radio_15.setOnClickListener(view15 -> {
            changeRatioImages5(imageView_rad_15,
                    imageView_rad_3,
                    imageView_rad_4,
                    imageView_rad_5,
                    imageView_rad_10);
            attempt.set(MyAnnotations.TIMES_15);
            attempts.set("15 Attempts");

        });
    }

    public void changeRatioImages(ImageView on, ImageView off1, ImageView off2, ImageView off3,
                                  ImageView off4, ImageView off5) {
        on.setImageResource(R.drawable.ic_radio_on);
        off1.setImageResource(R.drawable.ic_radio_off);
        off2.setImageResource(R.drawable.ic_radio_off);
        off3.setImageResource(R.drawable.ic_radio_off);
        off4.setImageResource(R.drawable.ic_radio_off);
        off5.setImageResource(R.drawable.ic_radio_off);

    }

    public void changeRatioImages5(ImageView on, ImageView off1, ImageView off2, ImageView off3,
                                   ImageView off4) {
        on.setImageResource(R.drawable.ic_radio_on);
        off1.setImageResource(R.drawable.ic_radio_off);
        off2.setImageResource(R.drawable.ic_radio_off);
        off3.setImageResource(R.drawable.ic_radio_off);
        off4.setImageResource(R.drawable.ic_radio_off);

    }

    public void setViewVisibility(boolean visible) {

        if (visible) {
            cl_take_pic.setVisibility(View.VISIBLE);
            cl_selfie.setVisibility(View.VISIBLE);
            cl_review_intruders.setVisibility(View.VISIBLE);

        } else {
            cl_take_pic.setVisibility(View.GONE);
            cl_selfie.setVisibility(View.GONE);
            cl_review_intruders.setVisibility(View.GONE);

        }

    }

    public void autoLockDialog() {
        AtomicReference<String> lockDelay = new AtomicReference<>(MyAnnotations.AFTER_SCREEN_OFF);

        View view = LayoutInflater.from(this).inflate(R.layout.layout_automatically_lock_dialog,
                null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view);

        ImageView imageView_rad_after_screen_off = view.findViewById(R.id.imageView_rad_after_screen_off);
        ImageView imageView_rad_sec_30 = view.findViewById(R.id.imageView_rad_sec_30);
        ImageView imageView_rad_Immediately = view.findViewById(R.id.imageView_rad_Immediately);
        ImageView imageView_rad_mint_1 = view.findViewById(R.id.imageView_rad_mint_1);
        ImageView imageView_rad_mint_5 = view.findViewById(R.id.imageView_rad_mint_5);
        ImageView imageView_rad_min_10 = view.findViewById(R.id.imageView_rad_min_10);

        LinearLayout ll_radio_after_screen_off = view.findViewById(R.id.ll_radio_after_screen_off);
        LinearLayout ll_30_sec = view.findViewById(R.id.ll_30_sec);
        LinearLayout ll_radio_Immediately = view.findViewById(R.id.ll_radio_Immediately);
        LinearLayout ll_radio_mint_1 = view.findViewById(R.id.ll_radio_mint_1);
        LinearLayout ll_radio_mint_5 = view.findViewById(R.id.ll_radio_mint_5);
        LinearLayout ll_radio_min_10 = view.findViewById(R.id.ll_radio_min_10);
        switch (appPreferences.getString(MyAnnotations.AUTOMATICALLY_LOCK, "")) {
            case MyAnnotations.AFTER_SCREEN_OFF:
                changeRatioImages(imageView_rad_after_screen_off,
                        imageView_rad_sec_30,
                        imageView_rad_Immediately,
                        imageView_rad_mint_1,
                        imageView_rad_mint_5,
                        imageView_rad_min_10);

                break;
            case MyAnnotations.IMMEDIATELY:
                changeRatioImages(imageView_rad_Immediately,
                        imageView_rad_after_screen_off,
                        imageView_rad_sec_30,
                        imageView_rad_mint_1,
                        imageView_rad_mint_5,
                        imageView_rad_min_10);
                break;
            case MyAnnotations.SECONDS_30:
                changeRatioImages(imageView_rad_sec_30,
                        imageView_rad_after_screen_off,
                        imageView_rad_Immediately,
                        imageView_rad_mint_1,
                        imageView_rad_mint_5,
                        imageView_rad_min_10);
                break;
            case MyAnnotations.MINUTE_1:
                changeRatioImages(imageView_rad_mint_1,
                        imageView_rad_after_screen_off,
                        imageView_rad_Immediately,
                        imageView_rad_sec_30,
                        imageView_rad_mint_5,
                        imageView_rad_min_10);
                break;
            case MyAnnotations.MINUTES_5:
                changeRatioImages(imageView_rad_mint_5,
                        imageView_rad_after_screen_off,
                        imageView_rad_sec_30,
                        imageView_rad_Immediately,
                        imageView_rad_mint_1,
                        imageView_rad_min_10);
                break;
            case MyAnnotations.MINUTES_10:
                changeRatioImages(imageView_rad_min_10,
                        imageView_rad_after_screen_off,
                        imageView_rad_sec_30,
                        imageView_rad_Immediately,
                        imageView_rad_mint_1,
                        imageView_rad_mint_5);
                break;

            default:
                changeRatioImages(imageView_rad_after_screen_off,
                        imageView_rad_sec_30,
                        imageView_rad_Immediately,
                        imageView_rad_mint_1,
                        imageView_rad_mint_5,
                        imageView_rad_min_10);
                break;
        }
        ll_radio_after_screen_off.setOnClickListener(view1 -> {
            changeRatioImages(imageView_rad_after_screen_off,
                    imageView_rad_sec_30,
                    imageView_rad_Immediately,
                    imageView_rad_mint_1,
                    imageView_rad_mint_5,
                    imageView_rad_min_10);

            lockDelay.set(MyAnnotations.AFTER_SCREEN_OFF);
            period.set("After screen off");
        });
        ll_30_sec.setOnClickListener(view14 -> {
            changeRatioImages(imageView_rad_sec_30,
                    imageView_rad_after_screen_off,
                    imageView_rad_Immediately,
                    imageView_rad_mint_1,
                    imageView_rad_mint_5,
                    imageView_rad_min_10);
            lockDelay.set(MyAnnotations.SECONDS_30);
            period.set("30 seconds");

        });
        ll_radio_Immediately.setOnClickListener(view13 -> {
            changeRatioImages(imageView_rad_Immediately,
                    imageView_rad_after_screen_off,
                    imageView_rad_sec_30,
                    imageView_rad_mint_1,
                    imageView_rad_mint_5,
                    imageView_rad_min_10);
            lockDelay.set(MyAnnotations.IMMEDIATELY);
            period.set("Immediately");

        });
        ll_radio_mint_1.setOnClickListener(view12 -> {
            changeRatioImages(imageView_rad_mint_1,
                    imageView_rad_after_screen_off,
                    imageView_rad_Immediately,
                    imageView_rad_sec_30,
                    imageView_rad_mint_5,
                    imageView_rad_min_10);
            lockDelay.set(MyAnnotations.MINUTE_1);
            period.set("1 minutes");

        });
        ll_radio_mint_5.setOnClickListener(view15 -> {
            changeRatioImages(imageView_rad_mint_5,
                    imageView_rad_after_screen_off,
                    imageView_rad_sec_30,
                    imageView_rad_Immediately,
                    imageView_rad_mint_1,
                    imageView_rad_min_10);
            lockDelay.set(MyAnnotations.MINUTES_5);
            period.set("5 minutes");

        });
        ll_radio_min_10.setOnClickListener(view16 -> {
            changeRatioImages(imageView_rad_min_10,
                    imageView_rad_after_screen_off,
                    imageView_rad_sec_30,
                    imageView_rad_Immediately,
                    imageView_rad_mint_1,
                    imageView_rad_mint_5
            );
            lockDelay.set(MyAnnotations.MINUTES_10);
            period.set("10 minutes");

        });


        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view.findViewById(R.id.textView_button).setOnClickListener(v1 -> {
            textView_auto_lock_small.setText(period.get());
            appPreferences.addString(MyAnnotations.AUTOMATICALLY_LOCK, lockDelay.get());
            dialog.dismiss();
        });
    }

    public void checkAutoLockPreferences() {
        switch (appPreferences.getString(MyAnnotations.AUTOMATICALLY_LOCK, "")) {
            case MyAnnotations.AFTER_SCREEN_OFF:
                period.set("After screen off");
                break;
            case MyAnnotations.IMMEDIATELY:
                period.set("Immediately");

                break;
            case MyAnnotations.SECONDS_30:
                period.set("30 seconds");

                break;
            case MyAnnotations.MINUTE_1:
                period.set("1 minute");

                break;
            case MyAnnotations.MINUTES_5:
                period.set("5 minutes");

                break;
            case MyAnnotations.MINUTES_10:
                period.set("10 minutes");
                break;

            default:
                period.set("After screen off");
                break;
        }
        textView_auto_lock_small.setText(period.get());
    }

    public void checkTakePicturePreferences() {
        switch (appPreferences.getString(MyAnnotations.WRONG_ATTEMPTS, "")) {
            case MyAnnotations.TIMES_3:
                attempts.set("3 Attempts");
                break;
            case MyAnnotations.TIMES_4:
                attempts.set("4 Attempts");

                break;
            case MyAnnotations.TIMES_5:
                attempts.set("5 Attempts");

                break;
            case MyAnnotations.TIMES_10:
                attempts.set("10 Attempts");

                break;
            case MyAnnotations.TIMES_15:
                attempts.set("15 Attempts");

                break;
            default:
                attempts.set("3 Attempts");
                break;
        }
        textView_attempts.setText(attempts.get());
    }

    public void checkSwitchesPreferences() {
        //take selfie
        if (appPreferences.getBoolean(MyAnnotations.TAKE_SELFIE, false)) {
            imageView_selfie_switch3.setImageResource(R.drawable.ic_toggle_on);
        } else {
            imageView_selfie_switch3.setImageResource(R.drawable.ic_toggle_off);
        }
        //finger print
        if (appPreferences.getBoolean(MyAnnotations.FINGER_PRINT, false)) {
            imageView_finger_print_switch2.setImageResource(R.drawable.ic_toggle_on);
        } else {

            imageView_finger_print_switch2.setImageResource(R.drawable.ic_toggle_off);
        }
        //NEW_ALERT
        if (appPreferences.getBoolean(MyAnnotations.NEW_ALERT, false)) {
            imageView_new_alert_switch2.setImageResource(R.drawable.ic_toggle_on);
        } else {
            imageView_new_alert_switch2.setImageResource(R.drawable.ic_toggle_off);
        }
        //PATTERN_ENABLED
        if (appPreferences.getBoolean(MyAnnotations.PATTERN_ENABLED, false)) {
            imageView_pattern_enable_switch.setImageResource(R.drawable.ic_toggle_on);
        } else {
            imageView_pattern_enable_switch.setImageResource(R.drawable.ic_toggle_off);
        }
        //VIB_FEEDBACK
        if (appPreferences.getBoolean(MyAnnotations.VIB_FEEDBACK, false)) {
            imageView_vib_switch2.setImageResource(R.drawable.ic_toggle_on);
        } else {
            imageView_vib_switch2.setImageResource(R.drawable.ic_toggle_off);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean checkFingerPrintsAvailable() {

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:

            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:

                return false;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    enrollIntent.setAction(Settings.ACTION_BIOMETRIC_ENROLL);
//                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                            BIOMETRIC_STRONG);
//                    openSomeActivityForResult(enrollIntent);
//
//                }
                return false;


        }
        return false;

    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                }
            });

    public void openSomeActivityForResult(Intent intent) {
        someActivityResultLauncher.launch(intent);
    }


    @Override
    public void data(String data) {
//        goneFragment(FragmentFinger.newInstance());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!buttonClicked) {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!buttonClicked) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!buttonClicked) {
            finish();
        }

    }
}