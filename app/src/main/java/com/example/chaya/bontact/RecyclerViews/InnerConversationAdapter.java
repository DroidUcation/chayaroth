package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.AudioPlayerInnerHelper;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Helpers.SpecialFontsHelper;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.R;

import java.util.Date;


/**
 * Created by chaya on 6/26/2016.
 */

public class InnerConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int SYSTEM_MSG_VH = 0;
    public static final int VISITOR_TEXT_VH = 1;
    public static final int AGENT_TEXT_VH = 2;
    public static final int VISITOR_RECORD_VH = 3;
    public static final int AGENT_RECORD_VH = 4;
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
            case SYSTEM_MSG_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_system_item, null);
                viewHolder = new InnerConversationMsgHolder(view);
                break;
            case VISITOR_TEXT_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_item, null);
                viewHolder = new InnerConversationHolder(view);
                break;
            case VISITOR_RECORD_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_record, null);
                viewHolder = new InnerConversationVisitorRecordHolder(view);
                break;
            case AGENT_TEXT_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_agent_item, null);
                viewHolder = new InnerConversationHolder(view);
                break;
            case AGENT_RECORD_VH:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_agent_record, null);
                viewHolder = new InnerConversationVisitorRecordHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {

            // int idSurfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
            //  InnerConversationDataManager dataManager = new InnerConversationDataManager(context, idSurfer);
            //InnerConversation innerConversation = dataManager.convertCursorToInnerConversation(cursor);

            InnerConversationHolder innerConversationHolder;
            InnerConversationMsgHolder innerConversationMsgHolder;
            InnerConversationVisitorRecordHolder visitorRecordHolder;
            int actionType = cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE));
            String msg = cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_MESS));
            String date = cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_TIME_REQUEST));
            switch (getItemViewType(position)) {

                case SYSTEM_MSG_VH:
                    innerConversationMsgHolder = (InnerConversationMsgHolder) holder;
                    if (actionType == ChanelsTypes.webCall)
                        innerConversationMsgHolder.msg.setText(R.string.webcall_cancled);
                    else
                        innerConversationMsgHolder.msg.setText(getMsgConcatWithDate(msg, date));
                    innerConversationMsgHolder.msg.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    break;
                case AGENT_TEXT_VH:
                    innerConversationHolder = (InnerConversationHolder) holder;
                    innerConversationHolder.msg.setText(getMsgConcatWithDate(msg, date));
                    innerConversationHolder.name.setText(getNameForAgent(cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_AGENT_NAME))));
                    innerConversationHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(actionType));
                    break;
                case VISITOR_TEXT_VH:
                    innerConversationHolder = (InnerConversationHolder) holder;
                    innerConversationHolder.msg.setText(getMsgConcatWithDate(msg, date));
                    innerConversationHolder.name.setText(getNameForVisitor(getNameForAgent(cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_FROM)))));
                    int icon = ChanelsTypes.getIconByChanelType(actionType);
                    if (icon != 0)
                        innerConversationHolder.chanelIcon.setText(icon);
                    break;
                case VISITOR_RECORD_VH:
                    visitorRecordHolder = ((InnerConversationVisitorRecordHolder) holder);
                    visitorRecordHolder.name.setText(getNameForVisitor(getNameForAgent(cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_FROM)))));
                    visitorRecordHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(actionType));
                    visitorRecordHolder.date.setText(getDateToDisplay(date));
                    setRecordPlayer(visitorRecordHolder);
                    break;
                case AGENT_RECORD_VH:
                    visitorRecordHolder = ((InnerConversationVisitorRecordHolder) holder);
                    visitorRecordHolder.name.setText(getNameForAgent(cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_AGENT_NAME))));
                    visitorRecordHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(actionType));
                    visitorRecordHolder.date.setText(getDateToDisplay(date));
                    setRecordPlayer(visitorRecordHolder);
                    break;
            }
        }
    }

    private String getDateToDisplay(String timeRequest) {
        Date date = DateTimeHelper.convertFullFormatStringToDate(timeRequest);
        return DateTimeHelper.getDisplayDate(date, R.string.hh_mm_format);
    }

    private boolean setRecordPlayer(InnerConversationVisitorRecordHolder visitorRecordHolder) {
        if (cursor == null)
            return false;
        boolean record = cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_RECORD)) == 1 ? true : false;
        String mess = cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_MESS));
        int actionType = cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE));
        int req_id = cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_REQ_ID));

        String url = null;
        if (record == true && mess != null && Integer.parseInt(mess) > 5 &&
                (actionType == ChanelsTypes.callback || actionType == ChanelsTypes.webCall)) {
            AgentDataManager agentDataManager = new AgentDataManager();
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(context.getResources().getString(R.string.base_api))
                    .appendPath(context.getResources().getString(R.string.rout_api))
                    .appendPath("record")
                    .appendPath(agentDataManager.getAgentToken(context))
                    .appendPath(String.valueOf(req_id))
                    .appendPath(req_id + ".mp3");
            url = builder.build().toString();
            Uri recordUri = Uri.parse(url);

            if (!visitorRecordHolder.player.preparePlayer(recordUri))
                return false;
        } else if (actionType == ChanelsTypes.callback && record == true)
            visitorRecordHolder.player.audioPlayerProblematicPrepare(R.string.short_record);
        else if (actionType == ChanelsTypes.callback)
            visitorRecordHolder.player.audioPlayerProblematicPrepare(R.string.account_not_allow);
        return true;
    }

    private Spannable getMsgConcatWithDate(String strMsg, String timeRequest) {
        Spannable span = null;
        String msg = strMsg;

        String date = getDateToDisplay(timeRequest);
        if (msg != null && date != null) {
            msg = msg.concat("   " + date);
            int index = msg.indexOf(date);
            span = new SpannableString(msg);
            span.setSpan(new RelativeSizeSpan(0.7f), index, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return span;
        }
        return null;
    }

    private String getNameForAgent(String agentName) {
        if (agentName == null)
            return "agent";
        else
            return agentName;
    }

    private String getNameForVisitor(String from) {
        if (from == null)
            return "visitor";
        else
            return from;
    }

    @Override
    public int getItemViewType(int position) {

        if (cursor != null && cursor.moveToPosition(position)) {
            if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_SYSTEM_MSG)) == 0)//not a system msg
            {
                if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_REP_REQUEST)) == 1) {
                    if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE)) == ChanelsTypes.callback ||
                            (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE)) == ChanelsTypes.webCall &&
                                    cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_MESS)) != null))
                        return AGENT_RECORD_VH;
                    return AGENT_TEXT_VH;
                } else {
                    if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE)) == ChanelsTypes.callback ||
                            (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE)) == ChanelsTypes.webCall &&
                                    cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_MESS)) != null))
                        return VISITOR_RECORD_VH;
                    if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE)) == ChanelsTypes.webCall)
                        return SYSTEM_MSG_VH;
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

    //********************View Holders************************************

    public class InnerConversationVisitorRecordHolder extends RecyclerView.ViewHolder {

        TextView chanelIcon, name, date;
        AudioPlayerInnerHelper player;

        public InnerConversationVisitorRecordHolder(View itemView) {
            super(itemView);
            chanelIcon = (TextView) itemView.findViewById(R.id.chanelIcon);
            chanelIcon.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
            name = (TextView) itemView.findViewById(R.id.displayName);
            name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            date = (TextView) itemView.findViewById(R.id.date);
            player = new AudioPlayerInnerHelper(itemView);
        }
    }

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
