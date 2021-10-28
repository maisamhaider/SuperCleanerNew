package com.example.junckcleaner.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.models.AppModel;

import java.util.List;

@Dao // Data access object
public interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppModel appModel);

    @Update
    void update(AppModel appModel);

    @Delete
    void delete(AppModel appModel);


    @Query(MyAnnotations.DELETE_FROM_table_apps)
    void deleteAll();
    @Query("DELETE FROM table_apps WHERE app_packages = :pack")
    void deleteWithName(String pack);

    @Query(MyAnnotations.SELECT_ALL_FROM_table_apps)
    LiveData<List<AppModel>> getAllApp();

    @Query("SELECT app_packages FROM table_apps")
    LiveData<List<String>> getAllAppStrings();




}


