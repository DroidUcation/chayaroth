package com.example.chaya.bontact.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by chaya on 5/25/2016.
 */
public class DbBontact extends SQLiteOpenHelper {

    private static final String DBName= "Bontact.db";
    private static final int DBVersion= 1;
     private String CreateConversationTable="CREATE TABLE "+Contract.Conversation.TABLE_NAME+
            "( _id INTEGER PRIMARY KEY   AUTOINCREMENT, "+
            Contract.Conversation.COLUMN_ID+" INT,  "+
            Contract.Conversation.COLUMN_NAME+" TEXT, "+
             Contract.Conversation.COLUMN_AVATAR+" TEXT, "+
            Contract.Conversation.COLUMN_RETURNING+" INT, "+
            Contract.Conversation.COLUMN_CLOSED+" INT, "+
            Contract.Conversation.COLUMN_RESOLVED+" INT, "+
            Contract.Conversation.COLUMN_LAST_DATE+" TEXT,"+
            Contract.Conversation.COLUMN_LAST_TYPE+" INT, "+
            Contract.Conversation.COLUMN_ACTION_ID+" INT, "+
            Contract.Conversation.COLUMN_REPLY+" INT, "+
            Contract.Conversation.COLUMN_PAGE+" TEXT, "+
            Contract.Conversation.COLUMN_IP+" TEXT, "+
            Contract.Conversation.COLUMN_BROWSER+" TEXT, "+
            Contract.Conversation.COLUMN_TITLE+" TEXT, "+
            Contract.Conversation.COLUMN_UNREAD+" INT, "+
            Contract.Conversation.COLUMN_PHONE+" TEXT, "+
            Contract.Conversation.COLUMN_EMAIL+" TEXT, "+
            Contract.Conversation.COLUMN_AGENT+" TEXT, "+
            Contract.Conversation.COLUMN_DISPLAY_NAME+" TEXT "+
            " )";
    private String CreateInnerConversationTable="CREATE TABLE "+Contract.InnerConversation.TABLE_NAME+
            "( _id INTEGER PRIMARY KEY   AUTOINCREMENT, "+
    Contract.InnerConversation.COLUMN_ID_SURFUR+" INT, "+
    Contract.InnerConversation.COLUMN_CONVERSATION_PAGE+" TEXT,  "+
    Contract.InnerConversation.COLUMN_ACTION_TYPE+" INT,  "+
    Contract.InnerConversation.COLUMN_TIME_REQUEST+" TEXT, "+
    Contract.InnerConversation.COLUMN_FROM+" TEXT, "+
    Contract.InnerConversation.COLUMN_MESS+" TEXT, "+
    Contract.InnerConversation.COLUMN_REQ_ID+" INT,  "+
    Contract.InnerConversation.COLUMN_REP_REQUEST+" INT,  "+
    Contract.InnerConversation.COLUMN_RECORD+" INT,  "+
    Contract.InnerConversation.COLUMN_AGENT_NAME+" TEXT,  "+
    Contract.InnerConversation.COLUMN_DATA_TYPE+" INT,  "+
    Contract.InnerConversation.COLUMN_NAME+" TEXT,  "+
    Contract.InnerConversation.COLUMN_SYSTEM_MSG+" INT,  "+
    Contract.InnerConversation.COLUMN_RECORD_URL+" TEXT ,"+
    "FOREIGN KEY("+Contract.InnerConversation.COLUMN_ID_SURFUR+") REFERENCES "+Contract.Conversation.TABLE_NAME+"("+Contract.Conversation.COLUMN_ID+")"+
            " )";
    private String DropConversationTable="DROP TABLE IF EXISTS " + Contract.Conversation.TABLE_NAME;
    private String DropInnerConversationTable="DROP TABLE IF EXISTS " + Contract.InnerConversation.TABLE_NAME;

    public static ArrayList<String> getAllInnerConversationFields()
    {
        ArrayList<String> strings= new ArrayList<>();
        strings.add(Contract.InnerConversation.COLUMN_ID_SURFUR);
        strings.add(Contract.InnerConversation.COLUMN_CONVERSATION_PAGE);
        strings.add(Contract.InnerConversation.COLUMN_ACTION_TYPE);
        strings.add(Contract.InnerConversation.COLUMN_TIME_REQUEST);
        strings.add(Contract.InnerConversation.COLUMN_FROM);
        strings.add(Contract.InnerConversation.COLUMN_MESS);
        strings.add(Contract.InnerConversation.COLUMN_REQ_ID);
        strings.add( Contract.InnerConversation.COLUMN_REP_REQUEST);
        strings.add( Contract.InnerConversation.COLUMN_RECORD);
        strings.add( Contract.InnerConversation.COLUMN_AGENT_NAME);
       strings.add( Contract.InnerConversation.COLUMN_DATA_TYPE);
        strings.add( Contract.InnerConversation.COLUMN_NAME);
       strings.add( Contract.InnerConversation.COLUMN_SYSTEM_MSG);
        strings.add( Contract.InnerConversation.COLUMN_RECORD_URL);
        return strings;
    }
    public static ArrayList<String> getAllConversationFields()
    {
        ArrayList<String> strings= new ArrayList<>();
        strings.add(Contract.Conversation.COLUMN_ID);
        strings.add(Contract.Conversation.COLUMN_NAME);
       strings.add(Contract.Conversation.COLUMN_AVATAR);
       strings.add(Contract.Conversation.COLUMN_RETURNING);
       strings.add(Contract.Conversation.COLUMN_CLOSED);
        strings.add(Contract.Conversation.COLUMN_RESOLVED);
        strings.add(  Contract.Conversation.COLUMN_LAST_DATE);
       strings.add(Contract.Conversation.COLUMN_LAST_TYPE);
        strings.add(Contract.Conversation.COLUMN_ACTION_ID);
      strings.add( Contract.Conversation.COLUMN_REPLY);
        strings.add(Contract.Conversation.COLUMN_PAGE);
         strings.add( Contract.Conversation.COLUMN_IP);
         strings.add(Contract.Conversation.COLUMN_BROWSER);
        strings.add(Contract.Conversation.COLUMN_TITLE);
        strings.add(Contract.Conversation.COLUMN_UNREAD);
        strings.add(Contract.Conversation.COLUMN_PHONE);
        strings.add( Contract.Conversation.COLUMN_EMAIL);
        strings.add( Contract.Conversation.COLUMN_AGENT);
      //  strings.add(Contract.Conversation.FLAG_COUNTRY);
        strings.add(Contract.Conversation.COLUMN_DISPLAY_NAME);
        return strings;
    }

    public DbBontact(Context context) {
        super(context,DBName,null,DBVersion);
    }
    private SQLiteDatabase database;

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(CreateConversationTable);
      db.execSQL(CreateInnerConversationTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DropConversationTable);
        db.execSQL(DropInnerConversationTable);
        onCreate(db);
    }

    public long insert(String table, ContentValues values)
    {
        database=getWritableDatabase();
        long result=database.insert(table,null,values);
        return result;
    }
    public boolean update(String table,ContentValues values)
    {
        database=getWritableDatabase();
      //  database.update(table,values,);
        return true;
    }
    public int delete(String selection, String[] selectionArgs){

        database=getWritableDatabase();
         return database.delete(Contract.Conversation.TABLE_NAME,selection,selectionArgs);


    }

}
