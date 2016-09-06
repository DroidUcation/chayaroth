package com.example.chaya.bontact.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by chaya on 6/12/2016.
 */
public class InboxProvider extends ContentProvider {
    private SQLiteDatabase db;
    private DbBontact dbBontact;

    @Override
    public boolean onCreate() {

        dbBontact = new DbBontact(getContext());
        db = dbBontact.getWritableDatabase();
        return (db != null);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(Contract.Conversation.TABLE_NAME);
        if (!TextUtils.isEmpty(sortOrder)) {
            sortOrder = Contract.Conversation.COLUMN_LAST_DATE + " DESC"; //Sort by modified date as default
        }
        Cursor cursor = sqLiteQueryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = dbBontact.insertOrUpdateById(Contract.Conversation.TABLE_NAME, values, new String[]{Contract.Conversation.COLUMN_ID_SURFER});
        // if (rowID > 0) {
        // Uri _uri = ContentUris.withAppendedId(Contract.Conversation.INBOX_URI, rowID);
        getContext().getContentResolver().notifyChange(uri, null);
        // checkDb();
        return uri;
        // }

        // return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long result = dbBontact.delete(Contract.Conversation.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return (int) result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        long result = dbBontact.update(Contract.Conversation.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        //Log.d("update conversation", values.toString());
        return (int) result;
    }

    /*public void checkDb() {
        Cursor cursor = query(Contract.Conversation.INBOX_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME));
                int online = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_IS_ONLINE));
                Log.d("cursor name " + cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME)), cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_IS_ONLINE)) + "");
            } while (cursor.moveToNext());
        }
    }*/

}
