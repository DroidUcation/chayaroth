package com.example.chaya.bontact.Ui.Fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.R;


public class DashboardFragment extends Fragment{

    private View RootView;
    AgentDataManager agentDataManager;
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
        agentDataManager=new AgentDataManager();
            TextView welcome_msg = (TextView) RootView.findViewById(R.id.txt_welcom_msg);
            if (agentDataManager.getAgentName(getContext())!=null)
            welcome_msg.append(" "+agentDataManager.getAgentName(getContext()));
       else {
            //TODO:handle the case that a user name not found
        }
        return RootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}

