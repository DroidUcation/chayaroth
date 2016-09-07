package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.Helpers.AvatarHelper;
import com.example.chaya.bontact.Helpers.ChannelsTypes;
import com.example.chaya.bontact.Helpers.CircleTransform;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.R;

import com.example.chaya.bontact.Ui.Activities.InnerConversationActivity;
import com.squareup.picasso.Picasso;

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
        int lastType = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_TYPE));
        int idSurfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
        int unread = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD));
        // String displayName=cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME));
        //   ConversationDataManager conversationDataManager = new ConversationDataManager(context);
        // Conversation conversation = conversationDataManager.convertCursorToConversation(cursor);
        //  Conversation conversation= new Conversation();
        // if (conversation != null) {
        holder.displayName.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME)));
        AvatarHelper.setAvatar(context, cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_AVATAR)),
                cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME))
                , holder.avatar);
        //holder.avatar.setImageResource(Integer.parseInt(conversation.avatar));
        int icon = ChannelsTypes.getIconByChanelType(lastType);
        if (icon != 0)
            holder.chanelIcon.setText(icon);
       /*if (cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_MESSAGE)) == null)
            holder.lastSentence.setText(ChannelsTypes.getDeafultMsgByChanelType(context, cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_TYPE))));
        else*/
        if (lastType == ChannelsTypes.webCall || lastType == ChannelsTypes.callback)
            holder.lastSentence.setText(ChannelsTypes.getDeafultMsgByChanelType(context, lastType));
        else
            holder.lastSentence.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_MESSAGE)));
        String dateStringToConvert = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_DATE));
        String timeAgo = null;
        if (dateStringToConvert != null && context != null) {
            timeAgo = DateTimeHelper.getDateToInbox(dateStringToConvert, context);
            if (timeAgo != null)
                holder.date.setText(timeAgo);
        }
        //if (cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_IS_ONLINE)) == 1) {
        // Conversation conversation = conversationDataManager.getConversationByIdSurfer(idSurfer);
        if (VisitorsDataManager.isOnline(idSurfer))
            holder.online.setVisibility(View.VISIBLE);
        else
            holder.online.setVisibility(View.GONE);

        holder.unread.setText(String.valueOf(unread));
        if (unread > 0) {
            holder.unread.setVisibility(View.VISIBLE);
            // holder.unread.setBackground(context.getDrawable(R.drawable.online_point));
            holder.displayName.setTypeface(null, Typeface.BOLD);
            holder.lastSentence.setTypeface(null, Typeface.BOLD);
            holder.date.setTypeface(null, Typeface.BOLD);
        } else {
            // holder.unread.setBackground(null);
            holder.unread.setVisibility(View.GONE);
            holder.displayName.setTypeface(null, Typeface.NORMAL);
            holder.lastSentence.setTypeface(null, Typeface.NORMAL);
            holder.date.setTypeface(null, Typeface.NORMAL);
        }

        int agentSelectedId = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_AGENT_SELECTED_ID));
        if (agentSelectedId != 0 && agentSelectedId != AgentDataManager.getAgentInstance().getIdRep()) {
            holder.takenBy.setVisibility(View.VISIBLE);
            holder.takenBy.setText(String.valueOf(agentSelectedId));
            holder.locked.setVisibility(View.VISIBLE);
            holder.itemView.setEnabled(false);
        } else {
            holder.takenBy.setVisibility(View.GONE);
            holder.locked.setVisibility(View.GONE);
            holder.itemView.setEnabled(true);

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

        ImageView avatar, online, locked;
        TextView displayName, lastSentence, date, unread, takenBy;
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
            online = (ImageView) itemView.findViewById(R.id.online_point);
            takenBy = (TextView) itemView.findViewById(R.id.taken_by_agent);
            locked = (ImageView) itemView.findViewById(R.id.locked);
            itemView.setOnClickListener(this);
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

