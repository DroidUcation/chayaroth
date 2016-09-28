package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.chaya.bontact.Models.Visitor;
import com.example.chaya.bontact.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 6/21/2016.
 */
public class AvatarHelper {

    public static int getAvatarBackgroundColor(String letter) {
        if (letter == null) return -1;
        Character character = letter.toUpperCase().charAt(0);
        if (character != null && Character.isLetter(character)) {
            return ColorGenerator.MATERIAL.getColor(character);
           /* if (character <= 'F')
                return Color.GREEN;
            if (character <= 'L')
                return Color.BLUE;
            if (character <= 'R')
                return Color.RED;
            if (character <= 'Z')
                return Color.MAGENTA;*/
        }
        return -1;
    }

    public static void setAvatar(Context context, String avatarUrl, String displayName, ImageView avatarView) {
        //set default
        avatarView.setBackground(context.getResources().getDrawable(R.drawable.avatar_bg));
        avatarView.setImageResource(R.drawable.default_avatar);
        //has picture
        if (avatarUrl != null) {
            Picasso.with(context)
                    .load(avatarUrl)
                    .transform(new CircleTransform())
                    .into(avatarView);
        } else {//has display name
            String letter = null;
            if (displayName != null && !displayName.startsWith("#"))
                letter = displayName.substring(0, 1);
            if (letter != null) {
                int color = getAvatarBackgroundColor(letter);
                if (color != -1)
                    avatarView.setImageDrawable(TextDrawable.builder()
                            .buildRound(letter, color));
            }
        }
    }

    public static Bitmap decodeAvatarBase64(String avatar)
    {
        avatar=avatar.substring(avatar.indexOf(",")+1,avatar.length());
        byte[] decodedString = Base64.decode(avatar, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        agentPicture.setImageBitmap(decodedByte);
        return decodedByte;
    }
}
