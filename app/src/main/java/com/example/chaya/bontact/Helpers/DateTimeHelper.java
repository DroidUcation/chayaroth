package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.R;

import java.security.Timestamp;
import java.sql.Time;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by chaya on 6/30/2016.
 */
public class DateTimeHelper {
    public static SimpleDateFormat dateFullFormatN = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static SimpleDateFormat dateHoursMinutesFormat = new SimpleDateFormat("HH:mm");

    public static SimpleDateFormat fullInitTimeZone() {
        SimpleDateFormat dateFullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFullFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFullFormat;
    }
    public static SimpleDateFormat hoursMinitesInitTimeZone() {
        SimpleDateFormat dateFullFormat = new SimpleDateFormat("HH:mm");
        dateFullFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFullFormat;
    }

    public static String getDiffToNow(String dateString, Context context) {
        dateString = convertDateStringToDbFormat(dateString);
        try {
            Date createdTime = dateFullFormatN.parse(dateString);
            Date currentTime = new Date();
            long diff = currentTime.getTime() - createdTime.getTime();

         /*   long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;*/
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays > 1)
                return getDateToDisplay(createdTime);
            if (diffDays == 1)
                return context.getResources().getString(R.string.yesterday);

            return getTimeFromDateToDisplay(createdTime);
           /* if(diffHours>1&&diffHours<24)
                return diffHours+" "+ context.getResources().getString(R.string.hours);
            if(diffHours==1)
                return context.getResources().getString(R.string.hour);
            if(diffMinutes<60)
                return diffMinutes+" "+context.getResources().getString(R.string.minutes);*/
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static String getDateToDisplay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        return day + "/" + month + "/" + year;
    }

    public static String getTimeFromDateToDisplay(String fullDateString) {
        try {
            fullDateString = fullDateString.replace('T', ' ').replace('Z', ' ');

            Date date = fullInitTimeZone().parse(fullDateString);
            return getTimeFromDateToDisplay(date);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return null;
    }

    public static String getTimeFromDateToDisplay(Date date) {
        String strDate = hoursMinitesInitTimeZone().format(date);
        return strDate;
    }

    public static String convertDateToFullFormatString(Date date) {
        //dateFullFormat.setTimeZone( TimeZone.getTimeZone("GMT"));
        return dateFullFormatN.format(date).replace('T', ' ').replace('Z', ' ');
    }

    public static String convertDateStringToDbFormat(String dateString) {
        if (dateString != null)
            return dateString.replace('T', ' ').replace('Z', ' ');
        return null;
    }
}
