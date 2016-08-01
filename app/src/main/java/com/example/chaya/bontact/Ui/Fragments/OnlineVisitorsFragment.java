package com.example.chaya.bontact.Ui.Fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.RecyclerViews.DividerItemDecoration;
import com.example.chaya.bontact.RecyclerViews.InboxAdapter;
import com.example.chaya.bontact.RecyclerViews.OnlineVisitorsAdapter;

public class OnlineVisitorsFragment extends Fragment {
    View rootView;
    RecyclerView recyclerView;
    OnlineVisitorsAdapter adapter;


    public OnlineVisitorsFragment() {
    }


    public static OnlineVisitorsFragment newInstance() {
        OnlineVisitorsFragment fragment = new OnlineVisitorsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().setTitle(R.string.onlinevisitors_title);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_online_visitors, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.online_visitors_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        if (recyclerView != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            adapter = new OnlineVisitorsAdapter(getContext());
           // adapter.setOnItemChangedListener()
            recyclerView.setAdapter(adapter);
            adapter.notifyItemInserted(adapter.getItemCount()-1);
        }
       return rootView;
    }



}
