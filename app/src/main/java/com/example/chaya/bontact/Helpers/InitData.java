package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Services.RegisterGcmService;
import com.example.chaya.bontact.Socket.io.SocketManager;


/**
 * Created by chaya on 7/27/2016.
 */
public class InitData {

    Context context;

    public void start(Context context) {
        this.context = context;
        if (SocketManager.getInstance() != null)
            SocketManager.getInstance().initSocketManager(context);
        SharedPreferences preferences = context.getSharedPreferences(context.getResources().getString(R.string.gcm_token), context.MODE_PRIVATE);
        String gcmToken = preferences.getString(context.getResources().getString(R.string.token), null);
        if (gcmToken == null) {
        Intent intentService = new Intent(context, RegisterGcmService.class);
        context.startService(intentService);
        }
        AgentDataManager agentDataManager= new AgentDataManager();
         String token = agentDataManager.getAgentToken(context);
        if (token != null) {
            ConversationDataManager conversationDataManager = new ConversationDataManager(context);
            conversationDataManager.getAllUnreadFromServer(context);
            conversationDataManager.getFirstDataFromServer(context, token);
        }
    }


}
