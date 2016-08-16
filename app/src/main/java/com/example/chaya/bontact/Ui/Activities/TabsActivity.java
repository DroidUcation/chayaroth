package com.example.chaya.bontact.Ui.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        int resFirstTabTitle = R.string.inbox_title;
        Bundle args = getIntent().getExtras();
        if (args != null) {
            resFirstTabTitle = args.getInt(getString(R.string.first_tab_title_key));
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager, resFirstTabTitle);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.purple));
        inbox_fab = (FloatingActionButton) findViewById(R.id.inbox_fab);
        inbox_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(adapter.getPosition(getResources().getString(R.string.onlinevisitors_title)), true);
            }
        });

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

}
