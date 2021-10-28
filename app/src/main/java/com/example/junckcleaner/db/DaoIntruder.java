package com.example.junckcleaner.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.junckcleaner.models.ModelIntruder;

import java.util.List;

@Dao // Data access object
public interface DaoIntruder {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ModelIntruder intruder);

    @Update
    void update(ModelIntruder intruder);

    @Delete
    void delete(ModelIntruder intruder);
    @Query("DELETE FROM table_intruders WHERE imagePath = :path")
    void deleteByName(String path);


    @Query("DELETE FROM table_intruders")
    void deleteIntruders();

    @Query("SELECT * FROM table_intruders")
    LiveData<List<ModelIntruder>> getAllIntruders();

    @Query("SELECT * FROM table_intruders ORDER BY id DESC LIMIT 1")
    LiveData<ModelIntruder> lastInserted();
}


