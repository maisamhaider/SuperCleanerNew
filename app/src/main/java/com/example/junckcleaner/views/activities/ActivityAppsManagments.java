package com.example.junckcleaner.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.junckcleaner.R;
import com.example.junckcleaner.adapters.AdapterUninstallApps;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.models.AppModel;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;
import com.example.junckcleaner.viewmodel.ViewModelApps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ActivityAppsManagments extends AppCompatActivity implements TrueFalse {
    RecyclerView recyclerView;
    TextView textView_install;
    ViewModelApps viewModelApps;

    Utils utils;
    AdapterUninstallApps uninstallApps;
    List<Integer> poses = new ArrayList<>();
    int deleteListSize = 0;
    boolean oneRun = false;
    Set<String> hashSet = new HashSet<>();

    List<String> list;
    AppPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_managments);
        utils = new Utils(this);
        preferences = new AppPreferences(this);
        recyclerView = findViewById(R.id.recyclerView);
        textView_install = findViewById(R.id.textView_install);
        textView_install.setEnabled(false);
        findViewById(R.id.imageView_back).setOnClickListener(v -> finish());


        textView_install.setOnClickListener(view -> {
            if (!uninstallApps.getList().isEmpty()) {
                deleteListSize = uninstallApps.getPoses().size();
                poses = uninstallApps.getPoses();
                for (String app : uninstallApps.getList()) {
                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:" + app));
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    intent.putExtra("app", app);
                    openSomeActivityForResult(intent);
                }

            }

        });

    }


    ActivityResultLauncher<Intent> someActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                list.remove((int) poses.get(deleteListSize - 1));
                                loadRecyclerView();

                                deleteListSize--;
                            }
                        }

                    });

    public void openSomeActivityForResult(Intent intent) {

        someActivityResultLauncher.launch(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void isTrue(boolean isTrue) {
        textView_install.setEnabled(isTrue);
        if (isTrue) {
            textView_install.setBackgroundResource(R.drawable.ripple_main_color_curved);
        } else {
            textView_install.setBackgroundResource(R.drawable.shape_disable_curved_dark);
        }
    }

    public void loadRecyclerView() {
        viewModelApps = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ViewModelApps.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        Executor executor = Executors.newCachedThreadPool();
        Handler handler = new Handler(Looper.getMainLooper());


        executor.execute(() -> {
            if (preferences.getStringSet(MyAnnotations.PROTECTED_APPS) != null) {
                hashSet.addAll(preferences.getStringSet(MyAnnotations.PROTECTED_APPS));
            }
            if (preferences.getStringSet(MyAnnotations.USER_APPS) != null &&
                    !preferences.getStringSet(MyAnnotations.USER_APPS).isEmpty()) {
                list = new ArrayList<>();
                list.addAll(preferences.getStringSet(MyAnnotations.USER_APPS));
            }
            handler.post(() -> {
                if (list.isEmpty()) {
                    Toast.makeText(ActivityAppsManagments.this, "No App found", Toast.LENGTH_SHORT).show();

                } else {
                    uninstallApps = new AdapterUninstallApps(ActivityAppsManagments.this);
                    uninstallApps.setListener(ActivityAppsManagments.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(uninstallApps);
                    uninstallApps.setApp(list);
                }


            });
        });


    }

}