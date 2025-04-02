package com.project.prayerpal;

import android.graphics.Bitmap;

public class PrayerModel {

    private String prayerName;
    private Bitmap prayerImage;
    private String prayerTime;

    public PrayerModel(String prayerName, String prayerTime, Bitmap prayerImage){

        this.prayerName = prayerName;
        this.prayerTime = prayerTime;
        this.prayerImage = prayerImage;
    }


    public void setPrayerName(String prayerName){

        this.prayerName = prayerName;
    }
    public String getPrayerName() {

        return prayerName;
    }

    public void setPrayerImage(Bitmap prayerImage){

        this.prayerImage = prayerImage;
    }

    public Bitmap getPrayerImage() {

        return prayerImage;
    }

    public void setPrayerTime(String prayerTime){

        this.prayerTime = prayerTime;
    }

    public String getPrayerTime(){

        return prayerTime;
    }
}
