package com.project.prayerpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.List;

public class ClosestMosqueActivity extends AppCompatActivity implements OnMapReadyCallback{

    LatLng currentLocation;
    LatLng mosqueLocation;
    String mosqueName;
    double originLat;
    double originLng;
    double mosqueLat;
    double mosqueLng;
    SupportMapFragment mapFragment;
    MarkerOptions starterMarkerOptions;
    MarkerOptions endMarkerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closest_mosque);

        Intent intent = getIntent();
        originLat = intent.getDoubleExtra(String.valueOf(R.string.origin_lat), 0.0);
        originLng = intent.getDoubleExtra(String.valueOf(R.string.origin_lng), 0.0);

        currentLocation = new LatLng(originLat, originLng);

        mosqueLat = Double.parseDouble(MainActivity.placesData.getMapList().get(0).get(String.valueOf(
                R.string.mosque_lat)));
        mosqueLng = Double.parseDouble(MainActivity.placesData.getMapList().get(0).get(String.valueOf(
                R.string.mosque_lng
        )));
        mosqueName = MainActivity.placesData.getMapList().get(0).get(String.valueOf(R.string.mosque_name));

        mosqueLocation = new LatLng(mosqueLat, mosqueLng);

        starterMarkerOptions = new MarkerOptions().position(currentLocation).title(String.valueOf(
                R.string.current_location_marker));
        endMarkerOptions = new MarkerOptions().position(mosqueLocation).title(String.valueOf(
                R.string.mosque_marker));

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.closestmosquemap);

        mapFragment.getMapAsync(ClosestMosqueActivity.this);

        Log.d("habib", "onCreate ran");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        if (googleMap == null) {

            Log.d("habib", "map is null");
        }

        try {

            Log.d("habib", "enter on map ready");

            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyB4KjBvflRc6Sde_VbWuL2331YUFs4d764")
                    .build();

            Log.d("habib", "Geo api context created");

            DirectionsApiRequest directionsApiRequest = DirectionsApi.newRequest(context)
                    .origin(currentLocation.toString())
                    .destination(mosqueLocation.toString())
                    .mode(TravelMode.WALKING);

            Log.d("habib", "Directions api request created");

            DirectionsResult directionsResult = null;

            try {
                directionsResult = directionsApiRequest.await();

                Log.d("habib", "Directions result recieved");

                if (directionsResult.routes.length > 0) {

                    Log.d("habib", "if statement");

                    DirectionsRoute route = directionsResult.routes[0];

                    Log.d("habib", "directions route created");

                    String polyLine = route.overviewPolyline.getEncodedPath();

                    Log.d("habib", "polyline created");

                    List<LatLng> points = PolyUtil.decode(polyLine);

                    Log.d("habib", "Latlng created");

                    for (LatLng point : points) {

                        googleMap.addMarker(new MarkerOptions().position(point));
                    }

                    Log.d("habib", "end of for loop");
                }

            } catch (ApiException | InterruptedException | IOException e) {

                e.printStackTrace();
            }

            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            googleMap.addMarker(starterMarkerOptions);
            googleMap.addMarker(endMarkerOptions);
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(14));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

            Log.d("habib", "onMapReady ran");

        }catch (Exception e){

            e.printStackTrace();
        }

    }
}