package com.example.chaya.bontact.Helpers;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.Models.Conversation;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by chaya on 6/26/2016.
 */
public class DbToolsHelper {

    public static ContentValues convertObjectToContentValues(Object obj, ArrayList<String> tableFields)
    {
        if(obj==null)
            return null;
        ContentValues contentValues=new ContentValues();
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
    public static JSONObject convertCursorToJsonObject(Cursor cursor)
    {
        if (cursor == null )
            return null;

        JSONObject jsonObject = new JSONObject();
        String resStr = null;
        int resInt;

        for (String column : cursor.getColumnNames()) {
            try {
                if(cursor.isNull(cursor.getColumnIndex(column))) {
                    jsonObject.put(column, null);
                }
                else {
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
    }

}
