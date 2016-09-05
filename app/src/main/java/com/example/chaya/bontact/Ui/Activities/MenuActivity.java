/*
package com.example.chaya.bontact.Ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Helpers.CircleTransform;
import com.example.chaya.bontact.R;
import com.squareup.picasso.Picasso;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    AgentDataManager agentDataManager;
    ImageView agentPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        //getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, DashboardFragment.newInstance()).commit();

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
        agentPicture = (ImageView) header.findViewById(R.id.agent_pic);
        TextView loggedInAs = (TextView) header.findViewById(R.id.loggedInAsTxt);
        if (loggedInAs != null)
            loggedInAs.append(" " + agentDataManager.getAgentName(this));

        String avatar = agentDataManager.getAgentAvatarUrl();
        if (avatar != null) {
            avatar = avatar.replace("https", "http");
            Picasso.with(this)
                    .load(avatar)
                    .placeholder(R.mipmap.bontact_launcher) // optional
                    .transform(new CircleTransform())
                    .error(R.mipmap.bontact_launcher)         // optional
                    .into(agentPicture);
        }
        //get data to inbox
      */
/*  String token = agentDataManager.getAgentToken(this);
        if (token != null) {
            conversationDataManager = new ConversationDataManager(this);
            conversationDataManager.getConverationsUnreadCount(this);
            conversationDataManager.getFirstDataFromServer(this, token);
        }*//*

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }*/
/* else {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }*//*

        super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if (ReplaceViews(item.getItemId()) == false)
            return false;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean ReplaceViews(int id) {
        Intent intent;
        if (id == R.id.nav_dashboard) {
            //setProgressBarCenterState(View.GONE);
            //  getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, DashboardFragment.newInstance()).addToBackStack(null).commit();
            return true;
        } else if (id == R.id.nav_online_v || id == R.id.onlineVisitors_dashboard_layout || id == R.id.nav_inbox || id == R.id.requests_dashboard_layout) {
            intent = new Intent(this, MainActivity.class);
            if (id == R.id.nav_online_v || id == R.id.onlineVisitors_dashboard_layout)
                intent.putExtra(getString(R.string.first_tab_title_key), R.string.onlinevisitors_title);
            else
                intent.putExtra(getString(R.string.first_tab_title_key), R.string.inbox_title);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_settings) {
            // getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, new SettingsFragment()).addToBackStack(null).commit();
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_exit) {
            finish();
            return true;
        }
        return false;
    }
    @Override
    public void onClick(View v) {

        ReplaceViews(v.getId());

    }

*/
/*

    public boolean replaceViews(int id) {
        Intent intent;
        if (id == R.id.nav_dashboard) {
            //startActivity(new Intent(this, MenuActivity.class));
            dashboard.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            return true;
        } else {
            dashboard.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            if (id == R.id.nav_online_v) {
                viewPager.setCurrentItem(adapter.getPosition(getResources().getString(R.string.onlinevisitors_title)), true);
                return true;
            } else if (id == R.id.nav_inbox) {
                viewPager.setCurrentItem(adapter.getPosition(getResources().getString(R.string.inbox_title)), true);
                return true;
            } else if (id == R.id.nav_settings) {
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_exit) {
                finish();
                return true;
            }
        }
        return false;
    }
 *//*

}


*/
