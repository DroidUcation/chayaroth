package com.example.chaya.bontact.Ui.Activities;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Socket.io.SocketManager;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AgentDataManager agentDataManager = new AgentDataManager();
        Intent intent;
        if (agentDataManager.isLoggedIn(this) == true) {
            SocketManager socketManager = new SocketManager(this);
            intent = new Intent(this, MenuActivity.class);
        } else
            intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        AgentDataManager agentDataManager = new AgentDataManager();
        Intent intent;
        if (agentDataManager.isLoggedIn(this) == true) {
            SocketManager socketManager = new SocketManager(this);
            intent = new Intent(this, MenuActivity.class);
        } else
            intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //setContentView(R.layout.activity_splash);

    }*/

    /*@Override
    protected void onResume() {
        super.onResume();

        //todo:bring numbers to dashbord.
        AgentDataManager agentDataManager = new AgentDataManager();
        Intent intent;
        if (agentDataManager.isLoggedIn(this) == true) {
            SocketManager socketManager = new SocketManager(this);
            intent = new Intent(this, MenuActivity.class);
        } else
            intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }*/


}

