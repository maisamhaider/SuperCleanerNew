package com.example.junckcleaner.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.junckcleaner.models.AppModel;
import com.example.junckcleaner.models.ModelIntruder;


@Database(entities = {AppModel.class, ModelIntruder.class}, version = 4)
public abstract class MyRoom extends RoomDatabase {
    private static MyRoom instance;

    public abstract AppDao appDao();

    public abstract DaoIntruder daoIntruder();


    public static synchronized MyRoom getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, MyRoom.class, "DATABASE_ROOM")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
