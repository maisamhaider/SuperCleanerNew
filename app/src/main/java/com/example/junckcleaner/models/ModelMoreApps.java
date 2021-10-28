package com.example.junckcleaner.models;

public class ModelMoreApps {

    String name;
    String app_packege;
    String body;

    public ModelMoreApps() {
    }

    public ModelMoreApps(String name, String app_packege, String body) {
        this.name = name;
        this.app_packege = app_packege;
        this.body = body;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApp_packege() {
        return app_packege;
    }

    public void setApp_packege(String app_packege) {
        this.app_packege = app_packege;
    }

    public String getBody() {
        return body;
    }


    public void setBody(String body) {
        this.body = body;
    }

}
