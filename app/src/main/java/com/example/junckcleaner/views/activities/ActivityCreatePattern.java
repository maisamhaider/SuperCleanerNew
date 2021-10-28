package com.example.junckcleaner.views.activities;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;
import com.takwolf.android.lock9.Lock9View;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ActivityCreatePattern extends AppCompatActivity {
    AppPreferences appPreferences;
    Lock9View lock_9_view;
    String pattern;
    boolean firstTry = false;
    TextView textView_patter_not_matched, textView_timer;
    ImageView imageView_locked_pattern_app;
    int i = 0;

    //create view
    TextView textView_PIN;

    //draw view
    View view_disable;

    boolean isTimeStarted = false;
    Utils utils;
    AlertDialog dialog;

    @Override

    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));

        appPreferences = new AppPreferences(this);
        utils = new Utils(this);

        String whatToDo = getIntent().getStringExtra(MyAnnotations.EXTRA_WHAT_TO_DO);

        if (whatToDo.equals(MyAnnotations.CHANGE_PATTERN)) {
            setContentView(R.layout.layout_create_pattern);
            textView_PIN = findViewById(R.id.textView_PIN);

        } else if (whatToDo.equals(MyAnnotations.CREATE_PATTERN)) {
            setContentView(R.layout.layout_create_pattern);
            textView_PIN = findViewById(R.id.textView_PIN);

        } else {
            //draw patter
            setContentView(R.layout.layout_draw_pattern);
            textView_timer = findViewById(R.id.textView_timer);
            view_disable = findViewById(R.id.view_disable);
            findViewById(R.id.textView_forget_pattern).setOnClickListener(view -> {
                startActivity(new Intent(this, ActivitySetSecurityQuestion.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.FORGOT_PATTERN)
                        .putExtra(MyAnnotations.IN_APP, true));
                finish();
            });
        }

        textView_patter_not_matched = findViewById(R.id.textView_patter_not_matched);
        imageView_locked_pattern_app = findViewById(R.id.imageView_locked_pattern_app);

        lock_9_view = findViewById(R.id.lock_9_view);


        if (whatToDo.equals(MyAnnotations.CHANGE_PATTERN)) {
            changePasswordCode();
        } else if (whatToDo.equals(MyAnnotations.CREATE_PATTERN)) {
            createPasswordCode();
        } else {
            drawPasswordCode();

        }


    }


    public void createPasswordCode() {
        lock_9_view.setCallBack(password -> {
            if (!firstTry) {
                firstTry = true;
                pattern = password;
                textView_patter_not_matched.setText("Confirm your pattern");
            } else {
                firstTry = false;
                if (pattern.equals(password)) {
                    appPreferences.addString(MyAnnotations.PATTERN, pattern);
                    appPreferences.addBoolean(MyAnnotations.IS_LOCKED, true);
                    appPreferences.addBoolean(MyAnnotations.PATTERN_ENABLED, true);
                    appPreferences.addBoolean(MyAnnotations.PATTERN_IS_CREATED, true);
                    Toast.makeText(this, "Pattern created", Toast.LENGTH_SHORT).show();
                    if (appPreferences.getString(MyAnnotations.SECURITY_Q, "").isEmpty()) {
                        startActivity(new Intent(this, ActivitySetSecurityQuestion.class)
                                .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, "")
                                .putExtra(MyAnnotations.IN_APP, true));
                        finish();

                    } else {
                        startActivity(new Intent(this, AppsLockerActivity.class));

                    }
                    finish();
                } else {
                    if (appPreferences.getBoolean(MyAnnotations.VIB_FEEDBACK, false)) {
                        utils.setViberate();
                    }
                    textView_patter_not_matched.setTextColor(Color.RED);
                    textView_patter_not_matched.setText("Wrong confirmation pattern");

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        textView_patter_not_matched.setTextColor(ContextCompat.getColor(
                                ActivityCreatePattern.this,
                                R.color.white_opacity_90));
                        textView_patter_not_matched.setText("Create an unlock pattern");
                    }, 1500);
                    Toast.makeText(this, "Pattern didn't match try again please", Toast.LENGTH_SHORT).show();

                }

            }

        });

        imageView_locked_pattern_app.setImageResource(R.drawable.ic_shield_lock_icon);

        textView_PIN.setOnClickListener(view -> {
            if (!appPreferences.getBoolean(MyAnnotations.PIN_IS_CREATED, false)) {
                startActivity(new Intent(ActivityCreatePattern.this, ActivityCreatePin.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PIN)
                );
            } else {
                startActivity(new Intent(ActivityCreatePattern.this, ActivityCreatePin.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CHANGE_PIN));

            }
            finish();
        });
        findViewById(R.id.imageView_back).setOnClickListener(v -> {
            finish();
        });
    }

    public void drawPasswordCode() {
        pattern = appPreferences.getString(MyAnnotations.PATTERN, "");
        lock_9_view.setCallBack(password -> {
            if (pattern.equals(password)) {
                if (appPreferences.getString(MyAnnotations.SECURITY_Q, "").isEmpty()) {
                    startActivity(new Intent(this, ActivitySetSecurityQuestion.class)
                            .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, "")
                            .putExtra(MyAnnotations.IN_APP, true));
                    finish();
                } else {
                    startActivity(new Intent(this, AppsLockerActivity.class));

                }
                finish();
            } else {
                i = i + 1;
                if (appPreferences.getBoolean(MyAnnotations.VIB_FEEDBACK, false)) {
                    utils.setViberate();
                }
                Toast.makeText(this, "Pattern Incorrect", Toast.LENGTH_SHORT).show();
                if (i >= 5) {
                    textView_patter_not_matched.setText("you are blocked for while.");
                    isTimeStarted = true;
                    view_disable.setVisibility(View.VISIBLE);
                    new CountDownTimer(30000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            textView_timer.setText(String.valueOf(millisUntilFinished / 1000));
                            //here you can have your logic to set text to edittext
                            isTimeStarted = true;
                        }

                        public void onFinish() {
                            textView_timer.setText("");
                            i = 0;
                            isTimeStarted = false;
                            view_disable.setVisibility(View.GONE);
                            textView_patter_not_matched.setText("try again");
                        }

                    }.start();
                } else {

                    textView_patter_not_matched.setText("Wrong pattern");
                    textView_patter_not_matched.setTextColor(Color.RED);
                }
            }
        });

        imageView_locked_pattern_app.setImageResource(R.drawable.ic_shield_lock_icon);

        if (!isTimeStarted) {
            view_disable.setVisibility(View.GONE);
        }
        view_disable.setOnClickListener(view -> {
        });

        if (checkFingerPrintsAvailable()) {
            if (appPreferences.getBoolean(MyAnnotations.FINGER_PRINT, false)) {
                fingerprints();
            }
        }


    }

    public void changePasswordCode() {
        textView_patter_not_matched.setText("Draw your pattern");
        String savePassword = appPreferences.getString(MyAnnotations.PATTERN, "");
        AtomicReference<String> newPassword = new AtomicReference<>("");
        AtomicReference<String> confirmPassword = new AtomicReference<>("");
        AtomicBoolean enteredSavePass = new AtomicBoolean(false);
        AtomicBoolean enteredNewPassword = new AtomicBoolean(false);
        AtomicBoolean enteredConfirmPassword = new AtomicBoolean(false);

        lock_9_view.setCallBack(password -> {

            if (!enteredSavePass.get()) {
                if (savePassword.equals(password)) {
                    enteredSavePass.set(true);
                    textView_patter_not_matched.setText("Draw new pattern");
                    textView_patter_not_matched.setText("Draw new pattern");
                } else {
                    enteredSavePass.set(false);
                    textView_patter_not_matched.setText("Wrong pattern");
                    textView_patter_not_matched.setText("Wrong pattern");

                }
            } else if (!enteredNewPassword.get()) {
                enteredNewPassword.set(true);
                newPassword.set(password);
                textView_patter_not_matched.setText("Draw confirm pattern");
                textView_patter_not_matched.setText("Draw confirm pattern");
            } else if (!enteredConfirmPassword.get()) {
                confirmPassword.set(password);
                if (newPassword.get().equals(confirmPassword.get())) {
                    enteredConfirmPassword.set(true);
                    appPreferences.addString(MyAnnotations.PATTERN, confirmPassword.get());
                    textView_patter_not_matched.setText("Pattern is changes successfully");
                    textView_patter_not_matched.setText("Pattern is changes successfully");

                } else {
                    if (appPreferences.getBoolean(MyAnnotations.VIB_FEEDBACK, false)) {
                        utils.setViberate();
                    }
                    textView_patter_not_matched.setText("Try again.Draw your pattern");

                }
                enteredSavePass.set(false);
                enteredNewPassword.set(false);
                enteredConfirmPassword.set(false);
            }
        });

        imageView_locked_pattern_app.setImageResource(R.drawable.ic_shield_lock_icon);
        textView_PIN.setOnClickListener(view -> {
            if (!appPreferences.getBoolean(MyAnnotations.PIN_IS_CREATED, false)) {
                startActivity(new Intent(this, ActivityCreatePin.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PIN));
            } else {
                startActivity(new Intent(this, ActivityCreatePin.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CHANGE_PIN));
            }
            finish();


        });
        findViewById(R.id.imageView_back).setOnClickListener(v -> {
            finish();
        });

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
                return false;


        }
        return false;
    }

    public void fingerprints() {
        Executor executor;
        BiometricPrompt biometricPrompt;

        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
                // use login pass
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    // show pattern or pin pad
                }

            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(ActivityCreatePattern.this, AppsLockerActivity.class));
                finish();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        biometricPrompt.authenticate(createPrompt());
    }

    public BiometricPrompt.PromptInfo createPrompt() {
        BiometricPrompt.PromptInfo promptInfo;
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("unlock by fingerprint")
                .setNegativeButtonText("Cancel")
                .build();

        return promptInfo;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
