package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chaya on 6/30/2016.
 */
public class DateTimeHelper {
    public static String getDiffToNow(String dateString, Context context)
    {
        dateString= dateString.replace('T',' ');
        dateString= dateString.replace('Z',' ');

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            Date created=dateFormat.parse(dateString);
            dateFormat=new SimpleDateFormat("dd/MM/yyyy");
            Calendar createdDateCal = Calendar.getInstance();
            createdDateCal.setTime(created);

            Date now=new Date();
            Calendar currentDateCal = Calendar.getInstance();
            currentDateCal.setTime(now);

            int currentYear=currentDateCal.get(Calendar.YEAR);
            int createdYear=createdDateCal.get(Calendar.YEAR);
            int currentMonth=currentDateCal.get(Calendar.MONTH);
            int createdMonth=createdDateCal.get(Calendar.MONTH);

            if(currentYear>createdYear || currentMonth>createdMonth)
            {
             Date returnDate=  createdDateCal.getTime();
                return dateFormat.format(returnDate);
               // return created.getYear()+"/"+created.getMonth()+"/"+ created.getDay();
            }
            int createdDay = createdDateCal.get(Calendar.DAY_OF_MONTH);
            int currentDay = currentDateCal.get(Calendar.DAY_OF_MONTH);

            if(currentDay> createdDay)
            {
               int dayDiff=currentDay-createdDay;
                if(dayDiff==1)
                    return  context.getResources().getString(R.string.yesterday);
               // return dayDiff+" "+context.getResources().getString(R.string.days);
                Date returnDate=  createdDateCal.getTime();
                return dateFormat.format(returnDate);

            }
            else
            {

                int currentHours=currentDateCal.get(Calendar.HOUR);
                int createdHours=createdDateCal.get(Calendar.HOUR);
                if(currentHours>createdHours)
                {
                  int hoursDiff= currentHours-createdHours ;
                    return hoursDiff+" "+context.getResources().getString(R.string.hours);
                }
                else
                {
                    int currentMinutes=currentDateCal.get(Calendar.MINUTE);
                   int createdMinutes=createdDateCal.get(Calendar.MINUTE);
                   int minutesDiff=currentMinutes-createdMinutes;
                return minutesDiff+" "+context.getResources().getString(R.string.minutes);
             }

            }

        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }
}
