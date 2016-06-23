package com.example.chaya.bontact.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Activities.MenuActivity;
import com.example.chaya.bontact.Ui.Fragments.DashboardFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by chaya on 6/23/2016.
 */
public class ConverastionDataManager implements Callback {

    private Context context;

  public ConverastionDataManager()
  {
      context=null;

  }
    public void getFirstDataFromServer(Context context, String token)
    {
        int current_page=0;
        this.context=context;
        getDataFromServer(context,token,current_page);
    }
    public void getNextDataFromServer(Context context, String token)
    {
       this.context=context;
    }

    private void getDataFromServer(Context context,String token,int current_page)
    {
        if(token==null)
            sendRes(false,null, ErrorType.other);
        this.context=context;
        String url = context.getResources().getString(R.string.domain_api) + context.getResources().getString(R.string.conversation_api) + token;
        url += "?page=" + current_page;

        OkHttpRequests requests = new OkHttpRequests(url,this);
        try {
            requests.run();
        } catch (Exception e) {
            sendRes(false,null, ErrorType.network_problems);
        }
    }

    public boolean saveData(JSONObject conversation)
    {
       /* JSONArray conversationList = null;//get the data for conversation
        try {
            conversationList = conversation.getJSONArray("data");
        ArrayList<String> fields = DbBontact.getAllConversationFields();//get all table fields
        ContentValues cv = new ContentValues();
        *//*f(current_page==0)
            context.getContentResolver().delete(Contract.Conversation.INBOX_URI,null,null);*//*
        for (int i = 0; i < conversationList.length(); i++) {
            JSONObject row = conversationList.getJSONObject(i);
            //put in cv all fields that can be added to the table
            cv.clear();
            for (String key : fields) {
                if (row.has(key)) {
                    cv.put(key, "");
                    int ValueI = -1;
                    String ValueS = "no";
                    ValueI = row.optInt(key, -1);
                    if (ValueI != -1) {
                        Log.d(key, " int " + ValueI);
                        cv.put(key, ValueI);
                    } else {
                        ValueS = row.optString(key, "no");
                        if (!ValueS.equals("no")) {
                            Log.d(key, "str " + ValueS);
                            cv.put(key, ValueS);
                        } else
                            //Date ValueD=row.optD
                            Log.d("else " + key, row.get(key).toString());
                    }


if (key.equals(Contract.Conversation.FLAG_COUNTRY))//get the country - Extreme case
                    {
                        JSONObject flag = row.getJSONObject(key);
                      ValueS= flag.optString(Contract.Conversation.COLUMN_COUNTRY,"no");
                        if(!ValueS.equals("no"))
                        {
                            Log.d(key, "str " + ValueS);
                        cv.put(key, ValueS);
                        }
                        }

                    }
                }
            Uri uri = context.getContentResolver().insert(Contract.Conversation.INBOX_URI, cv);
            }
        }*/
return true;
    }
    @Override
    public void onFailure(Call call, IOException e) {
        sendRes(false,null,ErrorType.network_problems);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
            sendRes(false,null,ErrorType.network_problems);
            return;
        }
            try {
                 JSONObject res = new JSONObject(response.body().string()).getJSONObject("conversations");
                sendRes(true,res.toString(),null);
            } catch (JSONException e) {
               sendRes(false,null,ErrorType.other);
            }
       sendRes(true,response.body().string(),null);
        return;
    }
    public void sendRes(boolean isSuccsed, String response, ErrorType errorType)
    {
        if(context!=null)
            ((ServerCallResponse)context).OnServerCallResponse(isSuccsed,response,errorType,getClass());
    }

}
