package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.widget.Toast;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
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
    InnerConversationDataManager innerConversationDataManager;

    public SendResponseHelper() {
        agent = AgentDataManager.getAgentInstance();
    }

    public void sendCallBack(Context context, int idSurfer, String telephone) {
        String url = null;
        if (agent == null) {
            agent = AgentDataManager.getAgentInstance();
        }
        url = context.getResources().getString(R.string.dev_domain_api) +
                "returnchannel/callback/" +
                agent.getToken() +
                "?surferid=" + idSurfer +
                "&name=" + agent.getName() +
                "&telephone=" + telephone;

        OkHttpRequests okHttpRequests = new OkHttpRequests(url, null);


    }

    public void sendChat(Context context, String msg, int id_surfer) {

        if (agent == null) {
            agent = AgentDataManager.getAgentInstance();
        }

        innerConversationDataManager = new InnerConversationDataManager(context, id_surfer);
        innerConversationDataManager.addTextMsgToList(ChannelsTypes.chat, msg, false);

        if (agent.getRep() != null) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("rep_Sur", true)
                        .put("systemMsg", false)
                        .put("id_Representive", agent.getRep().idRepresentive)
                        .put("name", agent.getName())
                        .put("txt", msg)
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

    public void sendSms(Context context, String msg, int id_surfer) {
        innerConversationDataManager = new InnerConversationDataManager(context, id_surfer);
        innerConversationDataManager.addTextMsgToList(ChannelsTypes.sms, msg, false);
        sendSmsOrEmail(context, ChannelsTypes.sms, msg, id_surfer);
    }

    public void sendEmail(Context context, String msg, int id_surfer) {
        innerConversationDataManager = new InnerConversationDataManager(context, id_surfer);
        innerConversationDataManager.addTextMsgToList(ChannelsTypes.email, msg, false);
        sendSmsOrEmail(context, ChannelsTypes.email, msg, id_surfer);
    }

    private void sendSmsOrEmail(Context context, int ChannelType, String contentMsg, int idSurfer) {
        String url = null;
        if (agent == null) {
            agent = AgentDataManager.getAgentInstance();
        }
        url = context.getResources().getString(R.string.dev_domain_api) +
                "returnchannel/" +
                ChannelsTypes.convertChannelTypeToString(context, ChannelType) + "/" +
                agent.getToken();

        JSONObject postDataObject = new JSONObject();
        try {
            postDataObject.put("surferid", idSurfer).put("name", agent.getName()).put("content", contentMsg);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        OkHttpRequests okHttpRequests = new OkHttpRequests(url, null, postDataObject.toString());
    }

    public boolean isAllowedChannelToResponse(Conversation conversation, int current_channel) {

        if (conversation == null)
            return false;
        switch (current_channel) {
            case ChannelsTypes.callback:
                return conversation.phone == null ? false : true;
            case ChannelsTypes.sms:
                return conversation.phone == null ? false : true;
            case ChannelsTypes.chat:
                return conversation.isOnline == false ? false : true;
            case ChannelsTypes.email:
                return conversation.email == null ? false : true;
        }
        return false;
    }


}
