package com.example.chaya.bontact.Helpers;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.Models.Conversation;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 6/26/2016.
 */
public class DbToolsHelper {

    public static ContentValues convertObjectToContentValues(Object obj, ArrayList<String> tableFields) {
        if (obj == null)
            return null;
        ContentValues contentValues = new ContentValues();
        for (Field field : obj.getClass().getDeclaredFields()) {
            String key = field.getName();
            if (tableFields.contains(key)) {
                Object value = null;
                try {
                    value = field.get(obj);
                    if (value instanceof Integer)
                        contentValues.put(key, (Integer) value);
                    else if (value instanceof String)
                        contentValues.put(key, (String) value);
                    else if (value instanceof Boolean)
                        contentValues.put(key, (Boolean) value);
              /*  else if(value==null)
                    contentValues.put(key, (byte[]) null);*/
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return contentValues;
    }

    public static ContentValues convertConversationToContentValues(Conversation conversation) {
        if (conversation == null)
            return null;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Conversation.COLUMN_ID_SURFER, conversation.idSurfer);
        contentValues.put(Contract.Conversation.COLUMN_NAME, conversation.visitor_name);
        contentValues.put(Contract.Conversation.COLUMN_AVATAR, conversation.avatar);
        contentValues.put(Contract.Conversation.COLUMN_RETURNING, conversation.returning);
        contentValues.put(Contract.Conversation.COLUMN_CLOSED, conversation.closed);
        contentValues.put(Contract.Conversation.COLUMN_RESOLVED, conversation.resloved);
        contentValues.put(Contract.Conversation.COLUMN_LAST_DATE, DateTimeHelper.convertDateStringToDbFormat(conversation.lastdate));
        contentValues.put(Contract.Conversation.COLUMN_LAST_TYPE, conversation.lasttype);
        contentValues.put(Contract.Conversation.COLUMN_ACTION_ID, conversation.actionId);
        contentValues.put(Contract.Conversation.COLUMN_REPLY, conversation.reply);
        contentValues.put(Contract.Conversation.COLUMN_LAST_MESSAGE, removeHtmlTags(contentValues.getAsString(Contract.Conversation.COLUMN_LAST_MESSAGE)));
        contentValues.put(Contract.Conversation.COLUMN_PAGE, conversation.page);
        contentValues.put(Contract.Conversation.COLUMN_IP, conversation.ip);
        contentValues.put(Contract.Conversation.COLUMN_BROWSER, conversation.browser);
        contentValues.put(Contract.Conversation.COLUMN_TITLE, conversation.title);
        contentValues.put(Contract.Conversation.COLUMN_UNREAD, conversation.unread);
        contentValues.put(Contract.Conversation.COLUMN_PHONE, conversation.phone);
        contentValues.put(Contract.Conversation.COLUMN_EMAIL, conversation.email);
        contentValues.put(Contract.Conversation.COLUMN_AGENT, conversation.agent);
        contentValues.put(Contract.Conversation.COLUMN_DISPLAY_NAME, conversation.displayname);
        contentValues.put(Contract.Conversation.COLUMN_IS_ONLINE, conversation.isOnline);
        contentValues.put(Contract.Conversation.COLUMN_AGENT_SELECTED_ID, conversation.agentSelectedId);

        return contentValues;
    }

    public static Conversation convertCursorToConversation(Cursor cursor) {
        if (cursor == null)
            return null;
        Conversation conversation = new Conversation();
        conversation.idSurfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
        conversation.visitor_name = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_NAME));
        conversation.avatar = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_AVATAR));
        conversation.returning = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_RETURNING)) == 1 ? true : false;
        conversation.closed = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_CLOSED)) == 1 ? true : false;
        conversation.resloved = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_RESOLVED)) == 1 ? true : false;
        conversation.lastdate = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_DATE));
        conversation.lasttype = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_TYPE));
        conversation.actionId = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ACTION_ID));
        conversation.reply = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_REPLY)) == 1 ? true : false;
        conversation.lastMessage = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_MESSAGE));
        conversation.page = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_PAGE));
        conversation.ip = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_IP));
        conversation.browser = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_BROWSER));
        conversation.title = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_TITLE));
        conversation.unread = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD));
        conversation.phone = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_PHONE));
        conversation.email = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_EMAIL));
        conversation.agent = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_AGENT));
        conversation.displayname = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME));
        conversation.isOnline = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_IS_ONLINE)) == 1 ? true : false;
        conversation.agentSelectedId=cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_AGENT_SELECTED_ID));
        return conversation;
       /* JSONObject jsonObject = DbToolsHelper.convertCursorToJsonObject(new Conversation(), cursor);
        if (jsonObject.length() > 0) {
            Gson gson = new Gson();
            Conversation conversation = gson.fromJson(jsonObject.toString(), Conversation.class);
            return conversation;
        }
        return null;*/
    }

  /*  public static JSONObject convertCursorToJsonObject(Cursor cursor) {
        if (cursor == null)
            return null;

        JSONObject jsonObject = new JSONObject();
        String resStr = null;
        int resInt;

        for (String column : cursor.getColumnNames()) {
            try {
                if (cursor.isNull(cursor.getColumnIndex(column))) {
                    jsonObject.put(column, null);
                } else {
                    resStr = cursor.getString(cursor.getColumnIndex(column));
                    if (resStr == null) {
                        resInt = cursor.getInt(cursor.getColumnIndex(column));
                        jsonObject.put(column, resInt);
                    } else
                        jsonObject.put(column, resStr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        return jsonObject;
    }*/

    public static JSONObject convertCursorToJsonObject(Object obj, Cursor cursor) {
        if (cursor == null || obj == null)
            return null;

        String resStr;
        int resInt;
        JSONObject jsonObject = new JSONObject();

        for (String column : cursor.getColumnNames()) {
            try {
                if (cursor.isNull(cursor.getColumnIndex(column))) {
                    jsonObject.put(column, null);
                } else {
                    for (Field field : obj.getClass().getDeclaredFields()) {
                        if (field.getName().equals(column)) {
                            if (field.get(obj) instanceof Boolean) {
                                jsonObject.put(column, cursor.getInt(cursor.getColumnIndex(column)) > 0);
                            } else {
                                resStr = cursor.getString(cursor.getColumnIndex(column));
                                if (resStr == null) {
                                    resInt = cursor.getInt(cursor.getColumnIndex(column));
                                    jsonObject.put(column, resInt);
                                } else {
                                    jsonObject.put(column, resStr);
                                }
                                break;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public static String removeHtmlTags(String msg) {
        if (msg == null)
            return null;
        msg = msg.replaceAll("<br/>", "/n");
        msg = msg.replaceAll("<br>", "/n");
        msg = msg.replaceAll("<(.*?)\\>", "");//Removes all items in brackets
        //msg = msg.replaceAll("<(.*?)\\\n", "");//Must be undeneath
        msg = msg.replaceFirst("(.*?)\\>", "");//Removes any connected item to the last bracket
        msg = msg.replaceAll("&nbsp;", "");
        msg = msg.replaceAll("&amp;", "");
        return msg;
    }
}
