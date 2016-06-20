package com.example.chaya.bontact.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.InboxAdapter;
import com.example.chaya.bontact.MenuActivity;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.ServerCalls.OkHttpRequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;


public class InboxFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int INBOX_LOADER = 0;

    private RecyclerView recyclerView;
    private InboxAdapter adapter;
    private  View rootView;
   private ProgressBar progressBar;
    public InboxFragment() {
    }

    public static InboxFragment newInstance() {
        InboxFragment fragment = new InboxFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //have to wait until data is coming back and then put the data.
        getActivity().getSupportLoaderManager().initLoader(INBOX_LOADER, null,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.inbox_recyclerview);
        if(recyclerView != null)
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
        return rootView;

    }
    @Override
    public Loader onCreateLoader(int id, Bundle args) {

         String sortOrder = Contract.Conversation.COLUMN_LAST_DATE  + " DESC"; //Sort by modified date as default
        CursorLoader cursorLoader= new CursorLoader(getContext(),Contract.Conversation.INBOX_URI,null,null,null,sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //TODO: why after clicking the second time the app is crassing

        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        if (cursor != null && cursor.moveToFirst()) {
            adapter = new InboxAdapter(getContext(), cursor);
            recyclerView.setAdapter(adapter);

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



}
