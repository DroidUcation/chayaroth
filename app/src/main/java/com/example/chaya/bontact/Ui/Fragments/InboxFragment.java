package com.example.chaya.bontact.Ui.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConverastionDataManager;
import com.example.chaya.bontact.RecyclerViews.InboxAdapter;
import com.example.chaya.bontact.RecyclerViews.DividerItemDecoration;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Activities.MenuActivity;


public class InboxFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int INBOX_LOADER = 0;
    private RecyclerView recyclerView;
    private InboxAdapter adapter;
    private  View rootView;
    ProgressBar progressBarBottom;
    SwipeRefreshLayout refreshLayout;

    private LinearLayoutManager linearLayoutManager;

    int lastVisibleItem;
    public InboxFragment() {
    }

    public static InboxFragment newInstance() {
        InboxFragment fragment = new InboxFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getActivity().setTitle(R.string.inbox_title);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.inbox_recyclerview);
        linearLayoutManager= new LinearLayoutManager(getActivity());
        if(recyclerView != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        }
        recyclerView.addOnScrollListener(scrollListener);

        //  progressBarFirstData = (ProgressBar)rootView.findViewById(R.id.loading_first_inbox_data);

        progressBarBottom = (ProgressBar) rootView.findViewById(R.id.loading_next_inbox_data);

        refreshLayout= (SwipeRefreshLayout) rootView.findViewById(R.id.inbox_swipe_refresh);
        refreshLayout.setOnRefreshListener(refreshListener);
       refreshLayout.setColorSchemeColors(R.color.orange_dark);
        initLoader();
        return rootView;

    }
    public void initLoader()
    {
        getActivity().getSupportLoaderManager().initLoader(INBOX_LOADER, null,this);

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        ((MenuActivity)getActivity()).setProgressBarCenterState(View.VISIBLE);
        lastVisibleItem=0;
        String sortOrder = Contract.Conversation.COLUMN_LAST_DATE  + " DESC"; //Sort by modified date as default
        CursorLoader cursorLoader= new CursorLoader(getContext(),Contract.Conversation.INBOX_URI,null,null,null,sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        ((MenuActivity)getActivity()).setProgressBarCenterState(View.GONE);
        if(refreshLayout==null)
            refreshLayout=(SwipeRefreshLayout) rootView.findViewById(R.id.inbox_swipe_refresh);
        refreshLayout.setRefreshing(false);
        progressBarBottom.setVisibility(View.GONE);
        if (cursor != null && cursor.moveToFirst()) {
            adapter = new InboxAdapter(getContext(), cursor);
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(lastVisibleItem);
            recyclerView.setVisibility(View.VISIBLE);

        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
    RecyclerView.OnScrollListener scrollListener =  new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(linearLayoutManager.findLastCompletelyVisibleItemPosition()+1 ==adapter.getItemCount())//end of data
            {
                lastVisibleItem=linearLayoutManager.findLastCompletelyVisibleItemPosition();
                AgentDataManager agentDataManager=new AgentDataManager();
                ConverastionDataManager converastionDataManager=new ConverastionDataManager();
                converastionDataManager.getNextDataFromServer(getContext(),agentDataManager.getAgentToken(getContext()));
                progressBarBottom.setVisibility(View.VISIBLE);
            }

        }
    };
   SwipeRefreshLayout.OnRefreshListener refreshListener=  new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            initLoader();
        }
 };
   }
