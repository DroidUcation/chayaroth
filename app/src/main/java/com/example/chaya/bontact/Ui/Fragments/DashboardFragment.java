package com.example.chaya.bontact.Ui.Fragments;
import android.graphics.Typeface;
import android.os.Bundle;
import android.sax.RootElement;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
        getActivity().setTitle(R.string.dashboard_title);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        RootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        RelativeLayout request_v = (RelativeLayout) RootView.findViewById(R.id.requests_dashboard_layout);
        request_v.setOnClickListener((View.OnClickListener) getActivity());
        RelativeLayout online_v = (RelativeLayout) RootView.findViewById(R.id.onlineVisitors_dashboard_layout);
        online_v.setOnClickListener((View.OnClickListener) getActivity());
        Log.d("this", this.toString());
        agentDataManager=new AgentDataManager();
        TextView welcome_msg = (TextView) RootView.findViewById(R.id.txt_welcom_name);
        if (agentDataManager.getAgentName(getContext())!=null)
            welcome_msg.append(" "+agentDataManager.getAgentName(getContext()));
        else {
            //TODO:handle the case that a user name not found
        }
         TextView arrow= (TextView) RootView.findViewById(R.id.online_arrow_btn);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fontawesome-webfont.ttf" );
        arrow.setTypeface(font);
        arrow= (TextView) RootView.findViewById(R.id.visitors_arrow_btn);
        arrow.setTypeface(font);
        return RootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }




}

