package com.example.chaya.bontact.Socket.io;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.Models.Visitor;
import com.example.chaya.bontact.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

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

    private SocketManager() {
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
            socket.on(Socket.EVENT_CONNECT, connectListener)
                    .on(Socket.EVENT_DISCONNECT, disconnectListener)
                    .on(Socket.EVENT_RECONNECT, reconnectListener)
                    .on("pushmessage", pushMessListener)
                    .on("surferUpdate", surferUpdatedListener)
                    .on("surferLeaved", surferLeavedListener);
            socket.connect();
        } catch (URISyntaxException e) {
            int x = 0;
        }

    }

    Emitter.Listener disconnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("DISCONNECT", args.toString());
        }
    };

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

                VisitorsDataManager visitorsDataManager = new VisitorsDataManager(context);
                ConversationDataManager conversationDataManager = new ConversationDataManager(context);
                for (int i = 0; i < visitors.length(); i++) {
                    Visitor visitor = gson.fromJson(visitors.getJSONObject(i).toString(), Visitor.class);
                    int idSurfer = visitor.idSurfer;
                    Conversation conversation = conversationDataManager.getConversationByIdSurfer(idSurfer);
                    if (conversation != null) {//surfer is in conversation
                        conversationDataManager.updateOnlineState(idSurfer, 1);
                        visitor.isNew = false;
                        visitor.displayName = conversation.displayname;
                    } else {//new visitor
                        visitor.isNew = true;
                        visitor.displayName = "#" + idSurfer;
                    }
                    visitorsDataManager.addVisitorToList(context, visitor);
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
                Visitor visitor = gson.fromJson(data.getJSONObject("surfer").toString(), Visitor.class);
                ConversationDataManager conversationDataManager = new ConversationDataManager(context);
                Conversation conversation = conversationDataManager.getConversationByIdSurfer(idSurfer);
                if (conversation != null) {//surfer is in conversation
                    conversationDataManager.updateOnlineState(idSurfer, 1);
                    visitor.isNew = false;
                    visitor.displayName = conversation.displayname;
                } else {//new visitor
                    visitor.isNew = true;
                    visitor.displayName = "#" + idSurfer;
                }
                VisitorsDataManager.addVisitorToList(context, visitor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Emitter.Listener surferLeavedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null) {
                int id_surfer = Integer.parseInt(args[0].toString());
                Log.d("id", id_surfer + "");
                ConversationDataManager converastionDataManager = new ConversationDataManager(context);
                if (converastionDataManager.getConversationByIdSurfer(id_surfer) != null)
                    converastionDataManager.updateOnlineState(id_surfer, 0);
                VisitorsDataManager.removeVisitorFromList(context, VisitorsDataManager.getVisitorByIdSurfer(id_surfer));

            }
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
            final Conversation current_conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);

            if (current_conversation != null) {
                InnerConversationDataManager innerConversationDataManager = new InnerConversationDataManager(context, current_conversation);
                final InnerConversation innerConversation = buildObjectFromJsonData(data, conversationDataManager);
                if (innerConversationDataManager.saveData(innerConversation) == true) {
                    updateConversationDetails(conversationDataManager, id_surfer, innerConversation,data.optString("type"));
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, " you have a new message from " + current_conversation.displayname, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                if (AgentDataManager.getAgentInstanse() != null) {
                    conversationDataManager.getFirstDataFromServer(context, AgentDataManager.getAgentInstanse().getToken());
                    int current_unread_conversation_count = ConversationDataManager.getAllUnreadConversations(context);
                    ConversationDataManager.setAllUnreadConversations(context, current_unread_conversation_count + 1);
                }
            }
        }

    };
    Ack sendChatEmitCallBack = new Ack() {
        @Override
        public void call(Object... args) {
            String json = gson.toJson(args);
            Log.d("emit chat", json);
        }
    };

    private InnerConversation buildObjectFromJsonData(JSONObject data, ConversationDataManager conversationDataManager) {
        InnerConversation innerConversation = new InnerConversation();
        try {
            innerConversation.id = InnerConversationDataManager.getIdAsPlaceHolder();
            innerConversation.idSurfer = data.getInt("idSurfer");
            int type = ChanelsTypes.convertStringChannelToInt(data.optString("type", null));
            innerConversation.actionType = type;
            innerConversation.mess = data.optString("message", ChanelsTypes.getDeafultMsgByChanelType(context, type));
            innerConversation.rep_request = false;
            if (AgentDataManager.getAgentInstanse() != null)
                innerConversation.agentName = AgentDataManager.getAgentInstanse().getName();
            innerConversation.timeRequest = DateTimeHelper.getCurrentStringDateInGmtZero();
            innerConversation.datatype = data.optInt("datatype", 1);
            innerConversation.from_s = data.optString("from_s", "visitor");
            Conversation conversation = conversationDataManager.getConversationByIdSurfer(innerConversation.idSurfer);
            innerConversation.name = conversation.getVisitor_name();
            //TODO:update in conversation new data and last type etc.

            return innerConversation;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateConversationDetails(ConversationDataManager conversationDataManager, int id_surfer, InnerConversation innerConversation, String type) {
        if (conversationDataManager == null)
            return;
        Conversation conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);
        if (conversation == null)
            return;
        //int actionType = ChanelsTypes.convertStringChannelToInt(type);
        conversationDataManager.updateLastType(id_surfer,innerConversation.actionType);//set last type
        if (innerConversation.actionType == ChanelsTypes.sms && (conversation.phone == null || !conversation.phone.equals(innerConversation.from_s)))
            conversationDataManager.updatePhoneNumber(id_surfer, innerConversation.from_s);//set phone
        if (innerConversation.actionType == ChanelsTypes.email && (conversation.email == null || !conversation.email.equals(innerConversation.from_s)))
            conversationDataManager.updateEmail(id_surfer, innerConversation.from_s);//set phone
        conversationDataManager.updateUnread(id_surfer, conversation.unread + 1);//set unread
        Log.d("from_s", innerConversation.from_s);
    }

    public void emitNotificationRegister(String token) {

        if (socket == null)
            return;
        JSONObject jsonObject = new JSONObject();
        if (AgentDataManager.getAgentInstanse() != null && AgentDataManager.getAgentInstanse().getRep() != null)
            try {
                jsonObject.put("idRepresentative", AgentDataManager.getAgentInstanse().getRep().idRepresentive);
                jsonObject.put("registrationId", token);
                jsonObject.put("allow", true);
                jsonObject.put("device", "android");
                jsonObject.put("allServices", true);
                jsonObject.put("lastconnect", DateTimeHelper.getCurrentStringDateInGmtZero());
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

    public void inviteToChat(final int id_surfer) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            Agent agent = AgentDataManager.getAgentInstanse();
            if (agent != null && agent.getRep() != null)
                jsonObject.put("rep_Sur", true)
                        .put("systemMsg", false)
                        .put("id_Representive", agent.getRep().idRepresentive)
                        .put("name", agent.getRep().name)
                        .put("txt", agent.getSettings().openingStatement)
                        .put("agentReply", true)
                        .put("id_Surfer", id_surfer)
                        .put("id_Call", 0)
                        .put("id_Customer", agent.getRep().idCustomer);
            data.put("surfer", new JSONObject(gson.toJson(VisitorsDataManager.getVisitorByIdSurfer(id_surfer))))
                    .put("preChat", new JSONObject())
                    .put("messageObj", jsonObject);

            socket.emit("inviteStartChat", data, new Ack() {
                        @Override
                        public void call(Object... args) {
                            JSONObject json = null;
                            try {
                                json = new JSONObject(args[0].toString());
                                Intent intent = new Intent(context.getResources().getString(R.string.invite_complete_action));
                                intent.setType("*/*");
                                intent.putExtra(context.getResources().getString(R.string.is_successed_key), json.getBoolean("status"));
                                intent.putExtra(context.getResources().getString(R.string.id_surfer), id_surfer);
                                context.sendBroadcast(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }
}



