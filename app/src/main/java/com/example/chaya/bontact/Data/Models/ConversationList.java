package com.example.chaya.bontact.Data.Models;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 6/16/2016.
 */
public class ConversationList {


    List<Conversation> conversations;

    public ConversationList() {
        conversations = new ArrayList<Conversation>();
    }

    public List<Conversation> getConversations() {
        return conversations;
    }
}
