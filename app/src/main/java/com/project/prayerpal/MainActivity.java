package com.project.prayerpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.PrayerTimes;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    PrayerTimes prayerTimes;
    Prayer prayer;
    TextView remainingTimeTextView;
    TextView salahRemainingTextView;
    RecyclerView recyclerView;
    MainCardNavigationAdapter cardAdapter;
    MosqueFinder mosqueFinder;
    FusedLocationProviderClient flp;
    double originLat;
    double originLng;
    LatLng currentLocation;
    static PlacesData placesData;
    private final static int request_code = 100;
    String[] prayerArr;
    ArrayList<PrayerModel> prayerModelArrayList;
    RecyclerView prayerRecyclerView;
    boolean hasNotificationsBeenScheduled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){

            actionBar.setDisplayShowTitleEnabled(false);
        }

        ImageView imageViewSettings = findViewById(R.id.imageViewSettings);

        prayerRecyclerView = findViewById(R.id.main_prayer_recycler_view);

        imageViewSettings.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        remainingTimeTextView = (TextView) findViewById(R.id.timeRemaining);
        salahRemainingTextView = (TextView) findViewById(R.id.salahRemaining);

        prayer = new Prayer();

        //Button button = findViewById(R.id.nearestMosque);

        recyclerView = findViewById(R.id.main_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        List<String> cardTitles = new ArrayList<>();
        cardTitles.add(getString(R.string.prayer_times_nav_title));
        cardTitles.add(getString(R.string.mosque_near_me_nav_title));

        List<Bitmap> cardIcons = new ArrayList<>();
        cardIcons.add(BitmapFactory.decodeResource(getResources(), R.drawable.duaicon));
        cardIcons.add(BitmapFactory.decodeResource(getResources(), R.drawable.mosqueicon));

        getPermission(new MyCallbackInterface() {
            @Override
            public void onSuccess() {

                getCurrentLocation(new MyCallbackInterface() {
                    @Override
                    public void onSuccess() {

                        prayerTimes = prayer.getPrayerTimes(new Coordinates(originLat, originLng), new Date(), getApplicationContext());

                        prayerArr = prayer.formatPrayerTimes(prayerTimes);

                        Log.d("habib", "reached before recievePrayerAlerts");
                        recievePrayerAlerts();
                        Log.d("habib", "reached after recievePrayerAlerts");

                        createPrayerRecyclerView();

                        updateTextViewThread(remainingTimeTextView, salahRemainingTextView, prayer, prayerTimes);

                        placesData = new PlacesData(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), getApplicationContext());

                        mosqueFinder = new MosqueFinder(MainActivity.this, originLat, originLng);

                        mosqueFinder.fetchPlace();

                        cardAdapter = new MainCardNavigationAdapter(cardTitles, cardIcons, originLat, originLng, getApplicationContext());
                        recyclerView.setAdapter(cardAdapter);

                    }

                    @Override
                    public void onFailure() {

                    }
                });

            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void askForNotificationPermission(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED){

                Log.d("habib", "permission not granted");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }

            Log.d("habib", "permission granted");
        }
    }

    public void recievePrayerAlerts(){

        askForNotificationPermission();

        Log.d("habib", "shared preferences created.");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean recieveNotifications = sharedPreferences.getBoolean("recieveNotification", false);
        Log.d("habib", "recieveNotifications: " + recieveNotifications);

        boolean hasNotificationsBeenScheduled = sharedPreferences.getBoolean("hasNotificationsBeenScheduled", false);
        Log.d("habib", "hasNotificationsBeenScheduled: " + hasNotificationsBeenScheduled);

        if(recieveNotifications & !hasNotificationsBeenScheduled){

            Log.d("habib", "if statement entered.");
            List<Date> timeDelayForEachPrayer = prayer.getTimeBetweenCurrentTimeAndEachPrayerTime(prayerTimes);

            long sec =  2 * 60000L;
            Calendar calendar = Calendar.getInstance();
            sec = calendar.getTimeInMillis() + sec;
            calendar.setTime(new Date(sec));
            NotificationScheduler.scheduleNotification(this, calendar.getTimeInMillis(), 1);

            /*
            Log.d("habib", "timeDelayForEachPrayer set.");
            Log.d("habib", "for loop started");
            for(int i = 0; i < timeDelayForEachPrayer.size(); i++){

                Calendar calendar = Calendar.getInstance();
                //calendar.setTime(new Date(sec));
                calendar.setTime(timeDelayForEachPrayer.get(i));

                NotificationScheduler.scheduleNotification(this, calendar.getTimeInMillis(), i);
                Log.d("habib", "scheduled notification");
            }

            Log.d("habib", "for loop ended.");
        */
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("hasNotificationsBeenScheduled", true);
            editor.apply();

            Log.d("habib", "applied to shared preferences.");
        }
    }

    public void createPrayerRecyclerView(){

        prayerModelArrayList = new ArrayList<>();

        String currentPrayerName = prayer.findCurrentPrayer(prayerTimes);

        createPrayerModelObjects(currentPrayerName);
        addAdapterToRecyclerview();
    }

    public void addAdapterToRecyclerview(){

        PrayerCardAdapter prayerCardAdapter = new PrayerCardAdapter(getApplicationContext(), prayerModelArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        prayerRecyclerView.setLayoutManager(linearLayoutManager);
        prayerRecyclerView.setAdapter(prayerCardAdapter);
        prayerRecyclerView.setNestedScrollingEnabled(false);
        prayerRecyclerView.setHasFixedSize(true);
        prayerRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void createPrayerModelObjects(String prayerName){

        Resources resources = getResources();

        switch(prayerName){

            case "Fajr":

                PrayerModel fajr = new PrayerModel(prayerName, prayerArr[0], BitmapFactory.decodeResource(resources, R.drawable.sunrise));
                prayerModelArrayList.add(fajr);
                break;
            case "Dhur":

                PrayerModel dhur = new PrayerModel(prayerName, prayerArr[1], BitmapFactory.decodeResource(resources, R.drawable.afternoon));
                prayerModelArrayList.add(dhur);
                break;
            case "Asr":

                PrayerModel asr = new PrayerModel(prayerName, prayerArr[2], BitmapFactory.decodeResource(resources, R.drawable.lateafternoon));
                prayerModelArrayList.add(asr);
                break;
            case "Maghrib":

                PrayerModel maghrib = new PrayerModel(prayerName, prayerArr[3], BitmapFactory.decodeResource(resources, R.drawable.evening));
                prayerModelArrayList.add(maghrib);
                break;
            case "Isha":

                PrayerModel isha = new PrayerModel(prayerName, prayerArr[4], BitmapFactory.decodeResource(resources, R.drawable.midnight));
                prayerModelArrayList.add(isha);
                break;
        }
    }

    public void updateTextViewThread(TextView remainingTimeTextView, TextView salahRemainingTextView, Prayer prayer, PrayerTimes prayerTimes){

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {

                    Time.createTimeTextViews(remainingTimeTextView, salahRemainingTextView, prayer, prayerTimes, MainActivity.this);
                } catch (Exception e) {

                    throw new RuntimeException(e);
                }

                handler.postDelayed(this, 60000);
            }
        });
    }

    public void getCurrentLocation(MyCallbackInterface callback) {

        flp = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            flp.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            originLat = addresses.get(0).getLatitude();
                            originLng = addresses.get(0).getLongitude();

                            currentLocation = new LatLng(originLat, originLng);

                            callback.onSuccess();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }else{

            Toast.makeText(this, "Location permission not granted",Toast.LENGTH_LONG).show();
        }
    }

    public void getPermission(MyCallbackInterface callback) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            askPermission();
        } else {

            Log.d("habib", "permission already granted");
        }

        callback.onSuccess();
    }

    public void askPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, request_code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}