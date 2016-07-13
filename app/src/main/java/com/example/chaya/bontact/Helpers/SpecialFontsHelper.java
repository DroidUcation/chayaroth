package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.graphics.Typeface;

import com.example.chaya.bontact.R;

/**
 * Created by chaya on 7/12/2016.
 */
public class SpecialFontsHelper {
    private static Typeface fontAwesome;
    public static Typeface getFont(Context context,int resFont) {
        switch(resFont)
        {
            case R.string.font_awesome:
             fontAwesome=  Typeface.createFromAsset(context.getAssets(),context.getResources().getString(resFont));
                return fontAwesome;
         }
        return null;
}}
