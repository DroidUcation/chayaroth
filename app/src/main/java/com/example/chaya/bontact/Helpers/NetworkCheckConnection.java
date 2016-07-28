package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by chaya on 7/27/2016.
 */
public class NetworkCheckConnection {

    public static boolean isConnected( Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
