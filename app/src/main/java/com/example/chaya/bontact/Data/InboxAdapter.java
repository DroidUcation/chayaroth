/*

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

        import static android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        import static android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER;
        import static android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_URI;

public class InboxAdapter extends RecyclerView.Adapter {

    Cursor cursor;
    Context mContext;

    public InboxAdapter(Context context, Cursor cursor) {
        mContext = context;
        this.cursor = cursor;
    }

    @Override
    public InboxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.inbox_item, null);
        return new InboxHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        cursor.moveToPosition(position);

        holder.tvContactName.setText(cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
        holder.tvContactNumber.setText(cursor.getString(cursor.getColumnIndex(NUMBER)));
        String imageUri = cursor.getString(cursor.getColumnIndex(PHOTO_URI));

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(imageUri));
            holder.ivContactPhoto.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            holder.ivContactPhoto.setImageBitmap(null);
        }
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
*/
