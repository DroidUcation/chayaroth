package com.example.chaya.bontact.Socket.io;

/**
 * Created by chaya on 7/5/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConverastionDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;

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
            socket = IO.socket("https://prd-socket01-eus.azurewebsites.net/");
        } catch (URISyntaxException e) {
        }
        socket.on(Socket.EVENT_CONNECT,connectListener )
               .on("pushmessage",pushMessListener )
                .on("surferUpdate", surferUpdatedListener)
                .on("surferLeaved", surferLeavedListener);
        socket.connect();
    }


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
            try {
                JSONObject jsonObject=new JSONObject(args[0].toString());
               JSONArray jsonArray= jsonObject.getJSONArray("Surfers");
                for( int i=0;i<jsonArray.length();i++)
                {
                  int idSurfer=  jsonArray.getJSONObject(i).getInt("id_Surfer");
                    ConverastionDataManager converastionDataManager =new ConverastionDataManager(context);
                    converastionDataManager.updateOnlineState(context,idSurfer,1);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    Emitter.Listener surferUpdatedListener =new Emitter.Listener() {
        @Override
        public void call(Object... args) {
        JSONObject data=(JSONObject) args[0];
            try {
                int idSurfer= data.getJSONObject("surfer").getInt("idSurfer");
                ConverastionDataManager converastionDataManager =new ConverastionDataManager(context);
                converastionDataManager.updateOnlineState(context,idSurfer,1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    Emitter.Listener surferLeavedListener=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            /*JSONObject data=(JSONObject) args[0];
            try {
                int idSurfer= data.getJSONObject("surfer").getInt("idSurfer");
                ConverastionDataManager converastionDataManager =new ConverastionDataManager(context);
                converastionDataManager.updateOnlineState(context,idSurfer,0);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
    };

    Emitter.Listener pushMessListener= new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject data = (JSONObject) args[0];

            int id_surfer = 0;
            try {
                id_surfer = data.getInt(Contract.Conversation.COLUMN_ID_SURFER);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ConverastionDataManager converastionDataManager = new ConverastionDataManager(context);
            Conversation current_conversation = converastionDataManager.getConversationByIdSurfer(id_surfer);

            if (current_conversation != null) {
                InnerConversationDataManager innerConversationDataManager = new InnerConversationDataManager(context, current_conversation);
                final InnerConversation innerConversation = buildObjectFromJsonData(data, converastionDataManager);
                if (innerConversationDataManager.saveData(innerConversation) == true) {

                    updateConversationDeatails(converastionDataManager,id_surfer,innerConversation);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, " you have a new message from " + innerConversation.getName(), Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    if (AgentDataManager.getAgentInstanse() != null)
                        converastionDataManager.getFirstDataFromServer(context, AgentDataManager.getAgentInstanse().getToken());
                }
            }
        }

    };
    private InnerConversation buildObjectFromJsonData(JSONObject data,ConverastionDataManager converastionDataManager    )
    {
        InnerConversation innerConversation=new InnerConversation();
        try {

            innerConversation.idSurfer=data.getInt("idSurfer");
            innerConversation.mess=data.getString("message");
            int type=ChanelsTypes.convertStringChannelToInt(data.getString("actionType"));
            innerConversation.actionType=type;
            innerConversation.rep_request=false;
            if(AgentDataManager.getAgentInstanse()!=null)
               innerConversation.agentName= AgentDataManager.getAgentInstanse().getName();
            innerConversation.timeRequest= DateTimeHelper.dateFullFormat.format(new Date());
            innerConversation.datatype=data.getInt("datatype");
            innerConversation.from_s=data.getString("from_s");
           Conversation conversation= converastionDataManager.getConversationByIdSurfer(innerConversation.idSurfer);
            innerConversation.name=conversation.getVisitor_name();
          //TODO:update in conversation new data and last type etc.

            return innerConversation;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void updateConversationDeatails(ConverastionDataManager converastionDataManager, int id_surfer,InnerConversation innerConversation)
    {
    if(converastionDataManager==null)
        return;

    converastionDataManager.updateConversation(context,id_surfer,Contract.Conversation.COLUMN_LAST_TYPE,innerConversation.actionType);//set last type
    if(innerConversation.datatype==ChanelsTypes.sms)
        converastionDataManager.updateConversation(context,id_surfer,Contract.Conversation.COLUMN_PHONE,innerConversation.from_s);//set phone
    if(innerConversation.datatype==ChanelsTypes.email)
        converastionDataManager.updateConversation(context,id_surfer,Contract.Conversation.COLUMN_EMAIL,innerConversation.from_s);//set phone
    //if(innerConversation.datatype==ChanelsTypes.callback)
       // converastionDataManager.updateConversation(context,id_surfer,Contract.Conversation.COLUMN_EMAIL,innerConversation.from_s);//set phone
    //todo:check the type field in data also
        converastionDataManager.updateConversation(context,id_surfer,Contract.Conversation.COLUMN_UNREAD,1);
    }


}


