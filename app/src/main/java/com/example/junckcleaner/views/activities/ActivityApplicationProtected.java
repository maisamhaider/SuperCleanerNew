package com.example.junckcleaner.views.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.junckcleaner.R;
import com.example.junckcleaner.adapters.AdapterProtectedApps;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;
import com.example.junckcleaner.viewmodel.ViewModelApps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ActivityApplicationProtected extends AppCompatActivity implements TrueFalse {
    RecyclerView recyclerView;
    TextView textView_save, textView_no_data_found;
    ViewModelApps viewModelApps;

    Utils utils;
    Set<String> hashSet = new HashSet<>();
    AppPreferences preferences;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_protected);
        utils = new Utils(this);
        preferences = new AppPreferences(this);
        recyclerView = findViewById(R.id.recyclerView);
        textView_save = findViewById(R.id.textView_save);
        textView_no_data_found = findViewById(R.id.textView_no_data_found);
        findViewById(R.id.imageView_back).setOnClickListener(v -> finish());
        viewModelApps = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ViewModelApps.class);


    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModelApps = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ViewModelApps.class);

        AdapterProtectedApps adapterProtectedApps = new AdapterProtectedApps(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterProtectedApps);
        adapterProtectedApps.setListener(this);

        textView_save.setBackgroundResource(R.drawable.ripple_main_color_curved);
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            if (preferences.getStringSet(MyAnnotations.PROTECTED_APPS) != null) {
                hashSet.addAll(preferences.getStringSet(MyAnnotations.PROTECTED_APPS));
            }

            if (preferences.getStringSet(MyAnnotations.ALL_APPS) != null &&
                    !preferences.getStringSet(MyAnnotations.ALL_APPS).isEmpty()) {
                list = new ArrayList<>();
                list.addAll(preferences.getStringSet(MyAnnotations.ALL_APPS));
            }
            handler.post(() -> {
                if (list.isEmpty()) {
                    Toast.makeText(ActivityApplicationProtected.this,
                            "No App found", Toast.LENGTH_SHORT).show();
                    textView_no_data_found.setVisibility(View.VISIBLE);
                } else {
                    textView_no_data_found.setVisibility(View.GONE);
                    adapterProtectedApps.setList(hashSet);
                    adapterProtectedApps.submitList(list);
                }

            });
        });


        textView_save.setOnClickListener(view -> {
            // Room has data

            preferences.adStringSet(MyAnnotations.PROTECTED_APPS, hashSet);

        });

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    public void isTrue(boolean isTrue) {
//        textView_save.setEnabled(isTrue);
//        if (isTrue) {
//            textView_save.setBackgroundResource(R.drawable.ripple_main_color_curved);
//        } else {
//            textView_save.setBackgroundResource(R.drawable.shape_disable_curved_dark);
//        }
    }
}