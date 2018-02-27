package com.hasee.pangci.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.hasee.pangci.R;
import com.hasee.pangci.adapter.CommonAdapter;
import com.hasee.pangci.bean.Resources;
import com.hasee.pangci.common.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class RecommendFragment extends Fragment {
    private static final String TAG = "RecommendFragment";
    @BindView(R.id.rl_video_list)
    RecyclerView rlVideoList;
    private ArrayList<Resources> mResourcesBeanArrayList = new ArrayList<>();
    private static final String RESOURCETYPE = "recommend";
    private View mView;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
            ButterKnife.bind(this, mView);
            return mView;
        }
        mView = inflater.inflate(R.layout.recommend_fragment_layout, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean firstInstall = isFirstInstall();
        if(firstInstall){
            showActivityRule("");
        }
        mProgressDialog = CommonUtils.buildProgressDialog(getActivity());//加载数据dialog
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //查询推荐类的资源
        if (mResourcesBeanArrayList.size() == 0) {
            BmobQuery<Resources> bmobQuery = new BmobQuery<>();
            bmobQuery.addWhereEqualTo("ContentType", RESOURCETYPE);
            bmobQuery.order("-createdAt");//降序
            bmobQuery.setLimit(500);
            bmobQuery.findObjects(new FindListener<Resources>() {
                @Override
                public void done(List<Resources> list, BmobException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            Resources resourcesBean = list.get(i);
                            mResourcesBeanArrayList.add(resourcesBean);
                        }
                        CommonAdapter adapter = new CommonAdapter(mResourcesBeanArrayList, getActivity());
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                        rlVideoList.setLayoutManager(gridLayoutManager);
                        rlVideoList.setAdapter(adapter);
                        mProgressDialog.dismiss();//加载完成
                    } else {
                        Toast.makeText(getActivity(), "网络加载出错!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    //是否
    private boolean isFirstInstall() {
        SharedPreferences preferences = getActivity().getSharedPreferences("isFirstInstall", Context.MODE_PRIVATE);

        //判断是不是首次登录，
        if (preferences.getBoolean("isFirstInstall", true)) {
            SharedPreferences.Editor editor = preferences.edit();
            //第二次将标志位设置为false，
            editor.putBoolean("isFirstInstall", false);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }
    private void showActivityRule(String stringExtra) {
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_activity_rule, null);
        final AlertDialog ativityRuleDialog = showBackTransparent(getActivity(), inflate);
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
    private   AlertDialog showBackTransparent(Context context, View view) {
        final AlertDialog dialog_backTransparent = new AlertDialog.Builder(context).create();
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
