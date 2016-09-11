package com.example.chaya.bontact.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by chaya on 6/12/2016.
 */
public class RepresentativeProvider extends ContentProvider {

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
        sqLiteQueryBuilder.setTables(Contract.Agents.TABLE_NAME);
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

        long rowID = dbBontact.insertOrUpdateById(Contract.Agents.TABLE_NAME, values, new String[]{Contract.Agents.COLUMN_ID_REP});
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long result = dbBontact.delete(Contract.Agents.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return (int) result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        long result = dbBontact.update(Contract.Agents.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return (int) result;
    }


}
