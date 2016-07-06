package com.example.chaya.bontact.Socket.io;

/**
 * Created by chaya on 7/5/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConverastionDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
public class SocketManager {

    public static Socket socket;
    private Context context;
    private Gson gson=null;

    public Socket getSocket()
    {
        return socket;
    }
    public SocketManager(Context context)
    {
        this.context=context;
         gson=new Gson();
        connectSocket();
    }

    public void connectSocket() {
        try {
            socket = IO.socket("https://dev-socket01-eus.azurewebsites.net/");
        } catch (URISyntaxException e) {
        }
        socket.on(Socket.EVENT_CONNECT,connectListener )
               .on("pushmessage",pushMessListener );
        socket.connect();
    }

 Emitter.Listener pushMessListener= new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int id_surfer=0;
            try {
                id_surfer = data.getInt(Contract.Conversation.COLUMN_ID_SURFER);
            } catch (JSONException e) {
                e.printStackTrace();
            }

              /*  String json = gson.toJson(arg);
                InnerConversation innerConversation = gson.fromJson(, InnerConversation.class);
                 id_surfer = innerConversation.getIdSurfer();*/

                ConverastionDataManager converastionDataManager = new ConverastionDataManager();
                Conversation conversation = converastionDataManager.getConversationByIdSurfer(id_surfer);

                if (conversation != null) {
                    InnerConversationDataManager innerConversationDataManager = new InnerConversationDataManager(conversation);
                    innerConversationDataManager.saveData(data.toString());
                } else {
                    if (AgentDataManager.getAgentInstanse() != null)
                        converastionDataManager.getFirstDataFromServer(context, AgentDataManager.getAgentInstanse().getToken());
                }
        }

    };
    Emitter.Listener connectListener=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            AgentDataManager agentDataManager=new AgentDataManager();
            if(agentDataManager.isLoggedIn(context)) {

                JSONObject jsonObject=null;
                try {
                    jsonObject=new JSONObject(gson.toJson(agentDataManager.getAgentInstanse().rep));
                    socket.emit("repConnected",jsonObject,connectEmitCallBack );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
   Ack connectEmitCallBack= new Ack() {
        @Override
        public void call(Object... args) {
            String json=  gson.toJson(args);
        }
    };

}


