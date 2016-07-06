package com.example.chaya.bontact.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
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

    public ConverastionDataManager() {
        if (conversationList == null)
            conversationList = new ArrayList<>();

        context = null;

    }

    public void getFirstDataFromServer(Context context, String token) {
        current_page = 0;
        this.context = context;
        /*String sortOrder = Contract.Conversation.COLUMN_LAST_DATE  + " DESC";
      Cursor cursor=  context.getContentResolver().query(Contract.Conversation.INBOX_URI,null,null,null,sortOrder);
        if(cursor.moveToFirst()&&cursor.getCount()>0)
        for(int i=0;i<cursor.getCount();i++)
        {
            conversationList.add(cursor.moveToPosition(i))
        }*/
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
        Log.d("res", conversations);
        Gson gson = new Gson();
        try {
            JSONObject jsonObject = null;
            jsonObject = new JSONObject(conversations);
            JSONArray jsonConversationArray = jsonObject.getJSONArray("data");
         /*   if (context != null && current_page == 0)//check if it is the first data or not
            {
               context.getContentResolver().delete(Contract.Conversation.INBOX_URI, null, null);
              conversationList.
            }*/

            for (int i = 0; i < jsonConversationArray.length(); i++) {
                String strObj = jsonConversationArray.getJSONObject(i).toString();
                Conversation conversation = gson.fromJson(strObj, Conversation.class);
              /*  if(conversation.getLastSentence()==null)
                {
                    String lastSentence=ChanelsTypes.getDefultStringByChanelType(context, conversation.getLasttype());
                   // setLastSentence(context,conversation,lastSentence);
                    conversation.setLastSentence(lastSentence);
                }*/
                //add last defult sentences
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

            } /*catch (IllegalAccessException e) {
                e.printStackTrace();
            }*/
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
       /* Gson gson  =new Gson();
        if(cursor!=null&&cursor.moveToFirst()) {
            cursor.get
            Conversation conversation = new Conversation();
            gson.fromJson(strObj,Conversation.class);

        }*/
        return null;
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
}