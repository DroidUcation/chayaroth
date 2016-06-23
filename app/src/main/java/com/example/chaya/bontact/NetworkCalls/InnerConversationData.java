package com.example.chaya.bontact.NetworkCalls;

import android.content.Context;
import android.util.Log;

import com.example.chaya.bontact.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by chaya on 6/19/2016.
 */
public class InnerConversationData {

    private String token;
    private int id_surfer;
    private Context context;
    private JSONObject resFromServer;


    public InnerConversationData(Context context, String token, int id_surfer) {
        this.token=token;
        this.id_surfer=id_surfer;
        this.context=context;
        resFromServer=null;


    }

    public boolean getDataFromServer() {
        if (token == null) {
            return false;
        }
        String url = context.getResources().getString(R.string.domain_api) + context.getResources().getString(R.string.inner_conversation_api);
        url+= token+"/"+id_surfer;


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
                    resFromServer=res;
                    //pushToDB(res.getJSONObject("conversations"));
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

    public String getToken() {
        return token;
    }

    public JSONObject getResFromServer() {
        return resFromServer;
    }

    public Context getContext() {
        return context;
    }

    public int getId_surfer() {
        return id_surfer;
    }
}

