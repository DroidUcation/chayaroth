package com.example.chaya.bontact.NetworkCalls;

import com.example.chaya.bontact.Helpers.ErrorType;

/**
 * Created by chaya on 6/26/2016.
 */
public interface ServerCallResponse {
    void onServerCallResponse(boolean isSuccsed, String response, ErrorType errorType, Class sender);
}
