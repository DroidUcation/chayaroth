package com.example.chaya.bontact.RecyclerViews;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Helpers.DateTimeHelper;
import com.example.chaya.bontact.R;


/**
 * Created by chaya on 6/26/2016.
 */
public class InnerConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VISITOR_VH_ITEM=0;
    public static  final int AGENT_VH_ITEM=1;
    public static final int SYSTEM_NSG_VH_ITEM=2;
    Cursor cursor;
    Context context;

    public InnerConversationAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view=null;
        RecyclerView.ViewHolder viewHolder=null;
        switch(viewType)
        {
            case VISITOR_VH_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_visitor_item, null);
                viewHolder=new InnerConversationVisitorHolder(view);
                break;
            case AGENT_VH_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.inner_conversation_agent_item, null);
                viewHolder=new InnerConversationAgentHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type=getItemViewType(position);
        cursor.moveToPosition(position);
        String msg=cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_MESS));
       String date=cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_TIME_REQUEST));
         date=DateTimeHelper.getTimeFromDateToDisplay(date);
        switch(type)
        {
            case AGENT_VH_ITEM:
                if(msg!=null)
                {
                    InnerConversationAgentHolder agentHolder= (InnerConversationAgentHolder)holder;
                    agentHolder.msg.setText(msg);
                    agentHolder.date.setText(date);
                    String name=cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_AGENT_NAME));
                    if(name==null)
                        name="agent";
//
//                agentHolder.name.setText(name);
                    agentHolder.chanelIcon.setText(R.string.chat_icon);


                }

                break;

            case VISITOR_VH_ITEM:
                if(msg!=null)
                ((InnerConversationVisitorHolder)holder).msg.setText(cursor.getString(cursor.getColumnIndex(Contract.InnerConversation.COLUMN_MESS)));

                break;
        }
    }


    @Override
    public int getItemViewType(int position) {

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
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class InnerConversationAgentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView msg,name,date,chanelIcon;

        public InnerConversationAgentHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.mess);
           // name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            chanelIcon = (TextView) itemView.findViewById(R.id.chanelIcon);
            Typeface font = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf" );
//            chanelIcon.setTypeface(font);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }


}
    class InnerConversationVisitorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView msg;

        public InnerConversationVisitorHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.mess);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }


    }}


