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


public class InboxFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {


    private static final int INBOX_LOADER = 0;

    RecyclerView recyclerView;
    InboxAdapter adapter;
    String token;
    int current_page;

    public InboxFragment() {
    }


    public static InboxFragment newInstance() {
        InboxFragment fragment = new InboxFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(INBOX_LOADER, null,this);
       /* recyclerView = (RecyclerView) getActivity().findViewById(R.id.inbox_recyclerview);
        if(recyclerView != null)
         recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
*/
        current_page=0;
        SharedPreferences Preferences = getContext().getSharedPreferences("UserDeatails", Context.MODE_PRIVATE);
        token= Preferences.getString(getResources().getString(R.string.token),"");
        getConversationData();
    }


    public void  getConversationData()
    {
        if(token==null)
        {
            return;
        }
        String url = getResources().getString(R.string.domain_api) + getResources().getString(R.string.conversation_api)+token;
        url += "?page="+current_page;


        Callback callback= new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("fail", "onFailure: ");

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Headers responseHeaders = response.headers();
            try {
                final JSONObject jsonObject =new JSONObject(response.body().string());
                Log.d("object",jsonObject.toString());
               pushToDB(jsonObject.getJSONObject("conversations"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        };

        OkHttpRequests requests = new OkHttpRequests(url,callback);

        try {
            requests.run();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void pushToDB(JSONObject jsonObject) throws JSONException {

        ContentValues cv = new ContentValues();
        JSONArray values = jsonObject.getJSONArray("data");

        Log.d("@@@@", values.toString());
        for (int i = 0; i < values.length(); i++) {
            JSONObject row = values.getJSONObject(i);
            Iterator<String> keys = row.keys();

            while (keys.hasNext())
            {
                String key = keys.next();
                int ValueI=-1;
                String ValueS="no";
                ValueI = row.optInt(key, -1);
                if (ValueI != -1)
                    {
                        Log.d(key," int "+ValueI);
                        cv.put(key,ValueI);
                    }
                else
                    {
                        ValueS = row.optString(key, "no");
                             if (ValueS != "no")
                             {
                                 Log.d(key,"str "+ValueS);
                                 cv.put(key,ValueS);
                             }
                            else
                                 //Date ValueD=row.optD
                                 Log.d("else "+key, row.get(key).toString());
                    }
             }
            //cv.put( "_id",+(i+5));
            Log.d("cv",cv.toString());
            Uri uri=getContext().getContentResolver().insert(Contract.Conversation.INBOX_URI,cv);
            Log.d("uri",uri.toString());



        }

    }






    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),Contract.Conversation.INBOX_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Set
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.inbox_recyclerview);
        if(recyclerView != null)
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setVisibility(View.VISIBLE);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onClick(View v) {

    }
}
