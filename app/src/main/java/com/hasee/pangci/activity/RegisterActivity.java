package com.hasee.pangci.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hasee.pangci.common.CommonUtils;
import com.hasee.pangci.common.MessageEvent;
import com.hasee.pangci.R;
import com.hasee.pangci.bean.User;
import com.hasee.pangci.permission.PermissionListener;
import com.hasee.pangci.permission.PermissionManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.register_account_et)
    EditText mAccountEditText;
    @BindView(R.id.register_password_et)
    EditText mPasswordEditText;
    @BindView(R.id.register_inviter_et)
    EditText mInviterEditText;
    @BindView(R.id.register_password_again_et)
    EditText mPasswordAgainEt;
    @BindView(R.id.register_confirm_tv)
    TextView mConfirmRegisterTv;
    @BindView(R.id.register_tool_bar)
    Toolbar mToolbar;
    int[] headIcons = {R.drawable.ic_avatar1, R.drawable.ic_avatar2, R.drawable.ic_avatar3, R.drawable.ic_avatar4, R.drawable.ic_avatar5
            , R.drawable.ic_avatar6, R.drawable.ic_avatar7, R.drawable.ic_avatar8, R.drawable.ic_avatar9, R.drawable.ic_avatar10, R.drawable.ic_avatar11};
    private static final int REQUEST_CODE_READ_PHONE_STATE = 0;
    private PermissionManager helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mConfirmRegisterTv.setOnClickListener(this);
        mToolbar.setTitle("注册");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerAccount() {
        final String account = mAccountEditText.getText().toString().trim();
        final String password = mPasswordEditText.getText().toString().trim();
        final String inviter = mInviterEditText.getText().toString().trim();
        String againPwd = mPasswordAgainEt.getText().toString().trim();

        if (CommonUtils.checkStrIsNull(account, password, againPwd)) {
            Toast.makeText(this, "选项不能为空!", Toast.LENGTH_LONG).show();
        } else if (account.length() < 6 || password.length() < 6) {
            Toast.makeText(this, "长度不得小于6!", Toast.LENGTH_LONG).show();
        } else if (!password.equals(againPwd)) {
            Toast.makeText(this, "密码输入不一致!", Toast.LENGTH_LONG).show();
        } else if (!account.matches("[a-zA-Z]{1}[a-zA-Z0-9_]{1,15}")) {
            Toast.makeText(this, "账号由字母数字下划线组成且开头必须是字母，不能超过16位", Toast.LENGTH_LONG).show();
        } else if (!password.matches("[a-zA-Z0-9]{1,16}")) {
            Toast.makeText(this, "密码由字母和数字构成，不能超过16位", Toast.LENGTH_LONG).show();
        } else {
            //判断账号是否存在
            BmobQuery<User> bmobQuery = new BmobQuery<>();
            bmobQuery.addWhereEqualTo("userAccount", account);
            bmobQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e == null) {
                        //查询成功
                        if (list.size() != 0) {
                            Toast.makeText(RegisterActivity.this, "账号已存在!", Toast.LENGTH_LONG).show();
                        } else {
                            //查询邀请人是否存在
                            if (!TextUtils.isEmpty(inviter)) {
                                //邀请人不为空
                                BmobQuery<User> bmobQuerys = new BmobQuery<>();
                                bmobQuerys.addWhereEqualTo("userAccount", inviter);
                                bmobQuerys.findObjects(new FindListener<User>() {
                                    @Override
                                    public void done(List<User> list, BmobException e) {
                                        if (e == null) {
                                            //查询成功
                                            if (list.size() == 0) {
                                                Toast.makeText(RegisterActivity.this, "邀请人不存在!", Toast.LENGTH_LONG).show();
                                            } else {
                                                final BmobQuery<User> bmobQuery = new BmobQuery<>();
                                                bmobQuery.addWhereEqualTo("userAccount", inviter);
                                                bmobQuery.findObjects(new FindListener<User>() {
                                                    @Override
                                                    public void done(List<User> list, BmobException e) {
                                                        if (e == null) {
                                                            String imei = list.get(0).getUserIMEI();
                                                            //判断邀请人是不是之前自己注册的账号(根据IMEI值来判断)
                                                            //查询被邀请人的IMEI值
                                                            if (CommonUtils.getPhoneImei(RegisterActivity.this).equals(imei)) {
                                                                //邀请人是之前自己的账号
                                                                Toast.makeText(RegisterActivity.this, "邀请码不能是自己已注册的账号!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                //邀请人不是自己已注册的账号
                                                                //给邀请人加积分
                                                                updateAndQueryDb(inviter);
                                                                //新注册用户插入数据库
                                                                insertDataToServer(account, password, inviter);
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            //查询失败
                                            Toast.makeText(RegisterActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                //邀请人为空
                                //新注册用户插入数据库
                                insertDataToServer(account, password, inviter);
                            }
                        }
                    } else {
                        //查询失败
                        Toast.makeText(RegisterActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void queryImei(final String inviter, final String account, final String password) {

    }

    private void insertDataToServer(String account, String password, String inviter) {
        //给用户随机分配头像
        Random random = new Random();
        final User user = new User();
        user.setUserAccount(account);
        user.setUserPassword(password);
        user.setMemberLevel("青铜");
        user.setInviter(inviter);//邀请人 可为空
        user.setUserIntegral("20");//首次注册送积分20
        user.setUserHeadImg(headIcons[random.nextInt(12)]);//[0,12)之间
        //动态申请权限
        if (gradePermissionManager() == false) {
            return;
        }
//        user.setUserIMEI(CommonUtils.getPhoneImei(this));
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    EventBus.getDefault().post(new MessageEvent(user, "register"));
                    Toast.makeText(RegisterActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败,请重试!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateAndQueryDb(String userName) {
        //由于只能根据id去修改,先根据账号查 再修改
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("userAccount", userName);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    User user = list.get(0);
                    updateDb(user.getObjectId(), user.getUserIntegral());
                }
            }
        });
    }



    /**
     *  给邀请人添加积分
     * @param objectId
     * @param oldIntegral
     */
    private void updateDb(String objectId, String oldIntegral) {
        int oIntergral = Integer.parseInt(oldIntegral);
        User user = new User();
        final String tempIntegral = String.valueOf(oIntergral + 20);
        user.setUserIntegral(tempIntegral + "");
        user.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_confirm_tv:
                registerAccount();
                break;

            default:

                break;
        }
    }

    boolean temp;

    /**
     * 动态申请权限
     * @return
     */
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
                        Toast.makeText(RegisterActivity.this, "您已经拒绝,注册无法进行!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onShowRationale(String[] permissions) {
                        helper.setIsPositive(true);
                        Toast.makeText(RegisterActivity.this, "用户注册需要,请在设置中开启此权限!", Toast.LENGTH_SHORT).show();
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
}
