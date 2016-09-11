package com.example.chaya.bontact.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 5/25/2016.
 */
public class DbBontact extends SQLiteOpenHelper {

    public static final String DBName = "Bontact.db";
    public static final int DBVersion = 4;
    public String CreateConversationTable = "CREATE TABLE " + Contract.Conversation.TABLE_NAME + " (" +
            Contract.Conversation.COLUMN_ID_SURFER + "  INTEGER PRIMARY KEY,  " +
            Contract.Conversation.COLUMN_NAME + " TEXT, " +
            Contract.Conversation.COLUMN_AVATAR + " TEXT, " +
            Contract.Conversation.COLUMN_RETURNING + " INT, " +
            Contract.Conversation.COLUMN_CLOSED + " INT, " +
            Contract.Conversation.COLUMN_RESOLVED + " INT, " +
            Contract.Conversation.COLUMN_LAST_DATE + " TEXT," +
            Contract.Conversation.COLUMN_LAST_TYPE + " INT, " +
            Contract.Conversation.COLUMN_ACTION_ID + " INT, " +
            Contract.Conversation.COLUMN_REPLY + " INT, " +
            Contract.Conversation.COLUMN_PAGE + " TEXT, " +
            Contract.Conversation.COLUMN_IP + " TEXT, " +
            Contract.Conversation.COLUMN_BROWSER + " TEXT, " +
            Contract.Conversation.COLUMN_TITLE + " TEXT, " +
            Contract.Conversation.COLUMN_UNREAD + " INT, " +
            Contract.Conversation.COLUMN_PHONE + " TEXT, " +
            Contract.Conversation.COLUMN_EMAIL + " TEXT, " +
            Contract.Conversation.COLUMN_LAST_MESSAGE + " TEXT, " +
            Contract.Conversation.COLUMN_DISPLAY_NAME + " TEXT ," +
            Contract.Conversation.COLUMN_ASSIGN + "  INTEGER ,  " +
            "FOREIGN KEY(" +Contract.Conversation.COLUMN_ASSIGN + ") REFERENCES " + Contract.Agents.TABLE_NAME + "(" + Contract.Agents.COLUMN_ID_REP + ")" +
            " )";
    public String CreateInnerConversationTable = "CREATE TABLE " + Contract.InnerConversation.TABLE_NAME + "(" +
            Contract.InnerConversation.COLUMN_ID + "  INTEGER PRIMARY KEY,  " +
            Contract.InnerConversation.COLUMN_ID_SURFUR + " INT, " +
            Contract.InnerConversation.COLUMN_CONVERSATION_PAGE + " TEXT,  " +
            Contract.InnerConversation.COLUMN_ACTION_TYPE + " INT,  " +
            Contract.InnerConversation.COLUMN_TIME_REQUEST + " TEXT, " +
            Contract.InnerConversation.COLUMN_FROM + " TEXT, " +
            Contract.InnerConversation.COLUMN_MESS + " TEXT, " +
            Contract.InnerConversation.COLUMN_REQ_ID + " INT,  " +
            Contract.InnerConversation.COLUMN_REP_REQUEST + " INT,  " +
            Contract.InnerConversation.COLUMN_RECORD + " INT,  " +
            Contract.InnerConversation.COLUMN_AGENT_NAME + " TEXT,  " +
            Contract.InnerConversation.COLUMN_DATA_TYPE + " INT,  " +
            Contract.InnerConversation.COLUMN_NAME + " TEXT,  " +
            Contract.InnerConversation.COLUMN_SYSTEM_MSG + " INT,  " +
            Contract.InnerConversation.COLUMN_RECORD_URL + " TEXT ," +
            "FOREIGN KEY(" + Contract.InnerConversation.COLUMN_ID_SURFUR + ") REFERENCES " + Contract.Conversation.TABLE_NAME + "(" + Contract.Conversation.COLUMN_ID_SURFER + ")" +
            " )";
    public String CreateAgentsTable = "CREATE TABLE " + Contract.Agents.TABLE_NAME + "(" +
            Contract.Agents.COLUMN_ID_REP + "  INTEGER PRIMARY KEY,  " +
            Contract.Agents.COLUMN_NAME + " TEXT,  " +
            Contract.Agents.COLUMN_USERNAME + " TEXT, " +
            Contract.Agents.COLUMN_IMG + " TEXT " +
            " )";


    public String DropConversationTable = "DROP TABLE IF EXISTS " + Contract.Conversation.TABLE_NAME;
    public String DropInnerConversationTable = "DROP TABLE IF EXISTS " + Contract.InnerConversation.TABLE_NAME;
    public String DropAgentsTable = "DROP TABLE IF EXISTS " + Contract.Agents.TABLE_NAME;

    public static ArrayList<String> getAllInnerConversationFields() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(Contract.InnerConversation.COLUMN_ID_SURFUR);
        strings.add(Contract.InnerConversation.COLUMN_ID);
        strings.add(Contract.InnerConversation.COLUMN_CONVERSATION_PAGE);
        strings.add(Contract.InnerConversation.COLUMN_ACTION_TYPE);
        strings.add(Contract.InnerConversation.COLUMN_TIME_REQUEST);
        strings.add(Contract.InnerConversation.COLUMN_FROM);
        strings.add(Contract.InnerConversation.COLUMN_MESS);
        strings.add(Contract.InnerConversation.COLUMN_REQ_ID);
        strings.add(Contract.InnerConversation.COLUMN_REP_REQUEST);
        strings.add(Contract.InnerConversation.COLUMN_RECORD);
        strings.add(Contract.InnerConversation.COLUMN_AGENT_NAME);
        strings.add(Contract.InnerConversation.COLUMN_DATA_TYPE);
        strings.add(Contract.InnerConversation.COLUMN_NAME);
        strings.add(Contract.InnerConversation.COLUMN_SYSTEM_MSG);
        strings.add(Contract.InnerConversation.COLUMN_RECORD_URL);
        return strings;
    }

    public static String[] getAllConversationFields() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(Contract.Conversation.COLUMN_ID_SURFER);
        strings.add(Contract.Conversation.COLUMN_NAME);
        strings.add(Contract.Conversation.COLUMN_AVATAR);
        strings.add(Contract.Conversation.COLUMN_RETURNING);
        strings.add(Contract.Conversation.COLUMN_CLOSED);
        strings.add(Contract.Conversation.COLUMN_RESOLVED);
        strings.add(Contract.Conversation.COLUMN_LAST_DATE);
        strings.add(Contract.Conversation.COLUMN_LAST_TYPE);
        strings.add(Contract.Conversation.COLUMN_ACTION_ID);
        strings.add(Contract.Conversation.COLUMN_REPLY);
        strings.add(Contract.Conversation.COLUMN_PAGE);
        strings.add(Contract.Conversation.COLUMN_IP);
        strings.add(Contract.Conversation.COLUMN_BROWSER);
        strings.add(Contract.Conversation.COLUMN_TITLE);
        strings.add(Contract.Conversation.COLUMN_UNREAD);
        strings.add(Contract.Conversation.COLUMN_PHONE);
        strings.add(Contract.Conversation.COLUMN_EMAIL);
        strings.add(Contract.Conversation.COLUMN_DISPLAY_NAME);
        strings.add(Contract.Conversation.COLUMN_LAST_MESSAGE);
        strings.add(Contract.Conversation.COLUMN_ASSIGN);
        //strings.add(Contract.Agents.COLUMN_ID_REP);
        // strings.add(Contract.Conversation.COLUMN_AGENT_SELECTED_ID);
        String[] arr=new String[strings.size()];
        arr= strings.toArray(arr);
        return arr;
    }

    public DbBontact(Context context) {
        super(context, DBName, null, DBVersion);
    }

    private SQLiteDatabase database;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateConversationTable);
        db.execSQL(CreateInnerConversationTable);
        db.execSQL(CreateAgentsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DropConversationTable);
        db.execSQL(DropInnerConversationTable);
        db.execSQL(DropAgentsTable);
        onCreate(db);
    }

    /*  public long insert(String tableName, ContentValues values) {
          database = getWritableDatabase();
          long result = database.insert(tableName, null, values);
          return result;
      }
  */
    public long update(String table, ContentValues values, String selection, String[] selectionArgs) {
        database = getWritableDatabase();
        long res = database.update(table, values, selection, selectionArgs);
        //Log.e("UPDATE", "name " + values.get(Contract.Conversation.COLUMN_DISPLAY_NAME) + " UNREAD " + String.valueOf(values.get(Contract.Conversation.COLUMN_UNREAD)));
        return res;
    }

    public long delete(String tableName, String selection, String[] selectionArgs) {

        database = getWritableDatabase();
        return database.delete(tableName, selection, selectionArgs);


    }

    public long insertOrUpdateById(String table, ContentValues values, String[] columns) {

        database = getWritableDatabase();
        try {
            long row_id = database.insertOrThrow(table, null, values);
            //  Log.e("INSERT", "name " + values.get(Contract.Conversation.COLUMN_DISPLAY_NAME) + " UNREAD " + String.valueOf(values.get(Contract.Conversation.COLUMN_UNREAD)));
            return row_id;
        } catch (SQLiteConstraintException e) {
            if (columns == null)
                return 0;
            String whereColumnsStr = "";
            String[] whereColumnsArgs = new String[columns.length];

            for (int i = 0; i < columns.length; i++) {
                if (i > 0)
                    whereColumnsStr += " AND ";
                whereColumnsStr += columns[i] + "=?";
                whereColumnsArgs[i] = values.getAsString(columns[i]);
            }

            long result = update(table, values, whereColumnsStr, whereColumnsArgs);
           /* if (result == 0)
                throw e;
            else*/
            return result;
        }

    }

}
