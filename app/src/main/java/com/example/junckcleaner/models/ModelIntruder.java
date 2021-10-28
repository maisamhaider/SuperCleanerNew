package com.example.junckcleaner.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.junckcleaner.annotations.MyAnnotations;

@Entity(tableName = MyAnnotations.table_intruders)
public class ModelIntruder {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "app_name")
    private String app_name;
    @ColumnInfo(name = "imagePath")
    private String imagePath;
    private String attempts;
    private String date;
    private String time;


    public ModelIntruder(String app_name,
                         String imagePath,
                         String attempts,
                         String date,
                         String time) {
        this.app_name = app_name;
        this.imagePath = imagePath;
        this.attempts = attempts;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAttempts() {
        return attempts;
    }

    public void setAttempts(String attempts) {
        this.attempts = attempts;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
