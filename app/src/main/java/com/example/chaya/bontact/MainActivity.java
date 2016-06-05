package com.example.chaya.bontact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btn_login = (Button)findViewById(R.id.btn_login);
        View.OnClickListener listener= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.btn_login:btn_login.animate().alpha(0f).scaleX(0f).scaleY(0f);
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

        //drawer navigation
        // mPlanetTitles = getResources().getStringArray(R.array.menu);
        // mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // mDrawerList = (ListView) findViewById(R.id.navigation_bar);

        // Set the adapter for the list view
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this,
        //      R.layout.activity_main, mPlanetTitles));
        // Set the list's click listener
        //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

}

