package com.example.junckcleaner.views.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.junckcleaner.R;
import com.example.junckcleaner.adapters.GameBoosterAdapter2;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ActivitySelectGameBoosterApps extends AppCompatActivity {

    Utils utils;
    Set<String> hashSet = new HashSet<>();
    AppPreferences preferences;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game_booster_apps);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        TextView textView_done = findViewById(R.id.textView_done);
        utils = new Utils(this);
        preferences = new AppPreferences(this);

        findViewById(R.id.imageView_back).setOnClickListener(v -> finish());


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        GameBoosterAdapter2 adapter = new GameBoosterAdapter2(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            if (preferences.getStringSet(MyAnnotations.GAME_BOOSTER_APPS) != null) {
                hashSet.addAll(preferences.getStringSet(MyAnnotations.GAME_BOOSTER_APPS));
            }

            if (preferences.getStringSet(MyAnnotations.ALL_APPS) != null &&
                    !preferences.getStringSet(MyAnnotations.ALL_APPS).isEmpty()) {
                list = new ArrayList<>();
                list.addAll(preferences.getStringSet(MyAnnotations.ALL_APPS));
            }
            handler.post(() -> {
                if (list.isEmpty()) {
                    Toast.makeText(ActivitySelectGameBoosterApps.this,
                            "No App found", Toast.LENGTH_SHORT).show();
//                    textView_no_data_found.setVisibility(View.VISIBLE);
                } else {
                    adapter.setList(hashSet);
                    adapter.submitList(list);
                }

            });
        });


//        Set<String> strings = new HashSet<>();


        textView_done.setOnClickListener(v -> {
            preferences.adStringSet(MyAnnotations.GAME_BOOSTER_APPS, hashSet);
            finish();
        });
    }
}