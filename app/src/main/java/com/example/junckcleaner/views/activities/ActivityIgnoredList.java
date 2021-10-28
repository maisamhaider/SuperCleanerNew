package com.example.junckcleaner.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.junckcleaner.R;
import com.example.junckcleaner.adapters.AdapterIgnoredApps;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.permissions.MyPermissions;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ActivityIgnoredList extends AppCompatActivity implements TrueFalse {
    RecyclerView recyclerView;
    TextView textView_save, textView_no_data_found;

    Utils utils;
    Set<String> ignoredList = new HashSet<>();
    List<String> list = new ArrayList<>();
    MyPermissions permissions;
    AppPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ignored_list);
        utils = new Utils(this);
        permissions = new MyPermissions(this);
        preferences = new AppPreferences(this);


        recyclerView = findViewById(R.id.recyclerView);
        textView_save = findViewById(R.id.textView_save);
        textView_no_data_found = findViewById(R.id.textView_no_data_found);
        textView_save.setBackgroundResource(R.drawable.ripple_main_color_curved);


        findViewById(R.id.imageView_back).setOnClickListener(v -> finish());


    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(this, "permission Granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "permission Denied", Toast.LENGTH_SHORT).show();
                }
            });

    public void openSomeActivityForResult(Intent intent) {
        someActivityResultLauncher.launch(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();


        AdapterIgnoredApps adapterIgnoredApps = new AdapterIgnoredApps(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterIgnoredApps);
        adapterIgnoredApps.setListener(this);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //ignored apps
            if (preferences.getStringSet(MyAnnotations.IGNORE_APPS) != null) {
                ignoredList.addAll(preferences.getStringSet(MyAnnotations.IGNORE_APPS));

            }
            //all apps
            if (preferences.getStringSet(MyAnnotations.ALL_APPS) != null &&
                    !preferences.getStringSet(MyAnnotations.ALL_APPS).isEmpty()) {
                list = new ArrayList<>();
                list.addAll(preferences.getStringSet(MyAnnotations.ALL_APPS));
            }
            handler.post(() -> {
                if (list.isEmpty()) {
                    Toast.makeText(ActivityIgnoredList.this,
                            "No App found", Toast.LENGTH_SHORT).show();
                    textView_no_data_found.setVisibility(View.VISIBLE);


                } else {
                    textView_no_data_found.setVisibility(View.GONE);
                    adapterIgnoredApps.setList(ignoredList);
                    adapterIgnoredApps.submitList(list);
                }

            });
        });


        textView_save.setOnClickListener(view -> {
            preferences.adStringSet(MyAnnotations.IGNORE_APPS, ignoredList);
        });

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