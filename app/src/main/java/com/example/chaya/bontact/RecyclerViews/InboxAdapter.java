

package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.R;

import com.example.chaya.bontact.Ui.Activities.MenuActivity;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxHolder> {

    Cursor cursor;
    Context context;

    public InboxAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }


    @Override
    public InboxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inbox_item, null);
        return new InboxHolder(view);

    }

    @Override
    public void onBindViewHolder(InboxHolder holder, int position) {

        if (!cursor.moveToPosition(position))
            return;
        ConversationDataManager conversationDataManager = new ConversationDataManager(context);
        Conversation conversation = conversationDataManager.convertCursorToConversation(cursor);

        if (conversation != null) {
            holder.displayName.setText(conversation.displayname);
            holder.avatar.setImageResource(Integer.parseInt(conversation.avatar));
            int icon = ChanelsTypes.getIconByChanelType(conversation.lasttype);
            if (icon != 0)
                holder.chanelIcon.setText(icon);

           if (conversation.lastMessage == null)
                holder.lastSentence.setText(ChanelsTypes.getDeafultMsgByChanelType(context, conversation.getLasttype()));
            else

            holder.lastSentence.setText(conversation.lastMessage);
            String dateStringToConvert = conversation.lastdate;
            String timeAgo = null;
            if (dateStringToConvert != null && context != null) {
                timeAgo = DateTimeHelper.getDiffToNow(dateStringToConvert, context);
                if (timeAgo != null)
                    holder.date.setText(timeAgo);
            }
            if (conversation.isOnline == true) {
                holder.onlinePoint.setVisibility(View.VISIBLE);
            }
            if (conversation.unread > 0) {
                holder.unread.setText(Integer.toString(conversation.unread));
                holder.setUnRead(true);
            }
        }/*
        cursor.moveToPosition(position);

        // init data to display
        int lastType = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_TYPE));
        int chanelIcon = ChanelsTypes.getIconByChanelType(lastType);
        String avatarUrl = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_AVATAR));
        int isUnread = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD));

        if (lastSentences == null)

        String dateStringToConvert = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_DATE));
        String timeAgo = null;
        if (dateStringToConvert != null && context != null) {
            timeAgo = DateTimeHelper.getDiffToNow(dateStringToConvert, context);
        }
        int isOnline = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_IS_ONLINE));

        //set in item
        holder.displayName.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME)));

        String avatarStr = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_AVATAR));
        holder.avatar.setImageResource(Integer.parseInt(avatarStr));
        holder.chanelIcon.setText(chanelIcon);
        if (lastType == ChanelsTypes.webCall || lastType == ChanelsTypes.sms || lastType == ChanelsTypes.callback)
            holder.chanelIcon.setTextSize(14);
        if (lastSentences != null)


        if (timeAgo != null)
            holder.date.setText(timeAgo);
        if (isOnline == 1) {
            holder.onlinePoint.setVisibility(View.VISIBLE);
        }
        if (isUnread >= 1) {
            holder.unread.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD)));
            holder.setUnRead(true);
        }*/
    }

    @Override
    public int getItemCount() {
        if (cursor != null)
            return cursor.getCount();
        return 0;
    }


    public Cursor swapCursor(Cursor cursor) {

        if (this.cursor == cursor) {
            return null;
        }
        Cursor oldCursor = this.cursor;
        this.cursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }



    class InboxHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView avatar, onlinePoint;
        TextView displayName, lastSentence, date, unread;
        TextView chanelIcon;


        public InboxHolder(View itemView) {

            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            displayName = (TextView) itemView.findViewById(R.id.displayName);
            lastSentence = (TextView) itemView.findViewById(R.id.last_sentence);
            date = (TextView) itemView.findViewById(R.id.date);
            unread = (TextView) itemView.findViewById(R.id.unread);

            chanelIcon = (TextView) itemView.findViewById(R.id.chanelIcon);
            Typeface font = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
            chanelIcon.setTypeface(font);
            onlinePoint = (ImageView) itemView.findViewById(R.id.online_point);
            ;
            itemView.setOnClickListener(this);
            avatar.setOnClickListener(imagesClickListener);

        }

        public void setUnRead(boolean status) {
            if (status == true) {
                displayName.setTypeface(null, Typeface.BOLD);
                lastSentence.setTypeface(null, Typeface.BOLD);
                date.setTypeface(null, Typeface.BOLD);
                unread.setVisibility(View.VISIBLE);
            }
        }


        @Override
        public void onClick(View v) {
            Log.d("on click", "inbox adaptrer");

            Conversation conversation = null;
            int position = this.getAdapterPosition();
            cursor.moveToPosition(position);
            ConversationDataManager conversationDataManager = new ConversationDataManager(v.getContext());

            int id_surfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
            conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);

            AgentDataManager agentDataManager = new AgentDataManager();
            String token = agentDataManager.getAgentToken(v.getContext());
            if (token != null && conversation != null) {
                InnerConversationDataManager innerConversationDataManager = new InnerConversationDataManager(v.getContext(), conversation);
                ((MenuActivity) v.getContext()).setProgressBarCenterState(View.VISIBLE);
                innerConversationDataManager.getData(v.getContext(), token);

            }
        }

        View.OnClickListener imagesClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

    }
}

