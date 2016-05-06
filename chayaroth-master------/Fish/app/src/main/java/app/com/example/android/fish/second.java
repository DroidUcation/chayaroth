
package app.com.example.android.fish;

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
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_second);
        insertToDB();
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
    private void insertToDB()
    {
        ContentValues values=new ContentValues();
        //values.put(AndroidContract.ContractDb.COLUMN_TITLE,getString(R.string.title_one));
        values.put(AndroidContract.ContractDb.COLUMN_TEXT,getString( R.string.one));
        values.put(AndroidContract.ContractDb.COLUMN_IMG, Images.Id.ONE);
        getContentResolver().insert(AndroidContract.ContractDb.URI,values );

        values.clear();
      //values.put(AndroidContract.ContractDb.COLUMN_TITLE,getString(R.string.title_two));
        values.put(AndroidContract.ContractDb.COLUMN_TEXT,getString( R.string.two));
        values.put(AndroidContract.ContractDb.COLUMN_IMG, Images.Id.TWO);
        getContentResolver().insert(AndroidContract.ContractDb.URI,values );

        values.clear();
        //values.put(AndroidContract.ContractDb.COLUMN_TITLE,getString(R.string.title_three));
       values.put(AndroidContract.ContractDb.COLUMN_TEXT,getString( R.string.three));
        values.put(AndroidContract.ContractDb.COLUMN_IMG, Images.Id.THREE);
        getContentResolver().insert(AndroidContract.ContractDb.URI,values );

        values.clear();
        //values.put(AndroidContract.ContractDb.COLUMN_TITLE,getString(R.string.title_four));
        values.put(AndroidContract.ContractDb.COLUMN_TEXT,getString( R.string.four));
        values.put(AndroidContract.ContractDb.COLUMN_IMG, Images.Id.FOUR);
        getContentResolver().insert(AndroidContract.ContractDb.URI,values );

        values.clear();
        //values.put(AndroidContract.ContractDb.COLUMN_TITLE,getString(R.string.title_five));
        values.put(AndroidContract.ContractDb.COLUMN_TEXT,getString( R.string.five));
        values.put(AndroidContract.ContractDb.COLUMN_IMG, Images.Id.FIVE);
        getContentResolver().insert(AndroidContract.ContractDb.URI,values );
    }

    public void open(View view) {

        ImageView imageView=(ImageView) findViewById(R.id.picture) ;
        TextView textView=(TextView) findViewById(R.id.text);

        //String [] projection={AndroidContract.ContractDb.COLUMN_TEXT,AndroidContract.ContractDb.COLUMN_IMG};
        //String [] selectionArgs={view.getId()+""};
       // Cursor cursor=getContentResolver().query(AndroidContract.ContractDb.URI,projection,AndroidContract.ContractDb.COLUMN_ID_FACT+"? = ",selectionArgs,null);
//ContentValues values=new ContentValues();
       switch(view.getTag().toString())
        {
            case "one":

               imageView.setImageResource(R.drawable.googlepic);
                Toast.makeText(second.this, R.string.one, Toast.LENGTH_LONG).show();
                break;
            case "two":
                imageView.setImageResource(R.drawable.chalalit);
                Toast.makeText(second.this, R.string.two, Toast.LENGTH_LONG).show();
                break;
            case "three":
                imageView.setImageResource(R.drawable.mancal_google);
                Toast.makeText(second.this, R.string.three, Toast.LENGTH_LONG).show();
                break;
            case "four":
                imageView.setImageResource(R.drawable.alotofandroid);
                Toast.makeText(second.this, R.string.four, Toast.LENGTH_LONG).show();
                break;
            case "five":
              imageView.setImageResource(R.drawable.androidbuba);
                Toast.makeText(second.this,R.string.five, Toast.LENGTH_LONG).show();

                break;
        }
    }
}
