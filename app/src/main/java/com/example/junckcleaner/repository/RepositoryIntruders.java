package com.example.junckcleaner.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.junckcleaner.db.DaoIntruder;
import com.example.junckcleaner.db.MyRoom;
import com.example.junckcleaner.models.ModelIntruder;

import java.util.List;

public class RepositoryIntruders {
    LiveData<List<ModelIntruder>> allApps;


    MyRoom myRoom;
    DaoIntruder daoIntruder;

    public RepositoryIntruders(Application application) {
        myRoom = MyRoom.getInstance(application);
        daoIntruder = myRoom.daoIntruder();


    }

    public LiveData<List<ModelIntruder>> getIntruders() {
        return daoIntruder.getAllIntruders();
    }

    public LiveData<ModelIntruder> lastInserted() {


        return daoIntruder.lastInserted();
    }


    public void insertIntruder(ModelIntruder apps) {
        new InsertIntruderThread(apps, daoIntruder);
    }

    public void updateIntruder(ModelIntruder apps) {
        new UpdateIntruderThread(apps, daoIntruder);
    }

    public void deleteIntruder(ModelIntruder apps) {
        new DeleteIntruderThread(apps, daoIntruder);
    }

    public void deleteIntruderByPath(String path) {
        new DeleteIntruderByPathThread(path, daoIntruder);
    }

    public void deleteAllIntruders() {

        new DeleteAllIntrudersThread(daoIntruder);
    }


    static class InsertIntruderThread implements Runnable {
        // to stop the thread
        private boolean exit;
        Thread t;
        ModelIntruder apps;
        DaoIntruder appDao;

        InsertIntruderThread(ModelIntruder apps, DaoIntruder appDao) {
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


    private static class UpdateIntruderThread implements Runnable {
        Thread thread;
        ModelIntruder apps;
        DaoIntruder appDao;

        public UpdateIntruderThread(ModelIntruder apps, DaoIntruder appDao) {
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

    private static class DeleteIntruderThread implements Runnable {
        Thread thread;
        ModelIntruder apps;
        DaoIntruder appDao;

        public DeleteIntruderThread(ModelIntruder apps, DaoIntruder appDao) {
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

    private static class DeleteIntruderByPathThread implements Runnable {
        Thread thread;
        String path;
        DaoIntruder appDao;

        public DeleteIntruderByPathThread(String path, DaoIntruder appDao) {
            thread = new Thread(this);
            this.path = path;
            this.appDao = appDao;
            thread.start();
        }

        @Override
        public void run() {
            appDao.deleteByName(path);
        }
    }

    private class DeleteAllIntrudersThread implements Runnable {
        Thread thread;
        DaoIntruder appDao;

        public DeleteAllIntrudersThread(DaoIntruder appDao) {
            thread = new Thread(this);
            this.appDao = appDao;
            thread.start();
        }

        @Override
        public void run() {
            appDao.deleteIntruders();
        }
    }
}