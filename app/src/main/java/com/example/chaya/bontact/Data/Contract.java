package com.example.chaya.bontact.Data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by chaya on 5/25/2016.
 */
public class Contract {
    public static class Conversation implements BaseColumns{
        public static final String AUTHORITY = "com.example.chaya.bontact.Data.InboxProvider";
        public static final String URL = "content://" + AUTHORITY;
        public static final Uri INBOX_URI = Uri.parse(URL);
        public static final String TABLE_NAME="Conversation";
        public static final String COLUMN_ID="idSurfer";
        public static final String COLUMN_AVATAR="avatar";
        public static final String COLUMN_NAME="visitor_name";
        public static final String COLUMN_RETURNING="returning";
        public static final String COLUMN_CLOSED="closed";
        public static final String COLUMN_RESOLVED="resloved";
        public static final String COLUMN_LAST_DATE="lastdate";
        public static final String COLUMN_LAST_TYPE="lasttype";
        public static final String COLUMN_ACTION_ID="actionId";
        public static final String COLUMN_REPLY="reply";
        public static final String COLUMN_PAGE="page";
        public static final String COLUMN_IP="ip";
        public static final String COLUMN_BROWSER="browser";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_UNREAD="unread";
        public static final String COLUMN_PHONE="phone";
        public static final String COLUMN_EMAIL="email";
        public static final String COLUMN_AGENT="agent";
        public static final String COLUMN_DISPLAY_NAME="displayname";
    }
    public static class InnerConversation implements BaseColumns{
        public static final String AUTHORITY = "com.example.chaya.bontact.Data.InnerConversationProvider";
        public static final String URL = "content://" + AUTHORITY;
        public static final Uri INNER_CONVERSATION_URI = Uri.parse(URL);
        public static final String TABLE_NAME="InnerConversation";
        public static final String COLUMN_CONVERSATION_PAGE="conversationPage";
        public static final String COLUMN_ID_SURFUR="idSurfer";
        public static final String COLUMN_ACTION_TYPE="actionType";
        public static final String COLUMN_TIME_REQUEST="timeRequest";
        public static final String COLUMN_FROM="from_s";
        public static final String COLUMN_MESS="mess";
        public static final String COLUMN_REQ_ID="req_id";
        public static final String COLUMN_REP_REQUEST="rep_request";
        public static final String COLUMN_RECORD="record";
        public static final String COLUMN_AGENT_NAME="agentName";
        public static final String COLUMN_DATA_TYPE="datatype";
        public static final String COLUMN_NAME="name";
        public static final String COLUMN_SYSTEM_MSG="systemMsg";
        public static final String COLUMN_RECORD_URL="recordUrl";

    }

}

