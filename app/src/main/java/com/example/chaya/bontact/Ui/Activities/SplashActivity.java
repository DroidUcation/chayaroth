package com.example.chaya.bontact.Ui.Activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Helpers.InitData;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Services.GCMPushReceiverService;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init(getIntent().getExtras());
    }

    @Override
    protected void onResume() {
        super.onResume();

        //finish();
    }

    public void init(Bundle args) {
        int id_surfer = 0;

        AgentDataManager agentDataManager = new AgentDataManager();
        Intent intent;
        if (agentDataManager.isLoggedIn(this) == true) {
            InitData initData = new InitData();
            initData.start(this);

            if (args != null)
                id_surfer = args.getInt(Contract.InnerConversation.COLUMN_ID_SURFUR);
            if (id_surfer != 0) {//from notification
                int push_type = args.getInt(getResources().getString(R.string.push_notification_type));
                if (push_type == GCMPushReceiverService.NEW_MESSAGE) {
                    intent = new Intent(this, InnerConversationActivity.class);
                    Bundle b = new Bundle();
                    b.putInt(Contract.InnerConversation.COLUMN_ID_SURFUR, id_surfer); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                } else {
                    intent = new Intent(this, TabsActivity.class);
                    intent.putExtra(getString(R.string.first_tab_title_key), R.string.onlinevisitors_title);
                }
            } else { //normal
                intent = new Intent(this, TabsActivity.class);
            }
        } else //not logged in
            intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}