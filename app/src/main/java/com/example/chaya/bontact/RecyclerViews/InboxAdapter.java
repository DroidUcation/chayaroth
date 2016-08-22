package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.CircleTransform;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.R;

import com.example.chaya.bontact.Ui.Activities.InnerConversationActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxHolder> {

    Cursor cursor;
    Context context;
    ConversationDataManager conversationDataManager;

    public InboxAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        conversationDataManager = new ConversationDataManager(context);

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
        //   ConversationDataManager conversationDataManager = new ConversationDataManager(context);
        // Conversation conversation = conversationDataManager.convertCursorToConversation(cursor);
        //  Conversation conversation= new Conversation();
        // if (conversation != null) {
        holder.displayName.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME)));
        setAvatar(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_AVATAR)),
                cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME))
                , holder.avatar);
        //holder.avatar.setImageResource(Integer.parseInt(conversation.avatar));
        int icon = ChanelsTypes.getIconByChanelType(cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_TYPE)));
        if (icon != 0)
            holder.chanelIcon.setText(icon);
        if (cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_MESSAGE)) == null)
            holder.lastSentence.setText(ChanelsTypes.getDeafultMsgByChanelType(context, cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_TYPE))));
        else
            holder.lastSentence.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_MESSAGE)));
        String dateStringToConvert = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_DATE));
        String timeAgo = null;
        if (dateStringToConvert != null && context != null) {
            timeAgo = DateTimeHelper.getDateToInbox(dateStringToConvert, context);
            if (timeAgo != null)
                holder.date.setText(timeAgo);
        }
        if (cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_IS_ONLINE)) == 1) {
            holder.onlinePoint.setVisibility(View.VISIBLE);
        }
        if (cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD)) > 0) {
            holder.unread.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD))));
            holder.setUnRead(true);
        }
        int agentSelectedId = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_AGENT_SELECTED_ID));
        if (agentSelectedId != 0 && agentSelectedId != AgentDataManager.getAgentInstanse().getIdRep()) {
            holder.itemView.setEnabled(false);
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.gray_opacity));
            holder.not_available.setVisibility(View.VISIBLE);
        } else {//holder.itemView.setEnabled(true);
            holder.itemView.setEnabled(true);
            //holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.not_available.setVisibility(View.GONE);
        }

    }


    public void setAvatar(String avatar, String displayName, ImageView avatarView) {
        //set default
        avatarView.setBackground(context.getResources().getDrawable(R.drawable.avatar_bg));
        avatarView.setImageResource(R.drawable.default_avatar);
      /*  if (conversation != null) {*/
        if (avatar != null) {//has picture
            Picasso.with(context)
                    .load(avatar)
                    .transform(new CircleTransform())
                    .into(avatarView);
        } else {//maybe letters
            String letter = null;
              /*  letter = conversation.visitor_name != null ? conversation.visitor_name.substring(0, 1) :
                        conversation.email != null ? conversation.email.substring(0, 1) : null;*/
            if (displayName != null && !displayName.startsWith("#"))
                letter = displayName.substring(0, 1);
            if (letter != null)
                avatarView.setImageDrawable(TextDrawable.builder()
                        .buildRound(letter, ColorGenerator.MATERIAL.getRandomColor()));
           /* }*/
        }
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
        TextView displayName, lastSentence, date, unread, not_available;
        TextView chanelIcon;
        View colorBack;


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
            not_available = (TextView) itemView.findViewById(R.id.taken_by_agent_txt);
            itemView.setOnClickListener(this);
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
            Log.d("on click", "inbox adapter");
            int position = this.getAdapterPosition();
            cursor.moveToPosition(position);
            int id_surfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
            // Conversation conversation = null;
            //ConversationDataManager conversationDataManager = new ConversationDataManager(v.getContext());
         /*   conversation = conversationDataManager.getConversationByIdSurfer(id_surfer);*/

           /* AgentDataManager agentDataManager = new AgentDataManager();
            String token = agentDataManager.getAgentToken(v.getContext());
            if (token != null && conversation != null) {*/
              /*  InnerConversationDataManager innerConversationDataManager = new InnerConversationDataManager(v.getContext(), conversation);
                // ((MenuActivity) v.getContext()).setProgressBarCenterState(View.VISIBLE);
                innerConversationDataManager.getData(v.getContext(), token);*/

          /*  if (conversation != null) {*/
            Intent intent = new Intent(v.getContext(), InnerConversationActivity.class);
            Bundle b = new Bundle();
            b.putInt(Contract.InnerConversation.COLUMN_ID_SURFUR, id_surfer); //Your id
            intent.putExtras(b); //Put your id to your next Intent
            //setProgressBarCenterState(View.GONE);
            v.getContext().startActivity(intent);

            /*}*/
        }


    }
}

