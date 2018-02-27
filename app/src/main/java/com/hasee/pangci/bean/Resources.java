package com.hasee.pangci.bean;

import cn.bmob.v3.BmobObject;



public class Resources extends BmobObject {

    /***
     * 资源标题
     */
    private String Title;

    /***
     * 视频内容资源请求头类别
     */
    private String httpstype;

    /***
     * 视频封面图片请求类别
     */
    private String coverhttptype;

    /***
     *资源类型
     */
    private String ContentType;

    /***
     * 资源喜欢人数
     */
    private String ContentLike;

    /***
     * 资源视频内容请求id
     */
    private String ContentId;

    /***
     * 资源封面id
     */
    private String Cover;

    /***
     * 资源免费还是收费
     */
    private String Authority;

    public Resources(String title, String httpstype, String coverhttptype, String contentType, String contentLike, String contentId, String authority, String cover) {
        Title = title;
        this.httpstype = httpstype;
        this.coverhttptype = coverhttptype;
        ContentType = contentType;
        ContentLike = contentLike;
        ContentId = contentId;
        Authority = authority;
        Cover = cover;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getHttpstype() {
        return httpstype;
    }

    public void setHttpstype(String httpstype) {
        this.httpstype = httpstype;
    }

    public String getContentType() {
        return ContentType;
    }

    public void setContentType(String contentType) {
        ContentType = contentType;
    }

    public String getContentLike() {
        return ContentLike;
    }

    public void setContentLike(String contentLike) {
        ContentLike = contentLike;
    }

    public String getContentId() {
        return ContentId;
    }

    public void setContentId(String contentId) {
        ContentId = contentId;
    }

    public String getAuthority() {
        return Authority;
    }

    public void setAuthority(String authority) {
        Authority = authority;
    }

    public String getCoverhttptype() {
        return coverhttptype;
    }

    public void setCoverhttptype(String coverhttptype) {
        this.coverhttptype = coverhttptype;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String cover) {
        Cover = cover;
    }

    @Override
    public String toString() {
        return "Resources{" +
                "Title='" + Title + '\'' +
                ", httpstype='" + httpstype + '\'' +
                ", coverhttptype='" + coverhttptype + '\'' +
                ", ContentType='" + ContentType + '\'' +
                ", ContentLike='" + ContentLike + '\'' +
                ", ContentId='" + ContentId + '\'' +
                ", Authority='" + Authority + '\'' +
                ",cover='"+Cover+'\''+
                '}';
    }
}
