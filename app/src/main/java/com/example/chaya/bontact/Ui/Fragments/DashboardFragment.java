package com.example.chaya.bontact.Ui.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.R;


public class DashboardFragment extends Fragment {

    private View RootView;
    AgentDataManager agentDataManager;
    TextView new_requests_count;
    TextView online_visitors_count;
    int unread_conversation;
    OnlineVisitorsCountReciver onlineVisitorsCountReciver;
    NewRequestsCountReciver newRequestsCountReciver;

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance() {//String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        newRequestsCountReciver = new NewRequestsCountReciver();
        onlineVisitorsCountReciver = new OnlineVisitorsCountReciver();
        IntentFilter intentFilter = IntentFilter.create(getResources().getString(R.string.change_visitors_list_action), "*/*");
        getContext().registerReceiver(onlineVisitorsCountReciver, intentFilter);
        intentFilter = IntentFilter.create(getResources().getString(R.string.change_unread_conversations_action), "*/*");
        getContext().registerReceiver(newRequestsCountReciver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(onlineVisitorsCountReciver);
        getContext().unregisterReceiver(newRequestsCountReciver);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RootView = null;
        getActivity().setTitle(R.string.dashboard_title);
        unread_conversation = ConversationDataManager.getAllUnreadConversations(getContext());

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
        agentDataManager = new AgentDataManager();
        TextView welcome_msg = (TextView) RootView.findViewById(R.id.txt_welcom_name);
        if (agentDataManager.getAgentName(getContext()) != null)
            welcome_msg.append(" " + agentDataManager.getAgentName(getContext()));
        else {
            //TODO:handle the case that a user name not found
        }
        new_requests_count = (TextView) request_v.findViewById(R.id.count_new_requests);
        if (new_requests_count != null)
            new_requests_count.setText(String.valueOf(ConversationDataManager.getAllUnreadConversations(getContext())));
        online_visitors_count = (TextView) online_v.findViewById(R.id.count_online_visitors);
        online_visitors_count.setText(String.valueOf(VisitorsDataManager.getVisitorsList().size()));

        TextView arrow = (TextView) RootView.findViewById(R.id.online_arrow_btn);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fontawesome-webfont.ttf");
        arrow.setTypeface(font);
        arrow = (TextView) RootView.findViewById(R.id.visitors_arrow_btn);
        arrow.setTypeface(font);
        return RootView;
    }

    public class OnlineVisitorsCountReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            online_visitors_count.setText(String.valueOf(VisitorsDataManager.getVisitorsList().size()));
        }
    }

    public class NewRequestsCountReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new_requests_count.setText(String.valueOf(ConversationDataManager.getAllUnreadConversations(getContext())));
        }
    }


}

