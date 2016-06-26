package com.example.chaya.bontact.NetworkCalls;

import com.example.chaya.bontact.Helpers.ErrorType;

/**
 * Created by chaya on 6/22/2016.
 */

public interface ServerCallResponseToUi {
    void OnServerCallResponseToUi(boolean isSuccsed, String response, ErrorType errorType, Class sender);
}
