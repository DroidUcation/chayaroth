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
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConversationDataManager;
import com.example.chaya.bontact.DataManagers.VisitorsDataManager;
import com.example.chaya.bontact.Helpers.AvatarHelper;
import com.example.chaya.bontact.Helpers.ChannelsTypes;
import com.example.chaya.bontact.Helpers.CircleTransform;
import com.example.chaya.bontact.Helpers.DatesHelper;
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

        if (cursor==null||!cursor.moveToPosition(position))
            return;
        int lastType = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_TYPE));
        int idSurfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
        int unread = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD));
        int assign = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ASSIGN));

        holder.displayName.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME)));
        AvatarHelper.setAvatar(context, cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_AVATAR)),
                cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME))
                , holder.avatar);
        //holder.avatar.setImageResource(Integer.parseInt(conversation.avatar));
        int icon = ChannelsTypes.getIconByChanelType(lastType);
        if (icon != 0)
            holder.chanelIcon.setText(icon);
        if (cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_MESSAGE)) == null)
            holder.lastSentence.setText(ChannelsTypes.getDeafultMsgByChanelType(context, cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_TYPE))));
        else if (lastType == ChannelsTypes.webCall || lastType == ChannelsTypes.callback)
            holder.lastSentence.setText(ChannelsTypes.getDeafultMsgByChanelType(context, lastType));
        else
            holder.lastSentence.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_MESSAGE)));
        String dateStringToConvert = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_DATE));
        if (dateStringToConvert != null && context != null) {
            dateStringToConvert = DatesHelper.getDateToDisplayInbox(context, dateStringToConvert);
            holder.date.setText(dateStringToConvert);
        }
        if (VisitorsDataManager.isOnline(idSurfer))
            holder.online.setVisibility(View.VISIBLE);
        else
            holder.online.setVisibility(View.GONE);

        holder.unread.setText(String.valueOf(unread));
        if (unread > 0) {
            holder.unread.setVisibility(View.VISIBLE);
            holder.displayName.setTypeface(null, Typeface.BOLD);
            holder.lastSentence.setTypeface(null, Typeface.BOLD);
            holder.date.setTypeface(null, Typeface.BOLD);
        } else {
            holder.unread.setVisibility(View.INVISIBLE);
            holder.displayName.setTypeface(null, Typeface.NORMAL);
            holder.lastSentence.setTypeface(null, Typeface.NORMAL);
            holder.date.setTypeface(null, Typeface.NORMAL);
        }
        String img = cursor.getString(cursor.getColumnIndex(Contract.Agents.COLUMN_IMG));
        if (img != null) {
            holder.assign.setImageBitmap(AvatarHelper.decodeAvatarBase64(img));
            holder.assign.setVisibility(View.VISIBLE);
        } else
            holder.assign.setVisibility(View.INVISIBLE);
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

        ImageView avatar, online, assign;
        TextView displayName, lastSentence, date, unread, takenBy;
        TextView chanelIcon, locked;


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
            assign = (ImageView) itemView.findViewById(R.id.assign);
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

