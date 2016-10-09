package com.example.chaya.bontact.Socket.io;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.Helpers.ChannelsTypes;
import com.example.chaya.bontact.Helpers.DatesHelper;
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
                    .on("surferLeaved", surferLeavedListener)
                    .on("selectConversation", selectConversationListener)
                    .on("unselectConversation", unSelectConversationListener)
                    .on("unselectConversationRep", unSelectConversationRepListener)
                    .on("refreshSelectConversation", refreshSelectListener)
                    .on("typing", typingListener);
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
                    jsonObject = new JSONObject(gson.toJson(agentDataManager.getAgentInstance().rep));
                    if (socket != null)
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
                // ConversationDataManager conversationDataManager = new ConversationDataManager(context);
                for (int i = 0; i < visitors.length(); i++) {
                    Visitor visitor = gson.fromJson(visitors.getJSONObject(i).toString(), Visitor.class);
                    syncVisitorWithConversation(visitor);
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
                syncVisitorWithConversation(visitor);
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
                    converastionDataManager.updateOnlineState(id_surfer, false);
                VisitorsDataManager.removeVisitorFromList(context, VisitorsDataManager.getVisitorByIdSurfer(id_surfer));
//                Toast.makeText(context, "surfer leaved", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void syncVisitorWithConversation(Visitor visitor) {
        ConversationDataManager conversationDataManager = new ConversationDataManager(context);
        Conversation conversation = conversationDataManager.getConversationByIdSurfer(visitor.idSurfer);
        if (conversation != null) {//surfer is in conversation
            conversationDataManager.updateOnlineState(visitor.idSurfer, true);
            visitor.isNew = false;
            visitor.displayName = conversation.displayname;
            visitor.avatar = conversation.avatar;
        } else {//new visitor
            visitor.isNew = true;
            visitor.displayName = "#" + visitor.idSurfer;
        }
    }

    Emitter.Listener selectConversationListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //Log.d("select", args.toString());
            JSONObject jsonObject = (JSONObject) args[0];
            try {
                jsonObject = jsonObject.getJSONObject("visitor");
                int idSurfer = jsonObject.optInt("idSurfer", 0);
                int idAgent = jsonObject.getJSONObject("agentselected").optInt("id", 0);
                if (idSurfer != 0) {
                    ConversationDataManager conversationDataManager = new ConversationDataManager(context);
                    // conversationDataManager.updateSelectedByAgent(idSurfer, idAgent, true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Emitter.Listener unSelectConversationListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("un select", args.toString());
            JSONObject jsonObject = (JSONObject) args[0];
            try {
                jsonObject = jsonObject.getJSONObject("visitor");
                int idSurfer = jsonObject.optInt("idSurfer", 0);
                int idAgent = jsonObject.optInt("agentselected", 0);
                if (idSurfer != 0 && idAgent != AgentDataManager.getAgentInstance().getIdRep()) {
                    ConversationDataManager conversationDataManager = new ConversationDataManager(context);
                    //conversationDataManager.updateSelectedByAgent(idSurfer, idAgent, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Emitter.Listener unSelectConversationRepListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("un select", args.toString());
            JSONObject jsonObject = (JSONObject) args[0];
            try {
                int idAgent = jsonObject.getInt("id");
                if (idAgent != 0) {
                    ConversationDataManager conversationDataManager = new ConversationDataManager(context);
                    //  conversationDataManager.updateUnSelectedByAgentForAll(idAgent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
                    updateConversationDetails(conversationDataManager, id_surfer, innerConversation);
                }
            } else {
                if (AgentDataManager.getAgentInstance() != null) {
                    conversationDataManager.getFirstDataFromServer(context, AgentDataManager.getAgentInstance().getToken());
                    //todo: use new api
                }
            }

          /*  if (id_surfer != ConversationDataManager.selectedIdConversation && current_conversation != null && current_conversation.unread == 0)
            {
                int current_unread_conversation_count = ConversationDataManager.getAllUnreadConversations(context);
                ConversationDataManager.setAllUnreadConversations(context, current_unread_conversation_count + 1);
            }*/
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
            int type = ChannelsTypes.convertStringChannelToInt(data.optString("type", null));
            innerConversation.actionType = type;
            innerConversation.mess = data.optString("message", ChannelsTypes.getDeafultMsgByChanelType(context, type));
            innerConversation.rep_request = false;
            if (AgentDataManager.getAgentInstance() != null)
                innerConversation.agentName = AgentDataManager.getAgentInstance().getName();
            innerConversation.timeRequest = DatesHelper.getCurrentStringDate();
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

    private void updateConversationDetails(ConversationDataManager conversationDataManager, int id_surfer, InnerConversation innerConversation) {
        if (conversationDataManager == null)
            return;
        Conversation conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);
        if (conversation == null)
            return;
        conversationDataManager.updateLastType(id_surfer, innerConversation.actionType);//set last type
        conversationDataManager.updateLastDate(id_surfer, innerConversation.timeRequest);
        if (innerConversation.mess != null && (innerConversation.actionType != ChannelsTypes.callback || innerConversation.actionType != ChannelsTypes.webCall))
            conversationDataManager.updateLastMessage(id_surfer, innerConversation.mess);
        if (innerConversation.actionType == ChannelsTypes.sms && (conversation.phone == null || !conversation.phone.equals(innerConversation.from_s)))
            conversationDataManager.updatePhoneNumber(id_surfer, innerConversation.from_s);//set phone
        if (innerConversation.actionType == ChannelsTypes.email && (conversation.email == null || !conversation.email.equals(innerConversation.from_s)))
            conversationDataManager.updateEmail(id_surfer, innerConversation.from_s);//set phone
        if (conversation.idSurfer != ConversationDataManager.selectedIdConversation) {
            conversationDataManager.updateUnread(id_surfer, conversation.unread + 1);//set unread
        } else {
            conversationDataManager.syncUnreadConversation(conversation);
        }
        Log.d("from_s", innerConversation.from_s);
    }

    public void emitNotificationRegister(String token) {

        if (socket == null)
            return;
        JSONObject jsonObject = new JSONObject();
        if (AgentDataManager.getAgentInstance() != null && AgentDataManager.getAgentInstance().getRep() != null)
            try {
                jsonObject.put("idRepresentative", AgentDataManager.getAgentInstance().getRep().idRepresentive);
                jsonObject.put("registrationId", token);
                jsonObject.put("allow", true);
                jsonObject.put("device", "android");
                jsonObject.put("allServices", true);
                jsonObject.put("lastconnect", DatesHelper.getCurrentStringDateInGmtZero());
                jsonObject.put("pushversion", 3);
                if (socket != null) {
                    Log.d("emit", jsonObject.toString());
                    socket.emit("registerDevice", jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

    public void emitChatMsg(JSONObject chatMsg, Conversation conversation) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("messageObj", chatMsg)
                    .put("surfer", new JSONObject(gson.toJson(conversation)));
            if (socket != null) {
                socket.emit("sendChatTxt", jsonObject, sendChatEmitCallBack);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void inviteToChat(final int id_surfer) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            Agent agent = AgentDataManager.getAgentInstance();
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
            if (socket != null) {
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    public void emitSelectConversationState(Conversation conversation, boolean state) {
        //do in background
        if (conversation == null)
            return;
        Gson gson = new Gson();
        try {
            JSONObject visitor;
            String s = gson.toJson(conversation);
            visitor = new JSONObject(s);
            visitor.put("agentselected", new JSONObject()
                    .put("id", AgentDataManager.getAgentInstance().getIdRep())
                    .put("name", AgentDataManager.getAgentInstance().getName()));
            JSONObject jsonObject = new JSONObject().put("visitor", visitor);
            if (socket != null) {
                if (state)
                    socket.emit("selectConversation", jsonObject, selectConversationCallback);
                else
                    socket.emit("unselectConversation", jsonObject, selectConversationCallback);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Ack selectConversationCallback = new Ack() {
        @Override
        public void call(Object... args) {
            String json = gson.toJson(args);
            //Log.d("select ", json);
        }
    };

    public void refreshSelectConversation() {
        if (socket != null) {
            socket.emit("refreshSelectConversation", new JSONObject());
        }
    }

    Emitter.Listener refreshSelectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            int id = ConversationDataManager.selectedIdConversation;
            if (id != 0) {
                ConversationDataManager conversationDataManager = new ConversationDataManager(context);
                emitSelectConversationState(conversationDataManager.getConversationByIdSurfer(id), true);
            }
        }
    };
    public Emitter.Listener typingListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            String name = null;
            int id_surfer = 0;
            boolean state;
            boolean isVisitor;

            JSONObject jsonObject = data.optJSONObject("agent");
            isVisitor = data.optBoolean("isVisitor");
            if (jsonObject != null && !isVisitor)
                name = jsonObject.optString("name");

            JSONObject surfer = data.optJSONObject("surfer");
            if (surfer != null) {
                id_surfer = surfer.optInt("idSurfer");
            }
            state = data.optBoolean("mode");
            if (name == null && isVisitor)
                name = "visitor";
            if (id_surfer != 0 && context != null && (name != null || !state)) {
                Intent intent = new Intent(context.getResources().getString(R.string.action_typing));
                intent.setType("*/*");
                intent.putExtra(context.getString(R.string.typing_name_key), name);
                intent.putExtra(context.getString(R.string.id_surfer), id_surfer);
                intent.putExtra(context.getString(R.string.typing_state_key), state);
                context.sendBroadcast(intent);
            }

            Log.d("typing", "klkl");
        }
    };
}



