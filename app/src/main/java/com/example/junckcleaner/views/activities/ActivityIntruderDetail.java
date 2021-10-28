package com.example.junckcleaner.views.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.utils.Utils;
import com.example.junckcleaner.viewmodel.ViewModelIntruder;

import java.io.File;

public class ActivityIntruderDetail extends AppCompatActivity {
    ViewModelIntruder viewModelIntruder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activityintrudor_detail);
        findViewById(R.id.imageView_back).setOnClickListener(v -> {
            finish();
        });
        viewModelIntruder = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ViewModelIntruder.class);

        String INTRUDER_APP = getIntent().getStringExtra(MyAnnotations.INTRUDER_APP);
        String INTRUDER_PATH = getIntent().getStringExtra(MyAnnotations.INTRUDER_PATH);
        String INTRUDER_DATE = getIntent().getStringExtra(MyAnnotations.INTRUDER_DATE);
        String INTRUDER_TIME = getIntent().getStringExtra(MyAnnotations.INTRUDER_TIME);
        String INTRUDER_ATTEMPTS = getIntent().getStringExtra(MyAnnotations.INTRUDER_ATTEMPTS);

        ImageView imageView_intruder = findViewById(R.id.imageView_intruder);
        TextView textView_appName = findViewById(R.id.textView_appName);
        TextView textView_try_count = findViewById(R.id.textView_try_count);
        TextView textView_date = findViewById(R.id.textView_date);
        TextView textView_time = findViewById(R.id.textView_time);
        TextView textView_install = findViewById(R.id.textView_install);

        Glide.with(this).load(INTRUDER_PATH).into(imageView_intruder);
        textView_appName.setText(new Utils(this).appInfo(INTRUDER_APP, MyAnnotations.APP_NAME));
        textView_date.setText(INTRUDER_DATE);
        textView_time.setText(INTRUDER_TIME);
        textView_try_count.setText(INTRUDER_ATTEMPTS);

        textView_install.setOnClickListener(view -> {
            boolean delete = new File(INTRUDER_PATH).delete();
            if (delete) {
                viewModelIntruder.deleteIntruderByPath(INTRUDER_PATH);
                finish();
            }
        });
    }
}