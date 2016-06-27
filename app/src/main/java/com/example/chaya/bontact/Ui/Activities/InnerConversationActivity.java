package com.example.chaya.bontact.Ui.Activities;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.RecyclerViews.DividerItemDecoration;
import com.example.chaya.bontact.RecyclerViews.InboxAdapter;
import com.example.chaya.bontact.RecyclerViews.InnerConversationAdapter;

public class InnerConversationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {



    private static final int INNER_CONVERSATION_LOADER = 0;
    private RecyclerView recyclerView;
    private InnerConversationAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int id_surfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_conversation);
        Bundle args = getIntent().getExtras();
        if (args != null) {
             this.id_surfer = args.getInt(Contract.InnerConversation.COLUMN_ID_SURFUR);
        }
        recyclerView = (RecyclerView) findViewById(R.id.inner_conversation_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(linearLayoutManager);
        }
       // recyclerView.addOnScrollListener(scrollListener);

        getSupportLoaderManager().initLoader(INNER_CONVERSATION_LOADER, null, this);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String sortOrder = null;
        String selectionCoulmns=Contract.InnerConversation.COLUMN_ID_SURFUR;
        String[] selectionArgs = { this.id_surfer+""};
        CursorLoader cursorLoader=new CursorLoader(this,Contract.InnerConversation.INNER_CONVERSATION_URI,null,selectionCoulmns+ "=?",selectionArgs,null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

//todo:if need the visibility
        recyclerView.setVisibility(View.VISIBLE);
        if (cursor != null && cursor.moveToFirst()) {
            adapter = new InnerConversationAdapter(this, cursor);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

   /* RecyclerView.OnScrollListener scrollListener =  new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int position= linearLayoutManager.findLastVisibleItemPosition();
            int cursorItemscount=adapter.getItemCount();
            if(position ==adapter.getItemCount())//end of data
            {
                Log.d("position",""+position);
                Log.d("cursor",""+adapter.getItemCount());
               *//* ConverastionDataManager converastionDataManager=new ConverastionDataManager();
                converastionDataManager.getNextDataFromServer(getContext());*//*
            }
            else
            {

            }
        }
    };*/

}
