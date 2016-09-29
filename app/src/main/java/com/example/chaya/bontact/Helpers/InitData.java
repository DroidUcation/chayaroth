package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.AgentListDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Services.RegisterGcmService;
import com.example.chaya.bontact.Socket.io.SocketManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


/**
 * Created by chaya on 7/27/2016.
 */
public class InitData {

    Context context;

    public void start(Context context) {
        this.context = context;
        if (SocketManager.getInstance() != null)
            SocketManager.getInstance().initSocketManager(context);
        SharedPreferences preferences = context.getSharedPreferences(context.getResources().getString(R.string.gcm_pref), context.MODE_PRIVATE);
        String gcmToken = preferences.getString(context.getResources().getString(R.string.gcm_token), null);
        if (gcmToken == null) {
            Intent intentService = new Intent(context, RegisterGcmService.class);
            context.startService(intentService);
        }
        AgentDataManager agentDataManager = new AgentDataManager();
        String token = agentDataManager.getAgentToken(context);
        if (token != null) {
            ConversationDataManager conversationDataManager = new ConversationDataManager(context);
            conversationDataManager.getAllUnreadFromServer(context);
            conversationDataManager.getFirstDataFromServer(context, token);
            AgentListDataManager.getAllAgents(context);
        }

        exportDB();

    }

    private void exportDB() {
        String package_name = "com.example.chaya.bontact";
        String Db_name = DbBontact.DBName;
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + package_name + "/databases/" + Db_name;
        String backupDBPath = Db_name;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
