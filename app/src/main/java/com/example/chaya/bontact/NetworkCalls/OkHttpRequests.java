package com.example.chaya.bontact.NetworkCalls;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.OkHttpClient.Builder;

/**
 * Created by chaya on 6/13/2016.
 */
public class OkHttpRequests implements Callback {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String url;
    private final OkHttpClient client;
    private Context context;

    public OkHttpRequests(String url, Context context) {
        this(url, context, null);
    }

    public OkHttpRequests(String url, Context context, String postData) {
        client = new OkHttpClient();
        this.url = url;
        this.context = context;
        try {
            if (postData == null)
                runGetRequest();
            else
                runPostRequest(postData);
        } catch (Exception e) {
            sendRes(false, null, ErrorType.network_problems);
        }
    }


    public void runGetRequest() throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(this);
    }

    public void runPostRequest(String postData) throws Exception {
        RequestBody body = RequestBody.create(JSON, postData);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "key=AIzaSyDk4Vgg0xNXMJasOiz3ofBvoDbdwpmGYDE")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(this);

    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.e("ON FAILER", "fall");
        sendRes(false, null, ErrorType.network_problems);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.e("on response", response.toString());
        if (!response.isSuccessful()) {
           /* else if (contextStatic != null)
                contextStatic.sendBroadcast(intent);*/
            sendRes(false, null, ErrorType.network_problems);
            return;
        }
        sendRes(true, response.body().string(), null);

    }

    public void sendRes(boolean isSuccsed, String response, ErrorType errorType) {
       /* if (sender != null && sender instanceof ServerCallResponse)
            ((ServerCallResponse) sender).OnServerCallResponse(isSuccsed, response, errorType, sender);*/
        if (context != null) {
            Intent intent = new Intent(context.getResources().getString(R.string.response_server_call_action));
            intent.setType("*/*");
            intent.putExtra(context.getResources().getString(R.string.is_successed_key), isSuccsed);
            if (response != null)
                intent.putExtra(context.getResources().getString(R.string.response_key), response);
            if (errorType != null)
                intent.putExtra(context.getResources().getString(R.string.error_type_key), errorType);

            context.sendBroadcast(intent);
        }
    }

}
