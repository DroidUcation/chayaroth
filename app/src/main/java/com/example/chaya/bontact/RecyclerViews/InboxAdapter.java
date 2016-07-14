

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
import com.example.chaya.bontact.DataManagers.ConverastionDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.R;

import java.util.List;

import com.example.chaya.bontact.Helpers.AvatarHelper;
import com.example.chaya.bontact.Ui.Activities.MenuActivity;
import com.squareup.picasso.Picasso;


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
/*
        if (!cursor.moveToPosition(position))
            return;
        ConverastionDataManager converastionDataManager = new ConverastionDataManager(context);
        Conversation conversation = converastionDataManager.convertCursorToConversation(cursor);

        if (conversation != null) {
            holder.displayName.setText(conversation.displayname);
            holder.avatar.setImageResource(Integer.parseInt(conversation.avatar));
            holder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(conversation.lasttype));
            if (conversation.lastSentence == null)
                holder.lastSentence.setText(ChanelsTypes.getDefultStringByChanelType(context, conversation.getLasttype()));
            else
                holder.lastSentence.setText(conversation.lastSentence);
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
        }*/
        cursor.moveToPosition(position);

        // init data to display
        int lastType=cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_TYPE));
        int chanelIcon= ChanelsTypes.getIconByChanelType(lastType);
        String avatarUrl=cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_AVATAR));
        int isUnread=cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD));
        String lastSentences =cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_SENTENCE));
        if(lastSentences==null)
            lastSentences= ChanelsTypes.getDefultStringByChanelType(context,lastType);
        String dateStringToConvert = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_DATE));
        String timeAgo=null;
        if(dateStringToConvert!=null && context!=null) {
            timeAgo = DateTimeHelper.getDiffToNow(dateStringToConvert, context);
        }
        int isOnline=cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_IS_ONLINE));

        //set in item
        holder.displayName.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME)));
       /* if(avatarUrl!=null)
        Picasso.with(context)
                .load(avatarUrl)
                .into(holder.avatar);*/
      /*  else {
            holder.avatar.setImageResource(AvatarHelper.getNextAvatar());
       }*/
        String avatarStr=cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_AVATAR));
        holder.avatar.setImageResource(Integer.parseInt(avatarStr));
        holder.chanelIcon.setText(chanelIcon);
        if(lastType==ChanelsTypes.webCall||lastType==ChanelsTypes.sms||lastType==ChanelsTypes.callback)
            holder.chanelIcon.setTextSize(14);
        if(lastSentences!=null)
            holder.lastSentence.setText(lastSentences);

        if(timeAgo!=null)
            holder.date.setText(timeAgo);
        if(isOnline==1)
        {
            holder.onlinePoint.setVisibility(View.VISIBLE);
        }
        if(isUnread>=1)
        {
            holder.unread.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD)));
            holder.setUnRead(true);
        }
    }

    @Override
    public int getItemCount() {
        if(cursor!=null)
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

        ImageView avatar,onlinePoint;
        TextView displayName, lastSentence,date,unread;
        TextView chanelIcon;


        public InboxHolder(View itemView) {

            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            displayName = (TextView) itemView.findViewById(R.id.displayName);
            lastSentence = (TextView) itemView.findViewById(R.id.last_sentence);
            date = (TextView) itemView.findViewById(R.id.date);
            unread = (TextView) itemView.findViewById(R.id.unread);

            chanelIcon=(TextView) itemView.findViewById(R.id.chanelIcon);
            Typeface font = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf" );
            chanelIcon.setTypeface(font);
            onlinePoint=(ImageView) itemView.findViewById(R.id.online_point);;
            itemView.setOnClickListener(this);
            avatar.setOnClickListener(imagesClickListener);

        }
        public void setUnRead(boolean status)
        {
            if(status==true)
            {
                displayName.setTypeface(null, Typeface.BOLD);
                lastSentence.setTypeface(null, Typeface.BOLD);
                date.setTypeface(null, Typeface.BOLD);

                unread.setVisibility(View.VISIBLE);
            }
        }


        @Override
        public void onClick(View v) {

            Conversation conversation=null;
            int position= this.getAdapterPosition();
            cursor.moveToPosition(position);
            ConverastionDataManager converastionDataManager=new ConverastionDataManager(v.getContext());

            int id_surfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
            conversation= converastionDataManager.getConversationByIdSurfer(id_surfer);

            AgentDataManager agentDataManager=new AgentDataManager();
            String token= agentDataManager.getAgentToken(v.getContext());
            if(token!=null&&conversation!=null)
            {
                InnerConversationDataManager innerConversationDataManager=new InnerConversationDataManager(v.getContext(),conversation);
                ((MenuActivity)v.getContext()).setProgressBarCenterState(View.VISIBLE);
                innerConversationDataManager.getData(v.getContext(),token);

            }
        }

        View.OnClickListener imagesClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

    }
}

/*

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
import com.example.chaya.bontact.DataManagers.ConverastionDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Helpers.SpecialFontsHelper;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.R;

import java.util.ArrayList;
import java.util.List;

import com.example.chaya.bontact.Helpers.AvatarHelper;
import com.example.chaya.bontact.Ui.Activities.MenuActivity;
import com.google.android.exoplayer.extractor.PositionHolder;
import com.squareup.picasso.Picasso;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxHolder> {

    private Cursor cursor;
    Context context;
    List<Conversation> conversationList;

    public InboxAdapter(Context context, Cursor cursor) {
        this.context = context;
        setCursor(cursor);
        conversationList = new ArrayList<>();
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        if (cursor != null && cursor.moveToFirst()) {
            ConverastionDataManager converastionDataManager = new ConverastionDataManager(context);
            Conversation conversation = null;
            if (conversationList == null)
                conversationList = new ArrayList<>();
            conversationList.clear();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                conversation = converastionDataManager.convertCursorToConversation(cursor);
                conversationList.add(conversation);
            }
        }
    }

    public Cursor getCursor() {
        return cursor;
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
        ConverastionDataManager converastionDataManager = new ConverastionDataManager(context);
        Conversation conversation = converastionDataManager.convertCursorToConversation(cursor);

        if (conversation != null) {
            holder.displayName.setText(conversation.displayname);
            holder.avatar.setImageResource(Integer.parseInt(conversation.avatar));
            holder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(conversation.lasttype));
            if (conversation.lastSentence == null)
                holder.lastSentence.setText(ChanelsTypes.getDefultStringByChanelType(context, conversation.getLasttype()));
            else
                holder.lastSentence.setText(conversation.lastSentence);
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
        }
    }

    @Override
    public int getItemCount() {
        if (cursor != null)
            return cursor.getCount();
        return 0;
    }


    public Cursor swapCursor(Cursor cursor) {
        if (getCursor() == cursor) {
            return null;
        }
        Cursor oldCursor = getCursor();
        setCursor(cursor);
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

// *********View Holder***************************************//*
/

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
            chanelIcon.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
            onlinePoint = (ImageView) itemView.findViewById(R.id.online_point);
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

            Conversation conversation = null;
            int position = this.getAdapterPosition();
            if (cursor.moveToPosition(position)) {
                ConverastionDataManager converastionDataManager = new ConverastionDataManager(v.getContext());
                conversation = converastionDataManager.convertCursorToConversation(cursor);

                AgentDataManager agentDataManager = new AgentDataManager();
                String token = agentDataManager.getAgentToken(v.getContext());
                if (token != null && conversation != null) {
                    InnerConversationDataManager innerConversationDataManager = new InnerConversationDataManager(v.getContext(), conversation);
                    ((MenuActivity) v.getContext()).setProgressBarCenterState(View.VISIBLE);
                    innerConversationDataManager.getData(v.getContext(), token);

                }
            }
        }

        View.OnClickListener imagesClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

    }
}


*/
