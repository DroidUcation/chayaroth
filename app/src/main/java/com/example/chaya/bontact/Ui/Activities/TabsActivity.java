package com.example.chaya.bontact.Ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Fragments.InboxFragment;
import com.example.chaya.bontact.Ui.Fragments.OnlineVisitorsFragment;

public class TabsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton inbox_fab;
    private ViewPagerAdapter adapter;
    ProgressBar progressBarCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        setProgressBarCenterState(View.VISIBLE);
        int resFirstTabTitle = R.string.inbox_title;
        Bundle args = getIntent().getExtras();
        if (args != null) {
            resFirstTabTitle = args.getInt(getString(R.string.first_tab_title_key));
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager, resFirstTabTitle);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
        //tabLayout.setBackgroundColor(getResources().getColor(R.color.purple));
        inbox_fab = (FloatingActionButton) findViewById(R.id.inbox_fab);
        if (resFirstTabTitle == R.string.inbox_title)
            inbox_fab.setVisibility(View.VISIBLE);
        else
            inbox_fab.setVisibility(View.GONE);
        inbox_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(adapter.getPosition(getResources().getString(R.string.onlinevisitors_title)), true);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       /* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);*/
        return super.onCreateOptionsMenu(menu);
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
                if (adapter.getPageTitle(position) == getResources().getString(R.string.inbox_title))
                    inbox_fab.setVisibility(View.VISIBLE);
                else
                    inbox_fab.setVisibility(View.GONE);
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
          /*  case R.id.settings_action:
                Intent intent =new Intent(this,MenuActivity.class);

                return true;*/
        }
        return true;
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

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void setProgressBarCenterState(int state) {

        if (progressBarCenter == null)
            progressBarCenter = (ProgressBar) findViewById(R.id.progress_bar_center);
        progressBarCenter.setVisibility(state);
    }


}
