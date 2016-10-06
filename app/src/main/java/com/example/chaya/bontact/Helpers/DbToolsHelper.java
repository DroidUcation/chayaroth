package com.example.chaya.bontact.Helpers;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.Models.Representative;
import com.google.android.exoplayer.C;
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

    public static ContentValues convertInnerToContentValues(InnerConversation innerConversation) {
        if (innerConversation == null)
            return null;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.InnerConversation.COLUMN_ID_SURFUR,innerConversation.idSurfer);
        contentValues.put(Contract.InnerConversation.COLUMN_ID,innerConversation.id);
        contentValues.put(Contract.InnerConversation.COLUMN_CONVERSATION_PAGE,innerConversation.conversationPage);
        contentValues.put(Contract.InnerConversation.COLUMN_ACTION_TYPE,innerConversation.actionType);
        contentValues.put(Contract.InnerConversation.COLUMN_TIME_REQUEST,innerConversation.timeRequest);
        contentValues.put(Contract.InnerConversation.COLUMN_FROM,innerConversation.from_s);
        contentValues.put(Contract.InnerConversation.COLUMN_MESS,innerConversation.mess);
        contentValues.put(Contract.InnerConversation.COLUMN_REQ_ID,innerConversation.req_id);
        contentValues.put(Contract.InnerConversation.COLUMN_REP_REQUEST,innerConversation.rep_request);
        contentValues.put(Contract.InnerConversation.COLUMN_RECORD,innerConversation.record);
        contentValues.put(Contract.InnerConversation.COLUMN_AGENT_NAME,innerConversation.agentName);
        contentValues.put(Contract.InnerConversation.COLUMN_DATA_TYPE,innerConversation.datatype);
        contentValues.put(Contract.InnerConversation.COLUMN_NAME,innerConversation.name);
        contentValues.put(Contract.InnerConversation.COLUMN_SYSTEM_MSG,innerConversation.systemMsg );
        contentValues.put(Contract.InnerConversation.COLUMN_RECORD_URL,innerConversation.recordUrl);
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
        //contentValues.put(Contract.Conversation.COLUMN_LAST_DATE, DateTimeHelper.convertDateStringToDbFormat(conversation.lastdate));
        contentValues.put(Contract.Conversation.COLUMN_LAST_DATE, conversation.lastdate);
        contentValues.put(Contract.Conversation.COLUMN_LAST_TYPE, conversation.lasttype);
        contentValues.put(Contract.Conversation.COLUMN_ACTION_ID, conversation.actionId);
        contentValues.put(Contract.Conversation.COLUMN_REPLY, conversation.reply);
        contentValues.put(Contract.Conversation.COLUMN_LAST_MESSAGE, removeHtmlTags(conversation.lastMessage));
        contentValues.put(Contract.Conversation.COLUMN_PAGE, conversation.page);
        contentValues.put(Contract.Conversation.COLUMN_IP, conversation.ip);
        contentValues.put(Contract.Conversation.COLUMN_BROWSER, conversation.browser);
        contentValues.put(Contract.Conversation.COLUMN_TITLE, conversation.title);
        contentValues.put(Contract.Conversation.COLUMN_UNREAD, conversation.unread);
        contentValues.put(Contract.Conversation.COLUMN_PHONE, conversation.phone);
        contentValues.put(Contract.Conversation.COLUMN_EMAIL, conversation.email);
     contentValues.put(Contract.Conversation.COLUMN_ASSIGN, conversation.assign);
        contentValues.put(Contract.Conversation.COLUMN_DISPLAY_NAME, conversation.displayname);
       // ContentValues.put(Contract.Agents.COLUMN_ID_REP,)
        //contentValues.put(Contract.Conversation.COLUMN_IS_ONLINE, VisitorsDataManager.getVisitorByIdSurfer(conversation.idSurfer) != null ? 1 : 0);
        // contentValues.put(Contract.Conversation.COLUMN_AGENT_SELECTED_ID, conversation.agentSelectedId);

        return contentValues;
    }

    public static ContentValues convertRepresentativeToContentValues(Representative representative) {
        if (representative == null)
            return null;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Agents.COLUMN_ID_REP, representative.idRepresentive);
        contentValues.put(Contract.Agents.COLUMN_NAME, representative.name);
        contentValues.put(Contract.Agents.COLUMN_USERNAME, representative.username);
        contentValues.put(Contract.Agents.COLUMN_IMG, representative.img);
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
      //  conversation.lastMessage = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_MESSAGE));
        conversation.page = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_PAGE));
        conversation.ip = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_IP));
        conversation.browser = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_BROWSER));
        conversation.title = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_TITLE));
        conversation.unread = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD));
        conversation.phone = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_PHONE));
        conversation.email = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_EMAIL));
       // conversation.agent = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_AGENT));
        conversation.displayname = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME));
        conversation.assign=cursor.getInt(cursor.getColumnIndex(Contract.Agents.COLUMN_ID_REP));
        // conversation.isOnline = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_IS_ONLINE)) == 1 ? true : false;
        // conversation.agentSelectedId = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_AGENT_SELECTED_ID));
        return conversation;
       /* JSONObject jsonObject = DbToolsHelper.convertCursorToJsonObject(new Conversation(), cursor);
        if (jsonObject.length() > 0) {
            Gson gson = new Gson();
            Conversation conversation = gson.fromJson(jsonObject.toString(), Conversation.class);
            return conversation;
        }
        return null;*/
    }

    public static Representative convertCursorToRepresentative(Cursor cursor) {
        if (cursor == null)
            return null;
        Representative representative = new Representative();
        representative.idRepresentive = cursor.getInt(cursor.getColumnIndex(Contract.Agents.COLUMN_ID_REP));
        representative.name = cursor.getString(cursor.getColumnIndex(Contract.Agents.COLUMN_NAME));
        representative.username = cursor.getString(cursor.getColumnIndex(Contract.Agents.COLUMN_USERNAME));
        representative.img = cursor.getString(cursor.getColumnIndex(Contract.Agents.COLUMN_IMG));
        return representative;

    }

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
