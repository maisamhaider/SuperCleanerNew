package com.example.junckcleaner.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.junckcleaner.models.AppModel;
import com.example.junckcleaner.repository.RepositoryApps;

import java.util.List;

public class ViewModelApps extends AndroidViewModel {
    private RepositoryApps repositoryApps;
    LiveData<List<String>> userApps;
    LiveData<List<String>> allApps;
    LiveData<List<String>> systemApps;

    public ViewModelApps(@NonNull Application application) {
        super(application);
        repositoryApps = new RepositoryApps(application);
        allApps = repositoryApps.getAllApps();
        systemApps = repositoryApps.getSystemApps();
        userApps = repositoryApps.getUserApps();
    }

//    public void insert(Note note) {
//        repositoryNote.insert(note);
//    }
//
//    public void update(Note note) {
//        repositoryNote.update(note);
//    }
//
//    public void delete(String app) {
//        repositoryNote.delete(note);
//    }
//
//    public void deleteAllNotes() {
//        repositoryNote.deleteAllNotes();
//    }

    public LiveData<List<String>> getAllApps() {
        return allApps;
    }

    public LiveData<List<String>> getAllApps2(boolean takeThisApp) {
        return repositoryApps.getAllApps2(takeThisApp);

    }

    public LiveData<List<String>> getAllApps3() {
        return repositoryApps.getAllApps3();

    }


    public LiveData<List<String>> getSystemApps() {
        return systemApps;
    }

    public LiveData<List<String>> getUserApps() {
        return userApps;
    }

    //Room functions
    public void insertApp(AppModel apps) {
        repositoryApps.insertApp(apps);
    }

    public void updateApp(AppModel apps) {
        repositoryApps.updateApp(apps);
    }

    public void deleteApp(AppModel apps) {
        repositoryApps.deleteApp(apps);
    }

    public void deleteAllApps() {
        repositoryApps.deleteAllApps();
    }

    public void deleteWithNameApps(String app) {
        repositoryApps.deleteWithNameApps(app);
    }

    public LiveData<List<AppModel>> appsFromRoom() {
        return repositoryApps.appsFromRoom();
    }

    public LiveData<List<String>> appsFromRoomStrings() {
        return repositoryApps.appsFromRoomStrings();
    }

}