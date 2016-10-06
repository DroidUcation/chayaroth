package com.example.chaya.bontact.DataManagers;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Helpers.ChannelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Helpers.DatesHelper;
import com.example.chaya.bontact.Helpers.DbToolsHelper;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Socket.io.SocketManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chaya on 6/23/2016.
 */
public class ConversationDataManager {

    public static List<Conversation> conversationList = null;
    private Context context;
    public static int current_page = 0;
    public static int unread_conversations = 0;
    public static int selectedIdConversation = 0;
    public ServerCallResponse innerEmptyDataCallback;

    public ConversationDataManager(Context context) {
        if (conversationList == null)
            conversationList = new ArrayList<>();
        this.context = context;
        if (context != null && conversationList.size() == 0) {
            fillConversationList(context);
            SharedPreferences preferences = context.getSharedPreferences(context.getResources().getString(R.string.sp_user_details), context.MODE_PRIVATE);
            setCurrentPage(preferences.getInt(context.getResources().getString(R.string.current_page), 0)-1, context);
        }
    }

    public static void setCurrentPage(int value, Context context) {
        current_page = value < 0 ? 0 : value;
        SharedPreferences preferences = context.getSharedPreferences(context.getResources().getString(R.string.sp_user_details), context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getResources().getString(R.string.current_page), current_page);
        editor.apply();
    }

    //--mange functions
    public void fillConversationList(Context context) {
        if (conversationList.size() > 0) {
            return;
        } //todo: do in async task
        Cursor cursor = context.getContentResolver().query(Contract.Conversation.INBOX_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst())
            do {
                Conversation conversation = DbToolsHelper.convertCursorToConversation(cursor);
                insertOrUpdate(conversation, false);
            } while (cursor.moveToNext());
        cursor.close();

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

    public Conversation insertOrUpdate(Conversation conversation, boolean insertToDb) {

        if (conversation == null)
            return null;
        if (conversationList == null)
            conversationList = new ArrayList<>();
        int index = conversationList.indexOf(getConversationByIdSurfer(conversation.idSurfer));
        if (index != -1) {
            return update(conversation);
        } else {
            //syncUnreadConversation(conversation);
            conversation.isOnline = VisitorsDataManager.isOnline(conversation.idSurfer);
            conversationList.add(conversation);
            if (insertToDb == true) {
                if (conversation.unread > 0 && conversation.idSurfer != selectedIdConversation) {
                    setAllUnreadConversations(context, getAllUnreadConversations(context) + 1);
                }

                ContentValues contentValues = DbToolsHelper.convertConversationToContentValues(conversation);
                if (contentValues != null && context != null) {
                    context.getContentResolver().insert(Contract.Conversation.INBOX_URI, contentValues);

                    notifyListChanged(conversation.idSurfer);
                    return conversation;
                }
            }
        }
        return null;
    }

    private Conversation update(Conversation conversation) {

        if (conversation == null || conversationList == null)
            return null;
        int index = conversationList.indexOf(getConversationByIdSurfer(conversation.idSurfer));
        // Log.d("updated", conversation.idSurfer + " " + conversation.unread + " " + index);
        if (index == -1) {
            return null;
        }
        //conversation = syncUnreadConversation(conversation);
        conversationList.set(index, conversation);
        String selectionStr = Contract.Conversation.COLUMN_ID_SURFER + "=?";
        String[] selectionArgs = {String.valueOf(conversation.idSurfer)};
        int result = context.getContentResolver().update(Contract.Conversation.INBOX_URI, DbToolsHelper.convertConversationToContentValues(conversation), selectionStr, selectionArgs);
        notifyListChanged(conversation.idSurfer);
        return conversation;
    }

    public Conversation syncUnreadConversation(Conversation conversation) {
        if (conversation == null)
            return null;
        if (conversation.idSurfer == selectedIdConversation && conversation.unread > 0) {
            conversation.unread = 0;
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(context.getResources().getString(R.string.base_dev_api))
                    .appendPath(context.getResources().getString(R.string.rout_api))
                    .appendPath(context.getResources().getString(R.string.contacts_rout_api))
                    .appendPath(context.getResources().getString(R.string.read_conversation_api))
                    .appendPath(AgentDataManager.getAgentInstance().getToken())
                    .appendPath(String.valueOf(conversation.idSurfer));
            String url = builder.build().toString();
            Log.d("syncunread", url);
            new OkHttpRequests(url, null);
            return conversation;
        }
        return null;
    }

    public boolean updateOnlineState(int idSurfer, boolean state) {
        Conversation conversation = getConversationByIdSurfer(idSurfer);
        if (conversation != null) {
            conversation.isOnline = state;
            //  if (update(conversation) != null) {
            /*Intent intent = new Intent(context.getResources().getString(R.string.change_visitor_online_state));
            intent.setType("");
            intent.putExtra(context.getResources().getString(R.string.online_state), state);
            intent.putExtra(context.getResources().getString(R.string.id_surfer), idSurfer);
            context.sendBroadcast(intent);*/
            return true;
            //  }
        }
        return false;


    }

    public boolean updateUnread(int idSurfer, int newUnreadCount) {
        Conversation conversation = getConversationByIdSurfer(idSurfer);
        if (conversation != null) {
            if (conversation.unread == 0 && newUnreadCount > 0 && conversation.idSurfer != selectedIdConversation)
                setAllUnreadConversations(context, getAllUnreadConversations(context) + 1);
            if (conversation.unread > 0 && newUnreadCount == 0 && conversation.idSurfer == selectedIdConversation)
                setAllUnreadConversations(context, getAllUnreadConversations(context) - 1);
            conversation.unread = newUnreadCount;
            if (update(conversation) != null)
                return true;
        }
        return false;
    }

    public boolean updateLastType(int idSurfer, int type) {
        Conversation conversation = getConversationByIdSurfer(idSurfer);
        if (conversation != null)
            conversation.lasttype = type;
        if (update(conversation) != null)
            return true;
        return false;
    }

    public boolean updateLastDate(int idSurfer, String lastDate) {
        //lastDate = DateTimeHelper.convertDateStringToDbFormat(lastDate);
        Conversation conversation = getConversationByIdSurfer(idSurfer);
        if (conversation != null)
            conversation.lastdate = lastDate;
        if (update(conversation) != null)
            return true;
        return false;
    }

    public boolean updateEmail(int idSurfer, String email) {
        Conversation conversation = getConversationByIdSurfer(idSurfer);
        if (conversation != null)
            conversation.email = email;
        if (update(conversation) != null)
            return true;
        return false;
    }

    public boolean updatePhoneNumber(int idSurfer, String phoneNumber) {
        Conversation conversation = getConversationByIdSurfer(idSurfer);
        if (conversation != null)
            conversation.phone = phoneNumber;
        if (update(conversation) != null)
            return true;
        return false;
    }

    public boolean updateLastMessage(int idSurfer, String lastMsg) {
        Conversation conversation = getConversationByIdSurfer(idSurfer);
        if (conversation != null) {
            conversation.lastMessage = lastMsg;
        }
        if (update(conversation) != null)
            return true;
        return false;
    }

    public void notifyListChanged(int idSurfer) {
        Intent intent = new Intent(context.getResources().getString(R.string.change_conversation_list_action));
        intent.setType("*/*");
        intent.putExtra(context.getResources().getString(R.string.id_surfer), idSurfer);
        if (context != null)
            context.sendBroadcast(intent);
    }

    public void notifyEmptyData() {
        Intent intent = new Intent(context.getResources().getString(R.string.empty_conversation_data_action));
        intent.setType("*/*");
        if (context != null)
            context.sendBroadcast(intent);
    }

    public static int getAllUnreadConversations(Context context) {
        return unread_conversations;
    }

    public static void setAllUnreadConversations(Context context, int unread) {
        unread_conversations = unread;
        Intent intent = new Intent(context.getResources().getString(R.string.change_unread_conversations_action));
        intent.setType("*/*");
        context.sendBroadcast(intent);
    }

    //--server functions
    public void getFirstDataFromServer(Context context, String token) {
        this.context = context;
        getDataFromServer(context, token, 0);
    }

    public void getNextDataFromServer(Context context, String token) {
        this.context = context;
        setCurrentPage(current_page+1,context);
        getDataFromServer(context, token, current_page);
    }

    public void getDataFromServer(Context context, String token, int current_page) {
        if (token != null) {
            this.context = context;
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(context.getResources().getString(R.string.base_dev_api))
                    .appendPath(context.getResources().getString(R.string.rout_api))
                    .appendPath(context.getResources().getString(R.string.contacts_rout_api))
                    .appendPath(context.getResources().getString(R.string.conversations_api))
                    .appendPath(token)
                    .appendPath(String.valueOf(current_page));

            String url = builder.build().toString();

            OkHttpRequests requests = new OkHttpRequests(url, getAllDataCallback);
        }
    }

    public boolean saveData(JSONArray jsonConversationArray) {
        if (jsonConversationArray == null)
            return false;
        if (jsonConversationArray.equals("[]") || jsonConversationArray.length() == 0) {
            notifyEmptyData();
        }
        Gson gson = new Gson();
        try {
            for (int i = 0; i < jsonConversationArray.length(); i++) {
                String strObj = jsonConversationArray.getJSONObject(i).toString();
                Conversation conversation = gson.fromJson(strObj, Conversation.class);
                conversation.lastdate = DatesHelper.convertDateToCurrentGmt(conversation.lastdate);
                insertOrUpdate(conversation, true);
            }
            SocketManager.getInstance().refreshSelectConversation();
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    ServerCallResponse getAllDataCallback = new ServerCallResponse() {
        @Override
        public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
            if (isSuccsed == true && response != null) {
                try {
                    JSONArray array = new JSONObject(response).getJSONObject("conversations").getJSONArray("data");
                    saveData(array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void getConversationByIdFromServer(String token, int idSurfer, ServerCallResponse regularCallback, ServerCallResponse emptyDataCallback) {
        if (emptyDataCallback != null)
            this.innerEmptyDataCallback = emptyDataCallback;
        if (token != null) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(context.getResources().getString(R.string.base_dev_api))
                    .appendPath(context.getResources().getString(R.string.rout_api))
                    .appendPath(context.getResources().getString(R.string.contacts_rout_api))
                    .appendPath(context.getResources().getString(R.string.conversation_by_id_api))
                    .appendPath(String.valueOf(idSurfer))
                    .appendPath(token);
            String url = builder.build().toString();
            if (regularCallback != null)
                new OkHttpRequests(url, regularCallback);
            else
                new OkHttpRequests(url, getFullConversationByIdOnResponse);
        }
    }


    public void getAllUnreadFromServer(Context context) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(context.getResources().getString(R.string.base_dev_api))
                .appendPath(context.getResources().getString(R.string.rout_api))
                .appendPath(context.getResources().getString(R.string.count_conversation_api))
                .appendPath(AgentDataManager.getAgentInstance().getToken());
        String url = builder.build().toString();
        OkHttpRequests okHttpRequests = new OkHttpRequests(url, getAllUnreadCountCallback);
    }

    ServerCallResponse getAllUnreadCountCallback = new ServerCallResponse() {
        @Override
        public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
            if (isSuccsed == true && response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("status");//user exists
                    if (msg.equals("true")) {
                        int unread_conversations = jsonObject.getJSONObject("conversations").getInt("newitems");
                        SharedPreferences Preferences = context.getSharedPreferences(context.getResources().getString(R.string.sp_count_numbers), context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = Preferences.edit();
                        editor.clear();
                        editor.putInt(context.getResources().getString(R.string.count_unread_conversation), unread_conversations);
                        editor.apply();

                        setAllUnreadConversations(context, unread_conversations);

                      /*  if (context != null && context instanceof ServerCallResponseToUi) {
                            ((ServerCallResponseToUi) context).OnServerCallResponseToUi(true, response, null, ConversationDataManager.class);
                        }*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    ServerCallResponse getFullConversationByIdOnResponse = new ServerCallResponse() {
        @Override
        public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
            if (isSuccsed == true && response != null) {
                Log.d("full api", response);
                Gson gson = new Gson();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response).getJSONObject("conversations");
                    Log.d("json obj", response);
                    if (jsonObject.length() == 0)
                        InnerConversationDataManager.notifyEmptyInnerData(innerEmptyDataCallback);

                    Conversation conversation = gson.fromJson(jsonObject.toString(), Conversation.class);
                    if (conversation != null) {
                        conversation.lastdate = DatesHelper.convertDateToCurrentGmt(conversation.lastdate);
                        insertOrUpdate(conversation, true);
                        InnerConversationDataManager innerConversationDataManager = new InnerConversationDataManager(context, conversation);
                        innerConversationDataManager.saveServersData(jsonObject.getJSONArray("data").toString(), innerEmptyDataCallback);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };


}