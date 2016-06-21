package com.example.chaya.bontact.Helpers;

import com.example.chaya.bontact.R;

import java.util.List;

/**
 * Created by chaya on 6/21/2016.
 */
public class Helper {
    public static int avatarPosition=0;
    public static List<Integer> avatars;
    public static int getAvatar()
    {

    if(avatarPosition>=10) {
        avatarPosition = 0;
    }
    else
    avatarPosition++;


        return avatarPosition;
    }
}
