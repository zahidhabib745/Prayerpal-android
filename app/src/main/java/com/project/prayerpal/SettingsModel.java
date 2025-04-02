package com.project.prayerpal;

public class SettingsModel {

    private String title;
    private String description;
    private boolean isChecked;
    private int settingId;
    private String groupParent;

    public SettingsModel(String title, String description, boolean isChecked, int settingId, String groupParent){

        this.title = title;
        this.description = description;
        this.isChecked = isChecked;
        this.settingId = settingId;
        this.groupParent = groupParent;
    }

    public void setGroupParent(String groupParent){

        this.groupParent = groupParent;
    }

    public String getGroupParent(){

        return groupParent;
    }

    public String getTitle(){

        return title;
    }

    public String getDescription(){

        return description;
    }

    public void setSettingId(int settingId){

        this.settingId = settingId;
    }

    public int getSettingId(){

        return settingId;
    }
}
