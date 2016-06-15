package com.example.chaya.bontact.ServerCalls;

import android.util.Log;
import android.widget.Toast;

import com.example.chaya.bontact.MainActivity;
import com.example.chaya.bontact.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chaya on 6/13/2016.
 */
public class OkHttpRequests {
    private final String url;
    private final OkHttpClient  client;
    private Callback callback;

    public OkHttpRequests(String url,Callback callback)
    {
        client = new OkHttpClient();
        this.url=url;
        this.callback=callback;
    }

    public void run() throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();
         client.newCall(request).enqueue(callback);

    }
}
