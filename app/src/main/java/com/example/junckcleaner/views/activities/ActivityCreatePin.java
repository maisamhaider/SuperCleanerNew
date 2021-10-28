package com.example.junckcleaner.views.activities;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.annotation.SuppressLint;
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
import com.example.junckcleaner.interfaces.SendData;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;

import java.util.concurrent.Executor;

public class ActivityCreatePin extends AppCompatActivity implements View.OnClickListener,
        SendData {
    View view_1, view_2, view_3, view_4;
    TextView textView_1, textView_2, textView_3, textView_4, textView_5, textView_6, textView_7,
            textView_8, textView_9, textView_0, textView_pattern,
            textView_pin_not_matched, textView;

    //create pin view
    ImageView imageView_back;

    // enter pin view
    View view_disable;
    TextView textView_forget_pin;
    TextView textView_timer;


    ImageView image_pin_locked_app;
    String password = "";
    String savedPassword = "";
    String firstEnterPassword = "";
    String newPassword = "";
    String confirmPassword = "";
    boolean firstEnteredPass = false;
    boolean oldPassDone = false;
    boolean newPassDone = false;
    boolean confirmPassDone = false;
    AppPreferences preferences;
    String whatToDo;
    int i = 0;
    Utils utils;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));

        preferences = new AppPreferences(this);
        utils = new Utils(this);
        whatToDo = getIntent().getStringExtra(MyAnnotations.EXTRA_WHAT_TO_DO);

        if (whatToDo.equals(MyAnnotations.CHANGE_PIN)) {
            savedPassword = preferences.getString(MyAnnotations.PIN, "");
            setContentView(R.layout.activity_create_pin);
            textView = findViewById(R.id.textView);
            textView_pattern = findViewById(R.id.textView_pattern);
            imageView_back = findViewById(R.id.imageView_back);

            textView.setText("Enter your PIN");

            textView_pattern.setOnClickListener(view -> {
                startActivity(new Intent(this, ActivityCreatePattern.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CHANGE_PATTERN));
                finish();

            });
        } else if (whatToDo.equals(MyAnnotations.CREATE_PIN)) {
            //create pin
            setContentView(R.layout.activity_create_pin);
            textView = findViewById(R.id.textView);
            textView_pattern = findViewById(R.id.textView_pattern);
            imageView_back = findViewById(R.id.imageView_back);

            textView_pattern.setOnClickListener(view -> {
                startActivity(new Intent(this, ActivityCreatePattern.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PATTERN));
                finish();

            });
        } else {
            //enter pin
            setContentView(R.layout.activity_enter_pin);
            textView_timer = findViewById(R.id.textView_timer);
            textView_forget_pin = findViewById(R.id.textView_forget_pin);
            view_disable = findViewById(R.id.view_disable);
            textView_pin_not_matched = findViewById(R.id.textView_pin_not_matched);
            textView_forget_pin.setOnClickListener(view -> {
                startActivity(new Intent(this, ActivitySetSecurityQuestion.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.FORGOT_PIN)
                        .putExtra(MyAnnotations.IN_APP, true));
                finish();
            });

        }

        if (imageView_back != null) {
            imageView_back.setOnClickListener(view -> {
                        startActivity(new Intent(this, ActivityLockSettings.class));
                        finish();
                    }
            );
        }
        image_pin_locked_app = findViewById(R.id.image_pin_locked_app);

        view_1 = findViewById(R.id.view_1);
        view_2 = findViewById(R.id.view_2);
        view_3 = findViewById(R.id.view_3);
        view_4 = findViewById(R.id.view_4);


        textView_1 = findViewById(R.id.textView_1);
        textView_2 = findViewById(R.id.textView_2);
        textView_3 = findViewById(R.id.textView_3);
        textView_4 = findViewById(R.id.textView_4);
        textView_5 = findViewById(R.id.textView_5);
        textView_6 = findViewById(R.id.textView_6);
        textView_7 = findViewById(R.id.textView_7);
        textView_8 = findViewById(R.id.textView_8);
        textView_9 = findViewById(R.id.textView_9);
        textView_0 = findViewById(R.id.textView_0);

        textView_1.setOnClickListener(this);
        textView_2.setOnClickListener(this);
        textView_3.setOnClickListener(this);
        textView_4.setOnClickListener(this);
        textView_5.setOnClickListener(this);
        textView_6.setOnClickListener(this);
        textView_7.setOnClickListener(this);
        textView_8.setOnClickListener(this);
        textView_9.setOnClickListener(this);
        textView_0.setOnClickListener(this);


        image_pin_locked_app.setImageResource(R.drawable.ic_shield_lock_icon);

        if (checkFingerPrintsAvailable()) {
            if (preferences.getBoolean(MyAnnotations.FINGER_PRINT, false)) {
                fingerprints();
            }

        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_0:
                password += textView_0.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_1:
                password += textView_1.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_2:
                password += textView_2.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_3:
                password += textView_3.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_4:
                password += textView_4.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_5:
                password += textView_5.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_6:
                password += textView_6.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_7:
                password += textView_7.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_8:
                password += textView_8.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_9:
                password += textView_9.getText().toString();
                goToNextActivity(password);
                break;


        }
    }


    private void goToNextActivity(String password) {
        setViews(password.length());

        if (whatToDo.equals(MyAnnotations.CHANGE_PIN)) {
            if (!oldPassDone) {
                //check saved pin matches entered pin
                if (password.length() == 4) {
                    setViews(0);
                    if (savedPassword.equals(password)) {
                        oldPassDone = true;
                        textView.setText("Enter new PIN");
                    } else {
                        if (preferences.getBoolean(MyAnnotations.VIB_FEEDBACK, false)) {
                            utils.setViberate();
                        }
                        textView.setText("Entered Wrong PIN");
                        oldPassDone = false;
                    }
                    this.password = "";

                }
            } else {
                if (!newPassDone) {
                    //get new pin
                    if (password.length() == 4) {
                        setViews(0);
                        newPassword = password;
                        newPassDone = true;
                        this.password = "";
                        textView.setText("Confirm your PIN");
                    }
                } else if (!confirmPassDone) {
                    //get confirm pin
                    if (password.length() == 4) {
                        setViews(0);

                        confirmPassword = password;
                        if (confirmPassword.equals(newPassword)) {
                            confirmPassDone = true;
                            preferences.addString(MyAnnotations.PIN, confirmPassword);
                            textView.setText("PIN changed");
                        } else {
                            if (preferences.getBoolean(MyAnnotations.VIB_FEEDBACK, false)) {
                                utils.setViberate();
                            }

                            textView.setTextColor(Color.RED);
                            textView.setText("Wrong confirmation PIN");
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                textView.setTextColor(ContextCompat.getColor(
                                        ActivityCreatePin.this,
                                        R.color.white_opacity_90));
                                textView.setText("Create PIN");
                            }, 1500);
                        }
                        oldPassDone = false;
                        newPassDone = false;
                        confirmPassDone = false;
                        this.password = "";
                    }
                }

            }
        }
        else if (whatToDo.equals(MyAnnotations.CREATE_PIN)) {
            //create pin
            if (!firstEnteredPass) {
                if (password.length() == 4) {
                    setViews(0);
                    firstEnteredPass = true;
                    firstEnterPassword = password;
                    this.password = "";
                    textView.setText("Enter again to confirm");
                }
            } else {
                if (password.length() == 4) {
                    setViews(0);
                    firstEnteredPass = false;
                    confirmPassword = password;
                    this.password = "";
                    if (firstEnterPassword.equals(confirmPassword)) {
                        preferences.addBoolean(MyAnnotations.IS_LOCKED, true);
                        preferences.addString(MyAnnotations.PIN, confirmPassword);
                        preferences.addBoolean(MyAnnotations.PIN_IS_CREATED, true);
                        preferences.addBoolean(MyAnnotations.PATTERN_ENABLED, false);
                        Toast.makeText(this, "PIN created", Toast.LENGTH_SHORT).show();
                        if (preferences.getString(MyAnnotations.SECURITY_Q, "").isEmpty()) {
                            startActivity(new Intent(this, ActivitySetSecurityQuestion.class)
                                    .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, "")
                                    .putExtra(MyAnnotations.IN_APP, true));
                            finish();

                        } else {
                            startActivity(new Intent(this, AppsLockerActivity.class));

                        }
                        finish();
                    } else {
                        if (preferences.getBoolean(MyAnnotations.VIB_FEEDBACK, false)) {
                            utils.setViberate();
                        }
                        textView.setTextColor(Color.RED);
                        textView.setText("Wrong confirmation PIN");
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            textView.setTextColor(ContextCompat.getColor(
                                    ActivityCreatePin.this,
                                    R.color.white_opacity_90));
                            textView.setText("Create a PIN code");
                        }, 1500);
                    }

                }
            }

        } else {
            //enter pin
            if (password.length() == 4) {
                setViews(0);
                if (preferences.getString(MyAnnotations.PIN, "").equals(password)) {
                    if (preferences.getString(MyAnnotations.SECURITY_Q, "").isEmpty()) {
                        startActivity(new Intent(this, ActivitySetSecurityQuestion.class)
                                .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, "")
                                .putExtra(MyAnnotations.IN_APP, true));
                        finish();

                    } else {
                        startActivity(new Intent(this, AppsLockerActivity.class));
                    }
                    finish();
                } else {
                    i++;
                    this.password = "";
                    if (preferences.getBoolean(MyAnnotations.VIB_FEEDBACK, false)) {
                        utils.setViberate();
                    }
                    if (i >= 5) {
                        //BLOCK USER FOR SOMETIME
                        textView_pin_not_matched.setText("Please wait util time finished");
                        view_disable.setVisibility(View.VISIBLE);
                        new CountDownTimer(30000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                textView_timer.setText(String.valueOf(millisUntilFinished / 1000));
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                textView_timer.setText("");
                                textView_pin_not_matched.setText("Try again");
                                i = 0;
                                view_disable.setVisibility(View.GONE);
//                                    textView_patter_not_matched.setText("try again");
                            }

                        }.start();
                    } else {
                        this.password = "";
                        textView_pin_not_matched.setTextColor(Color.RED);
                        textView_pin_not_matched.setText("Wrong PIN is tried");
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            textView_pin_not_matched.setTextColor(ContextCompat.getColor(
                                    ActivityCreatePin.this,
                                    R.color.white_opacity_90));
                            textView_pin_not_matched.setText("Try again");
                        }, 1500);
                    }

                }
            }
            view_disable.setOnClickListener(view -> {
                // do nothing but important to disable click pin pad
            });


        }

    }


    public void setViews(int length) {
        switch (length) {
            case 0:
                view_1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_empty_circle));
                view_2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_empty_circle));
                view_3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_empty_circle));
                view_4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_empty_circle));
                break;
            case 1:
                view_1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                break;
            case 2:
                view_1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                break;
            case 3:
                view_1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                break;
            case 4:
                view_1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                break;

        }
    }


    @Override
    public void data(String data) {
        if (data.equals("ok")) {
            startActivity(new Intent(this, AppsLockerActivity.class));
            finish();
        }

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

        biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
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
                        startActivity(new Intent(ActivityCreatePin.this, AppsLockerActivity.class));
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


}
