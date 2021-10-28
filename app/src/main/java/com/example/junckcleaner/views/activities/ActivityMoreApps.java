package com.example.junckcleaner.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.junckcleaner.R;
import com.example.junckcleaner.adapters.AdapterMoreApps;
import com.example.junckcleaner.models.ModelMoreApps;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivityMoreApps extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    TextView textViewAppsNotFond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_apps);
        recyclerView = findViewById(R.id.recyclerView);
        textViewAppsNotFond = findViewById(R.id.textViewAppsNotFond);

        rootNode = FirebaseDatabase.getInstance();
        databaseReference = rootNode.getReference().child("apps");
        List<String> list = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String child = snap.getKey();
                    list.add(child);
                }
                if (!list.isEmpty()) {
                    getData(list);
                } else {
                    textViewAppsNotFond.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivityMoreApps.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
//        saveMoreAppToDevice("app_1","logo.png");

    }


    public void loadRecycler(ArrayList<ModelMoreApps> list) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        AdapterMoreApps moreApps = new AdapterMoreApps(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setAdapter(moreApps);
        recyclerView.setLayoutManager(layoutManager);

        moreApps.submitList(list);

        findViewById(R.id.imageView_back).setOnClickListener(v -> {
            finish();
        });
    }

    public void saveMoreAppToDevice(String folder, String file) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        StorageReference storageReference = firebaseStorage.getReference().child(folder + "/" + file);

        File localFile = null;
         try {

            localFile = new File(getExternalFilesDir("")+file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            // Local temp file has been created
            FileDownloadTask.TaskSnapshot snapshot = taskSnapshot;
        }).addOnFailureListener(exception -> {
            // Handle any errors
            exception.printStackTrace();
        });
    }

    public ArrayList<ModelMoreApps> getData(List<String> list) {
        ArrayList<ModelMoreApps> appsArrayList;
        appsArrayList = new ArrayList<>();
        final ModelMoreApps[] moreApps = {null};
        if (!list.isEmpty()) {
            for (String app : list) {
                databaseReference = rootNode.getReference().child("apps/" + app);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        moreApps[0] = snapshot.getValue(ModelMoreApps.class);
                        appsArrayList.add(moreApps[0]);

                        if (!appsArrayList.isEmpty()) {
                            loadRecycler(appsArrayList);
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ActivityMoreApps.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
        return appsArrayList;
    }
}