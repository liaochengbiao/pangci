package com.hasee.pangci.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hasee.pangci.R;
import com.hasee.pangci.bean.FeedBack;
import com.hasee.pangci.bean.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;


public class IntegralActivity extends AppCompatActivity {
    @BindView(R.id.integral_iv_user_icon)
    CircleImageView mCircleImageView;
    @BindView(R.id.integral_tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.integral_tv)
    TextView mIntegralTv;
    private User mUser;

    @OnClick(R.id.integral_confirm_tv)
    void onClick() {
        //兑换积分
        if (mUser != null) {
            String temp = mIntegralTv.getText().toString();
            String substring = temp.substring(5, temp.length());
            int integral = Integer.parseInt(substring);//当前积分
            if (integral >= 100) {
                //兑换后 修改数据库积分并新增一个月时间 存到sp
                updateAndQueryDb();
            } else {
                Toast.makeText(this, "您的积分不足,赶紧邀请好友注册吧!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateAndQueryDb() {
        //由于只能根据id去修改,先根据账号查 再修改
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("userAccount", mUser.getUserAccount());
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    User user = list.get(0);
                    updateDb(user.getObjectId(), user.getUserIntegral());
                } else {
                    Toast.makeText(IntegralActivity.this, "兑换出错,请重试!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /***
     * @param objectId
     * @param integral    需要减去的积分
     * @param oldIntegral 现有积分
     */
    private void updateDb(String objectId, String oldIntegral) {
        int oIntergral = Integer.parseInt(oldIntegral);
        User user = new User();
        final String tempIntegral = String.valueOf(oIntergral - 100);
        user.setUserIntegral(tempIntegral + "");
        user.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(IntegralActivity.this, "兑换成功,请重新登录后获取会员时间!", Toast.LENGTH_SHORT).show();
                    mIntegralTv.setText("当前积分:" + tempIntegral);
                    SharedPreferences login_info = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
                    SharedPreferences.Editor edit = login_info.edit();
                    edit.putString("integral", tempIntegral);
                    edit.apply();

                    //兑换成功之后 插入表FeedBack 告知后台兑换了积分 然后给会员添加会员时间 如果是青铜则修改为白银
                    FeedBack feedBack = new FeedBack();
                    feedBack.setUserName(mUser.getUserAccount());
                    feedBack.setContent("兑换积分成功");
                    feedBack.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Log.i("FeedBack","成功");
                            }else {
                                Log.i("FeedBack",e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_acitvity);
        ButterKnife.bind(this);
        mToolbar.setTitle("我的积分");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getData();
    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (User) bundle.getSerializable("user");
        Integer userHeadImg = mUser.getUserHeadImg();
        String userIntegral = mUser.getUserIntegral();
        mCircleImageView.setImageResource(userHeadImg);
        String temp = TextUtils.isEmpty(userIntegral) ? "当前积分:0" : "当前积分:" + userIntegral;
        mIntegralTv.setText(temp);
    }
}
