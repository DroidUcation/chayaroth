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
 * Created by chaya on 6/26/2016.
 */
public class InnerConversationProvider extends ContentProvider {
    // private SQLiteDatabase db;
    private DbBontact dbBontact;

    @Override
    public boolean onCreate() {

        dbBontact = new DbBontact(getContext());
        return true;
        //db = dbBontact.getWritableDatabase();
        // return (db != null);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(Contract.InnerConversation.TABLE_NAME);
        Cursor cursor = sqLiteQueryBuilder.query(dbBontact.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
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
        long rowID = dbBontact.insertOrUpdateById(Contract.InnerConversation.TABLE_NAME, values, new String[]{Contract.InnerConversation.COLUMN_ID, Contract.InnerConversation.COLUMN_ACTION_TYPE});
        if (rowID > 0) {
            // Uri _uri = ContentUris.withAppendedId(Contract.InnerConversation.INNER_CONVERSATION_URI, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long result = dbBontact.delete(Contract.InnerConversation.TABLE_NAME, selection, selectionArgs);
        return (int) result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
