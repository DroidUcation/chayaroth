package app.com.example.android.androidtesting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class second extends AppCompatActivity {
    AlarmManager alarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_second);
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, 5000, createIntent());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This is an android information app", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    private PendingIntent createIntent ()
    {
        Intent i = new Intent(this,AlarmServiceIntent.class);
        PendingIntent p= PendingIntent.getService(this,0,i,0);
        return p;
    }

    public void open(View view) {

        ImageView imageView=(ImageView) findViewById(R.id.picture) ;
        TextView textView=(TextView) findViewById(R.id.text);

        String [] projection={AndroidContract.ContractDb.COLUMN_TEXT,AndroidContract.ContractDb.COLUMN_IMG};
        String [] selectionArgs={(String) view.getTag()};
         Cursor cursor=getContentResolver().query(AndroidContract.ContractDb.URI,projection,AndroidContract.ContractDb.COLUMN_TITLE+"= ? ",selectionArgs,null);
        cursor.moveToFirst();
        imageView.setImageResource(cursor.getInt(cursor.getColumnIndex(AndroidContract.ContractDb.COLUMN_IMG)));
        String toast=cursor.getString(cursor.getColumnIndex(AndroidContract.ContractDb.COLUMN_TEXT));
        Toast.makeText(second.this,toast, Toast.LENGTH_LONG).show();
        cursor.close();

    }
}
