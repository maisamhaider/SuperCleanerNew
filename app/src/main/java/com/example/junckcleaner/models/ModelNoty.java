package com.example.junckcleaner.models;

public class ModelNoty {

    private int id;
    private String notyTitle;
    private String notyContent;
    private String appIcon;

    public ModelNoty(String notyTitle, String notyContent, String appIcon) {
        this.notyTitle = notyTitle;
        this.notyContent = notyContent;
        this.appIcon = appIcon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotyTitle() {
        return notyTitle;
    }

    public void setNotyTitle(String notyTitle) {
        this.notyTitle = notyTitle;
    }

    public String getNotyContent() {
        return notyContent;
    }

    public void setNotyContent(String notyContent) {
        this.notyContent = notyContent;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }
}
