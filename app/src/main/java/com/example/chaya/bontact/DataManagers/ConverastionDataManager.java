package com.example.chaya.bontact.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by chaya on 6/23/2016.
 */
public class ConverastionDataManager implements Callback {

    private Context context;
    public  static int current_page=0;

  public ConverastionDataManager()
  {
      context=null;

  }
    public void getFirstDataFromServer(Context context, String token)
    {
         current_page=0;
        this.context=context;
        getDataFromServer(context,token,current_page);
    }
    public void getNextDataFromServer(Context context, String token)
    {
        current_page++;
       this.context=context;
        getDataFromServer(context,token,current_page);
    }

    private void getDataFromServer(Context context,String token,int current_page)
    {
        if(token==null)
            sendRes(false,null, ErrorType.other);
        this.context=context;

    Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(context.getResources().getString(R.string.base_api))
                .appendPath(context.getResources().getString(R.string.rout_api))
                .appendPath(context.getResources().getString(R.string.conversation_api))
                .appendPath(token)
                .appendQueryParameter("page",current_page+"");

        String url = builder.build().toString();
       /* String url = context.getResources().getString(R.string.domain_api) + context.getResources().getString(R.string.conversation_api) + token;
        url += "?page=" + current_page;*/

        OkHttpRequests requests = new OkHttpRequests( url.toString(),this);
        try {
            requests.run();
        } catch (Exception e) {
            sendRes(false,null, ErrorType.network_problems);
        }
    }

    public boolean saveData(String conversations) throws IllegalAccessException {

      /*  List<Conversation> conversationList=new ArrayList<Conversation>();
      for(int i=0;i< conversationList.size();i++)
      {
          Conversation obj = conversationList.get(i);
        ContentValues contentValues = new ContentValues();
        for (Field field : obj.getClass().getDeclaredFields()) {
          //  field.setAccessible(true); // if you want to modify private fields
            String key=field.getName();
            Object o=field.get(obj);
            if(o instanceof String)
            contentValues.put(key,(String)o);
            else
                if(o instanceof Integer)
                    contentValues.put(key,(Integer)o);
        }
      }*/
       JSONArray conversationList = null;//get the data for conversation
        try {
            JSONObject jsonConversation=new JSONObject(conversations);
            conversationList = jsonConversation.getJSONArray("data");
        ArrayList<String> fields = DbBontact.getAllConversationFields();//get all table fields
        ContentValues cv = new ContentValues();
        if(current_page==0)//fill DB from new
            context.getContentResolver().delete(Contract.Conversation.INBOX_URI,null,null);
        for (int i = 0; i < conversationList.length(); i++) {
            JSONObject row = conversationList.getJSONObject(i);
            //put in cv all fields that can be added to the table
            cv.clear();
            for (String key : fields) {
                if (row.has(key)) {
                    cv.put(key, "");
                    int ValueI = -1;
                    String ValueS = "no";
                    ValueI = row.optInt(key, -1);
                    if (ValueI != -1) {
                        Log.d(key, " int " + ValueI);
                        cv.put(key, ValueI);
                    } else {
                        ValueS = row.optString(key, "no");
                        if (!ValueS.equals("no")) {
                            Log.d(key, "str " + ValueS);
                            cv.put(key, ValueS);
                        } else
                            //Date ValueD=row.optD
                            Log.d("else " + key, row.get(key).toString());
                    }
                        if (key.equals(Contract.Conversation.FLAG_COUNTRY))//get the country - Extreme case
                         {
                        JSONObject flag = row.getJSONObject(key);
                      ValueS= flag.optString(Contract.Conversation.COLUMN_COUNTRY,"no");
                        if(!ValueS.equals("no"))
                        {
                            Log.d(key, "str " + ValueS);
                        cv.put(key, ValueS);
                        }
                        }

                    }
                }
            Uri uri = context.getContentResolver().insert(Contract.Conversation.INBOX_URI, cv);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
return true;
    }
    @Override
    public void onFailure(Call call, IOException e) {
        sendRes(false,null,ErrorType.network_problems);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
            sendRes(false,null,ErrorType.network_problems);
            return;
        }
                // JSONObject res = new JSONObject(response.body().string()).getJSONObject("conversations");
             //   res.getJSONObject("conversations")
                sendRes(true,response.body().string(),null);
    }
    public void sendRes(boolean isSuccsed, String response, ErrorType errorType)
    {
        if(isSuccsed==true&&response!=null)
        {
            JSONObject resObj=null;
            try {
                resObj=new JSONObject(response);
                resObj=resObj.getJSONObject("conversations");
                saveData(resObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        else{
            //don't do anything
        }
    }

}
