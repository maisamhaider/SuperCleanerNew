package com.example.junckcleaner.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.junckcleaner.models.FileModel;
import com.example.junckcleaner.repository.RepositoryStorage;

import java.util.List;

public class ViewModelStorage extends AndroidViewModel {
    LiveData<List<FileModel>> apks;
    RepositoryStorage repositoryStorage;


    public ViewModelStorage(Application application) {
        super(application);
        repositoryStorage = new RepositoryStorage(application);
        apks = repositoryStorage.getApks();
    }

    public LiveData<List<FileModel>> getApks() {
        return apks;
    }
}
