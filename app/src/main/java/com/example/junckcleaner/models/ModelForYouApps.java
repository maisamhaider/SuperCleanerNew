package com.example.junckcleaner.models;

public class ModelForYouApps {

    String name;
    String app_package;
    String logo_url;

    public ModelForYouApps() {
    }

    public ModelForYouApps(String name, String app_package, String logo_url) {
        this.name = name;
        this.app_package = app_package;
        this.logo_url = logo_url;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApp_package() {
        return app_package;
    }

    public void setApp_package(String app_package) {
        this.app_package = app_package;
    }


    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

}
