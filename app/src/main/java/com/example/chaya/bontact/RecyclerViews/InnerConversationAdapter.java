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

            int idSurfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
            InnerConversationDataManager dataManager = new InnerConversationDataManager(context, idSurfer);
            InnerConversation innerConversation = dataManager.convertCursorToInnerConversation(cursor);

            InnerConversationHolder innerConversationHolder;
            InnerConversationMsgHolder innerConversationMsgHolder;
            InnerConversationVisitorRecordHolder visitorRecordHolder;

            switch (getItemViewType(position)) {

                case SYSTEM_MSG_VH:
                    innerConversationMsgHolder = (InnerConversationMsgHolder) holder;
                    if (innerConversation.actionType == ChanelsTypes.webCall)
                        innerConversationMsgHolder.msg.setText(R.string.webcall_cancled);
                    else
                        innerConversationMsgHolder.msg.setText(getMsgConcatWithDate(innerConversation));
                    innerConversationMsgHolder.msg.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    break;
                case AGENT_TEXT_VH:
                    innerConversationHolder = (InnerConversationHolder) holder;
                    innerConversationHolder.msg.setText(getMsgConcatWithDate(innerConversation));
                    innerConversationHolder.name.setText(getNameForAgent(innerConversation));
                    innerConversationHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(innerConversation.actionType));
                    break;
                case VISITOR_TEXT_VH:
                    innerConversationHolder = (InnerConversationHolder) holder;
                    innerConversationHolder.msg.setText(getMsgConcatWithDate(innerConversation));
                    innerConversationHolder.name.setText(getNameForVisitor(innerConversation));
                    int icon = ChanelsTypes.getIconByChanelType(innerConversation.actionType);
                    if (icon != 0)
                        innerConversationHolder.chanelIcon.setText(icon);
                    break;
                case VISITOR_RECORD_VH:
                    visitorRecordHolder = ((InnerConversationVisitorRecordHolder) holder);
                    visitorRecordHolder.name.setText(getNameForVisitor(innerConversation));
                    visitorRecordHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(innerConversation.actionType));
                    visitorRecordHolder.date.setText(getDateToDisplay(innerConversation));
                    setRecordPlayer(visitorRecordHolder, innerConversation);
                    break;
                case AGENT_RECORD_VH:
                    visitorRecordHolder = ((InnerConversationVisitorRecordHolder) holder);
                    visitorRecordHolder.name.setText(getNameForAgent(innerConversation));
                    visitorRecordHolder.chanelIcon.setText(ChanelsTypes.getIconByChanelType(innerConversation.actionType));
                    visitorRecordHolder.date.setText(getDateToDisplay(innerConversation));
                    setRecordPlayer(visitorRecordHolder, innerConversation);
                    break;
            }
        }
    }

    private String getDateToDisplay(InnerConversation innerConversation) {
        Date date = DateTimeHelper.convertFullFormatStringToDate(innerConversation.getTimeRequest());
        return DateTimeHelper.getDisplayDate(date, R.string.hh_mm_format);
    }

    private boolean setRecordPlayer(InnerConversationVisitorRecordHolder visitorRecordHolder, InnerConversation innerConversation) {

        String url = null;
        if (innerConversation.record == true && innerConversation.mess != null && Integer.parseInt(innerConversation.mess) > 5 &&
                (innerConversation.actionType == ChanelsTypes.callback || innerConversation.actionType == ChanelsTypes.webCall)) {
            AgentDataManager agentDataManager = new AgentDataManager();
    /*       url = context.getResources().getString(R.string.domain_api);
            url += "record/" + agentDataManager.getAgentToken(context) + "/" + innerConversation.req_id + "/" + innerConversation.req_id + ".mp3";
           */
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(context.getResources().getString(R.string.base_api))
                    .appendPath(context.getResources().getString(R.string.rout_api))
                    .appendPath("record")
                    .appendPath(agentDataManager.getAgentToken(context))
                    .appendPath(String.valueOf(innerConversation.req_id))
                    .appendPath(innerConversation.req_id + ".mp3");
            url = builder.build().toString();
            Uri recordUri = Uri.parse(url);

            if (!visitorRecordHolder.player.preparePlayer(recordUri))
                return false;
        } else if (innerConversation.actionType == ChanelsTypes.callback && innerConversation.record == true)
            visitorRecordHolder.player.audioPlayerProblematicPrepare(R.string.short_record);
        else if (innerConversation.actionType == ChanelsTypes.callback)
            visitorRecordHolder.player.audioPlayerProblematicPrepare(R.string.account_not_allow);
        return true;
    }

    private Spannable getMsgConcatWithDate(InnerConversation innerConversation) {

        String msg = null;
        Spannable span = null;
        msg = innerConversation.getMess();

        String date = getDateToDisplay(innerConversation);
        if (msg != null && date != null) {
            msg = msg.concat("   " + date);
            int index = msg.indexOf(date);
            span = new SpannableString(msg);
            span.setSpan(new RelativeSizeSpan(0.7f), index, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return span;
        }
        return null;
    }

    private String getNameForAgent(InnerConversation innerConversation) {
        if (innerConversation.agentName == null)
            return "agent";
        else
            return innerConversation.agentName;
    }

    private String getNameForVisitor(InnerConversation innerConversation) {
        if (innerConversation.from_s == null)
            return "visitor";
        else
            return innerConversation.from_s;
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
