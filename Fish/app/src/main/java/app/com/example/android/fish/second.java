
package app.com.example.android.fish;

import android.content.ContentResolver;
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

public class second extends AppCompatActivity {
    DBFacts dbFacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
         dbFacts= new DBFacts(this);
        setContentView(R.layout.activity_second);
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

    public void open(View view) {

        ImageView imageView=(ImageView) findViewById(R.id.picture) ;
        TextView textView=(TextView) findViewById(R.id.text);
        ContentResolver resolver= getContentResolver();
        Cursor cursor =resolver.query(app.com.example.android.fish.DBFacts.class,null,null,null,null);
      switch(view.getTag().toString())
        {
            case "one":
                imageView.setImageResource(cursor.getInt(1));

             //   imageView.setImageResource(R.drawable.googlepic);
               // Intent intent=new Intent(Intent.ACTION_SENDTO);
                //intent.setData(Uri.parse("mailto:"));
                //intent.putExtra(intent.EXTRA_EMAIL, "chayarero@gmail.com");
                //intent.putExtra(intent.EXTRA_SUBJECT,"This a good intent");
                //startActivity(intent);
                Toast.makeText(second.this, R.string.one, Toast.LENGTH_LONG).show();
                //textView.setText(R.string.one);

                break;
            case "two":
                imageView.setImageResource(R.drawable.chalalit);
               // textView.setText(R.string.two);
                Toast.makeText(second.this, R.string.two, Toast.LENGTH_LONG).show();
                break;
            case "three":
                imageView.setImageResource(R.drawable.mancal_google);
               // textView.setText(R.string.three);
                Toast.makeText(second.this, R.string.three, Toast.LENGTH_LONG).show();
                break;
            case "four":
                imageView.setImageResource(R.drawable.alotofandroid);
               // textView.setText(R.string.four);
                Toast.makeText(second.this, R.string.four, Toast.LENGTH_LONG).show();
                break;
            case "five":
              imageView.setImageResource(R.drawable.androidbuba);
                //textView.setText(R.string.four);
                Toast.makeText(second.this,"So what's Android's green robot name?\n" +
                        "   He does not have an official name yet but Google's engineers call it Bugdroid.", Toast.LENGTH_LONG).show();

                break;
        }
    }
}
