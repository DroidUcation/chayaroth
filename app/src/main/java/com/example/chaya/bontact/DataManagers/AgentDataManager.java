package com.example.chaya.bontact.DataManagers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.Ui.Activities.MainActivity;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by chaya on 6/22/2016.
 */
public class AgentDataManager implements Callback {

    private static Agent agent=null;
    private Context context;
    public static Agent getAgent() {
        return agent;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    public Context getContext() {
        return context;
    }
    public Agent getAgentInstanse()
    {
        if(agent==null)
         return new Agent();
        return agent;
    }

    private static void setAgent(Agent agent) {
        AgentDataManager.agent = agent;
    }

    public AgentDataManager() {
        agent= getAgentInstanse();
       setContext(null);
    }

    public void getDataFromServer(String userName,String password,Context context)
    {
        this.context=context;
        String url = context.getResources().getString(R.string.domain_api) + context.getResources().getString(R.string.login_api);
        url += "?username=" + userName + "&pass=" + password;

        OkHttpRequests requests = new OkHttpRequests(url,this);
        try {
            requests.run();
        } catch (Exception e) {
          sendRes(false,null,ErrorType.network_problems);
        }
    }

    public boolean saveData(String response, Context context)
    {
        this.context=context;
        Gson gson =new Gson();
        setAgent(gson.fromJson(response,Agent.class));
        if(agent!=null&&context!=null)
        {
          String agent_str= gson.toJson(getAgent());
         SharedPreferences Preferences =context.getSharedPreferences(context.getResources().getString(R.string.sp_user_details), context.MODE_PRIVATE);
        SharedPreferences.Editor editor=Preferences.edit();
        editor.clear();
        editor.putString(context.getResources().getString(R.string.agent),gson.toJson(getAgent().getRep()));
        editor.putString(context.getResources().getString(R.string.settings),gson.toJson(getAgent().getSettings()));
        editor.putString(context.getResources().getString(R.string.token),gson.toJson(getAgent().getToken()));
        editor.apply();
           /* gson.fromJson(Preferences.getString(context.getResources().getString(R.string.agent),null),Agent.Rep.class);
            gson.fromJson(Preferences.getString(context.getResources().getString(R.string.settings),null),Agent.Settings.class);*/
        return true;
        }
            return false;
    }

    public  String getAgentToken(Context context)
    {
        if(isLoggedIn(context)==true)
              if(agent!=null)
            return getAgent().getToken();

        return null;
    }
    public String getAgentName(Context context)
    {
        if(isLoggedIn(context)==true)
            if(agent!=null)
             return getAgent().getName();
        return null;
    }
    public boolean isLoggedIn(Context context)
    {
        this.context=context;
        SharedPreferences Preferences =context.getSharedPreferences(context.getResources().getString(R.string.sp_user_details), context.MODE_PRIVATE);
        String token=Preferences.getString(context.getResources().getString(R.string.token),null);
        if(token!=null)//user is logged in
        {
            //set the agent object
            if(agent==null)
            { Gson gson=new Gson();
            agent.token=token;
            agent.rep= gson.fromJson(Preferences.getString(context.getResources().getString(R.string.agent),null), Agent.Rep.class);
            agent.settings=gson.fromJson(Preferences.getString(context.getResources().getString(R.string.settings),null), Agent.Settings.class);
            }
            return true;
        }
        return false;
    }

 /*   @Override
    public void onFailure(Call call, IOException e) {
        sendRes(false,null,ErrorType.network_problems);
    }
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
            sendRes(false, null, ErrorType.network_problems);
            return;
        }
        try {
            final JSONObject jsonObject =new JSONObject(response.body().string());
            String msg=jsonObject.getString("message");//user exists
            if (msg.equals("success")) {
                sendRes(true, jsonObject.toString(), null);
                return;
            }
            if(msg.equals("user not found")) {
                sendRes(false, null, ErrorType.user_not_exists);
                return;
            }
        }
        catch (JSONException e) {
            sendRes(false, null, ErrorType.network_problems);
            return;
        }
    }*/

    public void sendRes(boolean isSuccsed, String response, ErrorType errorType)
    {
        if(context!=null)
            ((ServerCallResponse)context).OnServerCallResponse(isSuccsed,response,errorType,getClass());

    }


}



