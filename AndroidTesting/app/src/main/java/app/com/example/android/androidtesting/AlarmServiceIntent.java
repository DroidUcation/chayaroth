package app.com.example.android.androidtesting;

import android.app.IntentService;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Intent;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

/**
 * Created by sari on 11/05/2016.
 */
public class AlarmServiceIntent extends IntentService {
    private static final int notificationID = 1;
    public AlarmServiceIntent() {
        super("alarmServiceIntent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        initDB();

        Intent i= new Intent(this,MainActivity.class);
        TaskStackBuilder taskStackBuilder=TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(second.class);
        taskStackBuilder.addNextIntent(i);
        PendingIntent pi= taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n = new  NotificationCompat.Builder(this).setContentTitle("5 new facts").setContentText("5 new facts about android").setSmallIcon(R.drawable.androidbuba).setContentIntent(pi).build();
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(notificationID,n);

    }
    private void initDB()
    {
        insert(getString(R.string.title_one),getString( R.string.one),Images.Id.ONE);
        insert(getString(R.string.title_two), getString(R.string.two), Images.Id.TWO);
        insert(getString(R.string.title_three), getString(R.string.three), Images.Id.THREE);
        insert(getString(R.string.title_four), getString(R.string.four), Images.Id.FOUR);
        insert(getString(R.string.title_five), getString(R.string.five), Images.Id.FIVE);
    }
    private Uri insert(String title,String text,int img)
    {
        ContentValues values=new ContentValues();
        values.put(AndroidContract.ContractDb.COLUMN_TITLE,title);
        values.put(AndroidContract.ContractDb.COLUMN_TEXT,text);
        values.put(AndroidContract.ContractDb.COLUMN_IMG, img);
        return  getContentResolver().insert(AndroidContract.ContractDb.URI, values);

    }

}
