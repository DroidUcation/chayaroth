package com.example.chaya.bontact;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btn_login = (Button)findViewById(R.id.btn_login);
        View.OnClickListener listener= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences Preferences=getSharedPreferences("UserDeatails",MODE_PRIVATE);
                switch (v.getId())
                {
                    case R.id.btn_login:
                        EditText username=(EditText) findViewById(R.id.etxt_user_name);
                        EditText pass=(EditText) findViewById(R.id.etxt_password);

                        SharedPreferences.Editor editor=Preferences.edit();
                        editor.putString(getResources().getString(R.string.user_name),username.getText().toString());
                        editor.putString(getResources().getString(R.string.password),pass.getText().toString());
                        editor.apply();
                        finish();
                      // String text= Preferences.getString("UserName","")+Preferences.getString("Password","");
                        //Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(MainActivity.this,MenuActivity.class);
                       startActivity(intent);
                }
            }
        };

        btn_login.setOnClickListener(listener);

       /*chat-example

        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChatRecyclerAdapter adapter = new ChatRecyclerAdapter();
        recyclerView.setAdapter(new ChatRecyclerAdapter());
        adapter.addItem(5L);
        adapter.addItem(85L);
        adapter.addItem(25L);
        adapter.addItem(54L);
        */


    }

}

