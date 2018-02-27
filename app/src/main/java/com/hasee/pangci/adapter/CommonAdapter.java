package com.hasee.pangci.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hasee.pangci.R;
import com.hasee.pangci.activity.LoginActivity;
import com.hasee.pangci.activity.MemberCenterActivity;
import com.hasee.pangci.bean.Resources;
import com.hasee.pangci.bean.User;
import com.hasee.pangci.common.CommonUtils;
import com.hasee.pangci.common.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author
 */
public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.ViewHolder> {
    private ArrayList<Resources> mResourcesBeanArrayList;
    private Context context;
    /**
     * 视频地址
     */
    private String videoUrl;
    /**
     * 0 跳登录 1 跳重置
     */
    private int flag = 0;
    private User mUser = new User();


    public CommonAdapter(ArrayList<Resources> vlist, Context context) {
        this.mResourcesBeanArrayList = vlist;
        this.context = context;
        SharedPreferences login_info = context.getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        mUser.setUserHeadImg(login_info.getInt("headImg", R.drawable.normal_login));
        mUser.setUserAccount(login_info.getString("account", "请登录"));
        mUser.setMemberLevel(login_info.getString("memberLevel", "请先登录"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position == mResourcesBeanArrayList.size() - 1 || position == mResourcesBeanArrayList.size() - 2) {
            //给最后一个view添加底部margin
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(CommonUtils.dp2px(160, context), CommonUtils.dp2px(200, context));
            layoutParams.setMargins(CommonUtils.dp2px(10, context), CommonUtils.dp2px(10, context), 0, CommonUtils.dp2px(10, context));
            holder.itemView.setLayoutParams(layoutParams);

        }
        final Resources resourcesBean = mResourcesBeanArrayList.get(position);
        holder.tvLookNum.setText(resourcesBean.getContentLike());
        holder.tvTitle.setText(resourcesBean.getTitle());
        holder.updateTime.setText(resourcesBean.getCreatedAt());
        //根据coverhttptype区分图片请求头
        switch (resourcesBean.getCoverhttptype()) {
            case "0":
                //封面地址
                Glide.with(context).load(Constant.ICONZEROHEADERURL + resourcesBean.getCover()).placeholder(R.drawable.vip).error(R.drawable.ic_upper_loading_failed).into(holder.ivCover);
                break;

            case "1":
                Glide.with(context).load(resourcesBean.getCover()).error(R.drawable.ic_upper_loading_failed).into(holder.ivCover);
                break;

            default:

                break;
        }


        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences login_info = context.getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
                String mMemberLevel = login_info.getString("memberLevel", "青铜");
                boolean isLogin = login_info.getBoolean("isLogin", false);
                Resources resource = mResourcesBeanArrayList.get(position);
                //根据httpsType区别视频请求头
                switch (resource.getHttpstype()) {
                    case "0":
                        //视频地址
                        videoUrl = Constant.MOVIEZEROHEADERURL + resourcesBean.getContentId();
                        break;
                    case "1":
                        videoUrl = Constant.MOVIEONEHEADERURL + resourcesBean.getContentId();
                        break;
                    case "2":
                        videoUrl = Constant.WPHEADERURL + resourcesBean.getContentId();
                        break;

                    default:

                        break;
                }
                //判断是否已经登录
                if (!isLogin) {
                    flag = 0;
                    buildDialog("请登录之后观看!", "去登录", flag);
                    return;
                }
                //判断是否是免费还是收费
                if ("common".equals(resourcesBean.getAuthority())) {
                    //免费直接看
//                    CommonUtils.openURL(videoUrl, context);
                    //用腾讯x5观看 优化
                    CommonUtils.openURLToX5(videoUrl,context);
                } else {
                    //1.收费
                    //2.看是否是付费会员
                    if ("青铜".equals(mMemberLevel)) {
                        flag = 1;
                        //没付费会员提示开通会员
                        buildDialog("请升级为付费会员观看!", "去充值", flag);
                    } else {
                        //付费会员直接看 看会员是否到期
                        if (login_info.getBoolean("isExpire", false)) {
                            flag = 1;
                            buildDialog("您的会员已到期,请续费后观看!", "去充值", flag);
                            return;
                        }
//                        CommonUtils.openURL(videoUrl, context);
                        //腾讯x5观看
                        CommonUtils.openURLToX5(videoUrl,context);
                    }
                }
            }
        });

    }

    private void buildDialog(String promptContent, String btnText, final int flag) {
        final AlertDialog hintDialog = new AlertDialog.Builder(context, R.style.ShowDialog).create();
        final View inflate_dialog = LayoutInflater.from(context).inflate(R.layout.play_dialog_hint, null);
        hintDialog.show();
        hintDialog.setContentView(inflate_dialog);
        TextView tvContent = (TextView) inflate_dialog.findViewById(R.id.tv_content);
        tvContent.setText(promptContent);
        TextView tvConfirm = (TextView) inflate_dialog.findViewById(R.id.tv_comfirm);
        tvConfirm.setText(btnText);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    Intent in = new Intent(context, LoginActivity.class);
                    context.startActivity(in);
                    hintDialog.dismiss();
                } else {
                    Intent intent = new Intent(context, MemberCenterActivity.class);
                    intent.setFlags(1);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", mUser);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    hintDialog.dismiss();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResourcesBeanArrayList != null ? mResourcesBeanArrayList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //封面
        @BindView(R.id.iv_cover)
        ImageView ivCover;
        //观看人数
        @BindView(R.id.tv_look_num)
        TextView tvLookNum;
        //标题
        @BindView(R.id.tv_title)
        TextView tvTitle;
        //标题
        @BindView(R.id.update_time)
        TextView updateTime;
        //播放按钮
        @BindView(R.id.iv_play)
        ImageView ivPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
