package com.project.prayerpal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.PrayerTimes;

import java.util.ArrayList;
import java.util.Date;

public class PrayerActivity extends AppCompatActivity {

    Prayer prayer;
    PrayerTimes prayerTimes;
    TextView date;
    ImageView back;
    ImageView forward;
    String[] prayerArr;
    Date today;
    ArrayList<PrayerModel> prayerModelArrayList;
    RecyclerView prayerRecyclerView;
    PrayerCardAdapter prayerCardAdapter;
    Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer);

        addToolbar();

        instantiateViews();

        addDateToDateObject();

        addPrayersToPrayerArr();

        createPrayerRecyclerView();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                today = yesterdaysPrayer(today);

                if(today == null){

                    Toast.makeText(PrayerActivity.this, "Oops can't go back to the past.", Toast.LENGTH_SHORT).show();
                    today = new Date();
                }

                date.setText(Time.formatDateToday(today, getApplicationContext()));
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                today = tommorowsPrayer(today);

                date.setText(Time.formatDateToday(today, getApplicationContext()));
            }
        });
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

    public void addDateToDateObject(){

        today = new Date();

        date.setText(Time.formatDateToday(today, getApplicationContext()));
    }

    public void addPrayersToPrayerArr(){

        prayer = new Prayer();

        prayerTimes = prayer.getPrayerTimes(new Coordinates(51.52063333422422, 0.06289134232855906), today, getApplicationContext());

        prayerArr = prayer.formatPrayerTimes(prayerTimes);
    }

    public void instantiateViews(){

        prayerRecyclerView = findViewById(R.id.prayer_recycler_view);
        date = (TextView) findViewById(R.id.date);
        back = findViewById(R.id.yesterday);
        forward = findViewById(R.id.forward);
    }

    public void createPrayerRecyclerView(){

        prayerModelArrayList = new ArrayList<>();

        createPrayerModelObjects();
        addAdapterToRecyclerview();
    }

    public void createPrayerModelObjects(){

        Resources resources = getResources();

        PrayerModel fajr = new PrayerModel(getResources().getString(R.string.fajr), prayerArr[0], BitmapFactory.decodeResource(resources, R.drawable.sunrise));
        prayerModelArrayList.add(fajr);

        PrayerModel dhur = new PrayerModel(getResources().getString(R.string.dhur), prayerArr[1],  BitmapFactory.decodeResource(getResources(), R.drawable.afternoon));
        prayerModelArrayList.add(dhur);

        PrayerModel asr = new PrayerModel(getResources().getString(R.string.asr), prayerArr[2],BitmapFactory.decodeResource(getResources(), R.drawable.lateafternoon));
        prayerModelArrayList.add(asr);

        PrayerModel maghrib = new PrayerModel(getResources().getString(R.string.maghrib), prayerArr[3],BitmapFactory.decodeResource(getResources(), R.drawable.evening));
        prayerModelArrayList.add(maghrib);

        PrayerModel isha = new PrayerModel(getResources().getString(R.string.isha), prayerArr[4],BitmapFactory.decodeResource(getResources(), R.drawable.midnight));
        prayerModelArrayList.add(isha);
    }

    public void addAdapterToRecyclerview(){

        prayerCardAdapter = new PrayerCardAdapter(getApplicationContext(), prayerModelArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        prayerRecyclerView.setLayoutManager(linearLayoutManager);
        prayerRecyclerView.setAdapter(prayerCardAdapter);
        prayerRecyclerView.setNestedScrollingEnabled(false);
        prayerRecyclerView.setHasFixedSize(true);
        prayerRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void updatePrayerCards(){

        prayerModelArrayList.get(0).setPrayerTime(prayerArr[0]);
        prayerModelArrayList.get(1).setPrayerTime(prayerArr[1]);
        prayerModelArrayList.get(2).setPrayerTime(prayerArr[2]);
        prayerModelArrayList.get(3).setPrayerTime(prayerArr[3]);
        prayerModelArrayList.get(4).setPrayerTime(prayerArr[4]);

        prayerCardAdapter.notifyDataSetChanged();
    }

    public Date tommorowsPrayer(Date date){

        Date tommorow = Time.getDateOfTommorow(date);

        prayerTimes = prayer.getPrayerTimes(new Coordinates(51.52063333422422, 0.06289134232855906), tommorow, getApplicationContext());

        prayerArr = prayer.formatPrayerTimes(prayerTimes);

        updatePrayerCards();

        return tommorow;

    }

    public Date yesterdaysPrayer(Date date){

        long currentTime = Time.getCurrentTime();

        Date yesterday = Time.getDateOfYesterday(date);

        long dateTime = Time.getTimeOfDate(yesterday);

        if(dateTime < currentTime){

            return null;
        }

        prayerTimes = prayer.getPrayerTimes(new Coordinates(51.52063333422422, 0.06289134232855906), yesterday, getApplicationContext());

        prayerArr = prayer.formatPrayerTimes(prayerTimes);

        updatePrayerCards();

        return yesterday;
    }
}