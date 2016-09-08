package com.example.chaya.bontact.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.Helpers.ChannelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Helpers.DatesHelper;
import com.example.chaya.bontact.Helpers.DbToolsHelper;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chaya on 6/26/2016.
 */
public class InnerConversationDataManager {

    private Context context;
    private Conversation current_conversation;
    private int idSurfer;
    //private List<InnerConversation> innerConversationsList;
    public static int idPlaceHolder = -1;
    public ServerCallResponse callbackEmptyData;

    public InnerConversationDataManager(Context context, Conversation current_conversation) {

        this.current_conversation = current_conversation;
        this.context = context;
        // innerConversationsList = new ArrayList<>();
        if (current_conversation != null)
            this.idSurfer = current_conversation.idSurfer;
    }

    public InnerConversationDataManager(Context context, int currentIdSurfer) {
        this(context, null);
        ConversationDataManager conversationDataManager = new ConversationDataManager(context);
        this.current_conversation = conversationDataManager.getConversationByIdSurfer(currentIdSurfer);
        idSurfer = currentIdSurfer;
    }


    public void getData(Context context, String token, ServerCallResponse callbackEmptyData) {

        this.context = context;
        this.callbackEmptyData = callbackEmptyData;
        //sendResToUi();
        getDataFromServer(context, token);
    }

    public void getDataFromServer(Context context, String token) {
        String id_surfer_string = String.valueOf(idSurfer);
        if (token != null && id_surfer_string != null) {
            this.context = context;
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(context.getResources().getString(R.string.base_dev_api))
                    .appendPath(context.getResources().getString(R.string.rout_api))
                    .appendPath(context.getResources().getString(R.string.inner_conversation_api))
                    .appendPath(token)
                    .appendPath(id_surfer_string);

            String url = builder.build().toString();

            OkHttpRequests requests = new OkHttpRequests(url, getDataOnResponse);
        }
    }

    public boolean saveServersData(String data, ServerCallResponse callbackEmptyData) {
        try {
            JSONArray DataArray = new JSONArray(data);
            if (DataArray.length() == 0) {
                if (callbackEmptyData != null)
                    notifyEmptyInnerData(callbackEmptyData);
                notifyEmptyInnerData();
                return false;
            }

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
                innerConversation.timeRequest = DatesHelper.convertDateToCurrentGmt(innerConversation.timeRequest)
                ;                //delete all place holder
                String selectionStr = Contract.InnerConversation.COLUMN_ID + "<0";
                context.getContentResolver().delete(Contract.InnerConversation.INNER_CONVERSATION_URI, selectionStr, null);
                saveData(innerConversation);
            }

            if (innerConversation != null && innerConversation.getMess() != null) {//check type
                ConversationDataManager conversationDataManager = new ConversationDataManager(context);
                conversationDataManager.updateLastMessage(current_conversation.idSurfer, innerConversation.getMess());
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
        if (current_conversation == null) {
            ConversationDataManager conversationDataManager = new ConversationDataManager(context);
            current_conversation = conversationDataManager.getConversationByIdSurfer(idSurfer);
        }
        if (current_conversation != null) {
            if (current_conversation.innerConversationData == null)
                current_conversation.innerConversationData = new ArrayList<>();
            current_conversation.innerConversationData.add(innerConversation);
        }
        if (context != null && contentValues != null) {
            //  contentValues.put(Contract.InnerConversation.COLUMN_TIME_REQUEST,DateTimeHelper.convertDateStringToDbFormat(innerConversation.timeRequest));
            contentValues.put(Contract.InnerConversation.COLUMN_MESS,
                    DbToolsHelper.removeHtmlTags(contentValues.getAsString(Contract.InnerConversation.COLUMN_MESS)));
            //insert
            context.getContentResolver().insert(Contract.InnerConversation.INNER_CONVERSATION_URI, contentValues);
            return true;
        }
        return false;
    }

    public static int getIdAsPlaceHolder() {
        return idPlaceHolder--;
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

    public void notifyEmptyInnerData() {
        if (this.callbackEmptyData != null)
            notifyEmptyInnerData(this.callbackEmptyData);
    }

    public static void notifyEmptyInnerData(ServerCallResponse callback) {
        if (callback != null)
            callback.OnServerCallResponse(true, "[]", null);
    }

    ServerCallResponse getDataOnResponse = new ServerCallResponse() {
        @Override
        public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
            if (isSuccsed == false && errorType == ErrorType.network_problems)
                notifyEmptyInnerData();
            if (isSuccsed == true) {
                try {
                    JSONObject res = new JSONObject(response);
                    if (res.getString("status").equals("true")) {
                        String inner_data = res.getJSONArray("data").toString();
                        Log.e("inner conversation", inner_data);
                        saveServersData(inner_data, null);
                        // sendResToUi();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void addTextMsgToList(int channelType, String textMsg, boolean systemMsg) {
        InnerConversation innerConversation = new InnerConversation();
        innerConversation.id = getIdAsPlaceHolder();
        innerConversation.actionType = channelType;
        innerConversation.mess = textMsg;
        innerConversation.rep_request = true;
        if (AgentDataManager.getAgentInstance() != null)
            innerConversation.agentName = AgentDataManager.getAgentInstance().getName();
        innerConversation.name = innerConversation.agentName;
        if (current_conversation != null)
            innerConversation.idSurfer = current_conversation.idSurfer;
        //innerConversation.timeRequest = DateTimeHelper.getCurrentStringDateInGmtZero();
        innerConversation.timeRequest = DateTimeHelper.dateFullFormat.format(new Date());
        if (channelType != ChannelsTypes.callback && channelType != ChannelsTypes.webCall)
            innerConversation.datatype = 1;//txt msg
        innerConversation.systemMsg = systemMsg;
        //Toast.makeText(InnerConversationActivity.this, "ADD MSG " + innerConversation.toString(), Toast.LENGTH_SHORT).show();
        saveData(innerConversation);
    }

}
