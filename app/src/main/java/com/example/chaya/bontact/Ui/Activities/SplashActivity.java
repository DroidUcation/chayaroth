package com.example.chaya.bontact.Ui.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AgentDataManager agentDataManager=new AgentDataManager();
        Intent intent;
   /*   if(  agentDataManager.isLoggedIn(this)==true)
            intent=new Intent(this,MenuActivity.class  );
        else*/
        intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
