package com.example.chaya.bontact.DataManagers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.Ui.Fragments.OnlineVisitorsFragment;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaya on 6/22/2016.
 */
public class AgentDataManager {

    private static Agent agent = null;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public static Agent getAgentInstance() {
        if (agent == null)
            return new Agent();
        return agent;
    }

    public static String getAgentAvatarUrl() {
        if (agent != null && agent.getRep() != null)
            return agent.getRep().avatar;
        return null;
    }

    private static void setNewAgent() {
        agent = new Agent();
    }

    public static boolean getMsgPushNotification() {

        return getAgentInstance().settings.msgPushNotification;
    }

    public static boolean getVisitorPushNotification() {
        return getAgentInstance().settings.visitorPushNotification;
    }

    public AgentDataManager() {
        agent = getAgentInstance();
        setContext(null);
    }

    public void getDataFromServer(String userName, String password, Context context) {
        this.context = context;

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(context.getResources().getString(R.string.base_api))
                .appendPath(context.getResources().getString(R.string.rout_api))
                .appendPath(context.getResources().getString(R.string.login_api))
                .appendQueryParameter("username", userName)
                .appendQueryParameter("pass", password);
        String url = builder.build().toString();
      /*  String url = context.getResources().getString(R.string.domain_api) + context.getResources().getString(R.string.login_api);
        url += "?username=" + userName + "&pass=" + password;
*/
        OkHttpRequests requests = new OkHttpRequests(url, loginCallback);

    }

    public boolean saveData(String response, Context context) {
        this.context = context;
        Gson gson = new Gson();
        Agent agentFromJson = gson.fromJson(response, Agent.class);
        agentFromJson.token = agentFromJson.token.replace('/', '+');
        agent.rep = agentFromJson.rep;
        agent.token = agentFromJson.token;
        agent.settings = agentFromJson.settings;
        agent.settings.msgPushNotification = true;
        agent.settings.visitorPushNotification = false;
        if (agent != null && context != null) {
            // String agent_str= gson.toJson(getAgentInstance());
            SharedPreferences Preferences = context.getSharedPreferences(context.getResources().getString(R.string.sp_user_details), context.MODE_PRIVATE);
            SharedPreferences.Editor editor = Preferences.edit();
            editor.clear();
            editor.putString(context.getResources().getString(R.string.agent), gson.toJson(getAgentInstance().getRep()));
            editor.putString(context.getResources().getString(R.string.settings), gson.toJson(getAgentInstance().getSettings()));
            editor.putString(context.getResources().getString(R.string.token), getAgentInstance().getToken());
            editor.apply();
           /* gson.fromJson(Preferences.getString(context.getResources().getString(R.string.agent),null),Agent.Rep.class);
            gson.fromJson(Preferences.getString(context.getResources().getString(R.string.settings),null),Agent.Settings.class);*/
            return true;
        }
        return false;
    }

    public String getAgentToken(Context context) {
        if (isLoggedIn(context) == true)
            if (agent != null)
                return getAgentInstance().getToken();

        return null;
    }

    public String getAgentName(Context context) {
        if (isLoggedIn(context) == true)
            if (agent != null)
                return getAgentInstance().getName();
        return null;
    }

    public int getAgentId(Context context) {
        if (isLoggedIn(context) == true)
            if (agent != null && getAgentInstance().getRep() != null)
                return getAgentInstance().getRep().idRepresentive;
        return 0;
    }

    public boolean isLoggedIn(Context context) {
        this.context = context;
        SharedPreferences Preferences = context.getSharedPreferences(context.getResources().getString(R.string.sp_user_details), context.MODE_PRIVATE);
        String token = Preferences.getString(context.getResources().getString(R.string.token), null);
        if (token != null)//user is logged in
        {
            //set the agent object
            if (agent.getToken() == null) {
                Gson gson = new Gson();
                agent.token = token;
                agent.rep = gson.fromJson(Preferences.getString(context.getResources().getString(R.string.agent), null), Agent.Rep.class);
                agent.settings = gson.fromJson(Preferences.getString(context.getResources().getString(R.string.settings), null), Agent.Settings.class);
            }
            return true;
        }
        return false;
    }

    public static boolean logOut(Context context) {

        //clear agent details
        SharedPreferences Preferences = context.getSharedPreferences(context.getResources().getString(R.string.sp_user_details), context.MODE_PRIVATE);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.clear().commit();
        setNewAgent();
        //claer db
        context.getContentResolver().delete(Contract.Conversation.INBOX_URI, null, null);
        context.getContentResolver().delete(Contract.InnerConversation.INNER_CONVERSATION_URI, null, null);
        ConversationDataManager.conversationList = null;
        ConversationDataManager.unread_conversations = 0;
        VisitorsDataManager.visitorsList = null;

        return true;

    }

    ServerCallResponse loginCallback = new ServerCallResponse() {
        @Override
        public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
            if (isSuccsed == false)
                sendResToUi(false, null, ErrorType.network_problems);

            try {
                if (response == null) {
                    sendResToUi(false, null, ErrorType.network_problems);
                    return;
                }
                JSONObject jsonObject = new JSONObject(response);
                String msg = jsonObject.getString("message");//user exists
                if (msg.equals("success")) {
                    sendResToUi(true, response, null);
                    return;
                }
                if (msg.equals("user not found")) {
                    sendResToUi(false, null, ErrorType.invalid_details);
                    return;
                }
            } catch (JSONException e) {
                sendResToUi(false, null, ErrorType.network_problems);
                return;
            }
        }
    };


    public void sendResToUi(boolean isSuccsed, String response, ErrorType errorType) {

        Intent intent = new Intent(context.getResources().getString(R.string.action_login_completed));
        intent.setType("*/*");
        intent.putExtra(context.getResources().getString(R.string.is_successed_key), isSuccsed);
        if (response != null)
            intent.putExtra(context.getResources().getString(R.string.response_key), response);
        if (errorType != null)
            intent.putExtra(context.getResources().getString(R.string.error_type_key), errorType);
        context.sendBroadcast(intent);

      /*  if (context != null && context instanceof ServerCallResponseToUi)
            ((ServerCallResponseToUi) context).OnServerCallResponseToUi(isSuccsed, response, errorType, getClass());*/

    }


}