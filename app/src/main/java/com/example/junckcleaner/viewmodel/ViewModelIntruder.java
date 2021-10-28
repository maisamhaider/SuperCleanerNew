package com.example.junckcleaner.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.junckcleaner.models.ModelIntruder;
import com.example.junckcleaner.repository.RepositoryIntruders;

import java.util.List;

public class ViewModelIntruder extends AndroidViewModel {
    private final RepositoryIntruders repositoryIntruders;
    LiveData<List<ModelIntruder>> intruders;


    public ViewModelIntruder(@NonNull Application application) {
        super(application);
        repositoryIntruders = new RepositoryIntruders(application);

    }


    public LiveData<List<ModelIntruder>> getIntruders() {
        return repositoryIntruders.getIntruders();
    }

    public LiveData<ModelIntruder> getLastInserted() {
        return repositoryIntruders.lastInserted();
    }

    public void insertIntruder(ModelIntruder intruder) {
        repositoryIntruders.insertIntruder(intruder);
    }

    public void updateIntruder(ModelIntruder intruder) {
        repositoryIntruders.updateIntruder(intruder);
    }

    public void deleteIntruder(ModelIntruder intruder) {
        repositoryIntruders.deleteIntruder(intruder);
    }

    public void deleteIntruderByPath(String intruder) {
        repositoryIntruders.deleteIntruderByPath(intruder);
    }

    public void deleteAllApps() {
        repositoryIntruders.deleteAllIntruders();
    }


}