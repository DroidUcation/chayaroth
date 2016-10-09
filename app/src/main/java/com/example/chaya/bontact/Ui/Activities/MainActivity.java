package com.example.chaya.bontact.Ui.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Helpers.AvatarHelper;
import com.example.chaya.bontact.Helpers.CircleTransform;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Fragments.DashboardFragment;
import com.example.chaya.bontact.Ui.Fragments.InboxFragment;
import com.example.chaya.bontact.Ui.Fragments.OnlineVisitorsFragment;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    ProgressBar progressBarCenter;
    CircularImageView agentPicture;
    FrameLayout dashboard;
    int resCurrentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_wrraper);

        setProgressBarCenterState(View.VISIBLE);
        int resFirstTabTitle = 0;
        if (savedInstanceState != null && savedInstanceState.getInt(getString(R.string.first_tab_title_key)) != 0) {
            resFirstTabTitle = savedInstanceState.getInt(getString(R.string.first_tab_title_key));
            resCurrentTitle = resFirstTabTitle;
        }
        if (resFirstTabTitle == 0) {
            Bundle args = getIntent().getExtras();
            if (args != null) {
                resFirstTabTitle = args.getInt(getString(R.string.first_tab_title_key));
                resCurrentTitle = resFirstTabTitle;
            }
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager, resFirstTabTitle);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));

        initMenu();
        dashboard = (FrameLayout) findViewById(R.id.dashboard_fragment);
        getSupportFragmentManager().beginTransaction().add(R.id.dashboard_fragment, DashboardFragment.newInstance()).addToBackStack(null).commit();
        replaceViews(resCurrentTitle);

    }

    protected void onSaveInstanceState(Bundle onOrientChange) {
        super.onSaveInstanceState(onOrientChange);
        onOrientChange.putInt(getString(R.string.first_tab_title_key), resCurrentTitle);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void initMenu() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        agentPicture = (CircularImageView) header.findViewById(R.id.agent_pic);
        TextView loggedInAs = (TextView) header.findViewById(R.id.loggedInAsTxt);
        if (loggedInAs != null)
            loggedInAs.append(" " + AgentDataManager.getAgentInstance().getName());
        String avatar = AgentDataManager.getAgentAvatarUrl();
        if (AgentDataManager.getAgentInstance().getRep().default_avatar) {
            agentPicture.setBackground(getResources().getDrawable(R.drawable.avatar_bg));
            agentPicture.setImageResource(R.drawable.default_avatar);

        } else if (avatar != null) {
            agentPicture.setImageBitmap(AvatarHelper.decodeAvatarBase64(avatar));
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int resTitle = getTitleForNavItem(item.getItemId());
        if (replaceViews(resTitle) == false)
            return false;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private int getTitleForNavItem(int navId) {
        switch (navId) {
            case R.id.nav_online_v:
                return R.string.onlinevisitors_title;
            case R.id.nav_inbox:
                return R.string.inbox_title;
            case R.id.nav_settings:
                return R.string.settings_title;
            case R.id.nav_exit:
                return R.string.exit_title;
            case R.id.nav_dashboard:
                return R.string.dashboard_title;
        }
        return R.string.dashboard_title;
    }

    public boolean replaceViews(int resTitle) {
        if (resTitle != 0)
            setTitle(getTitleToDisplay(resTitle));

        resCurrentTitle = resTitle;
        Intent intent;
        if (resTitle == R.string.dashboard_title) {
            dashboard.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            return true;
        } else {
            if (resTitle == R.string.onlinevisitors_title || resTitle == R.string.inbox_title) {
                viewPager.setCurrentItem(adapter.getPosition(getResources().getString(resTitle)), true);
                dashboard.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                return true;
            } else if (resTitle == R.string.settings_title) {
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            } else if (resTitle == R.string.exit_title) {
                finish();
                return true;
            }
        }
        return false;
    }

    private int getTitleToDisplay(int resTitle) {
        switch (resTitle) {
            case R.string.dashboard_title:
                return resTitle;
            case R.string.onlinevisitors_title:
            case R.string.inbox_title:
                return R.string.app_name;
        }
        return R.string.dashboard_title;
    }

    private void setupViewPager(ViewPager viewPager, int resFirstTabTitle) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new InboxFragment(), getResources().getString(R.string.inbox_title));
        adapter.addFragment(new OnlineVisitorsFragment(), getResources().getString(R.string.onlinevisitors_title));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(adapter.getPosition(getString(resFirstTabTitle)), true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (dashboard.getVisibility() != View.VISIBLE)//dashboard is not active
                replaceViews(R.string.dashboard_title);
            else
                finish();


        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.requests_dashboard_layout) {
            replaceViews(R.string.inbox_title);
            return;
        }
        if (v.getId() == R.id.onlineVisitors_dashboard_layout) {
            replaceViews(R.string.onlinevisitors_title);
            return;
        }
        if (v.getId() == R.id.inbox_fab) {
            viewPager.setCurrentItem(adapter.getPosition(getResources().getString(R.string.onlinevisitors_title)), true);
            return;
        }
    }

    public void setProgressBarCenterState(int state) {

        if (progressBarCenter == null)
            progressBarCenter = (ProgressBar) findViewById(R.id.progress_bar_center);
        progressBarCenter.setVisibility(state);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public int getPosition(String pageTitle) {

            return mFragmentTitleList.indexOf(pageTitle);
        }

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_header, null);
            TextView tv = (TextView) v.findViewById(R.id.tab_title);
            tv.setText(getPageTitle(position));
           /* ImageView img = (ImageView) v.findViewById(R.id.imgView);
            img.setImageResource(imageResId[position]);*/
            return v;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}
