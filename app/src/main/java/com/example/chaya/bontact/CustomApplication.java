package com.example.chaya.bontact;

import android.app.Application;

import io.intercom.android.sdk.Intercom;

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Intercom.initialize(this, "android_sdk-1ecf63064f44eba74a160a7617cca37c28042195", "seajbd1w");
    }


}