package com.example.chaya.bontact.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Activities.SplashActivity;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by chaya on 8/3/2016.
 */
public class GCMPushReceiverService extends GcmListenerService {

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("data", data.toString());
        //Getting the message from the bundle
        String message = data.getString("message");
        sendNotification(message);
        //Displaying a notiffication with the message
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.bontact_launcher)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent).
                        setSound(sound);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }


}
