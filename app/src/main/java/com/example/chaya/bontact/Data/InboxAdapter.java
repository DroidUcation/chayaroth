
package com.example.chaya.bontact.Data;


 import android.content.Context;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.provider.MediaStore;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;


        import com.example.chaya.bontact.R;

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

       holder.tvContactName.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_DISPLAY_NAME)));
        holder.tvContactNumber.setText(cursor.getString(cursor.getColumnIndex(Contract.Conversation.COLUMN_EMAIL)));
  /* img:
   String imageUri = cursor.getString(cursor.getColumnIndex(PHOTO_URI));

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(imageUri));
           holder.ivContactPhoto.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            holder.ivContactPhoto.setImageBitmap(null);
        }*/
    }


        @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    class InboxHolder extends RecyclerView.ViewHolder {

        ImageView ivContactPhoto;
        TextView tvContactName, tvContactNumber;

        public InboxHolder(View itemView) {
            super(itemView);
            ivContactPhoto = (ImageView) itemView.findViewById(R.id.ivContactPhoto);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvContactNumber = (TextView) itemView.findViewById(R.id.tvContactNumber);
        }
    }
}