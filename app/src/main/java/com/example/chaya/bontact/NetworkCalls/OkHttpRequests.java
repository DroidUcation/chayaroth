package com.example.chaya.bontact.NetworkCalls;

import android.util.Log;

import com.example.chaya.bontact.Helpers.ErrorType;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by chaya on 6/13/2016.
 */
public class OkHttpRequests implements Callback {
    private final String url;
    private final OkHttpClient client;
    private Object sender;

    public OkHttpRequests(String url, Object sender) {
        client = new OkHttpClient();
        this.url = url;
        this.sender = sender;
        try {
            run();
        } catch (Exception e) {
            sendRes(false, null, ErrorType.network_problems);
        }
    }

    public OkHttpRequests(String url, Object sender, JSONObject post) {
        client = new OkHttpClient();
        this.url = url;
        this.sender = sender;
        try {
            run();
        } catch (Exception e) {
            sendRes(false, null, ErrorType.network_problems);
        }
    }


    public void run() throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(this);

    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.d("ON FAILER", e.toString());

        sendRes(false, null, ErrorType.network_problems);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.d("on response", response.toString());
        if (!response.isSuccessful()) {
            sendRes(false, null, ErrorType.network_problems);
            return;
        }
        sendRes(true, response.body().string(), null);

    }

    public void sendRes(boolean isSuccsed, String response, ErrorType errorType) {
        if (sender != null && sender instanceof ServerCallResponse)
            ((ServerCallResponse) sender).OnServerCallResponse(isSuccsed, response, errorType);
    }

}
