package com.hasee.pangci.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class MemberFragment extends Fragment {
    private static final String TAG = "MemberFragment";
    @BindView(R.id.rl_video_list)
    RecyclerView rlVideoList;
    private ArrayList<Resources> mResourcesBeanArrayList = new ArrayList<>();
    private static final String RESOURCETYPE = "vip";
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
            ButterKnife.bind(this,mView);
            return mView;
        }
        mView = inflater.inflate(R.layout.member_fragment_layout, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = CommonUtils.buildProgressDialog(getActivity());//加载数据dialog
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //查询vip类的资源
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
}
