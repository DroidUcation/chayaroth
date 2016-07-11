package com.example.chaya.bontact.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.Helpers.AvatarHelper;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DbToolsHelper;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponseToUi;
import com.example.chaya.bontact.R;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 6/23/2016.
 */
public class ConverastionDataManager implements ServerCallResponse {

    public static List<Conversation> conversationList = null;
    private Context context;
    public static int current_page = 0;

    public ConverastionDataManager(Context context) {
        if (conversationList == null)
            conversationList = new ArrayList<>();
        this.context = context;
        fillConversationList(context);
    }

    public void getFirstDataFromServer(Context context, String token) {
        current_page = 0;
        this.context = context;
        getDataFromServer(context, token, current_page);
    }

    public void getNextDataFromServer(Context context, String token) {
        current_page++;
        this.context = context;
        getDataFromServer(context, token, current_page);
    }

    private void getDataFromServer(Context context, String token, int current_page) {
        if (token != null) {
            this.context = context;

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(context.getResources().getString(R.string.base_api))
                    .appendPath(context.getResources().getString(R.string.rout_api))
                    .appendPath(context.getResources().getString(R.string.conversation_api))
                    .appendPath(token)
                    .appendQueryParameter("page", current_page + "");

            String url = builder.build().toString();

            OkHttpRequests requests = new OkHttpRequests(url, this);
        }

    }

    public boolean saveData(String conversations) {

        Gson gson = new Gson();
        try {
            JSONObject jsonObject = null;
            jsonObject = new JSONObject(conversations);
            JSONArray jsonConversationArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonConversationArray.length(); i++) {
                String strObj = jsonConversationArray.getJSONObject(i).toString();
                Conversation conversation = gson.fromJson(strObj, Conversation.class);
                conversation.avatar= AvatarHelper.getNextAvatar()+"";
                conversationList.add(conversation);
                ContentValues contentValues = DbToolsHelper.convertObjectToContentValues(conversation, DbBontact.getAllConversationFields());
                if (contentValues != null && context != null) {
                    context.getContentResolver().insert(Contract.Conversation.INBOX_URI, contentValues);
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {

        if (isSuccsed == true && response != null) {
            JSONObject resObj = null;
            try {
                resObj = new JSONObject(response);
                resObj = resObj.getJSONObject("conversations");
                boolean result = saveData(resObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();

            }
        } else {
            //don't do anything
        }
    }

    public void sendResToUi(boolean isSuccsed, String response, ErrorType errorType) {
        if (context != null && context instanceof ServerCallResponseToUi) {
            ((ServerCallResponseToUi) context).OnServerCallResponseToUi(isSuccsed, response, errorType, getClass());
        }
    }

    public Conversation getConversationByIdSurfer(int idSurfer) {

        if (conversationList != null && conversationList.size() > 0) {
            for (Conversation conversation : conversationList) {
                if (conversation.idSurfer == idSurfer)
                    return conversation;
            }
             }
        return null;
    }

    public Conversation convertCursorToConversation(Cursor cursor) {
         if(cursor==null&&!cursor.moveToFirst())
                 return null;
                Gson gson =new Gson();
                JSONObject jsonObject=new JSONObject();
                String resStr=null;
                int resInt;

               for (String column : cursor.getColumnNames()) {
                   try {
                       resStr = cursor.getString(cursor.getColumnIndex(column));
                       if (resStr == null) {
                           resInt = cursor.getInt(cursor.getColumnIndex(column));
                           jsonObject.put(column, resInt);
                       }
                       else
                           jsonObject.put(column, resStr);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
               if(jsonObject.length()>0)
               {
                   Conversation conversation = gson.fromJson(jsonObject.toString(), Conversation.class);
                   return conversation;
               }
        return null;
    }
    public boolean insertOrUpdateConversationInList(Conversation conversation)
    {
        if(conversation==null)
            return false;
        if (conversationList == null)
            conversationList=new ArrayList<>();

        int index= conversationList.indexOf(getConversationByIdSurfer(conversation.idSurfer));
        if(index!=-1) {
            conversationList.set(index, conversation);
            return true;
        }
        else {
            conversationList.add(conversation);
            return true;
        }

    }
    public void fillConversationList(Context context)
    {
      Cursor cursor =context.getContentResolver().query(Contract.Conversation.INBOX_URI,null,null,null,null);
        while(cursor.moveToNext()) {
            Conversation conversation = convertCursorToConversation(cursor);
            insertOrUpdateConversationInList(conversation);
        }


    }
    public boolean setLastSentence(Context context, Conversation conversation,String sentence)
    {
        if(conversation!=null && sentence!=null)
        {
            conversation.setLastSentence(sentence);
            if(context!=null)
            {
                String selectionStr=Contract.Conversation.COLUMN_ID_SURFER+"=?";
                String[]  selectionArgs={conversation.idSurfer+""};
                ContentValues values=new ContentValues();
                values.put(Contract.Conversation.COLUMN_LAST_SENTENCE,sentence);
                context.getContentResolver().update(Contract.Conversation.INBOX_URI,values,selectionStr,selectionArgs );
            }
            return true;
        }
      return false;
    }
   public boolean updateConversation(Context context,int idSurfer,String fieldName,int value)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(fieldName,value);
        return updateConversation(context,contentValues,idSurfer);
    }
    public boolean updateConversation(Context context,int idSurfer,String fieldName,String value)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(fieldName,value);
        return updateConversation(context,contentValues,idSurfer);
    }
    public boolean updateConversation(Context context, int idSurfer,String fieldName,boolean value)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(fieldName,value);
        return updateConversation(context,contentValues,idSurfer);
    }
    private boolean updateConversation(Context context,ContentValues contentValues,int idSurfer)
     {
    String selectionStr=Contract.Conversation.COLUMN_ID_SURFER+"=?";
    String[]  selectionArgs={idSurfer+""};
   int result= context.getContentResolver().update(Contract.Conversation.INBOX_URI,contentValues,selectionStr,selectionArgs);
    if(result>0) {
      Cursor cursor= context.getContentResolver().query(Contract.Conversation.INBOX_URI,null,selectionStr,selectionArgs,null);
        cursor.moveToFirst();
       insertOrUpdateConversationInList(convertCursorToConversation(cursor));
        return true;
    }
    return false;
    }
    public void updateOnlineState(Context context, int idSurfer,int state)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(Contract.Conversation.COLUMN_IS_ONLINE,state);
        String selectionStr=Contract.Conversation.COLUMN_ID_SURFER+"=?";
        String[]  selectionArgs={idSurfer+""};
        context.getContentResolver().update(Contract.Conversation.INBOX_URI,contentValues,selectionStr,selectionArgs);
        Cursor cursor= context.getContentResolver().query(Contract.Conversation.INBOX_URI,null,selectionStr,selectionArgs,null);
       if( cursor.moveToFirst())
         insertOrUpdateConversationInList(convertCursorToConversation(cursor));

    }



}