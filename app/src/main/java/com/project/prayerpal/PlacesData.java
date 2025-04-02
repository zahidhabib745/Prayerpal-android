package com.project.prayerpal;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class PlacesData {

    ArrayList<HashMap<String, String>> mapList;
    ArrayList<HashMap<String, Double>> locationList;
    ArrayList<MapModel> mapsModelArrayList;
    Comparator<HashMap<String, String>> distanceComparator;
    Resources resources;

    public PlacesData(ArrayList<HashMap<String, String>> mapList, ArrayList<HashMap<String, Double>> locationList, ArrayList<MapModel> mapsModelArrayList, Context context){

        this.mapList = mapList;
        this.locationList = locationList;
        this.mapsModelArrayList = mapsModelArrayList;
        resources = context.getResources();
    }

    public ArrayList<HashMap<String, String>> getMapList(){

        return mapList;
    }

    public ArrayList<HashMap<String, Double>> getLocationList(){

        return locationList;
    }

    public ArrayList<MapModel> getMapsModelList(){

        return mapsModelArrayList;
    }

    public void addToMapModelList(MapModel mapModel){

        mapsModelArrayList.add(mapModel);
    }

    public void addToLocationList(HashMap<String, Double> location){

        locationList.add(location);
    }

    public void addToMapList(HashMap<String, String> map){

        createComparator();

        mapList.add(map);

        if(mapList.size() > 1){

            Collections.sort(mapList, distanceComparator);
        }
    }

    public void sortMapList(){

        createComparator();

        Collections.sort(mapList, distanceComparator);
    }

    public void createComparator(){

        distanceComparator = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> map1, HashMap<String, String> map2) {

                double distance1 = Double.parseDouble(map1.get(resources.getString(R.string.distance_in_meters)));
                double distance2 = Double.parseDouble(map2.get(resources.getString(R.string.distance_in_meters)));

                return Double.compare(distance1, distance2);
            }
        };
    }
}
