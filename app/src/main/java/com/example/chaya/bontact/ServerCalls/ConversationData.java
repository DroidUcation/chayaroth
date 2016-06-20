package com.example.chaya.bontact.ServerCalls;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.renderscript.Sampler;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by chaya on 6/19/2016.
 */
public class ConversationData {

    private String token;
    private int current_page;
    private Context context;

    public ConversationData(Context context, String token, int current_page) {
        this.token=token;
        this.current_page=current_page;
        this.context=context;

    }

    public boolean getDataFromServer() {
        if (token == null) {
            return false;
        }
        String url = context.getResources().getString(R.string.domain_api) + context.getResources().getString(R.string.conversation_api) + token;
        url += "?page=" + current_page;


        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("fail", "onFailure: ");
                //Todo:  Handle the case of fail call
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Headers responseHeaders = response.headers();
                try {
                    final JSONObject res = new JSONObject(response.body().string());
                    Log.d("object", res.toString());
                  pushToDB(res.getJSONObject("conversations"));
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        };

        OkHttpRequests requests = new OkHttpRequests(url, callback);

        try {
            requests.run();

        } catch (Exception e) {
            //todo:handle a case that run failed
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void pushToDB(JSONObject conversation) throws JSONException {

        JSONArray conversationList = conversation.getJSONArray("data");//get the data for conversation
        ArrayList<String> fields = DbBontact.getAllConversationFields();//get all table fields
        ContentValues cv = new ContentValues();
        if(current_page==0)
            context.getContentResolver().delete(Contract.Conversation.INBOX_URI,null,null);
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

                  /*  if (key.equals(Contract.Conversation.FLAG_COUNTRY))//get the country - Extreme case
                    {
                        JSONObject flag = row.getJSONObject(key);
                      ValueS= flag.optString(Contract.Conversation.COLUMN_COUNTRY,"no");
                        if(!ValueS.equals("no"))
                        {
                            Log.d(key, "str " + ValueS);
                        cv.put(key, ValueS);
                        }
                        }*/
                    }
                }
            Uri uri = context.getContentResolver().insert(Contract.Conversation.INBOX_URI, cv);
            }


        }
    }

