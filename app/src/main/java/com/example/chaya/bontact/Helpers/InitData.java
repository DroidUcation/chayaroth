package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponseToUi;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Socket.io.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaya on 7/27/2016.
 */
public class InitData  {

    Context context;

    public void start(Context context) {
        this.context = context;
        new SocketManager(context);

    }



}
