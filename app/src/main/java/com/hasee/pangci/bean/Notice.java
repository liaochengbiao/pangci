package com.hasee.pangci.bean;

import org.litepal.crud.DataSupport;

public class Notice extends DataSupport{

    private String notice;

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }
}
