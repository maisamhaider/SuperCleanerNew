package com.example.junckcleaner.views.activities;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.views.fragments.frags.FragmentAllFiles;
import com.example.junckcleaner.views.fragments.frags.FragmentAudio;
import com.example.junckcleaner.views.fragments.frags.FragmentDocuments;
import com.example.junckcleaner.views.fragments.frags.FragmentImages;
import com.example.junckcleaner.views.fragments.frags.FragmentVideos;

public class ActivityDuplicateFilesCommon extends BaseActivity {

    String type = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_comman_layout);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));


        type = getIntent().getStringExtra(MyAnnotations.DATA_TYPE);
        switch (type) {
            case MyAnnotations.ALL_SCAN:
                loadFragment(new FragmentAllFiles());
                break;
            case MyAnnotations.IMAGES:
                loadFragment(new FragmentImages());
                break;
            case MyAnnotations.VIDEOS:
                loadFragment(new FragmentVideos());
                break;
            case MyAnnotations.AUDIOS:
                loadFragment(new FragmentAudio());
                break;
            case MyAnnotations.DOCUMENTS:
                loadFragment(new FragmentDocuments());
                break;
        }


    }

    public void finishCall() {
        finish();
    }


    public void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.duplicateContainer, fragment, null).commit();

    }


}