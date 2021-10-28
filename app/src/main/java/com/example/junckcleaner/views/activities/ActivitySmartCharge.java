package com.example.junckcleaner.views.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.services.ServiceSmartCharge;

public class ActivitySmartCharge extends AppCompatActivity {

    ImageView imageView_main_on_off_switch,
            imageView_wifi_switch,
            imageView_brightness_switch,
            imageView_bluetooth_switch,
            imageView_Synchronized_switch,
            imageView_charging_finish_switch;

    TextView textView_wifi_on_off,
            textView_brightness_mode,
            textView_bluetooth_on_off,
            textView_sync_mode;

    AppPreferences preferences;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_charge);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_third));
        preferences = new AppPreferences(this);

        imageView_main_on_off_switch = findViewById(R.id.imageView_main_on_off_switch);
        imageView_wifi_switch = findViewById(R.id.imageView_wifi_switch);
        imageView_brightness_switch = findViewById(R.id.imageView_brightness_switch);
        imageView_bluetooth_switch = findViewById(R.id.imageView_bluetooth_switch);
        imageView_Synchronized_switch = findViewById(R.id.imageView_Synchronized_switch);
        imageView_charging_finish_switch = findViewById(R.id.imageView_charging_finish_switch);

        textView_wifi_on_off = findViewById(R.id.textView_wifi_on_off);
        textView_brightness_mode = findViewById(R.id.textView_brightness_mode);
        textView_bluetooth_on_off = findViewById(R.id.textView_bluetooth_on_off);
        textView_sync_mode = findViewById(R.id.textView_sync_mode);

        findViewById(R.id.imageView_back).setOnClickListener(v -> {
            finish();
        });


        imageView_main_on_off_switch.setOnClickListener(v -> {
            startServiceSmart();
            switchesOnOff(!preferences.getBoolean(MyAnnotations.SMART_CHARGE_ENABLED, false));

        });
        imageView_wifi_switch.setOnClickListener(v -> {
            if (preferences.getBoolean(MyAnnotations.SMART_CHARGE_ENABLED, false)) {
                if (preferences.getBoolean(MyAnnotations.WIFI_SWITCH, false)) {
                    textView_wifi_on_off.setText("On");
                    preferences.addBoolean(MyAnnotations.WIFI_SWITCH, false);
                    imageView_wifi_switch.setImageResource(R.drawable.ic_toggle_off);
                    if (!isOneSwitchOn()) {
                        switchesOnOff(false);

                    }
                } else {
                    textView_wifi_on_off.setText("Off");
                    preferences.addBoolean(MyAnnotations.WIFI_SWITCH, true);
                    imageView_wifi_switch.setImageResource(R.drawable.ic_toggle_on);
                }
            }

        });
        imageView_brightness_switch.setOnClickListener(v -> {
            if (preferences.getBoolean(MyAnnotations.SMART_CHARGE_ENABLED, false)) {
                if (preferences.getBoolean(MyAnnotations.BRIGHTNESS_SWITCH, false)) {
                    preferences.addBoolean(MyAnnotations.BRIGHTNESS_SWITCH, false);
                    textView_brightness_mode.setText("Auto");
                    imageView_brightness_switch.setImageResource(R.drawable.ic_toggle_off);
                    if (!isOneSwitchOn()) {
                        switchesOnOff(false);

                    }
                } else {
                    preferences.addBoolean(MyAnnotations.BRIGHTNESS_SWITCH, true);
                    textView_brightness_mode.setText("Manual");
                    imageView_brightness_switch.setImageResource(R.drawable.ic_toggle_on);
                }
            }

        });
        imageView_bluetooth_switch.setOnClickListener(v -> {
            if (preferences.getBoolean(MyAnnotations.SMART_CHARGE_ENABLED, false)) {
                if (preferences.getBoolean(MyAnnotations.BLUETOOTH_SWITCH, false)) {
                    preferences.addBoolean(MyAnnotations.BLUETOOTH_SWITCH, false);
                    textView_bluetooth_on_off.setText("On");
                    imageView_bluetooth_switch.setImageResource(R.drawable.ic_toggle_off);
                    if (!isOneSwitchOn()) {
                        switchesOnOff(false);

                    }
                } else {
                    textView_bluetooth_on_off.setText("Off");
                    preferences.addBoolean(MyAnnotations.BLUETOOTH_SWITCH, true);
                    imageView_bluetooth_switch.setImageResource(R.drawable.ic_toggle_on);
                }
            }

        });
        imageView_Synchronized_switch.setOnClickListener(v -> {
            if (preferences.getBoolean(MyAnnotations.SMART_CHARGE_ENABLED, false)) {
                if (preferences.getBoolean(MyAnnotations.SYNCHRONIZED_SWITCH, false)) {
                    preferences.addBoolean(MyAnnotations.SYNCHRONIZED_SWITCH, false);
                    textView_sync_mode.setText("Auto");
                    imageView_Synchronized_switch.setImageResource(R.drawable.ic_toggle_off);
                    if (!isOneSwitchOn()) {
                        switchesOnOff(false);

                    }
                } else {
                    textView_sync_mode.setText("Manual");
                    preferences.addBoolean(MyAnnotations.SYNCHRONIZED_SWITCH, true);
                    imageView_Synchronized_switch.setImageResource(R.drawable.ic_toggle_on);
                }
            }
        });
        imageView_charging_finish_switch.setOnClickListener(v -> {

            if (preferences.getBoolean(MyAnnotations.CHARGING_FINISHED_SWITCH, false)) {
                preferences.addBoolean(MyAnnotations.CHARGING_FINISHED_SWITCH, false);
                imageView_charging_finish_switch.setImageResource(R.drawable.ic_toggle_off);
                if (!preferences.getBoolean(MyAnnotations.SMART_CHARGE_ENABLED, false)) {
                    stopServiceSmart();
                }

            } else {
                startServiceSmart();
                preferences.addBoolean(MyAnnotations.CHARGING_FINISHED_SWITCH, true);
                imageView_charging_finish_switch.setImageResource(R.drawable.ic_toggle_on);
            }
        });


    }


    public void switchesOnOff(boolean on) {
        if (on) {
            startServiceSmart();
            imageView_main_on_off_switch.setImageResource(R.drawable.ic_toggle_on);
            imageView_wifi_switch.setImageResource(R.drawable.ic_toggle_on);
            imageView_brightness_switch.setImageResource(R.drawable.ic_toggle_on);
            imageView_bluetooth_switch.setImageResource(R.drawable.ic_toggle_on);
            imageView_Synchronized_switch.setImageResource(R.drawable.ic_toggle_on);

            textView_wifi_on_off.setText("Off");
            textView_brightness_mode.setText("Manual");
            textView_bluetooth_on_off.setText("Off");
            textView_sync_mode.setText("Manual");

            preferences.addBoolean(MyAnnotations.SMART_CHARGE_ENABLED, true);
            preferences.addBoolean(MyAnnotations.WIFI_SWITCH, true);
            preferences.addBoolean(MyAnnotations.BRIGHTNESS_SWITCH, true);
            preferences.addBoolean(MyAnnotations.BLUETOOTH_SWITCH, true);
            preferences.addBoolean(MyAnnotations.SYNCHRONIZED_SWITCH, true);


        } else {
            imageView_main_on_off_switch.setImageResource(R.drawable.ic_toggle_off);
            imageView_wifi_switch.setImageResource(R.drawable.ic_toggle_off);
            imageView_brightness_switch.setImageResource(R.drawable.ic_toggle_off);
            imageView_bluetooth_switch.setImageResource(R.drawable.ic_toggle_off);
            imageView_Synchronized_switch.setImageResource(R.drawable.ic_toggle_off);

            textView_wifi_on_off.setText("On");
            textView_brightness_mode.setText("Auto");
            textView_bluetooth_on_off.setText("On");
            textView_sync_mode.setText("Auto");

            preferences.addBoolean(MyAnnotations.SMART_CHARGE_ENABLED, false);
            preferences.addBoolean(MyAnnotations.WIFI_SWITCH, false);
            preferences.addBoolean(MyAnnotations.BRIGHTNESS_SWITCH, false);
            preferences.addBoolean(MyAnnotations.BLUETOOTH_SWITCH, false);
            preferences.addBoolean(MyAnnotations.SYNCHRONIZED_SWITCH, false);

            if (!preferences.getBoolean(MyAnnotations.CHARGING_FINISHED_SWITCH, false)) {
                stopServiceSmart();
            }
        }
    }

    public boolean isOneSwitchOn() {
        return preferences.getBoolean(MyAnnotations.WIFI_SWITCH, false) ||
                preferences.getBoolean(MyAnnotations.BRIGHTNESS_SWITCH, false) ||
                preferences.getBoolean(MyAnnotations.BLUETOOTH_SWITCH, false) ||
                preferences.getBoolean(MyAnnotations.SYNCHRONIZED_SWITCH, false);
    }

    public boolean isOneInSmartAndFinishedOn() {
        return preferences.getBoolean(MyAnnotations.SMART_CHARGE_ENABLED, false) ||
                preferences.getBoolean(MyAnnotations.CHARGING_FINISHED_SWITCH, false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (preferences.getBoolean(MyAnnotations.SMART_CHARGE_ENABLED, false)) {
            imageView_main_on_off_switch.setImageResource(R.drawable.ic_toggle_on);

        } else {
            imageView_main_on_off_switch.setImageResource(R.drawable.ic_toggle_off);

        }
        if (preferences.getBoolean(MyAnnotations.WIFI_SWITCH, false)) {
            imageView_wifi_switch.setImageResource(R.drawable.ic_toggle_on);
            textView_wifi_on_off.setText("Off");

        } else {
            imageView_wifi_switch.setImageResource(R.drawable.ic_toggle_off);
            textView_wifi_on_off.setText("On");

        }
        if (preferences.getBoolean(MyAnnotations.BRIGHTNESS_SWITCH, false)) {
            imageView_brightness_switch.setImageResource(R.drawable.ic_toggle_on);
            textView_brightness_mode.setText("Manual");
        } else {
            imageView_brightness_switch.setImageResource(R.drawable.ic_toggle_off);
            textView_brightness_mode.setText("Auto");
        }
        if (preferences.getBoolean(MyAnnotations.BLUETOOTH_SWITCH, false)) {
            imageView_bluetooth_switch.setImageResource(R.drawable.ic_toggle_on);
            textView_bluetooth_on_off.setText("Off");
        } else {
            imageView_bluetooth_switch.setImageResource(R.drawable.ic_toggle_off);
            textView_bluetooth_on_off.setText("On");

        }
        if (preferences.getBoolean(MyAnnotations.SYNCHRONIZED_SWITCH, false)) {
            imageView_Synchronized_switch.setImageResource(R.drawable.ic_toggle_on);
            textView_sync_mode.setText("Manual");

        } else {
            imageView_Synchronized_switch.setImageResource(R.drawable.ic_toggle_off);
            textView_sync_mode.setText("Auto");
        }

// finish
        if (preferences.getBoolean(MyAnnotations.CHARGING_FINISHED_SWITCH, false)) {
            imageView_charging_finish_switch.setImageResource(R.drawable.ic_toggle_on);
        } else {
            imageView_charging_finish_switch.setImageResource(R.drawable.ic_toggle_off);
        }


        if (isOneInSmartAndFinishedOn()) {
            startServiceSmart();
        } else {
            stopServiceSmart();
        }
    }

    public void startServiceSmart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ServiceSmartCharge.class));
        } else {
            startService(new Intent(this, ServiceSmartCharge.class));
        }
    }

    public void stopServiceSmart() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            notificationManager.cancel(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopService(new Intent(this, ServiceSmartCharge.class));

    }


}