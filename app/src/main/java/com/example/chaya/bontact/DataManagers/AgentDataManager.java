package com.example.chaya.bontact.DataManagers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.Helpers.ErrorType;
/*import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponseToUi;*/
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
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

    public static Agent getAgentInstanse() {
        if (agent == null)
            return new Agent();
        return agent;
    }

    private static void setAgent(Agent agent) {
        AgentDataManager.agent = agent;
    }

    public AgentDataManager() {
        agent = getAgentInstanse();
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
        OkHttpRequests requests = new OkHttpRequests(url, context);

    }



    public boolean saveData(String response, Context context) {
        this.context = context;
        Gson gson = new Gson();
        Agent agentFromJson = gson.fromJson(response, Agent.class);
        agent.rep = agentFromJson.rep;
        agent.token = agentFromJson.token;
        agent.settings = agentFromJson.settings;
        if (agent != null && context != null) {
            // String agent_str= gson.toJson(getAgentInstanse());
            SharedPreferences Preferences = context.getSharedPreferences(context.getResources().getString(R.string.sp_user_details), context.MODE_PRIVATE);
            SharedPreferences.Editor editor = Preferences.edit();
            editor.clear();
            editor.putString(context.getResources().getString(R.string.agent), gson.toJson(getAgentInstanse().getRep()));
            editor.putString(context.getResources().getString(R.string.settings), gson.toJson(getAgentInstanse().getSettings()));
            editor.putString(context.getResources().getString(R.string.token), getAgentInstanse().getToken());
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
                return getAgentInstanse().getToken();

        return null;
    }

    public String getAgentName(Context context) {
        if (isLoggedIn(context) == true)
            if (agent != null)
                return getAgentInstanse().getName();
        return null;
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

    public boolean logOut(Context context) {
        //clear agent details
        SharedPreferences Preferences = context.getSharedPreferences(context.getResources().getString(R.string.sp_user_details), context.MODE_PRIVATE);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.clear().commit();
        //claer db
        context.getContentResolver().delete(Contract.Conversation.INBOX_URI, null, null);
        context.getContentResolver().delete(Contract.InnerConversation.INNER_CONVERSATION_URI, null, null);
        return true;

    }



  /*  @Override
    public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType, Object sender) {
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
                sendResToUi(false, null, ErrorType.user_not_exists);
                return;
            }
        } catch (JSONException e) {
            sendResToUi(false, null, ErrorType.network_problems);
            return;
        }
    }
    public void sendResToUi(boolean isSuccsed, String response, ErrorType errorType) {
        Intent intent = new Intent(context.getResources().getString(R.string.response_server_call_to_ui_action));
        intent.setType("**");
        intent.putExtra(context.getResources().getString(R.string.is_successed_key), isSuccsed);
        if (response != null)
            intent.putExtra(context.getResources().getString(R.string.response_key), response);
        if (errorType != null)
            intent.putExtra(context.getResources().getString(R.string.error_type_key), errorType);

        context.sendBroadcast(intent);

        if (context != null && context instanceof ServerCallResponseToUi)
            ((ServerCallResponseToUi) context).OnServerCallResponseToUi(isSuccsed, response, errorType, getClass());

    }*/


}
