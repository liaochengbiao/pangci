package com.hasee.pangci.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.hasee.pangci.R;
import com.hasee.pangci.bean.FeedBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedBackFragment extends Fragment {
    @BindView(R.id.feedback_content_et)
    EditText mContentEt;

    @OnClick(R.id.feedback_confirm_tv)
    public void onClick() {
        //获取当前用户
        SharedPreferences login_info = getActivity().getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String userName = login_info.getString("account", "");
        String content = mContentEt.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            insertDataToDb(content, userName);
        } else {
            Toast.makeText(getActivity(), "内容不能为空!", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertDataToDb(String content, String userName) {
        FeedBack feedBack = new FeedBack();
        feedBack.setContent(content);
        feedBack.setUserName(userName);
        feedBack.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    mContentEt.setText("");
                    Toast.makeText(getActivity(), "反馈成功,后台将在第一时间回复您.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "反馈失败,请重试!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
