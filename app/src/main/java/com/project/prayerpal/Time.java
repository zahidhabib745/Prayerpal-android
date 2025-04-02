package com.project.prayerpal;

import android.content.Context;
import android.content.res.Resources;
import android.widget.TextView;

import com.batoulapps.adhan.PrayerTimes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {

    public static void createTimeTextViews(TextView remainingTimeTextView, TextView salahRemainingTextView, Prayer prayer, PrayerTimes prayerTimes, Context context){

        String[] dateAndTimeForNextPrayer = prayer.findNextPrayer(prayerTimes);
        String timeRemaining = prayer.remainingTimeTillNextprayer(dateAndTimeForNextPrayer[1], dateAndTimeForNextPrayer[0]);

        System.out.println(timeRemaining);

        salahRemainingTextView.setText( context.getResources().getString(R.string.time_remaining_till) + " " + dateAndTimeForNextPrayer[2]);
        remainingTimeTextView.setText(timeRemaining);
    }

    public static long getTimeOfDate(Date date){

        return date.getTime();
    }

    public static Date getDateOfTommorow(Date date){

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);

        return cal.getTime();
    }

    public static Date getDateOfYesterday(Date date){

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);

        return cal.getTime();
    }

    public static Date getDateOfDay(int increment){

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, increment);

        return calendar.getTime();
    }

    public static long getCurrentTime(){

        Calendar calendar = Calendar.getInstance();

        return calendar.getTimeInMillis();
    }

    public static String formatDateToday(Date date, Context context){

        SimpleDateFormat format = new SimpleDateFormat(context.getResources().getString(R.string.todays_date_format));

        return format.format(date);
    }

    public static Date formatStringTo24HrDate(String date, Context context) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat(context.getResources().getString(R.string.todays_date_format_with_24_Hour));

        return format.parse(date);
    }
}
