package com.example.junckcleaner.views.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.junckcleaner.R;
import com.example.junckcleaner.adapters.AdapterIntruders;
import com.example.junckcleaner.viewmodel.ViewModelIntruder;

public class ActivityIntrudersRecord extends AppCompatActivity {

    ViewModelIntruder viewModelIntruder;
    AdapterIntruders adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intruders_record);
        findViewById(R.id.imageView_back).setOnClickListener(v -> {
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModelIntruder = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ViewModelIntruder.class);
        adapter = new AdapterIntruders(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_intruders);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);

        viewModelIntruder.getIntruders().observe(this, modelIntruders -> {

            if (!modelIntruders.isEmpty()) {
                adapter.submitList(modelIntruders);
                findViewById(R.id.cl_no_intruders).setVisibility(View.GONE);
            } else {
                findViewById(R.id.cl_no_intruders).setVisibility(View.VISIBLE);
            }
        });
    }


}