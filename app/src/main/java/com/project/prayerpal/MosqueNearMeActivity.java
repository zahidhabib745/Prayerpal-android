package com.project.prayerpal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.gms.maps.OnMapReadyCallback;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MosqueNearMeActivity extends AppCompatActivity implements OnMapReadyCallback {//rename this to MosqueNearMeActivity

    boolean condition;
    Geocoder geocoder;
    RecyclerView mosqueRecyclerView;
    SupportMapFragment mapFragment;
    double originLat;
    double originLng;
    LatLng currentLocation;
    Toolbar toolBar;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosque_near_me);

        addToolbar();

        Intent intent = getIntent();

        resources = getResources();

        originLat = intent.getDoubleExtra(resources.getString(R.string.origin_lat), 0.0);
        originLng = intent.getDoubleExtra(resources.getString(R.string.origin_lng), 0.0);

        currentLocation = new LatLng(originLat, originLng);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mosqueRecyclerView = findViewById(R.id.recyclerView);//copy

        addAdapterToMosqueRecyclerView();

        mapFragment.getMapAsync(MosqueNearMeActivity.this);
    }

    public void addToolbar(){

        createToolbar();
        removeUIFromToolbar();
        addBackButtonToToolbar();
    }

    public void createToolbar(){

        toolBar = findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolBar);
    }

    public void removeUIFromToolbar(){

        CardView settingsImageView = findViewById(R.id.tool_bar_main_card_view);
        settingsImageView.setVisibility(View.GONE);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){

            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    public void addBackButtonToToolbar(){

        LayoutInflater inflater = LayoutInflater.from(this);
        View customBackButton = inflater.inflate(R.layout.custom_back_button, toolBar, false);

        customBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        toolBar.addView(customBackButton);
    }

    public void addDataFromMapListToMapModelList(){

        for (int i = 0; i < MainActivity.placesData.getMapList().size(); i++) {

            MainActivity.placesData.getMapsModelList().add(new MapModel(MainActivity.placesData.getMapList()
                    .get(i).get(resources.getString(R.string.mosque_name)), MainActivity.placesData.getMapList().get(i).get(resources.getString(R.string.mosque_address)),
                    MainActivity.placesData.getMapList().get(i).get(resources.getString(R.string.distance_in_meters)) + " meters"));
        }
    }

    public void addAdapterToMosqueRecyclerView() {


                addDataFromMapListToMapModelList();

                MapAdapter mapAdapter = new MapAdapter(MainActivity.placesData.getMapsModelList());//copy

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);//copy

                mosqueRecyclerView.setLayoutManager(linearLayoutManager);
                mosqueRecyclerView.setAdapter(mapAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MainActivity.placesData.getMapsModelList().clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        for (int i = 0; i < MainActivity.placesData.getMapList().size(); i++) {

            Double lat = Double.parseDouble(MainActivity.placesData.getMapList().get(i).get(getResources().getString(R.string.mosque_lat)));
            Double lng = Double.parseDouble(MainActivity.placesData.getMapList().get(i).get(resources.getString(R.string.mosque_lng)));

            if (lat != null && lng != null) {

                LatLng location = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions().position(location).title(resources.getString(R.string.mosque_marker)).icon(BitmapDescriptorFactory.fromResource(R.drawable.mosque_icon_pointer)));
            } else {

                Log.d("habib", "lat or lng is null");
            }
        }

        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.addMarker(new MarkerOptions().position(currentLocation).title(resources.getString(R.string.current_location_marker)).icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon_pointer)));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }

}