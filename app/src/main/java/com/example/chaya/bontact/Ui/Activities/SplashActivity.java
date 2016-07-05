package com.example.chaya.bontact.Ui.Activities;

import android.content.Intent;
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
   // private Socket socket;
    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_splash);
      //  connectSocket();

        //bring numbers to dashbord.
        AgentDataManager agentDataManager=new AgentDataManager();
        Intent intent;
        if(  agentDataManager.isLoggedIn(this)==true) {
            SocketManager socketManager=new SocketManager(this);

            intent = new Intent(this, MenuActivity.class);
        }
        else
            intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }


    /*public void connectSocket()
    {
        try {
            socket = IO.socket("https://prd-socket01-eus.azurewebsites.net/");
        } catch (URISyntaxException e) {}
       socket.on("connect",listener);
        socket.on("selectConversation",listener);
        socket.on("unselectConversation",listener);
        socket.on("refreshSelectConversation",listener);
        socket.on("surferUpdate",listener);

        socket.connect();

    }
    private Emitter.Listener listener =new Emitter.Listener() {
        @Override
        public void call(Object... args) {
         runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SplashActivity.this, "in function", Toast.LENGTH_SHORT).show();
                }
            });
          AgentDataManager agentDataManager=new AgentDataManager();
            agentDataManager.isLoggedIn();
            socket.emit("repConnected",)
        }

    };*/

}
