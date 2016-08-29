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

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Activities.SplashActivity;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaya on 8/3/2016.
 */
public class GCMPushReceiverService extends GcmListenerService {
    public static int NEW_VISITOR = 1;
    public static int NEW_MESSAGE = 0;
    public static int newMsgNotificationsCount = 0;
    public static int newMsgId = 0;
    public static int newVisitorNotificationsCount = 0;
    public static int newVisitorId = 0;
    // public static int id = 0;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("data", data.toString());

        String message = data.getString("message");
        String id_surfer = null;
        int push_type = 0;
        String bontactData = data.getString("bontactdata");
        try {
            JSONObject object = new JSONObject(bontactData);
            id_surfer = object.getJSONObject("surfer").getString("idSurfer");
            if (object.optString("pushtype", "new_message").equals("new visitor"))
                push_type = NEW_VISITOR;
            else
                push_type = NEW_MESSAGE;

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        sendNotification(message, Integer.parseInt(id_surfer), push_type);
    }

    public static void resetAllCounters() {
        newMsgNotificationsCount = 0;
        newVisitorNotificationsCount = 0;

    }

    private void sendNotification(String message, int id_surfer, int push_type) {
        Intent intent = new Intent(this, SplashActivity.class);
        Bundle b = new Bundle();
        b.putInt(Contract.InnerConversation.COLUMN_ID_SURFUR, id_surfer); //Your id
        b.putInt(getResources().getString(R.string.push_notification_type), push_type);
        intent.putExtras(b);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (push_type == NEW_MESSAGE) {
            newMsgNotificationsCount++;
            NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.bontact_launcher)
                    .setContentTitle("you have " + newMsgNotificationsCount + " new messages")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSound(sound);

            notificationManager.notify(newMsgId, noBuilder.build()); //0 = ID of notification
        } else {
            newVisitorNotificationsCount++;
            NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.bontact_launcher)
                    .setContentTitle("you have " + newVisitorNotificationsCount + " new visitors")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSound(sound);

            notificationManager.notify(newVisitorId, noBuilder.build()); //0 = ID of notification
        }

    }


}
