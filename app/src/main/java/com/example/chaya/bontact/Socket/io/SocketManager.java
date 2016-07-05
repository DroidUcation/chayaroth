package com.example.chaya.bontact.Socket.io;

/**
 * Created by chaya on 7/5/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Models.Agent;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
public class SocketManager {

    public static Socket socket;
    private Context context;
    public Socket getSocket()
    {
        return socket;
    }
    public SocketManager(Context context)
    {
        this.context=context;
        connectSocket();
    }

    public void connectSocket()
    {
        try {
            socket = IO.socket("https://dev-socket01-eus.azurewebsites.net/");
        } catch (URISyntaxException e) {}
        socket.on("connect",connectListener);
        socket.on("pushmessage",pushListener);

        socket.connect();
    }

    private Emitter.Listener connectListener =new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            AgentDataManager agentDataManager=new AgentDataManager();
            if(agentDataManager.isLoggedIn(context)) {
               socket.emit("repConnected", agentDataManager.getAgentInstanse().rep, new Ack() {
                   @Override
                   public void call(final Object... args) {
                       if(context instanceof Activity)
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,"emit" , Toast.LENGTH_SHORT).show();
                                Log.d("emit",args.toString());
                            }
                        }); }
               });}
        }

    };
    private Emitter.Listener pushListener =new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"push" , Toast.LENGTH_SHORT).show();
                    Log.d("push",args.toString());
                }
            });
        }

    };

}
