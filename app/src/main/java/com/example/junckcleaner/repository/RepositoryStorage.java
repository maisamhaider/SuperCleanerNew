package com.example.junckcleaner.repository;

import android.app.Application;
import android.os.Environment;

import androidx.lifecycle.LiveData;

import com.example.junckcleaner.models.FileModel;
import com.example.junckcleaner.utils.Utils;

import java.util.List;

public class RepositoryStorage {

    LiveData<List<FileModel>> apks;

    Utils utils;
    String path;

    public RepositoryStorage(Application application) {
        utils = new Utils(application.getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            path = Environment.getRootDirectory().getAbsolutePath();

        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        apks = utils.getFilesApks(path);
    }

    public LiveData<List<FileModel>> getApks() {
        return apks;

    }
}
