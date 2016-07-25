package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.R;

/**
 * Created by chaya on 7/25/2016.
 */
public class SendResponseHelper {
    Agent agent;

    public SendResponseHelper() {
        agent = AgentDataManager.getAgentInstanse();

    }

    public void sendCallBack(Context context, int idSurfer, String telephone) {
        Toast.makeText(context, "send callback", Toast.LENGTH_SHORT).show();
        String url = null;
        if (agent != null) {
            url = context.getResources().getString(R.string.dev_domain_api) +
                    "returnchannel/callback/" +
                    agent.getToken() +
                    "?surferid=" + idSurfer +
                    "&name=" + agent.getName() +
                    "&telephone=" + telephone;
        }
        OkHttpRequests okHttpRequests=new OkHttpRequests(url,this);

//todo:handel a case that a call back dont sucsses

    }
}
