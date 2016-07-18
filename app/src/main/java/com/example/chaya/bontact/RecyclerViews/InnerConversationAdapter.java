package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Helpers.SpecialFontsHelper;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.R;

import nl.changer.audiowife.AudioWife;


/**
 * Created by chaya on 6/26/2016.
 */

public class InnerConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VISITOR_TEXT_VH = 0;
    public static final int AGENT_TEXT_VH = 1;
    public static final int SYSTEM_MSG_VH = 2;
    public static final int VISITOR_RECORD_VH = 3;
    Cursor cursor;
    Context context;

    public InnerConversationAdapter(Context context, Cursor cursor) {
        this.context = context;
        setCursor(cursor);
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VISITOR_TEXT_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_item, null);
                viewHolder = new InnerConversationHolder(view);
                break;
            case AGENT_TEXT_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_agent_item, null);
                viewHolder = new InnerConversationHolder(view);
                break;
            case VISITOR_RECORD_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_record, null);
                viewHolder = new InnerConversationvVisitorRecordHolder(view);
                break;
            case SYSTEM_MSG_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_system_item, null);
                viewHolder = new InnerConversationMsgHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            //convert to object
            int idSurfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
            InnerConversationDataManager dataManager = new InnerConversationDataManager(context, idSurfer);
            InnerConversation innerConversation = dataManager.convertCursorToInnerConversation(cursor);

            int type = getItemViewType(position);

            //initial parameter msgand date
            String msg = null;
            if (innerConversation.actionType == ChanelsTypes.callback || innerConversation.actionType == ChanelsTypes.webCall) {
                if (innerConversation.recordUrl != null)
                    msg = innerConversation.recordUrl;
                else
                    msg = context.getResources().getString(R.string.webcall_cancled);
            } else
                msg = innerConversation.getMess();
            String date = DateTimeHelper.getTimeFromDateToDisplay(innerConversation.getTimeRequest());
            if (msg != null && date != null)
                msg = msg.concat("   " + date);
            int index = msg.indexOf(date);
            Spannable span = new SpannableString(msg);
            span.setSpan(new RelativeSizeSpan(0.7f), index, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String name = null;
            switch (getItemViewType(position)) {
                case SYSTEM_MSG_VH:
                    InnerConversationMsgHolder innerConversationBaseHolder = (InnerConversationMsgHolder) holder;
                    innerConversationBaseHolder.msg.setText(span);
                    innerConversationBaseHolder.msg.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    break;

                case AGENT_TEXT_VH:
                    if (innerConversation.agentName == null)
                        name = "agent";
                    else
                        name = innerConversation.agentName;
                case VISITOR_TEXT_VH:
                    if (name == null) {
                        if (innerConversation.from_s == null)
                            name = "visitor";
                        else
                            name = innerConversation.from_s;
                    }
                    InnerConversationHolder innerConversationHolder = (InnerConversationHolder) holder;
                    innerConversationHolder.msg.setText(span);
                    innerConversationHolder.name.setText(name);
                    innerConversationHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(innerConversation.actionType));

                    break;
                case VISITOR_RECORD_VH:
                    if (name == null) {
                        if (innerConversation.from_s == null)
                            name = "visitor";
                        else
                            name = innerConversation.from_s;
                    }
                    if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_RECORD)) == 1) {
                        InnerConversationvVisitorRecordHolder visitorRecordHolder = ((InnerConversationvVisitorRecordHolder) holder);
                        visitorRecordHolder.name.setText(name);
                        visitorRecordHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(innerConversation.actionType));
                        String url;
                        if (Integer.parseInt(innerConversation.mess) < 5)
                            url = "http://programmerguru.com/android-tutorial/wp-content/uploads/2013/04/hosannatelugu.mp3";
                        else {
                            AgentDataManager agentDataManager = new AgentDataManager();
                            url = context.getResources().getString(R.string.domain_api);
                            url += "record/" + agentDataManager.getAgentToken(context) + "/" + innerConversation.req_id + "/" + innerConversation.req_id + ".mp3";
                        }
                        visitorRecordHolder.setRecordUrl(url);

                    }
                    break;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {

        if (cursor != null && cursor.moveToPosition(position)) {
            if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_SYSTEM_MSG)) == 0)//not a system msg
            {
                if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_REP_REQUEST)) == 1) {
                    return AGENT_TEXT_VH;
                } else {
                    if (cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_RECORD_URL)) != null)
                        return VISITOR_RECORD_VH;
                    return VISITOR_TEXT_VH;
                }
            }
        }
        return SYSTEM_MSG_VH;
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

    //********************View Holder************************************
    class InnerConversationHolder extends InnerConversationMsgHolder {
        TextView chanelIcon, name;

        public InnerConversationHolder(View itemView) {
            super(itemView);
            chanelIcon = (TextView) itemView.findViewById(R.id.chanelIcon);
            chanelIcon.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
            name = (TextView) itemView.findViewById(R.id.displayName);
            name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));

            itemView.setOnClickListener(this);
        }

    }

    class InnerConversationvVisitorRecordHolder extends RecyclerView.ViewHolder {

        TextView chanelIcon, name;
        ImageView playBtn, pauseBtn;
        //AppCompatSeekBar seekBar;
        RelativeLayout playerLayout;

        public InnerConversationvVisitorRecordHolder(View itemView) {
            super(itemView);
            chanelIcon = (TextView) itemView.findViewById(R.id.chanelIcon);
            chanelIcon.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
            name = (TextView) itemView.findViewById(R.id.displayName);
            name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            playerLayout = (RelativeLayout) itemView.findViewById(R.id.player_layout);
            //playBtn = (ImageView) itemView.findViewById(R.id.play_btn);
            //pauseBtn = (ImageView) itemView.findViewById(R.id.pause_btn);

         //   playBtn.setOnClickListener(playListener);

        }

        public void setRecordUrl(String recordUrl) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            AudioWife.getInstance().init(context, Uri.parse(recordUrl))
                    .useDefaultUi(playerLayout, li).addOnPlayClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

           }
    class InnerConversationMsgHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView msg;

        public InnerConversationMsgHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.mess);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}

