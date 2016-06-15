package com.example.chaya.bontact;
import android.support.v4.app.Fragment;
import android.net.Uri;
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

import com.example.chaya.bontact.Fragments.DashboardFragment;
import com.example.chaya.bontact.Fragments.InboxFragment;
import com.example.chaya.bontact.Fragments.OnlineVisitorsFragment;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment=null;
        if (id == R.id.nav_dashboard)
        {
            fragment=DashboardFragment.newInstance();
        } else if (id == R.id.nav_online_v)
        {
            fragment=new OnlineVisitorsFragment();
        } else if (id == R.id.nav_inbox)
        {
            fragment=new InboxFragment();
        } else if (id == R.id.nav_settings)
        {

        } else if (id == R.id.nav_exit) {

        }
       if(ReplaceFragments(fragment)==false)
        return false;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public boolean ReplaceFragments(Fragment fragment)
    {
        if(fragment!=null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, fragment).commit();
            return true;
        }
        return  false;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment=null;
        switch(v.getId())
        {
            case R.id.onlineVisitors_btn_dashboard:
                fragment=new OnlineVisitorsFragment();
                break;
            case R.id.visitorsRequest_btn_dashboard:
                fragment=new InboxFragment();
        }
       ReplaceFragments(fragment);
    }
}

