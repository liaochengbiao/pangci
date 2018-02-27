package com.hasee.pangci.activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hasee.pangci.R;
import com.hasee.pangci.widget.GraphicLockView;

public class LockActivity extends AppCompatActivity implements GraphicLockView.OnGraphicLockListener {

    private TextView mTvIno;
    private GraphicLockView mGlGraphicLockView;
    private boolean isFirstSetPwd;
    private String mPassword;   //记录第一次绘制的密码
    private int type = 0;   //判断时登录还是设置密码
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        initView();
    }

    private void initView() {
        mTvIno = (TextView) findViewById(R.id.agl_tv_info);
        mGlGraphicLockView = (GraphicLockView) findViewById(R.id.agl_gl_lock);
        mToolbar=(Toolbar) findViewById(R.id.lock_tool_bar);
        isFirstSetPwd = true;
        mTvIno.setText("请绘制解锁图案");

        mToolbar.setTitle("设置密码");
        mToolbar.inflateMenu(R.menu.lock_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showDialog("1.手势密码设置不是必须的","2.一旦设置密码之后,下次进入应用需验证手势密码","3.成功绘制两次之后即代表密码设置成功");
                return false;
            }
        });
        mGlGraphicLockView.setOnGraphicLockListener(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void setPwdSuccess(String password) {
        if (type == 0) {   //设置密码
            if (isFirstSetPwd) {
                mTvIno.setText("再次绘制图案进行确认");
                mPassword = password;
                isFirstSetPwd = false;
            } else {
                if (mPassword.equals(password)) {
                    Log.d("GraphicLockActivity--->", "password====" + mPassword);
                    Toast.makeText(this, "设置密码成功", Toast.LENGTH_LONG).show();
                    mTvIno.setText("下次进入应用时需验证此手势密码");
                    //存到Sp里面
                    SharedPreferences lock_info = getSharedPreferences("LOCK_INFO", MODE_PRIVATE);
                    SharedPreferences.Editor edit = lock_info.edit();
                    edit.putBoolean("isSetting",true);
                    edit.putString("lockPwd",mPassword);
                    edit.apply();
//                    type = 1;
                } else {
                    Toast.makeText(this, "两次设置的密码不一致,请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }
/*        else if (type == 1) {  //登录
            if (mPassword.equals(password)) {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "密码错误,登录失败", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFirstSetPwd = true;
        mPassword = "";
    }

    @Override
    public void setPwdFailure() {
        if (type == 0) {
            mTvIno.setText("请至少连接四个点");
            Toast.makeText(this, "密码过短,设置失败", Toast.LENGTH_SHORT).show();
        }

/*        else if (type == 1) {
            mTvIno.setText("你可以登录了");
            Toast.makeText(this, "密码错误,登录失败", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void showDialog(String tv1, String tv2, String tv3) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        /**
         * 设置内容区域为自定义View
         */
        LinearLayout inflate_dialog= (LinearLayout) getLayoutInflater().inflate(R.layout.pay_dialog_hint,null);
        builder.setView(inflate_dialog);

        final AlertDialog dialog=builder.create();
        dialog.show();

        TextView tv_1 = (TextView) inflate_dialog.findViewById(R.id.tv_1);
        TextView tv_2 = (TextView) inflate_dialog.findViewById(R.id.tv_2);
        TextView tv_3 = (TextView) inflate_dialog.findViewById(R.id.tv_3);
        TextView tv_title = (TextView) inflate_dialog.findViewById(R.id.tv_title);
        tv_title.setText("设置说明");
        tv_1.setText(tv1);
        tv_2.setText(tv2);
        tv_3.setText(tv3);
        TextView tvConfirm = (TextView) inflate_dialog.findViewById(R.id.tv_comfirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
