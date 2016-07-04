package com.example.chaya.bontact.Helpers;

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
   /* public static String convertChanelTypeTostring(int chanelType)
    {
        switch (chanelType)
        {
         case callback:
        return "callback";
        case sms:
        return "sms";

        //case 33:
         case chat:
        return "chat";
        case email:
        return "email";
        case whatsapp:
        return "whatsapp";
        case webCall:
        return "web call";
        default:

        }
        return null;
    }*/
    public static String getDefultStringByChanelType(int chanelType)
    {
        switch (chanelType)
        {
            case callback:
                return "a callback is waiting";
            case sms:
                return "a sms is waiting";
            case chat:
                return "a chat is waiting";
            case email:
                return "a email is waiting";
            case whatsapp:
                return "a whatsapp is waiting";
            case webCall:
                return "a web call is waiting";
            default:

        }
        return null;
    }
}

