package com.example.chaya.bontact.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DbToolsHelper;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponseToUi;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Activities.InnerConversationActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 6/26/2016.
 */
public class InnerConversationDataManager implements ServerCallResponse {

    private Context context;
    private Conversation current_conversation;
    private List<InnerConversation> innerConversationsList;

    public InnerConversationDataManager(Context context, Conversation current_conversation)
    {

        this.current_conversation =current_conversation;
        this.context=context;
        innerConversationsList=new ArrayList<>();
    }
    public void getData(Context context, String token)
    {
        this.context=context;
        sendResToUi();
       /* String selectionStr=Contract.InnerConversation.COLUMN_ID_SURFUR+"=?";
        String[]  selectionArgs={current_conversation.idSurfer+""};
        if(context.getContentResolver().query(Contract.InnerConversation.INNER_CONVERSATION_URI,null,selectionStr,selectionArgs,null)!=null)//there ara culomns for this user
       */
       // sendResToUi();
        getDataFromServer(context,token);
    }

    public void getDataFromServer(Context context, String token)
    {
        if(current_conversation!=null)
        {
            String id_surfer_string=current_conversation.idSurfer+"";
        if(token!=null&&id_surfer_string!=null) {
            this.context = context;
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(context.getResources().getString(R.string.base_api))
                    .appendPath(context.getResources().getString(R.string.rout_api))
                    .appendPath(context.getResources().getString(R.string.inner_conversation_api))
                    .appendPath(token)
                    .appendPath(id_surfer_string);

            String url = builder.build().toString();

            OkHttpRequests requests = new OkHttpRequests(url, this);
        }
    }
    }
    public boolean saveData(String data)
    {
        try {
            JSONArray DataArray=new JSONArray(data);
            Gson gson  =new Gson();
            //delete this users data
            String selectionStr=Contract.InnerConversation.COLUMN_ID_SURFUR+"=?";
            String[]  selectionArgs={current_conversation.idSurfer+""};
            context.getContentResolver().delete(Contract.InnerConversation.INNER_CONVERSATION_URI, selectionStr, selectionArgs);

            InnerConversation innerConversation=null;
            for(int i=0;i<DataArray.length();i++)
            {
                String strObj= null;
                try {
                    strObj = DataArray.getJSONObject(i).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                innerConversation=  gson.fromJson(strObj,InnerConversation.class);
            saveData(innerConversation);
            }

            if(innerConversation!=null&&innerConversation.getMess()!=null)//check type
            {
                ConverastionDataManager converastionDataManager=new ConverastionDataManager(context);
                converastionDataManager.setLastSentence(context, current_conversation,innerConversation.getMess());
            }
            return true;

        } catch (JSONException e) {
          return false;
        }
    }
    public boolean saveData(InnerConversation innerConversation)
    {
        ContentValues contentValues= DbToolsHelper.convertObjectToContentValues(innerConversation,DbBontact.getAllInnerConversationFields());
        if(innerConversationsList==null)
            innerConversationsList=new ArrayList<>();
        innerConversationsList.add(innerConversation);
        if(context!=null&&contentValues!=null)
        {
            context.getContentResolver().insert(Contract.InnerConversation.INNER_CONVERSATION_URI, contentValues);
            return true;
        }
        return false;
    }
    @Override
    public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
        if(isSuccsed==true)
        {
            try {
                JSONObject res=new JSONObject(response);
               if(res.getString("status").equals("true"))
               {
                 String inner_data=res.getJSONArray("data").toString();
                boolean result= saveData(inner_data);
                  // sendResToUi();
               }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public Conversation getCurrent_conversation() {
        return current_conversation;
    }

    public void sendResToUi()
    {
        if(context!=null&&context instanceof ServerCallResponseToUi)
        {
            ((ServerCallResponseToUi)context).OnServerCallResponseToUi(true,current_conversation.idSurfer+"",null,getClass());
        }

    }

}
