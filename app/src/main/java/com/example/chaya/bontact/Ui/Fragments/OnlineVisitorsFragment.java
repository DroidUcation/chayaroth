package com.example.chaya.bontact.Ui.Fragments;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.RecyclerViews.DividerItemDecoration;
import com.example.chaya.bontact.RecyclerViews.OnlineVisitorsAdapter;
import com.example.chaya.bontact.Services.GCMPushReceiverService;

public class OnlineVisitorsFragment extends Fragment {

    View rootView;
    RecyclerView recyclerView;
    OnlineVisitorsAdapter adapter;
    BroadcastReceiver broadcastReceiver;
    ImageView noVisitorsImg;
    TextView noVisitorsTitle;
    LinearLayoutManager linearLayoutManager;

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = IntentFilter.create(getResources().getString(R.string.change_visitors_list_action), "*/*");
        broadcastReceiver = new VisitorsListChangesReciver();
        getContext().registerReceiver(broadcastReceiver, intentFilter);
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(GCMPushReceiverService.newVisitorId);
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(broadcastReceiver);

    }

    public OnlineVisitorsFragment() {
    }

    public static OnlineVisitorsFragment newInstance() {
        OnlineVisitorsFragment fragment = new OnlineVisitorsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //  getActivity().setTitle(R.string.onlinevisitors_title);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_online_visitors, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.online_visitors_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        if (recyclerView != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            adapter = new OnlineVisitorsAdapter(getContext());
            recyclerView.setAdapter(adapter);
        }
        setNoVisitorsMessages();
        return rootView;
    }

    public void setNoVisitorsMessages() {
        if (rootView == null)
            return;
        if (noVisitorsImg == null)
            noVisitorsImg = (ImageView) rootView.findViewById(R.id.no_visitors_img);
        if (noVisitorsTitle == null)
            noVisitorsTitle = (TextView) rootView.findViewById(R.id.no_visitors_title);

        if (VisitorsDataManager.getVisitorsList().size() > 0 || (linearLayoutManager != null && linearLayoutManager.findFirstCompletelyVisibleItemPosition() > -1)) {
            noVisitorsImg.setVisibility(View.GONE);
            noVisitorsTitle.setVisibility(View.GONE);
        } else {
            noVisitorsImg.setVisibility(View.VISIBLE);
            noVisitorsTitle.setVisibility(View.VISIBLE);
        }
    }

    public class VisitorsListChangesReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int position = intent.getIntExtra(getResources().getString(R.string.notify_adapter_key_item_postion), -1);
            int action = intent.getIntExtra(getResources().getString(R.string.notify_adapter_key_action), -1);
            setNoVisitorsMessages();
            if (position == -1 || action == -1)
                return;
            if (action == VisitorsDataManager.ACTION_NEW_VISITOR) {
                adapter.notifyDataSetChanged();
                // adapter.notifyItemInserted(position);
            } else if (action == VisitorsDataManager.ACTION_REMOVE_VISITOR) {
                adapter.notifyDataSetChanged();
                // adapter.notifyItemRemoved(position);
            } else if (action == VisitorsDataManager.ACTION_UPDATE_VISITOR) {
                adapter.notifyDataSetChanged();
                //adapter.notifyItemChanged(position);
            }
        }
    }

}
