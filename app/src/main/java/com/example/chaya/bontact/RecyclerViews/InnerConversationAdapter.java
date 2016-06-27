package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.R;


/**
 * Created by chaya on 6/26/2016.
 */
public class InnerConversationAdapter extends RecyclerView.Adapter<InnerConversationAdapter.InnerConversationHolder> {

    public static int VISITOR_VH_ITEM=0;
    public static int AGENT_VH_ITEM=1;
    public static int SYSTEM_NSG_VH_ITEM=1;
    Cursor cursor;
    Context context;

    public InnerConversationAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }
    @Override
    public InnerConversationHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view=null;
        switch(viewType)
        {
            case 0:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_item, null);
                break;
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_agent_item, null);
                break;
        }
        return new InnerConversationHolder(view);
    }

    @Override
    public void onBindViewHolder(InnerConversationHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.msg.setText(cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_MESS)));
    }

    @Override
    public int getItemViewType(int position) {
      //  return super.getItemViewType(position);
        if(cursor==null)
        {}
            if(cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_SYSTEM_MSG))==0)//not a system msg
            {
                if(cursor.getInt(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_REP_REQUEST))==1)//it is a agent msg
                  return AGENT_VH_ITEM;
                    return VISITOR_VH_ITEM;
            }
              return SYSTEM_NSG_VH_ITEM;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class InnerConversationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView msg;

        public InnerConversationHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.mess);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }


}}


