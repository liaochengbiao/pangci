package com.hasee.pangci.bean;


import org.litepal.crud.DataSupport;

public class NotificationBean extends DataSupport{

    private long id;

    private String notificationContent;

    private String tag;

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "NotificationBean{" +
                ", notificationContent='" + notificationContent + '\'' +
                ",tag='"+tag+'\''+
                '}';
    }
}
