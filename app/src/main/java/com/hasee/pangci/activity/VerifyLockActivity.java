package com.hasee.pangci.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hasee.pangci.R;
import com.hasee.pangci.widget.GraphicLockView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerifyLockActivity extends AppCompatActivity {
    @BindView(R.id.verify_agl_gl_lock)
    GraphicLockView mGraphicLockView;
    private String mAccount;
    private String mPassword;

    @OnClick(R.id.verify_forget_pwd_tv)
    void onClick() {
        //弹窗提示
        showDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_verify_lock);
        ButterKnife.bind(this);

        //获取存取的手势锁信息
        SharedPreferences lock_info = getSharedPreferences("LOCK_INFO", MODE_PRIVATE);
        final String lockPwd = lock_info.getString("lockPwd", "");

        //获取存取的登录信息
        SharedPreferences login_info = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        mAccount = login_info.getString("account", "");
        mPassword = login_info.getString("password", "");

        mGraphicLockView.setOnGraphicLockListener(new GraphicLockView.OnGraphicLockListener() {
            @Override
            public void setPwdSuccess(String password) {
                if (lockPwd.equals(password)) {
                    Intent intent = new Intent(VerifyLockActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VerifyLockActivity.this, "手势密码有误!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void setPwdFailure() {

            }
        });
    }

    private void showDialog() {

        final AlertDialog hintDialog = new AlertDialog.Builder(VerifyLockActivity.this, R.style.ShowDialog).create();
        View inflate_dialog = LayoutInflater.from(VerifyLockActivity.this).inflate(R.layout.verify_dialog_hint, null);
        hintDialog.show();
        hintDialog.setContentView(inflate_dialog);

        TextView account = (TextView) inflate_dialog.findViewById(R.id.tv_account);
        TextView confirm = (TextView) inflate_dialog.findViewById(R.id.tv_confirm);
        TextView cancel = (TextView) inflate_dialog.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintDialog.dismiss();
            }
        });
        final EditText password = (EditText) inflate_dialog.findViewById(R.id.et_pwd);
        account.setText("胖次账户:"+mAccount);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().trim().equals(mPassword)){
                    Intent intent = new Intent(VerifyLockActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(VerifyLockActivity.this, "密码有误,请重新输入!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        hintDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }
}
