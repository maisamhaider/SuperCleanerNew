package com.example.junckcleaner.views.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.junckcleaner.R;

public class ActivityForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        findViewById(R.id.imageView_back).setOnClickListener(v -> {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));
            finish();
        });
        findViewById(R.id.textView_send).setOnClickListener(v -> {
            finish();
        });
    }
}