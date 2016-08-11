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
import android.view.View;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.InitData;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Activities.InnerConversationActivity;
import com.example.chaya.bontact.Ui.Activities.MenuActivity;
import com.example.chaya.bontact.Ui.Activities.SplashActivity;
import com.example.chaya.bontact.Ui.Fragments.InboxFragment;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 8/3/2016.
 */
public class GCMPushReceiverService extends GcmListenerService {

    public static int notificationsCount = 0;
    public static int id = 0;
    public static List<String> strings = new ArrayList<>();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("data", data.toString());

        String message = data.getString("message");
        String id_surfer = null;
        String bontactData = data.getString("bontactdata");
        try {
            JSONObject object = new JSONObject(bontactData);
            id_surfer = object.getJSONObject("surfer").getString("idSurfer");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendNotification(message, Integer.parseInt(id_surfer));
    }


    private void sendNotification(String message, int id_surfer) {


        Intent intent = new Intent(this, InnerConversationActivity.class);
        Bundle b = new Bundle();
        b.putInt(Contract.InnerConversation.COLUMN_ID_SURFUR, id_surfer); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationsCount++;
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.bontact_launcher)
                .setContentTitle("u have " + notificationsCount + " new messages")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(sound);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (String s : strings)
            inboxStyle.addLine(s);
        noBuilder.setStyle(inboxStyle);
        notificationManager.notify(id, noBuilder.build()); //0 = ID of notification
    }


}
