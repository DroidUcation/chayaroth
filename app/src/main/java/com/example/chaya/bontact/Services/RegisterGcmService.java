package com.example.chaya.bontact.Services;

import android.app.IntentService;
import android.content.Intent;

import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Socket.io.SocketManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by chaya on 7/31/2016.
 */

public class RegisterGcmService extends IntentService {

    public RegisterGcmService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);

        try {
            String sender = getResources().getString(R.string.SenderId);
            String token = instanceID.getToken(sender, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            SocketManager.getInstance().emitNotificationRegister(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

