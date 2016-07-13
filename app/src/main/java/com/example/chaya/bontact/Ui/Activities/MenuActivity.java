package com.example.chaya.bontact.Ui.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chaya.bontact.Data.Contract;
import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConverastionDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.AlertComingSoon;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponseToUi;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Fragments.DashboardFragment;
import com.example.chaya.bontact.Ui.Fragments.InboxFragment;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ServerCallResponseToUi {

    AgentDataManager agentDataManager;
    ConverastionDataManager converastionDataManager;
    ProgressBar progressBarCenter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        getSupportFragmentManager().beginTransaction().add(R.id.content_fragment, DashboardFragment.newInstance()).addToBackStack(null).commit();
        agentDataManager = new AgentDataManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_layout);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView loggedInAs = (TextView) header.findViewById(R.id.loggedInAsTxt);
        loggedInAs.append(" " + agentDataManager.getAgentName(this));
        SwitchCompat logOut= (SwitchCompat) header.findViewById(R.id.switch_logout);
        logOut.setOnCheckedChangeListener(logoutListener);
        progressBarCenter = (ProgressBar) findViewById(R.id.loading_center);

        String token = agentDataManager.getAgentToken(this);
        if (token != null) {
            converastionDataManager = new ConverastionDataManager(this);
            converastionDataManager.getFirstDataFromServer(this, token);
        }
    }

    public void setProgressBarCenterState(int state) {
        if (progressBarCenter == null)
            progressBarCenter = (ProgressBar) findViewById(R.id.loading_center);
        progressBarCenter.setVisibility(state);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } /*else {
            DashboardFragment dashboardFragment = (DashboardFragment) getSupportFragmentManager().findFragmentByTag("DASHBOARD_FRAGMENT");
            if (dashboardFragment != null && dashboardFragment.isVisible()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
                builder.setIcon(R.mipmap.bontact_launcher)
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to Exit the app?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                exitApp();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
            else
            {
               // super.onBackPressed();
            }
        }*/
    }

    private void exitApp() {
       super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if (ReplaceFragments(item.getItemId()) == false)
            return false;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean ReplaceFragments(int id) {
        if (id == R.id.nav_dashboard) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, DashboardFragment.newInstance(),"DASHBOARD_FRAGMENT").addToBackStack(null).commit();
            return true;
        } else if (id == R.id.nav_online_v || id == R.id.onlineVisitors_dashboard_layout) {
            AlertComingSoon.show(this);
            return true;
        } else if (id == R.id.nav_inbox || id == R.id.requests_dashboard_layout) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, InboxFragment.newInstance()).addToBackStack(null).commit();
            return true;
        } else if (id == R.id.nav_settings) {
            AlertComingSoon.show(this);
            return true;

        } else if (id == R.id.nav_exit) {
            AlertComingSoon.show(this);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        ReplaceFragments(v.getId());
    }

    @Override
    public void OnServerCallResponseToUi(boolean isSuccsed, final String response, ErrorType errorType, Class sender) {

        if (sender == InnerConversationDataManager.class) {
            if (isSuccsed == true) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MenuActivity.this, InnerConversationActivity.class);
                        Bundle b = new Bundle();
                        b.putInt(Contract.InnerConversation.COLUMN_ID_SURFUR, Integer.parseInt(response)); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                        setProgressBarCenterState(View.GONE);
                        startActivity(intent);

                    }
                });

            }
        }

    }

    CompoundButton.OnCheckedChangeListener logoutListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked==false)//user logged out
            {
                agentDataManager.logOut(MenuActivity.this);
                Intent intent= new Intent(MenuActivity.this,MainActivity.class);
                startActivity(intent);

            }

        }
    };

}


