package com.example.junckcleaner.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.junckcleaner.annotations.MyAnnotations;

@Entity(tableName = MyAnnotations.new_alert_table, indices =
        {@Index(value = {"app_packages"}, unique = true)})
public class ModelNewAlert {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "app_names")
    private String app_name;
    @ColumnInfo(name = "app_packages")
    private String app_package;

    public ModelNewAlert(String app_name, String app_package) {
        this.app_name = app_name;
        this.app_package = app_package;
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

    public String getApp_package() {
        return app_package;
    }

    public void setApp_package(String app_package) {
        this.app_package = app_package;
    }
}
