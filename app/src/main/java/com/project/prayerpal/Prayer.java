package com.project.prayerpal;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TextView;

import com.batoulapps.adhan.CalculationMethod;
import com.batoulapps.adhan.CalculationParameters;
import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.Madhab;
import com.batoulapps.adhan.PrayerTimes;
import com.batoulapps.adhan.data.DateComponents;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Prayer {

    String fajr;
    String dhur;
    String asr;
    String maghrib;
    String isha;
    Context context;

    public PrayerTimes getPrayerTimes(Coordinates coordinates, Date date, Context context){

        this.context = context;

        if(date == null){

            Log.d("habib", "data is null: " + date );
        }

        DateComponents dateComponents = DateComponents.from(date);
        CalculationParameters params = CalculationMethod.MUSLIM_WORLD_LEAGUE.getParameters();
        params.madhab = Madhab.HANAFI;
        params.adjustments.fajr = 2;

        return new PrayerTimes(coordinates, dateComponents, params);
    }

    public String[] formatPrayerTimes(PrayerTimes prayerTimes){

        //add if statement to check if  amedTo12hr is true or false to then decide to change prayer time to 12hr or 24hr format.

        SimpleDateFormat twentyFourFormat = new SimpleDateFormat("HH:mm a");
        twentyFourFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));

        fajr = twentyFourFormat.format(prayerTimes.fajr);
        dhur = twentyFourFormat.format(prayerTimes.dhuhr);
        asr = twentyFourFormat.format(prayerTimes.asr);
        maghrib = twentyFourFormat.format(prayerTimes.maghrib);
        isha = twentyFourFormat.format(prayerTimes.isha);

        return new String[]{fajr, dhur, asr, maghrib, isha};
    }

    public String findCurrentPrayer(PrayerTimes prayerTimes){

        String prayerName = null;

        try{

            long currentTime = Time.getCurrentTime();

            if(currentTime < prayerTimes.fajr.getTime()){

                prayerName = context.getResources().getString(R.string.isha);
            }else if(currentTime < prayerTimes.dhuhr.getTime()){

                prayerName = context.getResources().getString(R.string.fajr);
            }else if(currentTime < prayerTimes.asr.getTime()){

                prayerName = context.getResources().getString(R.string.dhur);
            }else if(currentTime < prayerTimes.maghrib.getTime()){

                prayerName = context.getResources().getString(R.string.asr);
            }else if(currentTime < prayerTimes.isha.getTime()){

                prayerName = context.getResources().getString(R.string.maghrib);
            }
        }catch (Exception e){

            e.printStackTrace();
        }

        return prayerName;
    }

    public String[] findNextPrayer(PrayerTimes prayerTimes){

        Calendar calendar = Calendar.getInstance();
        String nextPrayer = null;
        String todaysDate = null;
        String prayerName = null;

        try {

            todaysDate = Time.formatDateToday(calendar.getTime(), context.getApplicationContext());

            long currentTime = Time.getCurrentTime();

            if(currentTime < prayerTimes.fajr.getTime()){

                nextPrayer = fajr;
                prayerName = context.getResources().getString(R.string.fajr);

            }else if(currentTime < prayerTimes.dhuhr.getTime()){

                nextPrayer = dhur;
                prayerName = context.getResources().getString(R.string.dhur);
            }else if(currentTime < prayerTimes.asr.getTime()){

                nextPrayer = asr;
                prayerName = context.getResources().getString(R.string.asr);
            }else if(currentTime < prayerTimes.maghrib.getTime()){

                nextPrayer = maghrib;
                prayerName = context.getResources().getString(R.string.maghrib);
            }else if(currentTime < prayerTimes.isha.getTime()){

                nextPrayer = isha;
                prayerName = context.getResources().getString(R.string.isha);
            }else{

                Date nextDay = Time.getDateOfDay(1);

                prayerTimes = getPrayerTimes(new Coordinates(51.52063333422422, 0.06289134232855906), nextDay, context.getApplicationContext());
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                formatter.setTimeZone(TimeZone.getTimeZone("Europe/London"));
                nextPrayer = formatter.format(prayerTimes.fajr);

                todaysDate = Time.formatDateToday(nextDay, context.getApplicationContext());
                prayerName = context.getResources().getString(R.string.fajr);
            }
        }catch(Exception e) {

            e.printStackTrace();
        }

        Log.d("habib", "prayer name: " + prayerName);
        Log.d("habib", "next prayer: " + nextPrayer);

        return new String []{nextPrayer, todaysDate, prayerName};
    }

    public List<Date> getTimeBetweenCurrentTimeAndEachPrayerTime(PrayerTimes prayerTimes){//seems like this is where the bug has occured that is causing the app to crash.

        List<Date> timeDelayForEachPrayer = new ArrayList<>();

        //long fajrTimeInMillisecs = prayerTimes.fajr.getTime();
        timeDelayForEachPrayer.add(prayerTimes.fajr);

        Log.d("habib", "fajr date added");

        //long dhurTimeInMillisecs = prayerTimes.dhuhr.getTime();
        timeDelayForEachPrayer.add(prayerTimes.dhuhr);
        Log.d("habib", "dhur date added");

        //long asrTimeInMillisecs = prayerTimes.asr.getTime();
        timeDelayForEachPrayer.add(prayerTimes.asr);
        Log.d("habib", "asr date added");

        //long maghribTimeInMillisecs = prayerTimes.maghrib.getTime();
        timeDelayForEachPrayer.add(prayerTimes.maghrib);
        Log.d("habib", "maghrib date added");

        //long ishaTimeInMillisecs = prayerTimes.isha.getTime();
        timeDelayForEachPrayer.add(prayerTimes.isha);
        Log.d("habib", "isha date added");

    /*
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        Log.d("habib", "for loop started");
        Log.d("habib", "timeDelatForEachPrayer size: " + timeDelayForEachPrayer.size());

        for(int i = 0; i < timeDelayForEachPrayer.size(); i++){

            long timeDifference = timeDelayForEachPrayer.get(i) - currentTime;

            long differenceInMinutes = timeDifference / (60 * 1000) % 60;
            long differenceInHours = timeDifference / (60 * 60 * 1000) % 24;

            String hour = Long.toString(differenceInHours);
            String minutes = Long.toString(differenceInMinutes);

            Log.d("habib", "hour: " + hour + " minutes: " + minutes);

            Log.d("habib", "timeDelay milliseconds subtracted from current time");

            timeDifference = Math.abs(timeDifference);

            timeDelayForEachPrayer.remove(i);
            timeDelayForEachPrayer.add(i, timeDifference);
        }

        Log.d("habib", "for loop ended");

        Log.d("habib", "returning from getBewteenCurrentTimeAndEachPrayerTime method");

     */
        return timeDelayForEachPrayer;
    }

    public String remainingTimeTillNextprayer(String todaysDate, String nextPrayer){

        Calendar calendar = Calendar.getInstance();
        String timeRemaining = null;
        String minutes;
        String hour;

        try {

            long currentTime = Time.getCurrentTime();

            String dateAndTimeForNextPrayer = todaysDate + " " + nextPrayer;

            Date timeAndDateForNextPrayer = Time.formatStringTo24HrDate(dateAndTimeForNextPrayer, context.getApplicationContext());

            if (timeAndDateForNextPrayer != null) {

                calendar.setTime(timeAndDateForNextPrayer);

                Calendar currentDate = Calendar.getInstance();

                if(calendar.get(Calendar.HOUR_OF_DAY) < currentDate.get(Calendar.HOUR_OF_DAY)
                        || (calendar.get(Calendar.HOUR_OF_DAY) == currentDate.get(Calendar.HOUR_OF_DAY) &&
                        calendar.get(Calendar.MINUTE) <= currentDate.get(Calendar.MINUTE))){

                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }else{

                Log.d("habib", "timeAndDateForNextPrayer is null");
            }

            long timeOfNextPrayer = calendar.getTimeInMillis();

            long timeDifference = timeOfNextPrayer - currentTime;

            timeDifference = Math.abs(timeDifference);

            long differenceInMinutes = timeDifference / (60 * 1000) % 60;
            long differenceInHours = timeDifference / (60 * 60 * 1000) % 24;

            hour = Long.toString(differenceInHours);
            minutes = Long.toString(differenceInMinutes);

            if(differenceInHours == 1){

                if(differenceInMinutes < 2){

                    timeRemaining = hour + " hour " + "and " + minutes + " minute";
                }else{

                    timeRemaining = hour + " hour " + "and " + minutes + " minutes";
                }

            } else if(differenceInHours == 0){

                if(differenceInMinutes < 2){

                    timeRemaining = minutes + " minute";
                }else{

                    timeRemaining = minutes + " minutes";
                }

            }else{

                if(differenceInMinutes < 2){

                    timeRemaining = hour + " hours " + "and " + minutes + " minute";
                }else{

                    timeRemaining = hour + " hours " + "and " + minutes + " minutes";
                }

            }

        }catch(ParseException e){

            e.printStackTrace();
        }

        return timeRemaining;
    }


/*
    public String remainingTimeTillNextprayer(String todaysDate, String nextPrayer){

        Calendar calendar = Calendar.getInstance();
        String timeRemaining = null;
        String minutes;
        String hour;
        String testDate;

        try {

            //long currentTime = Time.getCurrentTime();

            //currentTime = calendar.getTimeInMillis();

            testDate = todaysDate + " 01:06";

            Date dateObj = Time.formatStringTo24HrDate(testDate, context);

            long currentTime = dateObj.getTime();

            String dateAndTimeForNextPrayer = todaysDate + " " + nextPrayer;

            Date timeAndDateForNextPrayer = Time.formatStringTo24HrDate(dateAndTimeForNextPrayer, context);

            if (timeAndDateForNextPrayer != null) {

                calendar.setTime(timeAndDateForNextPrayer);

                Calendar currentDate = Calendar.getInstance();

                if(calendar.get(Calendar.HOUR_OF_DAY) < currentDate.get(Calendar.HOUR_OF_DAY)
                        || (calendar.get(Calendar.HOUR_OF_DAY) == currentDate.get(Calendar.HOUR_OF_DAY) &&
                        calendar.get(Calendar.MINUTE) <= currentDate.get(Calendar.MINUTE))){

                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }else{

                Log.d("habib", "timeAndDateForNextPrayer is null");
            }

            long timeOfNextPrayer = calendar.getTimeInMillis();

            long timeDifference = timeOfNextPrayer - currentTime;

            timeDifference = Math.abs(timeDifference);

            long differenceInMinutes = timeDifference / (60 * 1000) % 60;
            long differenceInHours = timeDifference / (60 * 60 * 1000) % 24;

            hour = Long.toString(differenceInHours);
            minutes = Long.toString(differenceInMinutes);

            if(differenceInHours == 1){

                if(differenceInMinutes < 2){

                    timeRemaining = hour + " hour " + "and " + minutes + " minute";
                }else{

                    timeRemaining = hour + " hour " + "and " + minutes + " minutes";
                }

            } else if(differenceInHours == 0){

                if(differenceInMinutes < 2){

                    timeRemaining = minutes + " minute";
                }else{

                    timeRemaining = minutes + " minutes";
                }

            }else{

                if(differenceInMinutes < 2){

                    timeRemaining = hour + " hours " + "and " + minutes + " minute";
                }else{

                    timeRemaining = hour + " hours " + "and " + minutes + " minutes";
                }

            }

        }catch(ParseException e){

            e.printStackTrace();
        }

        return timeRemaining;
    }

 */
}
