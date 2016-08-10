package com.example.chaya.bontact.Helpers;

import android.content.Context;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Socket.io.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaya on 7/25/2016.
 */
public class SendResponseHelper {
    Agent agent;

    public SendResponseHelper() {
        agent = AgentDataManager.getAgentInstanse();

    }

    public void sendCallBack(Context context, int idSurfer, String telephone) {
        String url = null;
        if (agent == null) {
            agent = AgentDataManager.getAgentInstanse();
        }
        url = context.getResources().getString(R.string.dev_domain_api) +
                "returnchannel/callback/" +
                agent.getToken() +
                "?surferid=" + idSurfer +
                "&name=" + agent.getName() +
                "&telephone=" + telephone;

       // OkHttpRequests okHttpRequests = new OkHttpRequests(url, this);

//todo:handel a case that a call back don't succsess

    }

    public void sendChat(Context context, String txt, int id_surfer) {
        if (agent == null) {
            agent = AgentDataManager.getAgentInstanse();
        }
        if (agent.getRep() != null) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("rep_Sur", true)
                        .put("systemMsg", false)
                        .put("id_Representive", agent.getRep().idRepresentive)
                        .put("name", agent.getName())
                        .put("txt", txt)
                        .put("agentReply", true)
                        .put("id_Surfer", id_surfer)
                        .put("id_Call", 0)
                        .put("id_Customer", agent.getRep().idCustomer);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            ConversationDataManager conversationDataManager = new ConversationDataManager(context);
            SocketManager.getInstance().emitChatMsg(postData, conversationDataManager.getConversationByIdSurfer(id_surfer));
        }
    }

    public void sendSmsOrEmail(Context context, int ChannelType, String contentMsg, int idSurfer) {
        String url = null;
        if (agent == null) {
            agent = AgentDataManager.getAgentInstanse();
        }
        url = context.getResources().getString(R.string.dev_domain_api) +
                "returnchannel/" +
                ChanelsTypes.convertChannelTypeToString(context, ChannelType) + "/" +
                agent.getToken();

        JSONObject postDataObject = new JSONObject();
        try {
            postDataObject.put("surferid", idSurfer).put("name", agent.getName()).put("content", contentMsg);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        //OkHttpRequests okHttpRequests = new OkHttpRequests(url, this, postDataObject.toString());
    }


    public boolean isAllowedCurrentChannelResponse(Conversation conversation, int current_channel) {

        if (conversation == null)
            return false;
        switch (current_channel) {
            case ChanelsTypes.callback:
                return conversation.phone == null ? false : true;
            case ChanelsTypes.sms:
                return conversation.phone == null ? false : true;
            case ChanelsTypes.chat:
                return conversation.isOnline == false ? false : true;
            case ChanelsTypes.email:
                return conversation.email == null ? false : true;
        }
        return false;
    }


}
