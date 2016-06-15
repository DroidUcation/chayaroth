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

import com.example.chaya.bontact.Data.InboxAdapter;
import com.example.chaya.bontact.MenuActivity;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.ServerCalls.OkHttpRequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;


public class InboxFragment extends Fragment implements LoaderManager.LoaderCallbacks {


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

       // recyclerView = (RecyclerView) getActivity().findViewById(R.id.inbox_recyclerview);
       // recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       // getActivity().getSupportLoaderManager().initLoader(INBOX_LOADER, null, this);
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
        ContentValues cv  = new ContentValues();


        JSONArray values= jsonObject.getJSONArray("data");
        Log.d("@@@@",values.toString());
        for(int i=0;i<values.length();i++)
        {
           JSONObject row= values.getJSONObject(i);
            //get all key values and put them into the cv in the db

        }
       /* while(keys.hasNext()){
            String key = keys.next();
        Object Value=null;
            try {
                 Value = jsonObject.get(key) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        if(Value!=null)
        {
            cv.put(key,);
        }*/
        }



    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri CONTACT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;//content provider uri
        return new CursorLoader(getContext(), CONTACT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    //don't match the parent's method
   /* @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        cursor.moveToFirst();
        adapter = new InboxAdapter(getContext(), cursor);
        recyclerView.setAdapter(adapter);
    }*/


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
}





