package com.example.junckcleaner.views.activities;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivityDND extends AppCompatActivity {


    ImageView imageVIew_dnd_switch;
    boolean dnd = true;
    AppPreferences preferences;
    TextView textViewStartAt;
    TextView textViewStopAt;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnd);
        preferences = new AppPreferences(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        findViewById(R.id.imageView_back).setOnClickListener(view -> finish());

        imageVIew_dnd_switch = findViewById(R.id.imageVIew_dnd_switch);
        textViewStartAt = findViewById(R.id.textView_start_at);
        textViewStopAt = findViewById(R.id.textView_stop_at);


        if (preferences.getBoolean(MyAnnotations.DND_SWITCH, false)) {
            imageVIew_dnd_switch.setImageResource(R.drawable.ic_toggle_on);

        } else {
            imageVIew_dnd_switch.setImageResource(R.drawable.ic_toggle_off);

        }
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        if (preferences.getLong(MyAnnotations.DND_START_TIME, 0) == 0) {
            calendar1.set(Calendar.HOUR, 20);
            calendar1.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.HOUR, 5);
            calendar2.set(Calendar.MINUTE, 0);
        } else {
            calendar1.setTimeInMillis(preferences.getLong(MyAnnotations.DND_START_TIME, 0));
            calendar2.setTimeInMillis(preferences.getLong(MyAnnotations.DND_END_TIME, 0));
        }
        textViewStartAt.setText(sdf.format(calendar1.getTime()));
        textViewStopAt.setText(sdf.format(calendar2.getTime()));

        imageVIew_dnd_switch.setOnClickListener(v -> {
            if (preferences.getBoolean(MyAnnotations.DND_SWITCH, false)) {
                preferences.addBoolean(MyAnnotations.DND_SWITCH, false);
                imageVIew_dnd_switch.setImageResource(R.drawable.ic_toggle_off);
            } else {
                preferences.addBoolean(MyAnnotations.DND_SWITCH, true);
                imageVIew_dnd_switch.setImageResource(R.drawable.ic_toggle_on);

            }
        });
        textViewStartAt.setOnClickListener(v -> timePicker("start"));
        textViewStopAt.setOnClickListener(v -> timePicker("end"));

    }

    public void timePicker(String whichTime) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            calendar.set(Calendar.HOUR, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);

            String time = simpleDateFormat.format(calendar.getTime());
            if (whichTime.equals("start")) {
                textViewStartAt.setText(time);
                preferences.addLong(MyAnnotations.DND_START_TIME, calendar.getTimeInMillis());

                long start = getTime(simpleDateFormat.format(preferences.getLong(MyAnnotations.DND_START_TIME,
                        0)));
                long end = getTime(simpleDateFormat.format(preferences.getLong(MyAnnotations.DND_END_TIME,
                        0)));
                if (start >= end) {
                    preferences.addLong(MyAnnotations.DND_END_TIME, start + 60000 * 5);
                    textViewStopAt.setText(simpleDateFormat.format(preferences.getLong(MyAnnotations.DND_END_TIME,
                            0)));

                }
            } else {
                long start = getTime(simpleDateFormat.format(preferences.getLong(MyAnnotations.DND_START_TIME,
                        0)));
                preferences.addLong(MyAnnotations.DND_END_TIME, calendar.getTimeInMillis());
                textViewStopAt.setText(time);
                long end = getTime(simpleDateFormat.format(preferences.getLong(MyAnnotations.DND_END_TIME,
                        0)));
                if (start >= end) {
                    preferences.addLong(MyAnnotations.DND_START_TIME, end - 60000 * 5);
                    textViewStartAt.setText(simpleDateFormat.format(preferences.getLong(MyAnnotations.DND_START_TIME,
                            0)));

                }
            }
        }, hour, minute, true);
        mTimePicker.show();

    }

    public long getTime(String string) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        Date date = new Date();
        try {
            date = sdf.parse(string);
            if (date != null) {
                return date.getTime();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }
}