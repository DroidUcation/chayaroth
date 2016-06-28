package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.R;


/**
 * Created by chaya on 6/26/2016.
 */
public class InnerConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VISITOR_VH_ITEM = 0;
    public static final int AGENT_VH_ITEM = 1;
    public static final int SYSTEM_NSG_VH_ITEM = 2;
    Cursor cursor;
    Context context;

    public InnerConversationAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case AGENT_VH_ITEM:
                view = inflater.inflate(R.layout.inner_conversation_agent_item, parent, false);
                viewHolder = new InnerConversationAgentHolder(view);
                break;
            case VISITOR_VH_ITEM:
                view = inflater.inflate(R.layout.inner_conversation_visitor_item, parent, false);
                viewHolder = new InnerConversationVisitorHolder(view);
                break;
          //  case SYSTEM_NSG_VH_ITEM:
            //    break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        cursor.moveToPosition(position);
        int type = getItemViewType(position);
        switch (type) {
            case AGENT_VH_ITEM:
                InnerConversationAgentHolder agentHolder = (InnerConversationAgentHolder) viewHolder;
                agentHolder.msg.setText(cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_MESS)));
                break;
            case VISITOR_VH_ITEM:
                InnerConversationVisitorHolder visitorHolder = (InnerConversationVisitorHolder) viewHolder;
                visitorHolder.msg.setText(cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_MESS)));
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        //  return super.getItemViewType(position);
        cursor.moveToPosition(position);
        if (cursor != null) {
            if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_SYSTEM_MSG)) == 0)//not a system msg
            {
                if (cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_REP_REQUEST)) == 1){
                    Log.d("type","agent");
                    return AGENT_VH_ITEM;
                } else {
                    Log.d("type","visitor");
                    return VISITOR_VH_ITEM;
                }
            } else {
                Log.d("type","system");
                return 0;

            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class InnerConversationAgentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView msg;
        RelativeLayout rootView;

        public InnerConversationAgentHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.mess);
            rootView = (RelativeLayout) itemView.getRootView();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    class InnerConversationVisitorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView msg;
        RelativeLayout rootView;

        public InnerConversationVisitorHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.mess);
            rootView = (RelativeLayout) itemView.getRootView();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}