package com.example.chaya.bontact.BroadCastRecivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.chaya.bontact.Ui.Activities.SplashActivity;

/**
 * Created by chaya on 8/3/2016.
 */
public class NewVisitorReciver extends BroadcastReceiver  {

public static final String ACTION_NEW_VISITOR   = "com.example.chaya.bontact.newVisitor";
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            //String message = intent.getStringExtra("message");
            Toast.makeText(context, "new visitor", Toast.LENGTH_SHORT).show();

            //do other stuff here
        }

}
