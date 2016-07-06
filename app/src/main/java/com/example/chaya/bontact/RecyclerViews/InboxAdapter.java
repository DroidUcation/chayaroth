
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
        cursor.moveToPosition(position);

        // init data to display
        int lastType=cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_TYPE));
        int chanelIcon= ChanelsTypes.getIconByChanelType(lastType);

        int isUnread=cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD));
        String lastSentences =cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_SENTENCE));
        if(lastSentences==null)
            lastSentences= ChanelsTypes.getDefultStringByChanelType(context,lastType);
        String dateStringToConvert = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_LAST_DATE));
        String timeAgo=null;
        if(dateStringToConvert!=null && context!=null) {
            timeAgo = DateTimeHelper.getDiffToNow(dateStringToConvert, context);
        }

        //set in item
        holder.displayName.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME)));
        holder.avatar.setImageResource(AvatarHelper.getNextAvatar());
        holder.chanelIcon.setText(chanelIcon);
        if(lastType==ChanelsTypes.webCall||lastType==ChanelsTypes.sms||lastType==ChanelsTypes.callback)
        holder.chanelIcon.setTextSize(14);
            if(lastSentences!=null)
             holder.lastSentence.setText(lastSentences);

        if(timeAgo!=null)
             holder.date.setText(timeAgo);

        if(isUnread==1)
        {
            holder.unread.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_UNREAD)));
          holder.setUnRead(true);
        }

    }


        @Override
    public int getItemCount() {
        return cursor.getCount();
    }





    class InboxHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView avatar;
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
            ConverastionDataManager converastionDataManager=new ConverastionDataManager();

           int id_surfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID_SURFER));
            conversation= converastionDataManager.getConversationByIdSurfer(id_surfer);

            AgentDataManager agentDataManager=new AgentDataManager();
            String token= agentDataManager.getAgentToken(v.getContext());
            if(token!=null&&conversation!=null)
            {
                InnerConversationDataManager innerConversationDataManager=new InnerConversationDataManager(conversation);
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


