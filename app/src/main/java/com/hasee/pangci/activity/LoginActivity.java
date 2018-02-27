package com.hasee.pangci.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hasee.pangci.R;
import com.hasee.pangci.bean.User;
import com.hasee.pangci.common.MessageEvent;
import com.hasee.pangci.permission.PermissionListener;
import com.hasee.pangci.permission.PermissionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.login_confirm_tv)
    TextView mLoginTextView;
    @BindView(R.id.login_account_et)
    EditText mAccountEditText;
    @BindView(R.id.login_password_et)
    EditText mPassWordEditText;
    private ProgressDialog mProgressDialog;
    @BindView(R.id.login_tool_bar)
    Toolbar mToolbar;
    private static final int REQUEST_CODE_READ_PHONE_STATE = 0;
    private PermissionManager helper;
    @BindView(R.id.login_not_account_tv)
    TextView mNotAccountTextView;
    @BindView(R.id.imageView_left)
    ImageView mImageViewLeft;
    @BindView(R.id.imageView_right)
    ImageView mImageViewRight;

    @OnFocusChange(R.id.login_password_et) void onChange(View v, boolean hasFocus){
        if(hasFocus){
            mImageViewLeft.setImageResource(R.drawable.ic_22_hide);
            mImageViewRight.setImageResource(R.drawable.ic_33_hide);
        }else {
            mImageViewLeft.setImageResource(R.drawable.ic_22);
            mImageViewRight.setImageResource(R.drawable.ic_33);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mLoginTextView.setOnClickListener(this);
        mNotAccountTextView.setOnClickListener(this);
        mToolbar.setTitle("登录");
        mToolbar.inflateMenu(R.menu.login_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(LoginActivity.this, UpdatePasswordActivity.class);
                startActivity(intent);
                return false;
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_confirm_tv:
                //登录成功跳主页
                checkIsExitUser();
                break;

            case R.id.login_not_account_tv:
                //没有账号跳注册
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
                break;

            default:

                break;
        }
    }

    private void checkIsExitUser() {
        //判断是否存在此用户
        String account = mAccountEditText.getText().toString();
        String password = mPassWordEditText.getText().toString();

        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("userAccount", account);
        bmobQuery.addWhereEqualTo("userPassword", password);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        //数据唯一 注册做了限制
                        User userTemp = list.get(0);
                        //判断账号是否在本机登录
//                        if (gradePermissionManager() == false) {return;}
//                        if (!userTemp.getUserIMEI().equals(CommonUtils.getPhoneImei(LoginActivity.this))) {
//                            Toast.makeText(LoginActivity.this, "账号不能在本机登录!", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
                        //保存到sp 下次进来直接登录
                        SharedPreferences login_info = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
                        SharedPreferences.Editor edit = login_info.edit();
                        //查下来得区分是否是充值会员 充值会员有时间
                        if ("青铜".equals(userTemp.getMemberLevel())) {
                            //青铜会员
                            //存进sp
                            edit.putInt("headImg", userTemp.getUserHeadImg());
                            edit.putString("account", userTemp.getUserAccount());
                            edit.putString("password", userTemp.getUserPassword());
                            edit.putString("memberLevel", userTemp.getMemberLevel());
                            edit.putString("integral", userTemp.getUserIntegral());
                            //存储登录状态
                            edit.putBoolean("isLogin", true);
                            edit.apply();
                        } else {
                            //黄金 白金 钻石会员
                            //存进sp
                            edit.putString("integral", userTemp.getUserIntegral());//积分
                            edit.putInt("headImg", userTemp.getUserHeadImg());
                            edit.putString("account", userTemp.getUserAccount());
                            edit.putString("password", userTemp.getUserPassword());
                            edit.putString("memberLevel", userTemp.getMemberLevel());
                            edit.putString("memberStartDate", userTemp.getMemberStartDate().getDate());
                            edit.putString("memberEndDate", userTemp.getMemberEndDate().getDate());
                            //存储登录状态
                            edit.putBoolean("isLogin", true);
                            edit.apply();
                        }
                        Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new MessageEvent(userTemp, "login"));
                        //主页面
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "账号或密码输入错误!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 1)
    public void handleEvent(MessageEvent event) {
        //区分哪里发来的事件
        if (!"register".equals(event.getFlag())) {
            return;
        }
        EventBus.getDefault().cancelEventDelivery(event);
        //获取来自注册页面的数据
        //刚注册的用户
        User userNew = event.getUser();
        mAccountEditText.setText(userNew.getUserAccount());
        mPassWordEditText.setText(userNew.getUserPassword());
        checkIsExitUser();
    }

    boolean temp;
    
    //动态申请权限
    public boolean gradePermissionManager() {
        helper = PermissionManager.with(this)
                .addRequestCode(REQUEST_CODE_READ_PHONE_STATE)
                .permissions(Manifest.permission.READ_PHONE_STATE)
                .setPermissionsListener(new PermissionListener() {
                    @Override
                    public void onGranted() {
                        temp = true;
                    }

                    @Override
                    public void onDenied() {
                        temp = false;
                        Toast.makeText(LoginActivity.this, "您已经拒绝,无法完成登录!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onShowRationale(String[] permissions) {
                        helper.setIsPositive(true);
                        Toast.makeText(LoginActivity.this, "用户登录需要,请在设置中开启此权限!", Toast.LENGTH_SHORT).show();
                    }
                })
                .request();
        return temp;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_READ_PHONE_STATE:
                helper.onPermissionResult(permissions, grantResults);
                break;

            default:

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
