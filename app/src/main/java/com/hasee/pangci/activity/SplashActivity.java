package com.hasee.pangci.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hasee.pangci.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {
    @BindView(R.id.iv_splash)
    ImageView mImageView;
    private AnimatorSet mAnimatorSet;
    private boolean mIsSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mAnimatorSet = new AnimatorSet();
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(mImageView, "translationX", 600, 0);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(mImageView, "translationY", -100, 90, -80, 70, -60, 50);
        mAnimatorSet.playTogether(objectAnimatorX,objectAnimatorY);
        mAnimatorSet.setDuration(2000);
        addListener();


        //判断是否有设置手势密码
        SharedPreferences lock_info = getSharedPreferences("LOCK_INFO", MODE_PRIVATE);
        mIsSetting = lock_info.getBoolean("isSetting", false);
    }

    private void addListener() {
        mAnimatorSet.start();
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    Thread.sleep(500);
                    if(mIsSetting){
                        Intent intent = new Intent(SplashActivity.this,VerifyLockActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
