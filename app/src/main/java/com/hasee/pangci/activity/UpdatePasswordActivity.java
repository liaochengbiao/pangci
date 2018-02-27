package com.hasee.pangci.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hasee.pangci.R;
import com.hasee.pangci.common.CommonUtils;
import com.hasee.pangci.bean.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class UpdatePasswordActivity extends AppCompatActivity {
    @BindView(R.id.update_tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.update_account_et)
    EditText mAccountEditText;
    @BindView(R.id.update_password_et)
    EditText mPasswordEditText;
    @BindView(R.id.update_password_again_et)
    EditText mAgainPasswordEt;
    private String objectId = null; //需要修改账号的id

    @OnClick(R.id.update_confirm_tv)
    void click() {
        updatePassword();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        ButterKnife.bind(this);
        mToolbar.setTitle("重置密码");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void updatePassword() {

        String account = mAccountEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        String againPassword = mAgainPasswordEt.getText().toString().trim();
        if (CommonUtils.checkStrIsNull(account, password, againPassword)) {
            Toast.makeText(this, "选项不能为空!", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.equals(againPassword) || password.length() < 6) {
            Toast.makeText(this, "密码输入不一致且长度得大于6位数!", Toast.LENGTH_SHORT).show();
            return;
        }else if (!password.matches("[a-zA-Z0-9]{1,16}")) {
            Toast.makeText(this, "密码由字母和数字构成，不能超过16位", Toast.LENGTH_LONG).show();
            return;
        }
        //由于只能通过objectId修改数据 先根据用户名查询到id
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("userAccount", account);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        //注册账号的时候已经限制不重名 所以有且只有一个直接取第一个
                        User user = list.get(0);
                        objectId = user.getObjectId();
                    } else {
                        Toast.makeText(UpdatePasswordActivity.this, "账号不存在!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdatePasswordActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //修改操作
        if (objectId == null) {return;}
        User user = new User();
        user.setUserPassword(password);
        user.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(UpdatePasswordActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdatePasswordActivity.this, "修改失败,请重试!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
