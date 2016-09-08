package com.example.chaya.bontact.Helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static String getCurrentDate() {
        Date date = new Date();

        final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return formatDate.format(date);
    }

    public static String getDateToInbox(String createdDate) {
        return createdDate;
    }
}
