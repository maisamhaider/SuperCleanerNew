package com.example.junckcleaner.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.junckcleaner.db.AppDao;
import com.example.junckcleaner.db.MyRoom;
import com.example.junckcleaner.models.AppModel;
import com.example.junckcleaner.utils.Utils;

import java.util.List;

public class RepositoryApps {
    LiveData<List<String>> userApps;
    LiveData<List<String>> allApps;
    LiveData<List<String>> systemApps;


    MyRoom myRoom;
    AppDao appDao;
    Application application;

    public RepositoryApps(Application application) {
        myRoom = MyRoom.getInstance(application);
        appDao = myRoom.appDao();
        this.application = application;
        allApps = new Utils(application.getApplicationContext()).getSysOrInstalledAppsList(true, true,false);
        systemApps = new Utils(application.getApplicationContext()).getSysOrInstalledAppsList(true, false,false);
        userApps = new Utils(application.getApplicationContext()).getSysOrInstalledAppsList(false, false,false);

    }

    public LiveData<List<String>> getAllApps() {
        return allApps;
    }

    public LiveData<List<String>> getAllApps2(boolean takeThisApp) {
        return new Utils(application.getApplicationContext()).getSysOrInstalledAppsList(true, true,takeThisApp);
    }

    public LiveData<List<String>> getAllApps3() {
        return new Utils(application.getApplicationContext()).activeApps();
    }

    public LiveData<List<String>> getSystemApps() {
        return systemApps;
    }

    public LiveData<List<String>> getUserApps() {
        return userApps;
    }

    public void insertApp(AppModel apps) {
        new InsertAppThread(apps, appDao);
    }

    public void updateApp(AppModel apps) {
        new UpdateAppsThread(apps, appDao);
    }

    public void deleteApp(AppModel apps) {
        new DeleteAppThread(apps, appDao);
    }

    public void deleteAllApps() {

        new DeleteAllAppsThread(appDao);

    }

    public void deleteWithNameApps(String app) {

        new DeleteAppWIthNameThread(app, appDao);
    }

    public LiveData<List<AppModel>> appsFromRoom() {
        return appDao.getAllApp();
    }

    public LiveData<List<String>> appsFromRoomStrings() {
        return appDao.getAllAppStrings();
    }


    static class InsertAppThread implements Runnable {
        // to stop the thread
        private boolean exit;
        Thread t;
        AppModel apps;
        AppDao appDao;

        InsertAppThread(AppModel apps, AppDao appDao) {
            t = new Thread(this);
            exit = false;
            this.apps = apps;
            this.appDao = appDao;
            t.start(); // Starting the thread
        }

        public void run() {
            appDao.insert(apps);

        }

        // for stopping the thread
        public void stop() {
            exit = true;
        }
    }


    private static class UpdateAppsThread implements Runnable {
        Thread thread;
        AppModel apps;
        AppDao appDao;

        public UpdateAppsThread(AppModel apps, AppDao appDao) {
            thread = new Thread(this);
            this.apps = apps;
            this.appDao = appDao;
            thread.start();
        }

        @Override
        public void run() {
            appDao.update(apps);
        }
    }

    private static class DeleteAppThread implements Runnable {
        Thread thread;
        AppModel apps;
        AppDao appDao;

        public DeleteAppThread(AppModel apps, AppDao appDao) {
            thread = new Thread(this);
            this.apps = apps;
            this.appDao = appDao;
            thread.start();
        }

        @Override
        public void run() {
            appDao.delete(apps);
        }
    }

    private static class DeleteAppWIthNameThread implements Runnable {
        Thread thread;
        String apps;
        AppDao appDao;

        public DeleteAppWIthNameThread(String apps, AppDao appDao) {
            thread = new Thread(this);
            this.apps = apps;
            this.appDao = appDao;
            thread.start();
        }

        @Override
        public void run() {
            appDao.deleteWithName(apps);
        }
    }

    private class DeleteAllAppsThread implements Runnable {
        Thread thread;
        AppDao appDao;

        public DeleteAllAppsThread(AppDao appDao) {
            thread = new Thread(this);
            this.appDao = appDao;
            thread.start();
        }

        @Override
        public void run() {
            appDao.deleteAll();
        }
    }
}