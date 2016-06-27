package com.example.chaya.bontact.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.Helpers.DbToolsHelper;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
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
 * Created by chaya on 6/26/2016.
 */
public class InnerConversationDataManager implements ServerCallResponse {

    private Context context;
    private Conversation current_conversation;
    private List<InnerConversation> innerConversationsList;

    public InnerConversationDataManager()
    {
        current_conversation =null;
        context=null;
        innerConversationsList=new ArrayList<>();
    }
    public void getFirstDataFromServer(Context context, String token, Conversation current_conversation)
    {
        this.context=context;
        this.current_conversation =current_conversation;
        getDataFromServer(context,token,current_conversation.idSurfer);
    }
    public void getNextDataFromServer(Context context, String token)
    {

    }

    private void getDataFromServer(Context context,String token,int id_surfer)
    {
        String id_surfer_string=id_surfer+"";
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
    public boolean saveData(String data)
    {
        Gson gson  =new Gson();
        try {
            JSONArray DataArray=new JSONArray(data);
            //check if it is the first data or not
            context.getContentResolver().delete(Contract.InnerConversation.INNER_CONVERSATION_URI,null,null);

            for(int i=0;i<DataArray.length();i++)
                {
                 String strObj=DataArray.getJSONObject(i).toString();
                 InnerConversation innerConversation=  gson.fromJson(strObj,InnerConversation.class);
                 ContentValues contentValues= DbToolsHelper.convertObjectToContentValues(innerConversation,DbBontact.getAllInnerConversationFields());

                    if(context!=null&&contentValues!=null)
                  context.getContentResolver().insert(Contract.InnerConversation.INNER_CONVERSATION_URI,contentValues);
                }
          return true;

        } catch (JSONException e) {
          return false;
        }
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
                   sendResToUi();
               }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public void sendResToUi()
    {
        if(context!=null&&context instanceof ServerCallResponseToUi)
        {
            ((ServerCallResponseToUi)context).OnServerCallResponseToUi(true,id_surfer+"",null,getClass());
        }

    }
}
