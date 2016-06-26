package com.example.chaya.bontact.Ui.Activities;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.ConverastionDataManager;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Fragments.DashboardFragment;
import com.example.chaya.bontact.Ui.Fragments.InboxFragment;
import com.example.chaya.bontact.Ui.Fragments.OnlineVisitorsFragment;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {
AgentDataManager agentDataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReplaceFragments(R.id.nav_dashboard);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //replace to refreah
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        agentDataManager=new AgentDataManager();
        String token= agentDataManager.getAgentToken(this);
        if(token!=null)
        {
            ConverastionDataManager converastionDataManager=new ConverastionDataManager();
            converastionDataManager.getFirstDataFromServer(this,token);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



//    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

       if(ReplaceFragments(item.getItemId())==false)
        return false;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public boolean ReplaceFragments(int id)
    {
        if (id == R.id.nav_dashboard )
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, DashboardFragment.newInstance()).commit();
            return true;
        } else if (id == R.id.nav_online_v || id==R.id.onlineVisitors_btn_dashboard)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, OnlineVisitorsFragment.newInstance()).commit();
            return true;
        } else if (id == R.id.nav_inbox|| id==R.id.visitorsRequest_btn_dashboard)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, InboxFragment.newInstance()).commit();
            return true;
        } else if (id == R.id.nav_settings)
        {

        } else if (id == R.id.nav_exit) {

        }

        return  false;
    }

    @Override
    public void onClick(View v) {
        ReplaceFragments(v.getId());
    }
}

