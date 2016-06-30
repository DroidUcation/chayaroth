
package com.example.chaya.bontact.RecyclerViews;
 import android.content.Context;
 import android.database.Cursor;
 import android.support.v7.widget.RecyclerView;
 import android.view.LayoutInflater;
 import android.view.View;
  import android.view.ViewGroup;
 import android.widget.ImageView;
  import android.widget.TextView;


 import com.example.chaya.bontact.Data.Contract;
 import com.example.chaya.bontact.DataManagers.AgentDataManager;
 import com.example.chaya.bontact.DataManagers.ConverastionDataManager;
 import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
 import com.example.chaya.bontact.Models.Conversation;
 import com.example.chaya.bontact.R;

 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.List;

 import com.example.chaya.bontact.Helpers.AvatarHelper;
 import com.example.chaya.bontact.Ui.Activities.MenuActivity;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxHolder> {

    Cursor cursor;
    Context context;
     List<Integer> avatars;

    public InboxAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
       avatars=AvatarHelper.getAvatarsList();
    }


    @Override
    public InboxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inbox_item, null);
        return new InboxHolder(view);

        }

    @Override
    public void onBindViewHolder(InboxHolder holder, int position) {
        cursor.moveToPosition(position);

        String dateString =cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {

            e.printStackTrace();
        }
       holder.displayName.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME)));
       dateString = dateFormat.format(convertedDate);
       holder.lastDate.setText( dateString);
      int avatarPosition = AvatarHelper.getAvatarPosition();
        holder.avatar.setImageResource(avatars.get(avatarPosition));

    /*String imageUri = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_AVATAR));
        if(imageUri!=null)
        {
            try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(imageUri));
            holder.ivContactPhoto.setImageBitmap(bitmap);
            }
            catch (Exception e) {
            e.printStackTrace();
            holder.ivContactPhoto.setImageBitmap(null);
            }
        }*/
    }


        @Override
    public int getItemCount() {
        return cursor.getCount();
    }





    class InboxHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView avatar;
        TextView displayName, lastDate;

        public InboxHolder(View itemView) {

            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            displayName = (TextView) itemView.findViewById(R.id.displayName);
            lastDate = (TextView) itemView.findViewById(R.id.lastDate);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Conversation conversation=null;
            int position= this.getAdapterPosition();
           cursor.moveToPosition(position);
            ConverastionDataManager converastionDataManager=new ConverastionDataManager();

           int id_surfer = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID));
            conversation= converastionDataManager.getConversationByIdSurfer(id_surfer);

            AgentDataManager agentDataManager=new AgentDataManager();
            String token= agentDataManager.getAgentToken(v.getContext());
            if(token!=null&&conversation!=null)
            {
                InnerConversationDataManager innerConversationDataManager=new InnerConversationDataManager(conversation);
                innerConversationDataManager.getDataFromServer(v.getContext(),token);
                ((MenuActivity)v.getContext()).setProgressBarCenterState(View.VISIBLE);
            }
            }

          }
    }


