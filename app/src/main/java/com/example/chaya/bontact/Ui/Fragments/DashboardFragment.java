package com.example.chaya.bontact.Ui.Fragments;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConverastionDataManager;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;


public class DashboardFragment extends Fragment implements ServerCallResponse{

    private View RootView;
    AgentDataManager agentDataManager;
   /* private JSONObject agent;
    private String token;
    private int current_page;
    private String user_name;*/

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance() {//String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RootView = null;
         agentDataManager=new AgentDataManager();
       String token= agentDataManager.getAgentToken();
        if(token!=null)
        {
            ConverastionDataManager converastionDataManager=new ConverastionDataManager();
            converastionDataManager.getFirstDataFromServer(getContext(),token);


        }
        /*agent = null;
        token = null;
        current_page = 0;
        user_name = null;*/
       /* SharedPreferences Preferences = getContext().getSharedPreferences(getResources().getString(R.string.sp_user_details), getContext().MODE_PRIVATE);
        try {
            agent = new JSONObject(Preferences.getString(getResources().getString(R.string.agent), ""));//get the agent object
            if (agent != null)//convert to json - succeeded
            {
                user_name = agent.getString(getResources().getString(R.string.name));//get user name to display
                token = Preferences.getString(getResources().getString(R.string.token), "");
                if (token != null)//token agent is found
                {
                    //Todo:get count number to dashboard
                    //Todo:check if always when come in dashboard have to bring new conversation data. and also handle with the curent page

                    //fill db with data for the inbox
                    ConversationData conversationData=new ConversationData(getContext(),token,current_page);
                    conversationData.getDataFromServer();
                }
            }
            // else: agent not found!!!
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        RootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        LinearLayout request_v = (LinearLayout) RootView.findViewById(R.id.visitorsRequest_btn_dashboard);
        request_v.setOnClickListener((View.OnClickListener) getActivity());
        LinearLayout online_v = (LinearLayout) RootView.findViewById(R.id.onlineVisitors_btn_dashboard);
        online_v.setOnClickListener((View.OnClickListener) getActivity());
        Log.d("this", this.toString());

        if (agentDataManager != null) {

            TextView welcome_msg = (TextView) RootView.findViewById(R.id.txt_welcom_msg);
            if (agentDataManager.getAgentName()!=null)
            welcome_msg.append(agentDataManager.getAgentName());
        } else {
            //TODO:handle the case that a user name not found
        }
        return RootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType,Class sender) {
        if(sender==ConverastionDataManager.class )
        {

        }

    }
}

