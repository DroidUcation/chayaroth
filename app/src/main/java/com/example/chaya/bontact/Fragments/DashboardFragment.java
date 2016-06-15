package com.example.chaya.bontact.Fragments;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chaya.bontact.MenuActivity;
import com.example.chaya.bontact.R;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    View RootView=null;
     @Override
    public void onClick(View v) {
      /*  Fragment fragment=null;
        switch(v.getId())
        {
            case R.id.onlineVisitors_btn_dashboard:
               fragment=new OnlineVisitorsFragment();
                break;
            case R.id.visitorsRequest_btn_dashboard:
                fragment=new InboxFragment();
        }
        ((MenuActivity)getActivity()).ReplaceFragments(fragment);*/
    }
    public DashboardFragment() {
            }

    public static DashboardFragment newInstance(){//String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         //Inflate the layout for this fragment
        RootView= inflater.inflate(R.layout.fragment_dashboard, container, false);

      LinearLayout request_v= (LinearLayout) RootView.findViewById(R.id.visitorsRequest_btn_dashboard);
        request_v.setOnClickListener((View.OnClickListener) getActivity());
        //request_v.setOnClickListener(this);
        LinearLayout online_v= (LinearLayout)RootView.findViewById(R.id.onlineVisitors_btn_dashboard);
        //online_v.setOnClickListener(this);
        online_v.setOnClickListener((View.OnClickListener) getActivity());
        Log.d("this",this.toString());

        SharedPreferences Preferences= getContext().getSharedPreferences("UserDeatails",getContext().MODE_PRIVATE);
        String username= Preferences.getString(getResources().getString(R.string.user_name),"");
        TextView welcome_msg=(TextView)RootView.findViewById(R.id.txt_welcom_msg);
        welcome_msg.append(username);
        return  RootView;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}
