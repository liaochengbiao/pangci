package com.hasee.pangci.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hasee.pangci.R;
import com.hasee.pangci.adapter.MyFragmentPagerAdapter;
import com.hasee.pangci.fragment.FeedBackFragment;
import com.hasee.pangci.fragment.NotificationFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity {
    @BindView(R.id.notification_tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.notification_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.notification_view_pager)
    ViewPager mViewPager;
    private FeedBackFragment feedBackFragment = new FeedBackFragment();
    private NotificationFragment notificationFragment = new NotificationFragment();
    private Fragment[] fragments = {notificationFragment, feedBackFragment};
    private String[] titles = {"系统消息", "帮助与反馈"};
    private ArrayList<Fragment> mFragmentArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        mToolbar.setTitle("我的消息");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //fragment 数据源
        for (int i = 0; i < fragments.length; i++) {
            mFragmentArrayList.add(fragments[i]);
        }

        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentArrayList);
        mViewPager.setAdapter(myFragmentPagerAdapter);
        //关联彼此
        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < titles.length; i++) {
            mTabLayout.getTabAt(i).setText(titles[i]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int flags = getIntent().getFlags();
        switch (flags) {
            case 0:
                mViewPager.setCurrentItem(0);
                break;

            case 1:
                mViewPager.setCurrentItem(1);
                break;

            default:
                break;
        }
    }
}
