
package com.example.chaya.bontact.RecyclerViews;
 import android.content.Context;
 import android.content.SharedPreferences;
 import android.database.Cursor;
 import android.support.v7.widget.RecyclerView;
 import android.view.LayoutInflater;
 import android.view.View;
  import android.view.ViewGroup;
 import android.widget.ImageView;
  import android.widget.TextView;
 import android.widget.Toast;


 import com.example.chaya.bontact.Data.Contract;
 import com.example.chaya.bontact.R;
 import com.example.chaya.bontact.NetworkCalls.InnerConversationData;

 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;

 import com.example.chaya.bontact.Helpers.AvatarHelper;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxHolder> {

    Cursor cursor;
    Context context;
     List<Integer> avatars;

    public InboxAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        FillAvatarsList();
    }
public void FillAvatarsList()
{
    if(avatars==null) {
        avatars = new ArrayList<Integer>();
    }

    avatars.add(R.drawable.avatar1);
    avatars.add(R.drawable.avatar2);
    avatars.add(R.drawable.avatar3);
    avatars.add(R.drawable.avatar4);
    avatars.add(R.drawable.avatar5);
    avatars.add(R.drawable.avatar6);
    avatars.add(R.drawable.avatar7);
    avatars.add(R.drawable.avatar8);
    avatars.add(R.drawable.avatar9);
    avatars.add(R.drawable.avatar10);
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
      int avatarPosition = AvatarHelper.getAvatar();
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

           int position= this.getAdapterPosition();
           cursor.moveToPosition(position);
            String name = cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME));
            int Id = cursor.getInt(cursor.getColumnIndex(Contract.Conversation.COLUMN_ID));
            SharedPreferences Preferences =context.getSharedPreferences(context.getResources().getString(R.string.sp_user_details), context.MODE_PRIVATE);
            String token = Preferences.getString(context.getResources().getString(R.string.token), "");
            if (token != null)//token agent is found
            {
                InnerConversationData innerConversationData = new InnerConversationData(v.getContext(), token, Id);
                innerConversationData.getDataFromServer();
                if(innerConversationData.getResFromServer()!=null)
                {
                    Toast.makeText(v.getContext(), innerConversationData.getResFromServer().toString(), Toast.LENGTH_SHORT).show();
                   // Intent intent =new Intent(v.getContext(),MenuActivity.class);
                  // v.getContext().startActivity(intent);
                }
            }
          }
    }

}
