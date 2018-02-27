package com.hasee.pangci.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hasee.pangci.R;
import com.hasee.pangci.X5.WebviewVideoActivity;
import com.hasee.pangci.adapter.MyFragmentPagerAdapter;
import com.hasee.pangci.bean.Notice;
import com.hasee.pangci.bean.User;
import com.hasee.pangci.common.DataCleanManagerUtils;
import com.hasee.pangci.common.DateFormat;
import com.hasee.pangci.common.MessageEvent;
import com.hasee.pangci.common.MessageEvent2;
import com.hasee.pangci.common.MessageEventNotice;
import com.hasee.pangci.fragment.AnimeFragment;
import com.hasee.pangci.fragment.MemberFragment;
import com.hasee.pangci.fragment.MovieFragment;
import com.hasee.pangci.fragment.NetdiskFragment;
import com.hasee.pangci.fragment.RecommendFragment;
import com.hasee.pangci.widget.FadeInTextView;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    @BindView(R.id.main_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.main_tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.main_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.main_navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.main_drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.main_fab)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.tv_marquee)
    TextView mNoticeTextView;
    @BindView(R.id.main_fab2)
    FloatingActionButton mFloatingActionButton2;

    private MemberFragment memberFragment = new MemberFragment();
    private RecommendFragment recommendFragment = new RecommendFragment();
    private MovieFragment movieFragment = new MovieFragment();
    private AnimeFragment animeFragment = new AnimeFragment();
    private NetdiskFragment netdiskFragment = new NetdiskFragment();
    private String[] tabTitles = {"推荐", "影视", "腐漫", "网盘", "VIP专区"};
    private Fragment[] fragments = {recommendFragment, movieFragment, animeFragment, netdiskFragment, memberFragment};
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private View mNavigationMemberInfoLl;
    private TextView mNavigationAccountTv;
    private TextView mNavigationMemberLevelTv;
    private TextView mNavigationResidueTv;
    private CircleImageView mHeadCIV;
    private User mUserInfo = new User();//用户信息
    private boolean isLogin;//判断用户是否登录
    private SharedPreferences mLogin_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//禁止截屏
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        initData();
        checkIsLogin();//判断之前是否登录，如果登录直接进
    }

    public void initView() {

        mNavigationView.setNavigationItemSelectedListener(this);
        mFloatingActionButton.setOnClickListener(this);
        //初始化公告栏
        Notice notice = DataSupport.findLast(Notice.class);
        if (notice == null) {
            mNoticeTextView.setText(getString(R.string.notice));
        } else {
            mNoticeTextView.setText(notice.getNotice());
        }
        mNoticeTextView.setOnClickListener(this);


        mFloatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivityRule();
            }
        });
    }

    public void initData() {
        mToolbar.setTitle("胖次");
        for (int i = 0; i < tabTitles.length; i++) {
            fragmentArrayList.add(fragments[i]);
        }
        //关联彼此
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        mViewPager.setAdapter(myFragmentPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        //初始化数据
        for (int i = 0; i < tabTitles.length; i++) {
            mTabLayout.getTabAt(i).setText(tabTitles[i]);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //初始化导航栏的控件
        View headerView = mNavigationView.getHeaderView(0);
        mNavigationMemberInfoLl = headerView.findViewById(R.id.navigation_member_info_ll);//会员信息布局
        mNavigationAccountTv = (TextView) headerView.findViewById(R.id.navigation_account_tv);
        mNavigationAccountTv.setOnClickListener(this);
        mNavigationMemberLevelTv = (TextView) headerView.findViewById(R.id.navigation_member_level_tv);//会员等级
        //会员剩余天数  ////////
        mNavigationResidueTv = (TextView) headerView.findViewById(R.id.navigation_residue_tv);
        mHeadCIV = (CircleImageView) headerView.findViewById(R.id.navigation_header_icon_civ);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_menu_item_info:
                if (mLogin_info != null) {
                    boolean isLogin = mLogin_info.getBoolean("isLogin", false);
                    if (isLogin) {
                        Intent intents = new Intent(MainActivity.this, NotificationActivity.class);
                        intents.setFlags(0);
                        startActivity(intents);
                    } else {
                        Toast.makeText(this, "您暂未登录!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.navigation_menu_item_cache:
                try {
                    DataCleanManagerUtils.clearAllCache(this);
                    Toast.makeText(MainActivity.this, "缓存清除成功!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.navigation_menu_item_exit:
                if (mLogin_info.getBoolean("isLogin", false)) {
                    //清除账号缓存信息
                    DataCleanManagerUtils.cleanSharedPreference(this, "LOGIN_INFO");
                    //清除手势锁的缓存信息
                    DataCleanManagerUtils.cleanSharedPreference(this, "LOCK_INFO");

                    mNavigationMemberInfoLl.setVisibility(View.GONE);//隐藏会员信息布局
                    mNavigationAccountTv.setText("点击登录");
                    mHeadCIV.setImageResource(R.drawable.normal_login);
                    isLogin = false;
                    if (mLogin_info != null) {//注销--》更新登录状态
                        SharedPreferences.Editor edit = mLogin_info.edit();
                        edit.putBoolean("isLogin", false);
                        edit.apply();
                    }
                }
                break;

            case R.id.navigation_menu_item_flock:
                if (!mLogin_info.getBoolean("isLogin", false)) {
                    Toast.makeText(this, "您暂未登录!", Toast.LENGTH_SHORT).show();
                } else if ("青铜".equals(mLogin_info.getString("memberLevel", "青铜"))) {
                    Toast.makeText(this, "请先升级会员", Toast.LENGTH_SHORT).show();
                } else if (!"钻石".equals(mLogin_info.getString("memberLevel", "青铜"))) {
                    Toast.makeText(this, "钻石会员才能加入云群!", Toast.LENGTH_LONG).show();
                } else {
                    //弹窗引导去反馈
                    showDialog();
                }
                break;

            case R.id.navigation_menu_item_version:
                Beta.checkUpgrade();
                break;

            case R.id.navigation_menu_item_member:
                Intent intent = new Intent(MainActivity.this, MemberCenterActivity.class);
                if (!isLogin) {
                    //未登录
                    intent.setFlags(0);//未登录
                    Toast.makeText(this, "您暂未登录!", Toast.LENGTH_SHORT).show();
                } else {
                    intent.setFlags(1);//登录
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", mUserInfo);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                break;

            case R.id.navigation_menu_item_integral:
                //积分
                if (mLogin_info != null) {
                    User user = new User();
                    boolean isLogin = mLogin_info.getBoolean("isLogin", false);
                    user.setUserIntegral(mLogin_info.getString("integral", ""));
                    user.setUserAccount(mLogin_info.getString("account", ""));
                    user.setUserHeadImg(mLogin_info.getInt("headImg", R.drawable.normal_login));
                    if (isLogin) {
                        Intent intents = new Intent(MainActivity.this, IntegralActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);
                        intents.putExtras(bundle);
                        startActivity(intents);
                    } else {
                        Toast.makeText(this, "您暂未登录!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.navigation_menu_item_lock:
                if (mLogin_info != null) {
                    boolean isLogin = mLogin_info.getBoolean("isLogin", false);
                    if (isLogin) {
                        Intent intent1 = new Intent(MainActivity.this, LockActivity.class);
                        startActivity(intent1);
                    } else {
                        Toast.makeText(this, "您暂未登录!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:

                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_account_tv:
                if ("点击登录".equals(mNavigationAccountTv.getText().toString())) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();
                } else {
                    Toast.makeText(this, "您已经登录!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.main_fab:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.tv_marquee:
                Intent intent = new Intent(MainActivity.this,MemberCenterActivity.class);
                startActivity(intent);
                break;

            default:

                break;
        }
    }

    /**
     * eventbus回调处理函数
     * @param event event对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)//默认优先级为0
    public void handleEvent(MessageEvent event) {
        //区分哪里发来的事件
        if (!"login".equals(event.getFlag())) {
            return;
        }
        User user = event.getUser();
        mUserInfo = user;
        isLogin = mLogin_info.getBoolean("isLogin", false);
        mNavigationMemberInfoLl.setVisibility(View.VISIBLE);
        if ("青铜".equals(user.getMemberLevel())) {
            //普通会员
            mHeadCIV.setImageResource(user.getUserHeadImg());
            mNavigationAccountTv.setText(user.getUserAccount());
            mNavigationMemberLevelTv.setText("会员等级:" + user.getMemberLevel());
            mNavigationResidueTv.setVisibility(View.GONE);
        } else {
            //充值会员
            mHeadCIV.setImageResource(user.getUserHeadImg());
            mNavigationAccountTv.setText(user.getUserAccount());
            mNavigationMemberLevelTv.setText("会员等级:" + user.getMemberLevel());
            mNavigationResidueTv.setVisibility(View.VISIBLE);
            int residueDays = DateFormat.differentDaysByMillisecond(getCurrentDate(), user.getMemberEndDate().getDate());
            if (residueDays <= 0) {
                mNavigationResidueTv.setText("您的会员已到期");
                SharedPreferences.Editor edit = mLogin_info.edit();
                edit.putBoolean("isExpire", true);
                edit.apply();
            } else {
                mNavigationResidueTv.setText("会员剩余天数:" + residueDays + "天");
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(MessageEvent2 event2) {
        //存到本地数据库
        Notice notice = new Notice();
        notice.setNotice(event2.getNotice());
        notice.save();
        mNoticeTextView.setText(event2.getNotice());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(MessageEventNotice messageEventNotice) {
        showPopWindow(messageEventNotice.getContent());
    }

    private void showPopWindow(String content) {
        View view = LayoutInflater.from(this).inflate(R.layout.notice_layout, mDrawerLayout, false);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置外部点击消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(mDrawerLayout, Gravity.BOTTOM, 0, 0);

        FadeInTextView fadeInTextView = (FadeInTextView) view.findViewById(R.id.notice_fade_in_tv);
        fadeInTextView.setTextString(content)
                .startFadeInAnimation()
                .setTextAnimationListener(new FadeInTextView.TextAnimationListener() {
                    @Override
                    public void animationFinish() {
                        Toast.makeText(MainActivity.this, "完毕!", Toast.LENGTH_SHORT).show();
                    }
                });

        view.findViewById(R.id.notice_action_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
    }

    private void checkIsLogin() {
        mLogin_info = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        isLogin = mLogin_info.getBoolean("isLogin", false);
        if (!"".equals(mLogin_info.getString("account", ""))) {
            //说明里面有记录
            //判断会员等级
            mNavigationMemberInfoLl.setVisibility(View.VISIBLE);//显示布局会员信息布局
            if ("青铜".equals(mLogin_info.getString("memberLevel", "青铜"))) {
                //普通会员
                mHeadCIV.setImageResource(mLogin_info.getInt("headImg", R.drawable.normal_login));
                mNavigationAccountTv.setText(mLogin_info.getString("account", ""));
                mNavigationMemberLevelTv.setText("会员等级:" + mLogin_info.getString("memberLevel", "青铜"));
                mNavigationResidueTv.setVisibility(View.GONE);
                mUserInfo.setUserHeadImg(mLogin_info.getInt("headImg", R.drawable.normal_login));
                mUserInfo.setMemberLevel(mLogin_info.getString("memberLevel", "青铜"));
                mUserInfo.setUserAccount(mLogin_info.getString("account", ""));
            } else {
                //充值会员
                mHeadCIV.setImageResource(mLogin_info.getInt("headImg", R.drawable.normal_login));
                mNavigationAccountTv.setText(mLogin_info.getString("account", ""));
                mNavigationMemberLevelTv.setText("会员等级:" + mLogin_info.getString("memberLevel", "青铜"));
                int residueDays = DateFormat.differentDaysByMillisecond(getCurrentDate(), mLogin_info.getString("memberEndDate", ""));
                if (residueDays <= 0) {
                    mNavigationResidueTv.setText("您的会员已到期");
                    SharedPreferences.Editor edit = mLogin_info.edit();
                    edit.putBoolean("isExpire", true);
                    edit.apply();
                } else {
                    mNavigationResidueTv.setText("会员剩余天数:" + residueDays + "天");
                }
                mUserInfo.setUserHeadImg(mLogin_info.getInt("headImg", R.drawable.normal_login));
                mUserInfo.setMemberLevel(mLogin_info.getString("memberLevel", "青铜"));
                mUserInfo.setUserAccount(mLogin_info.getString("account", ""));
            }
        }
    }

    private String getCurrentDate() {
        //获取当前时间 扣除会员天数
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    private long tempTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //先关闭侧滑
            if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawers();
                return false;
            }
            if (System.currentTimeMillis() - tempTime > 2000) {
                Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                tempTime = System.currentTimeMillis();
                return false;
            } else {
                finish();
                System.exit(0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册eventBus
        EventBus.getDefault().unregister(this);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        /**
         * 设置内容区域为自定义View
         */
        LinearLayout inflate_dialog = (LinearLayout) getLayoutInflater().inflate(R.layout.join_netdisk_dialog, null);
        builder.setView(inflate_dialog);

        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView tv_title = (TextView) inflate_dialog.findViewById(R.id.tv_title);
        tv_title.setText("加入云群");
        TextView tvConfirm = (TextView) inflate_dialog.findViewById(R.id.tv_comfirm);
        tvConfirm.setText("去加群");
        TextView tvCancel = (TextView) inflate_dialog.findViewById(R.id.tv_cancel);
        tvCancel.setText("再看看");
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                intent.setFlags(1);
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }

/*    private void showDialogs(String link) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ShowDialog);
        *//**
     * 设置内容区域为自定义View
     *//*
        View inflate_dialog = getLayoutInflater().inflate(R.layout.action_layout, null);
        builder.setView(inflate_dialog);

        final AlertDialog dialog = builder.create();
        dialog.show();

        ImageView actionImageView = (ImageView) inflate_dialog.findViewById(R.id.iv_action);
        Glide.with(this).load(link).error(R.drawable.aaa).into(actionImageView);
        actionImageView.setImageResource(R.drawable.ic_upper_loading_failed);


        ImageView deleteImageView = (ImageView) inflate_dialog.findViewById(R.id.iv_delete);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }*/
    private void showActivityRule( ) {
        View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_activity_rules, null);
        final android.app.AlertDialog ativityRuleDialog = showBackTransparent(MainActivity.this, inflate);
        ativityRuleDialog.setCancelable(true);
        Window window = ativityRuleDialog.getWindow();
        window.setGravity(Gravity.CENTER);
//        TextView tv_content = (TextView) ativityRuleDialog.findViewById(R.id.tv_content);
        TextView tv_know = (TextView) ativityRuleDialog.findViewById(R.id.tv_know);
//        tv_content.setText(stringExtra);
        tv_know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ativityRuleDialog.dismiss();
            }
        });
    }
    private android.app.AlertDialog showBackTransparent(Context context, View view) {
        final android.app.AlertDialog dialog_backTransparent = new android.app.AlertDialog.Builder(context).create();
        dialog_backTransparent.setCancelable(false);
        dialog_backTransparent.show();
        Window window_backTransparent = dialog_backTransparent.getWindow();
        window_backTransparent.setContentView(view);
//        WindowManager.LayoutParams params = window_backTransparent.getAttributes();
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
//        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(Color.parseColor("#00000000"));
//        window_backTransparent.setAttributes(params);
        window_backTransparent.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window_backTransparent.setBackgroundDrawable(colorDrawable);
        return dialog_backTransparent;
    }
}
