package com.example.chaya.bontact.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Helpers.DbToolsHelper;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 6/26/2016.
 */
public class InnerConversationDataManager {

    private Context context;
    private Conversation current_conversation;
    private List<InnerConversation> innerConversationsList;
    public static int idPlaceHolder = -1;

    public InnerConversationDataManager(Context context, Conversation current_conversation) {

        this.current_conversation = current_conversation;
        this.context = context;
        innerConversationsList = new ArrayList<>();
    }

    public InnerConversationDataManager(Context context, int currentIdSurfer) {
        this(context, null);
        ConversationDataManager conversationDataManager = new ConversationDataManager(context);
        current_conversation = conversationDataManager.getConversationByIdSurfer(currentIdSurfer);
    }


    public void getData(Context context, String token) {

        this.context = context;
        //sendResToUi();
        getDataFromServer(context, token);
    }

    public void getDataFromServer(Context context, String token) {
        if (current_conversation != null) {
            String id_surfer_string = current_conversation.idSurfer + "";
            if (token != null && id_surfer_string != null) {
                this.context = context;
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority(context.getResources().getString(R.string.base_api))
                        .appendPath(context.getResources().getString(R.string.rout_api))
                        .appendPath(context.getResources().getString(R.string.inner_conversation_api))
                        .appendPath(token)
                        .appendPath(id_surfer_string);

                String url = builder.build().toString();

                OkHttpRequests requests = new OkHttpRequests(url, getDataOnResponse);
            }
        }
    }

    public boolean saveServersData(String data) {
        try {
            JSONArray DataArray = new JSONArray(data);
            Gson gson = new Gson();
            InnerConversation innerConversation = null;
            for (int i = 0; i < DataArray.length(); i++) {
                String strObj = null;
                try {
                    strObj = DataArray.getJSONObject(i).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                innerConversation = gson.fromJson(strObj, InnerConversation.class);
                //delete all place holder
                String selectionStr = Contract.InnerConversation.COLUMN_ID + "<0";
                context.getContentResolver().delete(Contract.InnerConversation.INNER_CONVERSATION_URI, selectionStr, null);

                saveData(innerConversation);
            }

            if (innerConversation != null && innerConversation.getMess() != null)//check type
            {
                ConversationDataManager conversationDataManager = new ConversationDataManager(context);
                conversationDataManager.setLastSentence(context, current_conversation, innerConversation.getMess());
            }
            return true;

        } catch (JSONException e) {
            return false;
        }
    }

    public boolean saveData(InnerConversation innerConversation) {

        if (innerConversation == null)
            return false;
/*        if (innerConversation.id == 0)//inserted from send response agent
            innerConversation.id = getIdAsPlaceHolder();*/

        ContentValues contentValues = DbToolsHelper.convertObjectToContentValues(innerConversation, DbBontact.getAllInnerConversationFields());
        if (innerConversationsList == null)
            innerConversationsList = new ArrayList<>();
        innerConversationsList.add(innerConversation);
        if (context != null && contentValues != null) {
            contentValues.put(Contract.InnerConversation.COLUMN_TIME_REQUEST,
                    DateTimeHelper.convertDateStringToDbFormat(innerConversation.timeRequest));

            //insert
            context.getContentResolver().insert(Contract.InnerConversation.INNER_CONVERSATION_URI, contentValues);
            return true;
        }
        return false;
    }

    public static int getIdAsPlaceHolder() {
        return idPlaceHolder--;
    }

    public Conversation getCurrent_conversation() {
        return current_conversation;
    }

    public void sendResToUi() {
      /*  if (context != null && context instanceof ServerCallResponseToUi) {
            ((ServerCallResponseToUi) context).OnServerCallResponseToUi(true, current_conversation.idSurfer + "", null, getClass());
        }*/
    }

    public InnerConversation convertCursorToInnerConversation(Cursor cursor) {
        JSONObject jsonObject = DbToolsHelper.convertCursorToJsonObject(new InnerConversation(), cursor);
        if (jsonObject.length() > 0) {
            Gson gson = new Gson();
            InnerConversation innerConversation = gson.fromJson(jsonObject.toString(), InnerConversation.class);
            return innerConversation;
        }
        return null;
    }
    ServerCallResponse getDataOnResponse = new ServerCallResponse() {
        @Override
        public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
            if (isSuccsed == true) {
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("status").equals("true")) {
                        String inner_data = res.getJSONArray("data").toString();
                        Log.e("inner conversation", inner_data);
                        boolean result = saveServersData(inner_data);
                        // sendResToUi();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
