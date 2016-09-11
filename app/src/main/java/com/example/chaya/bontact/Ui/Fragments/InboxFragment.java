package com.example.chaya.bontact.Ui.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.RecyclerViews.InboxAdapter;
import com.example.chaya.bontact.RecyclerViews.DividerItemDecoration;
import com.example.chaya.bontact.R;


public class InboxFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int INBOX_LOADER = 0;
    private RecyclerView recyclerView;
    private InboxAdapter adapter;
    private View rootView;
    ProgressBar progressBarBottom;
    ProgressBar progressBarCenter;
    SwipeRefreshLayout refreshLayout;
    private LinearLayoutManager linearLayoutManager;
    int lastVisibleItem;
    ViewGroup container;
    ConversationDataManager conversationDataManager;
    OnlineStatesChangesReceiver onlineStatesChangesReceiver;

    public InboxFragment() {
        Log.d("now", "INBOX");
    }

    public static InboxFragment newInstance() {
        InboxFragment fragment = new InboxFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //getActivity().setTitle(R.string.inbox_title);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = IntentFilter.create(getResources().getString(R.string.change_visitors_list_action), "*/*");
        onlineStatesChangesReceiver = new OnlineStatesChangesReceiver();
        getContext().registerReceiver(onlineStatesChangesReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(onlineStatesChangesReceiver);
    }

    public void setProgressBarCenterState(int state) {
        if (rootView == null)
            return;
        if (progressBarCenter == null)
            progressBarCenter = (ProgressBar) getActivity().findViewById(R.id.progress_bar_center);
        progressBarCenter.setVisibility(state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        this.container = container;
      //  getActivity().setTitle(R.string.app_name);
        setProgressBarCenterState(View.VISIBLE);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.inbox_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        if (recyclerView != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.addOnScrollListener(scrollListener);
            adapter = new InboxAdapter(getContext(), null);
            recyclerView.setAdapter(adapter);
        }

        progressBarBottom = (ProgressBar) rootView.findViewById(R.id.progress_bar_bottom);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.inbox_swipe_refresh);
        refreshLayout.setOnRefreshListener(refreshListener);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.orange_dark));
        initLoader();
        return rootView;

    }

    public void initLoader() {
        getActivity().getSupportLoaderManager().initLoader(INBOX_LOADER, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // ((MenuActivity) getActivity()).setProgressBarCenterState(View.VISIBLE);
        lastVisibleItem = 0;

        String sortOrder = Contract.Conversation.COLUMN_LAST_DATE + " DESC"; //Sort by modified date as default
        CursorLoader cursorLoader = new CursorLoader(getContext(), Contract.Conversation.INBOX_URI, null, null, null, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (refreshLayout == null)
            refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.inbox_swipe_refresh);
        refreshLayout.setRefreshing(false);
        adapter.swapCursor(cursor);
        if (cursor != null) {
            progressBarBottom.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            setProgressBarCenterState(View.GONE);

        } else
            setProgressBarCenterState(View.VISIBLE);
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

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1 == adapter.getItemCount() && recyclerView.getVisibility() == View.VISIBLE)//end of data
            {
                Log.d("scroll", "dx " + dx + " dy " + dy);
                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                getData(false);
                progressBarBottom.setVisibility(View.VISIBLE);
            }

        }
    };
    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getData(true);
        }
    };

    private void getData(boolean isFirst) {
        AgentDataManager agentDataManager = new AgentDataManager();
        conversationDataManager = new ConversationDataManager(getContext());
        if (!isFirst)
            conversationDataManager.getNextDataFromServer(getContext(), agentDataManager.getAgentToken(getContext()));
        else
            conversationDataManager.getFirstDataFromServer(getContext(), agentDataManager.getAgentToken(getContext()));

    }

    public class OnlineStatesChangesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int position = intent.getIntExtra(getResources().getString(R.string.notify_adapter_key_item_postion), -1);
            int action = intent.getIntExtra(getResources().getString(R.string.notify_adapter_key_action), -1);

            if (position == -1 || action == -1)
                return;
            if (action == VisitorsDataManager.ACTION_NEW_VISITOR) {
                adapter.notifyDataSetChanged();
                // adapter.notifyItemInserted(position);
            } else if (action == VisitorsDataManager.ACTION_REMOVE_VISITOR) {
                adapter.notifyDataSetChanged();
                // adapter.notifyItemRemoved(position);
            } else if (action == VisitorsDataManager.ACTION_UPDATE_VISITOR) {
                // adapter.notifyDataSetChanged();
                //adapter.notifyItemChanged(position);
            }
        }
    }

}
