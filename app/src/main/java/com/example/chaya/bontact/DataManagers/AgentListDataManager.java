package com.example.chaya.bontact.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Helpers.DbToolsHelper;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.Models.Representative;
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
 * Created by chaya on 9/11/2016.
 */
public class AgentListDataManager {
    public static Context staticContext;
    public static List<Representative> representativeList = new ArrayList<>();

    public static void getAllAgents(Context context) {
        staticContext = context;
        if (AgentDataManager.getAgentInstance() != null) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(context.getResources().getString(R.string.base_dev_api))
                    .appendPath(context.getResources().getString(R.string.rout_api))
                    .appendPath(context.getResources().getString(R.string.agents_api))
                    .appendPath(AgentDataManager.getAgentInstance().getToken());
            String url = builder.build().toString();
            OkHttpRequests okHttpRequests = new OkHttpRequests(url, getAgentsCallback);
            fillAgentsList(context);
        }
    }

    private static ServerCallResponse getAgentsCallback = new ServerCallResponse() {
        @Override
        public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
            if (response != null && isSuccsed == true) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status") == true) {
                        JSONArray agents = jsonObject.getJSONArray("result");
                        saveData(agents);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    public static void fillAgentsList(Context context) {
        if (representativeList.size() > 0) {
            return;
        }
        Cursor cursor = context.getContentResolver().query(Contract.Agents.AGENTS_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Representative rep = DbToolsHelper.convertCursorToRepresentative(cursor);
                insert(rep, false);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public static boolean saveData(JSONArray agents) {
        if (agents == null)
            return false;
        Gson gson = new Gson();
        if (representativeList == null)
            representativeList = new ArrayList<>();
        for (int i = 0; i < agents.length(); i++) {
            try {
                String str = agents.getJSONObject(i).toString();
                Representative rep = gson.fromJson(str, Representative.class);
                insertOrUpdate(rep);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void insertOrUpdate(Representative rep) {
        if (rep == null)
            return;
        if (representativeList == null)
            representativeList = new ArrayList<>();
        int index = getRepIndex(rep.idRepresentive);
        if (index == -1)//agent not exists
            insert(rep, true);
        else
            update(index, rep, true);

    }

    public static int getRepIndex(int idRep) {
        if (representativeList == null || representativeList.size() == 0)
            return -1;
        for (Representative representative : representativeList)
            if (representative.idRepresentive == idRep)
                return representativeList.indexOf(representative);

        return -1;
    }

    private static void insert(Representative rep, boolean insertToDb) {
        representativeList.add(rep);
        if (!insertToDb)
            return;
        ContentValues contentValues = DbToolsHelper.convertRepresentativeToContentValues(rep);
        if (contentValues != null && staticContext != null) {
            staticContext.getContentResolver().insert(Contract.Agents.AGENTS_URI, contentValues);
        }

    }

    private static void update(int index, Representative rep, boolean updateToDb) {
        representativeList.set(index, rep);
        if (!updateToDb)
            return;
        ContentValues contentValues = DbToolsHelper.convertRepresentativeToContentValues(rep);
        if (contentValues != null && staticContext != null) {
            String selectionStr = Contract.Agents.COLUMN_ID_REP + "=?";
            String[] selectionArgs = {String.valueOf(rep.idRepresentive)};
            staticContext.getContentResolver().update(Contract.Agents.AGENTS_URI, contentValues, selectionStr, selectionArgs);
        }
    }


}
