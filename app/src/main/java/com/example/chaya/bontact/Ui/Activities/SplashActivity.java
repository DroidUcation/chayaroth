package com.example.chaya.bontact.Ui.Activities;

import android.content.Intent;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Helpers.InitData;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Services.RegisterGcmService;
import com.example.chaya.bontact.Socket.io.SocketManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

    @Override
    protected void onResume() {
        super.onResume();
        AgentDataManager agentDataManager = new AgentDataManager();
        Intent intent;
        if (agentDataManager.isLoggedIn(this) == true) {

            InitData initData = new InitData();
            initData.start(this);

            //  exportDB();
            intent = new Intent(this, MenuActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        //finish();
    }

    private void exportDB() {
        String package_name = "com.example.chaya.bontact";
        String Db_name = Contract.Conversation.TABLE_NAME;
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