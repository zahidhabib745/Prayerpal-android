package com.project.prayerpal;

import android.content.Context;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

public class MosqueFinder {

    private static final double EARTH_RADIUS = 3959.0;
    PlacesClient placesClient;
    double originLat;
    double originLng;
    Resources resources;
    String apiKey = BuildConfig.MAPS_API_KEY;

    public MosqueFinder(Context context, Double originLat, Double originLng){

        this.originLat = originLat;
        this.originLng = originLng;
        resources = context.getResources();

        initPlaces(context);
    }

    public void initPlaces(Context context){

        Places.initialize(context.getApplicationContext(), apiKey);
        placesClient = Places.createClient(context);
    }


    public Task<Boolean> fetchPlace(){

        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

        double[] northeastArray = calculateNortheastCoordinates(originLat, originLng, 2.0);
        double northeastLat = northeastArray[0];
        double northeastLng = northeastArray[1];

        double[] southwestArray = calculateSouthwestCoordinates(originLat, originLng, 2.0);
        double southwestLat = southwestArray[0];
        double southwestLng = southwestArray[1];

        Location origin = new Location("");
        origin.setLatitude(originLat);
        origin.setLongitude(originLng);

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        RectangularBounds bounds = RectangularBounds.newInstance(new LatLng(southwestLat, southwestLng), new LatLng(northeastLat, northeastLng));

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(bounds).setOrigin(new LatLng(originLat, originLng)).setTypesFilter(Arrays.asList(resources.getString(R.string.mosque_marker)))
                .setSessionToken(token).setQuery(resources.getString(R.string.mosque_marker)).build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {

            List<Task<Place>> tasks = new ArrayList<>();
            List<HashMap<String, String>> tempMapList = new ArrayList<>();

            for(AutocompletePrediction prediction : response.getAutocompletePredictions()){

                HashMap<String, String> map = new HashMap<>();

                addDetailToMap(prediction, map);

                tempMapList.add(map);

                Task<Place> task = getPlaceTask(prediction, placesClient);
                tasks.add(task);
            }

            Tasks.whenAllSuccess(tasks).addOnSuccessListener((places) -> {

                for(int i = 0; i < places.size(); i++){

                    Place place = (Place) places.get(i);

                    addLatLngOfMosque(place, tempMapList.get(i));
                    addDistanceOfMosques(origin, tempMapList.get(i));
                }

                MainActivity.placesData.getMapList().addAll(tempMapList);
                MainActivity.placesData.sortMapList();

                taskCompletionSource.setResult(true);
            }).addOnFailureListener((exception) -> {

                Log.d("habib", "Failed to get autocomplete predictions");
            });

        }).addOnFailureListener((exception) -> {

            taskCompletionSource.setResult(false);

            if(exception instanceof ApiException){

                ApiException apiException = (ApiException) exception;
             }

            Log.d("habib", "fetch place on fail ran");
        });

        return taskCompletionSource.getTask();
    }

    public Task<Place> getPlaceTask(AutocompletePrediction prediction, PlacesClient placesClient){

        TaskCompletionSource<Place> taskCompletionSource = new TaskCompletionSource<>();
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);

        FetchPlaceRequest request = FetchPlaceRequest.newInstance(prediction.getPlaceId(), placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((fetchPlaceResponse) -> {

            Place place = fetchPlaceResponse.getPlace();

            taskCompletionSource.setResult(place);
        }).addOnFailureListener((exception) -> {

            taskCompletionSource.setException(exception);
        });

        return taskCompletionSource.getTask();
    }

    public void addDetailToMap(AutocompletePrediction prediction, HashMap<String, String> map){

        addMosqueName(prediction, map);

        addMosqueAddress(prediction, map);

        addMosqueId(prediction, map);
    }

    public void addMosqueName(AutocompletePrediction prediction, HashMap<String, String> map){

        map.put(resources.getString(R.string.mosque_name), prediction.getPrimaryText(null).toString());
    }

    public void addMosqueAddress(AutocompletePrediction prediction, HashMap<String, String> map){

        map.put(resources.getString(R.string.mosque_address), prediction.getSecondaryText(null).toString());
    }

    public void addMosqueId(AutocompletePrediction prediction, HashMap<String, String> map){

        map.put(resources.getString(R.string.mosque_id), prediction.getPlaceId());
    }

    public void addLatLngOfMosque(Place place, HashMap<String, String> map){

        Location destination = new Location("");
        destination.setLatitude(place.getLatLng().latitude);
        destination.setLongitude(place.getLatLng().longitude);

        map.put(resources.getString(R.string.mosque_lat), String.valueOf(destination.getLatitude()));
        map.put(resources.getString(R.string.mosque_lng), String.valueOf(destination.getLongitude()));
    }

    public void addDistanceOfMosques(Location origin, HashMap<String, String> map){

        float[] distance = new float[1];

        Location.distanceBetween(origin.getLatitude(), origin.getLongitude(),
                Double.parseDouble(map.get(resources.getString(R.string.mosque_lat))),
                Double.parseDouble(map.get(resources.getString(R.string.mosque_lng))),
                distance);

        int roundFloat = (int)  distance[0];
        map.put(resources.getString(R.string.distance_in_meters), String.valueOf(roundFloat));
    }

    public double[] calculateNortheastCoordinates(double startingLatitude, double startingLongitude, double distance){

        double lat1 = Math.toRadians(startingLatitude);
        double lng1 = Math.toRadians(startingLongitude);
        double brng = Math.toRadians(45);

        double d = distance/EARTH_RADIUS;

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d) + Math.cos(lat1) * Math.sin(d) * Math.cos(brng));
        double lng2 = lng1 + Math.atan2(Math.sin(brng) * Math.sin(d) * Math.cos(lat1),
                Math.cos(d) - Math.sin(lat1) * Math.sin(lat2));

        double northeastLatitude = Math.toDegrees(lat2);
        double northeastLongitude = Math.toDegrees(lng2);

        return new double[]{northeastLatitude, northeastLongitude};
    }

    public double[] calculateSouthwestCoordinates(double startingLatitude, double startingLongitude, double distance){

        double lat1 = Math.toRadians(startingLatitude);
        double lng1 = Math.toRadians(startingLongitude);
        double brng = Math.toRadians(255);

        double d = distance/EARTH_RADIUS;

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d) + Math.cos(lat1) * Math.sin(d) * Math.cos(brng));
        double lng2 = lng1 + Math.atan2(Math.sin(brng) * Math.sin(d) * Math.cos(lat1),
                Math.cos(d) - Math.sin(lat1) * Math.sin(lat2));

        double southwestLatitude = Math.toDegrees(lat2);
        double southwestLongitude = Math.toDegrees(lng2);

        return new double[]{southwestLatitude, southwestLongitude};
    }
}
