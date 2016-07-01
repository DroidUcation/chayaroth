package com.example.chaya.bontact.Helpers;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.chaya.bontact.Data.DbBontact;
import com.example.chaya.bontact.Models.Conversation;

import java.lang.reflect.Field;
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
    public static Conversation convertCursorToConversation(Cursor cursor)
    {
        if(cursor==null&&!cursor.moveToFirst())
            return null;
        Conversation conversation=new Conversation();

        return conversation;
    }

}
