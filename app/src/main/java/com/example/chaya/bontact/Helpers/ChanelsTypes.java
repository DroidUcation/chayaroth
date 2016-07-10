package com.example.chaya.bontact.Helpers;

import android.content.Context;

import com.example.chaya.bontact.R;

/**
 * Created by chaya on 7/3/2016.
 */
public class ChanelsTypes {
    public static final int callback=1;
    public static final int sms=2;
    public static final int chat=3;
    public static final int email=4;
    public static final int whatsapp=5;
    public static final int webCall=16;

    public static String getDefultStringByChanelType(Context context, int chanelType)
    {
        switch (chanelType)
        {
            case callback:
                return context.getResources().getString(R.string.unread_callback);
            case sms:
                return context.getResources().getString(R.string.unread_sms);
            case chat:
                return context.getResources().getString(R.string.unread_chat);
            case email:
                return context.getResources().getString(R.string.unread_email);
            case whatsapp:
                return "a whatsapp is waiting";
            case webCall:
                return context.getResources().getString(R.string.unread_webcall);
            default:

        }
        return null;
    }
    public static int getIconByChanelType(int chanelType)
    {
        switch (chanelType)
        {
            case callback:
                return R.string.phone_calling_icon;
            case sms:
                return R.string.sms_icon;
            case chat:
                return R.string.chat_icon;
            case email:
                return R.string.email_icon;
            case whatsapp:
                return R.string.chat_icon;
            case webCall:
                return R.string.phone_calling_icon;
            default:

        }

        return 0;
    }
    public static int convertStringChannelToInt(String chanelString) {
        if (chanelString.toLowerCase().equals("whatsapp"))
            return whatsapp;
        if (chanelString.toLowerCase().equals("chat"))
            return chat;
        if (chanelString.toLowerCase().equals("sms"))
            return sms;
        if (chanelString.toLowerCase().equals("email"))
            return email;
        if (chanelString.toLowerCase().equals("callme"))
            return callback;
       /* if (chanelString.toLowerCase() == "webcall")
            return webCall;*/
        else
        {
         return 0;
        }
    }

}

