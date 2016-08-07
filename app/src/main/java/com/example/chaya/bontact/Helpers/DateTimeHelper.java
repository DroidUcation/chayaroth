package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.R;
import com.google.android.exoplayer.util.NalUnitUtil;

import org.joda.time.Interval;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

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

    public static SimpleDateFormat dateFullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static SimpleDateFormat dateHoursMinutesFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateHoursMinutesSecondsFormat = new SimpleDateFormat("HH:mm:ss");

    public static String getDateToInbox(String dateString, Context context) {
        dateString = dateString.replace('T', ' ');
        dateString = dateString.replace('Z', ' ');
        try {
            Date createdTime = dateFullFormat.parse(dateString);
            Date currentTime = new Date();
            //long diff = currentTime.getTime() - createdTime.getTime();
            long diff = getDiffDates(currentTime, createdTime);
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays > 1)
                return getDisplayDate(createdTime, R.string.dd_mm_yy_format);
            if (diffDays == 1)
                return context.getResources().getString(R.string.yesterday);

            return getDisplayDate(createdTime, R.string.hh_mm_format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String getDiffToOnlineVisitors(String stringDateFullFormat) {
        Date date1 = convertFullFormatStringToDate(stringDateFullFormat);
        Date date2 = convertFullFormatStringToDate(DateTimeHelper.getCurrentStringDateInGmtZero());
        Interval interval = new Interval(date1.getTime(), date2.getTime());
        PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder()
                .printZeroAlways()
                .appendHours()
                .appendSeparator(":")
                .appendMinutes()
                .appendSeparator(":")
                .appendSeconds()
                .toFormatter();
     return minutesAndSeconds.print(interval.toPeriod());
    }


    public static long getDiffDates(Date d1, Date d2) {
        return d1.getTime() - d2.getTime();
    }

    public static long getDiffDates(String d1, String d2) {
        return getDiffDates(convertFullFormatStringToDate(d1), convertFullFormatStringToDate(d2));
    }

    public static String getDisplayDate(Date date, int formatRes) {
        SimpleDateFormat format = null;

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
            //format.setTimeZone(TimeZone.getDefault());
            format.setTimeZone(TimeZone.getTimeZone("GMT+5"));
            return format.format(date);
        }
        return null;
    }

    public static String convertDateToFullFormatString(Date date) {
        return dateFullFormat.format(date);
    }

    public static Date convertFullFormatStringToDate(String dateString) {
        try {
            dateString=convertDateStringToDbFormat(dateString);
            return dateFullFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertDateStringToDbFormat(String dateString) {
        if (dateString != null)
            return dateString.replace('T', ' ').replace('Z', ' ');
        return null;
    }

    public static String getCurrentStringDateInGmtZero() {
        Date date = new Date();
        final SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Log.d("GMT time: ", sdf.format(date));
        return sdf.format(date);
    }
}