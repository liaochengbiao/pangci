package com.hasee.pangci.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.hasee.pangci.common.MessageEvent;
import com.hasee.pangci.common.MessageEvent2;
import com.hasee.pangci.common.MessageEventNotice;
import com.hasee.pangci.R;
import com.hasee.pangci.activity.LoginActivity;
import com.hasee.pangci.activity.NotificationActivity;
import com.hasee.pangci.bean.NotificationBean;
import com.hasee.pangci.bean.User;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

public class MyPushMessageReceiver extends BroadcastReceiver {
    private static final String NORMAL = "0";
    private static final String LINK = "1";
    private static final String ACTION = "2"; //活动与公告的推送

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            try {
                JSONObject jsonObject = new JSONObject(intent.getStringExtra("msg"));
                String tag = jsonObject.getString("tag");
                String title = jsonObject.getString("title");
                String content = jsonObject.getString("content");

                //数据插到本地数据库
                if (tag.equals(ACTION)) {
                    //通知主页公告栏显示活动详情
                    EventBus.getDefault().post(new MessageEvent2(content));
                } else {
                    NotificationBean notificationBean = new NotificationBean();
                    notificationBean.setTag(tag);
                    notificationBean.setNotificationContent(content);
                    notificationBean.save();
                    EventBus.getDefault().post(new MessageEvent(new User(), "receiver"));
                    buildNotification(context, content, title, tag);

                    //通知栏显示的同时顶部也显示会不会有点多余呢？
                    EventBus.getDefault().post(new MessageEventNotice(content));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void buildNotification(Context context, String content, String title, String tag) {
        Intent intent = null;
        SharedPreferences login_info = context.getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        boolean isLogin = login_info.getBoolean("isLogin", false);
        if (!isLogin) {
            intent = new Intent(context, LoginActivity.class);
        } else {
            //登录的情况 区分普通推送与链接推送
            if (tag.equals(NORMAL)) {
                //普通消息推送
                intent = new Intent(context, NotificationActivity.class);
            } else {
                //资源推送
                intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(content);
                intent.setData(content_url);
            }
        }
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                .setContentIntent(pi)
                .setAutoCancel(true)//设置点击后消失
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        manager.notify(1, notification);
    }
}
