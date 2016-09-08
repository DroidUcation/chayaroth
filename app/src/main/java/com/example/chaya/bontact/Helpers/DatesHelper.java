package com.example.chaya.bontact.Helpers;

import android.content.Context;

import com.example.chaya.bontact.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by chaya on 9/8/2016.
 */
public class DatesHelper {

    public static String convertDateToCurrentGmt(String stringDate) {
        if (stringDate == null)
            return null;
        stringDate = stringDate.replace('T', ' ').replace('Z', ' ');

        final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        formatDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date date = formatDate.parse(stringDate);
            if (date != null) {
                formatDate.setTimeZone(TimeZone.getDefault());
                return formatDate.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stringDate;
    }

    public static String getCurrentStringDate() {
        Date date = new Date();

        final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return formatDate.format(date);
    }


    public static String getDateToDisplayInbox(Context context, String createdDateString) {
        if (createdDateString == null)
            return null;
        final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            Date createdDate = formatDate.parse(createdDateString);
            Date currentDate = new Date();
            long diff = currentDate.getTime() - createdDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            if (diffDays > 1)
                return getDisplayDate(createdDate, R.string.dd_mm_yy_format);
            if (diffDays == 1)
                return context.getResources().getString(R.string.yesterday);

            return getDisplayDate(createdDate, R.string.hh_mm_format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return createdDateString;
    }

    public static String getDateToDisplayInnerConversation(String stringDate) {
        final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            Date date = formatDate.parse(stringDate);
            if (date != null)
                return getDisplayDate(date, R.string.hh_mm_format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateToDisplayInOnlineVisitors(String stringDate) {
        final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            Date date = formatDate.parse(stringDate);
            if (date != null)
                return getDisplayDate(date, R.string.hh_mm_format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getDisplayDate(Date date, int formatRes) {
        SimpleDateFormat format = null;
        if (date == null)
            return null;
        if (formatRes == R.string.dd_mm_yy_format) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH) + 1;
            return day + "/" + month + "/" + year;
        }
        if (formatRes == R.string.hh_mm_format) {
            format = new SimpleDateFormat("HH:mm");
        }
        if (formatRes == R.string.hh_mm_ss) {
            format = new SimpleDateFormat("HH:mm:ss");
        }
        if (format != null) {
            String s = format.format(date);
            return s;
        }
        return null;
    }
}
