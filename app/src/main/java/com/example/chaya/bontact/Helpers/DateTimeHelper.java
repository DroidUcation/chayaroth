package com.example.chaya.bontact.Helpers;

import android.content.Context;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by chaya on 6/30/2016.
 */
public class DateTimeHelper {
    public static String getDiffToNow(String dateString, Context context)
    {
        dateString= dateString.replace('T',' ');
        dateString= dateString.replace('Z',' ');
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        Date convertedDate=new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long newDate=new Date().getTime();
        long diffrence=newDate-convertedDate.getTime();
        long days= TimeUnit.MILLISECONDS.toDays(diffrence);
        if(days>0)
        {
            if(days==1)
                return  context.getResources().getString(R.string.yesterday);
            return  days+" "+context.getResources().getString(R.string.days);
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffrence);
        if(minutes==0)
            return context.getResources().getString(R.string.just_now);
        if(minutes>1)
            return ( minutes)+" "+context.getResources().getString(R.string.minutes);
        if(minutes==1)
            return  context.getResources().getString(R.string.minute);
        return null;
    }
}
