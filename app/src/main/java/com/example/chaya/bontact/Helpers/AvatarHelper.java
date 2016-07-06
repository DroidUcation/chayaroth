package com.example.chaya.bontact.Helpers;

import com.example.chaya.bontact.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 6/21/2016.
 */
public class AvatarHelper {
    public static int avatarPosition=0;
    public static List<Integer> avatars;
    public static int getAvatarPosition()
    {

    if(avatarPosition>=9) {
        avatarPosition = 0;
    }
    else
    avatarPosition++;


        return avatarPosition;
    }
    public static List<Integer> getAvatarsList()
    {
        if(avatars==null) {
            avatars = new ArrayList<Integer>();
        }

        avatars.add(R.drawable.avatar1);
        avatars.add(R.drawable.avatar2);
        avatars.add(R.drawable.avatar3);
        avatars.add(R.drawable.avatar4);
        avatars.add(R.drawable.avatar5);
        avatars.add(R.drawable.avatar6);
        avatars.add(R.drawable.avatar7);
        avatars.add(R.drawable.avatar8);
        avatars.add(R.drawable.avatar9);
        avatars.add(R.drawable.avatar10);
        return avatars;
    }
    public static int getNextAvatar()
    {
        getAvatarsList();

       return avatars.get(getAvatarPosition());
    }

}
