package com.example.chaya.bontact.Socket.io;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.Models.Visitor;
import com.example.chaya.bontact.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by chaya on 7/31/2016.
 */
public class SocketManager {
    private static SocketManager socketManager = new SocketManager();
    public static Socket socket = null;
    public static Context context;
    private Gson gson = null;

    public static SocketManager getInstance() {
        if (socketManager == null)
            socketManager = new SocketManager();

        return socketManager;
    }

    public Socket getSocket() {
        return socket;
    }

    public void initSocketManager(Context context) {
        this.context = context;
        gson = new Gson();
        if (socket == null)
            connectSocket();
    }

    public void connectSocket() {
        try {
            socket = IO.socket(context.getResources().getString(R.string.socket_url));
        } catch (URISyntaxException e) {

        }
        socket.on(Socket.EVENT_CONNECT, connectListener)
                .on(Socket.EVENT_RECONNECT, reconnectListener)
                .on("pushmessage", pushMessListener)
                .on("surferUpdate", surferUpdatedListener)
                .on("surferLeaved", surferLeavedListener);
        socket.connect();
    }

    Emitter.Listener connectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            AgentDataManager agentDataManager = new AgentDataManager();
            if (agentDataManager.isLoggedIn(context)) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(gson.toJson(agentDataManager.getAgentInstanse().rep));
                    socket.emit("agentConnected", jsonObject, connectEmitCallBack);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    Emitter.Listener reconnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String json = args[0].toString();
            Log.d("reconnect", json);
        }
    };
    Ack connectEmitCallBack = new Ack() {
        @Override
        public void call(Object... args) {
            String json = gson.toJson(args);
            try {
                JSONObject jsonObject = new JSONObject(args[0].toString());
                JSONArray visitors = jsonObject.getJSONArray("visitors");
                VisitorsDataManager visitorsDataManager = new VisitorsDataManager();

                for (int i = 0; i < visitors.length(); i++) {
                    Visitor visitor = gson.fromJson(visitors.getJSONObject(i).toString(), Visitor.class);
                    visitorsDataManager.addVisitorToList(visitor);

                    int idSurfer = visitors.getJSONObject(i).getInt("id_Surfer");
                    ConversationDataManager conversationDataManager = new ConversationDataManager(context);
                    if (conversationDataManager.getConversationByIdSurfer(idSurfer) != null)
                        conversationDataManager.updateOnlineState(context, idSurfer, 1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    Emitter.Listener surferUpdatedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                int idSurfer = data.getJSONObject("surfer").getInt("idSurfer");
                ConversationDataManager conversationDataManager = new ConversationDataManager(context);
                if (conversationDataManager.getConversationByIdSurfer(idSurfer) != null)//surfer is in conversation
                    conversationDataManager.updateOnlineState(context, idSurfer, 1);
                //  VisitorsDataManager.addVisitorToList();


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    Emitter.Listener surferLeavedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
//            JSONObject data=(JSONObject) args[0];
          /*  try {
                int idSurfer= data.getJSONObject("surfer").getInt("idSurfer");
                ConversationDataManager converastionDataManager =new ConversationDataManager(context);
                converastionDataManager.updateOnlineState(context,idSurfer,0);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
    };

    Emitter.Listener pushMessListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject data = (JSONObject) args[0];

            int id_surfer = 0;
            try {
                id_surfer = data.getInt(Contract.Conversation.COLUMN_ID_SURFER);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ConversationDataManager conversationDataManager = new ConversationDataManager(context);
            Conversation current_conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);

            if (current_conversation != null) {
                InnerConversationDataManager innerConversationDataManager = new InnerConversationDataManager(context, current_conversation);
                final InnerConversation innerConversation = buildObjectFromJsonData(data, conversationDataManager);
                if (innerConversationDataManager.saveData(innerConversation) == true) {
                    updateConversationDeatails(conversationDataManager, id_surfer, innerConversation);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, " you have a new message from " + innerConversation.getName(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                if (AgentDataManager.getAgentInstanse() != null) {
                    conversationDataManager.getFirstDataFromServer(context, AgentDataManager.getAgentInstanse().getToken());
                    int current_unread_conversation_count = ConversationDataManager.getUnreadConversations(context);
                    ConversationDataManager.setUnreadConversations(context, current_unread_conversation_count + 1);
                }
            }
        }

    };

    private InnerConversation buildObjectFromJsonData(JSONObject data, ConversationDataManager conversationDataManager) {
        InnerConversation innerConversation = new InnerConversation();
        try {

            innerConversation.idSurfer = data.getInt("idSurfer");
            innerConversation.mess = data.getString("message");
            int type = ChanelsTypes.convertStringChannelToInt(data.getString("actionType"));
            innerConversation.actionType = type;
            innerConversation.rep_request = false;
            if (AgentDataManager.getAgentInstanse() != null)
                innerConversation.agentName = AgentDataManager.getAgentInstanse().getName();
            innerConversation.timeRequest = DateTimeHelper.dateFullFormat.format(new Date());
            innerConversation.datatype = data.getInt("datatype");
            innerConversation.from_s = data.getString("from_s");
            Conversation conversation = conversationDataManager.getConversationByIdSurfer(innerConversation.idSurfer);
            innerConversation.name = conversation.getVisitor_name();
            //TODO:update in conversation new data and last type etc.

            return innerConversation;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateConversationDeatails(ConversationDataManager conversationDataManager, int id_surfer, InnerConversation innerConversation) {
        if (conversationDataManager == null)
            return;

        conversationDataManager.updateConversation(context, id_surfer, Contract.Conversation.COLUMN_LAST_TYPE, innerConversation.actionType);//set last type
        if (innerConversation.datatype == ChanelsTypes.sms)
            conversationDataManager.updateConversation(context, id_surfer, Contract.Conversation.COLUMN_PHONE, innerConversation.from_s);//set phone
        if (innerConversation.datatype == ChanelsTypes.email)
            conversationDataManager.updateConversation(context, id_surfer, Contract.Conversation.COLUMN_EMAIL, innerConversation.from_s);//set phone
        //if(innerConversation.datatype==ChanelsTypes.callback)
        // conversationDataManager.updateConversation(context,id_surfer,Contract.Conversation.COLUMN_EMAIL,innerConversation.from_s);//set phone
        //todo:check the type field in data also
        Conversation conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);
        if (conversation != null)
            conversationDataManager.updateConversation(context, id_surfer, Contract.Conversation.COLUMN_UNREAD, ++conversation.unread);
    }

    public void emitNotificationRegister(String token) {
        String[] split = token.split(":");

        if (socket == null)
            return;
        JSONObject jsonObject = new JSONObject();
        if (AgentDataManager.getAgentInstanse() != null && AgentDataManager.getAgentInstanse().getRep() != null)
            try {
                jsonObject.put("idRepresentative", AgentDataManager.getAgentInstanse().getRep().idRepresentive);
                jsonObject.put("registrationId", split[1].toString());
                jsonObject.put("allow", true);
                jsonObject.put("device", "android");
                jsonObject.put("allServices", true);
                jsonObject.put("lastconnect", DateTimeHelper.convertDateToFullFormatString(new Date()));
                jsonObject.put("pushversion", 3);
                Log.d("emit", jsonObject.toString());
                socket.emit("registerDevice", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

    public void emitChatMsg(JSONObject chatMsg, Conversation conversation) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("messageObj", chatMsg)
                    .put("surfer", new JSONObject(gson.toJson(conversation)));
            socket.emit("sendChatTxt", jsonObject, sendChatEmitCallBack);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    Ack sendChatEmitCallBack = new Ack() {
        @Override
        public void call(Object... args) {
            String json = gson.toJson(args);
            Log.d("emit chat", json);
        }
    };


}
