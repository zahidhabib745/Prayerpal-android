package com.project.prayerpal;

public class MapModel {

    private String mosqueName;
    private String mosqueAddress;
    private String mosqueDistance;

    public MapModel(String mosqueName, String mosqueAddress, String mosqueDistance){

        this.mosqueName = mosqueName;
        this.mosqueAddress = mosqueAddress;
        this.mosqueDistance = mosqueDistance;
    }


    public void setMosqueName(String mosqueName){

        this.mosqueName = mosqueName;
    }
    public String getMosqueName() {

        return mosqueName;
    }

    public void setMosqueAddress(String mosqueAddress){

        this.mosqueAddress = mosqueAddress;
    }

    public String getMosqueAddress() {

        return mosqueAddress;
    }

    public void setMosqueDistance(String mosqueDistance){

        this.mosqueDistance = mosqueDistance;
    }

    public String getMosqueDistance(){

        return mosqueDistance;
    }
}
