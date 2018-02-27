package com.hasee.pangci.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hasee.pangci.R;
import com.hasee.pangci.bean.User;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MemberCenterActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.ll_weixin)
    LinearLayout llWeixin;
    @BindView(R.id.member_center_tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.tv_user_name)
    TextView mUserNameTv;
    @BindView(R.id.tv_user_member_level)
    TextView mMemberLevelTv;
    @BindView(R.id.iv_user_icon)
    CircleImageView mHeadIcon;
    @BindViews({R.id.member_center_v1_cv, R.id.member_center_v2_cv, R.id.member_center_v3_cv, R.id.member_center_v4_cv})
    List<CardView> mCardViews;
    private int mFlags;
    @BindView(R.id.activity_rechange)
    LinearLayout mLinearLayout;

    @OnClick(R.id.iv_user_icon)
    void onClick() {
        if (mFlags == 0){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_center);
        ButterKnife.bind(this);
        getIntentData();
        initEvents();
    }

    private void initEvents() {
        llWeixin.setOnClickListener(this);
        mToolbar.setTitle("会员中心");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for (CardView cardView : mCardViews) {
            cardView.setOnClickListener(this);
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        mFlags = intent.getFlags();
        if (mFlags == 1) {
            Bundle bundle = intent.getExtras();
            User user = (User) bundle.getSerializable("user");
            mUserNameTv.setText(user.getUserAccount());
            mMemberLevelTv.setText("当前等级:" + user.getMemberLevel());
            mHeadIcon.setImageResource(user.getUserHeadImg());
        } else {
            mUserNameTv.setText("未登录");
            mMemberLevelTv.setText("请先登录");
            mHeadIcon.setImageResource(R.drawable.normal_login);
        }
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
        tv_title.setText("充值提示");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_weixin:
                showDialog("1.请关注微信公众号“壹号电影", "2.点击该公众号菜单栏“胖次有你”找到充值一栏即可开通", "3.充值成功后,请重新登录账号，获取会员权限");
                break;

            case R.id.member_center_v1_cv:
                Toast.makeText(this, "请选择下面的充值方式进行开通或续费!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.member_center_v2_cv:
                Toast.makeText(this, "请选择下面的充值方式进行开通或续费!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.member_center_v3_cv:
                Toast.makeText(this, "请选择下面的充值方式进行开通或续费!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.member_center_v4_cv:
                Toast.makeText(this, "请选择下面的充值方式进行开通或续费!", Toast.LENGTH_SHORT).show();
                break;

            default:

                break;
        }
    }
}
