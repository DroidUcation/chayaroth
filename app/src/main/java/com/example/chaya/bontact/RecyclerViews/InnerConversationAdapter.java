package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.Helpers.SpecialFontsHelper;
import com.example.chaya.bontact.Models.InnerConversation;
import com.example.chaya.bontact.R;


/**
 * Created by chaya on 6/26/2016.
 */
public class InnerConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VISITOR_VH_ITEM = 0;
    public static final int AGENT_VH_ITEM = 1;
    public static final int SYSTEM_MSG_VH_ITEM = 2;
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
            case VISITOR_VH_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_item, null);
                viewHolder = new InnerConversationHolder(view);
                break;
            case AGENT_VH_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_agent_item, null);
                viewHolder = new InnerConversationHolder(view);
                break;
            case SYSTEM_MSG_VH_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_system_item, null);
                viewHolder = new InnerConversationBaseHolder(view);
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

            //initial parameter msgand date
            String msg = null;
            if (innerConversation.actionType == ChanelsTypes.callback || innerConversation.actionType == ChanelsTypes.webCall) {
                if (innerConversation.record == true)
                    msg = innerConversation.recordUrl;
                else
                    msg=context.getResources().getString(R.string.webcall_cancled);
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
                case SYSTEM_MSG_VH_ITEM:
                    InnerConversationBaseHolder innerConversationBaseHolder = (InnerConversationBaseHolder) holder;
                    innerConversationBaseHolder.msg.setText(span);
                    innerConversationBaseHolder.msg.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    break;
                case AGENT_VH_ITEM:
                    if (innerConversation.agentName == null)
                        name = "agent";
                    else
                        name = innerConversation.agentName;
                case VISITOR_VH_ITEM:
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
            }
        }
    }


    @Override
    public int getItemViewType(int position) {

        if (cursor != null && cursor.moveToPosition(position)) {
            if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_SYSTEM_MSG)) == 0)//not a system msg
            {
                if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_REP_REQUEST)) == 1) {
                    return AGENT_VH_ITEM;
                } else {
                    return VISITOR_VH_ITEM;
                }
            }
        }
        return SYSTEM_MSG_VH_ITEM;
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
    class InnerConversationHolder extends InnerConversationBaseHolder {
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

    class InnerConversationBaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView msg;

        public InnerConversationBaseHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.mess);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
/*


public class InnerConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VISITOR_VH_ITEM = 0;
    public static final int AGENT_VH_ITEM = 1;
    public static final int SYSTEM_MSG_VH_ITEM = 2;
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
            case VISITOR_VH_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_item, null);
                break;
            case AGENT_VH_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_agent_item, null);
                break;
            case SYSTEM_MSG_VH_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_agent_item, null);

        }
        viewHolder = new InnerConversationHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            int idSurfer=cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
            InnerConversationDataManager dataManager=new InnerConversationDataManager(context,idSurfer);
            InnerConversation innerConversation= dataManager.convertCursorToInnerConversation(cursor);

            String msg = innerConversation.getMess();
            // String date = innerConversation.getTimeRequest();
            String date = DateTimeHelper.getTimeFromDateToDisplay(innerConversation.getTimeRequest());
            // int action_type = cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_ACTION_TYPE));
            //  int chanel = ChanelsTypes.getIconByChanelType(action_type);
            String name = null;
            switch (getItemViewType(position)) {
                case AGENT_VH_ITEM:
                    //  name = cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_AGENT_NAME));
                    if (innerConversation.agentName == null)
                        name = "agent";
                    else
                        name=innerConversation.agentName;
                    break;
                case VISITOR_VH_ITEM:
                    //  name = cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_NAME));
                    if (innerConversation.from_s == null)
                        name = "visitor";
                    else
                        name=innerConversation.from_s;
                    break;
                case  SYSTEM_MSG_VH_ITEM:
                    name="system";
                    break;

            }
            InnerConversationHolder innerConversationHolder = (InnerConversationHolder) holder;
            msg = msg.concat("   "+date);
            int index = msg.indexOf(date);
            Spannable span = new SpannableString(msg);
            span.setSpan(new RelativeSizeSpan(0.7f), index, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            innerConversationHolder.msg.setText(span);
            innerConversationHolder.name.setText(name);
            innerConversationHolder.chanelIcon.setText( ChanelsTypes.getIconByChanelType(innerConversation.actionType));

        }
    }

    @Override
    public int getItemViewType(int position) {

        if (cursor != null && cursor.moveToPosition(position)) {
            if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_SYSTEM_MSG)) == 0)//not a system msg
            {
                if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_REP_REQUEST)) == 1) {
                    return AGENT_VH_ITEM;
                } else {
                    return VISITOR_VH_ITEM;
                }
            }
        }
        return SYSTEM_MSG_VH_ITEM;
    }

    @Override
    public int getItemCount() {
        if(cursor!=null)
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
    /*/
/********************
 * View Holder************************************
 * class InnerConversationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
 * TextView msg, chanelIcon, date, name;
 * <p/>
 * public InnerConversationHolder(View itemView) {
 * super(itemView);
 * msg = (TextView) itemView.findViewById(R.id.mess);
 * chanelIcon = (TextView) itemView.findViewById(R.id.chanelIcon);
 * chanelIcon.setTypeface(SpecialFontsHelper.getFont(context, R.string.font_awesome));
 * date = (TextView) itemView.findViewById(R.id.date);
 * name = (TextView) itemView.findViewById(R.id.displayName);
 * name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
 * <p/>
 * itemView.setOnClickListener(this);
 * }
 *
 * @Override public void onClick(View v) {
 * <p/>
 * }
 * }
 * <p/>
 * }
 */


