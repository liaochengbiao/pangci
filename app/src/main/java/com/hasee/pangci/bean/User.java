package com.hasee.pangci.bean;


import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;


public class User extends BmobObject implements Serializable {

    private String userAccount;

    private String userPassword;

    private Integer userHeadImg;

    private String memberLevel;

    private BmobDate memberStartDate;

    private BmobDate memberEndDate;

    private String userIMEI;

    private String userIntegral;//积分

    private String inviter;//邀请人账号

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Integer getUserHeadImg() {
        return userHeadImg;
    }

    public void setUserHeadImg(Integer userHeadImg) {
        this.userHeadImg = userHeadImg;
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public BmobDate getMemberStartDate() {
        return memberStartDate;
    }

    public void setMemberStartDate(BmobDate memberStartDate) {
        this.memberStartDate = memberStartDate;
    }

    public BmobDate getMemberEndDate() {
        return memberEndDate;
    }

    public void setMemberEndDate(BmobDate memberEndDate) {
        this.memberEndDate = memberEndDate;
    }


    public String getUserIMEI() {
        return userIMEI;
    }

    public void setUserIMEI(String userIMEI) {
        this.userIMEI = userIMEI;
    }

    public String getUserIntegral() {
        return userIntegral;
    }

    public void setUserIntegral(String userIntegral) {
        this.userIntegral = userIntegral;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    @Override
    public String toString() {
        return "User{" +
                "userAccount='" + userAccount + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userHeadImg='" + userHeadImg + '\'' +
                ", memberLevel='" + memberLevel + '\'' +
                ", memberStartDate=" + memberStartDate +
                ", memberEndDate=" + memberEndDate +
                ",userIMEI=" + userIMEI +
                ",userIntegral="+userIntegral+
                ",inviter="+inviter+
                '}';
    }
}
