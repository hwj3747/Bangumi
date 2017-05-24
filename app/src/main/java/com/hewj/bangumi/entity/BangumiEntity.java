package com.hewj.bangumi.entity;

/**
 * Created by hewj on 2017/5/8.
 */

public class BangumiEntity {
    String title;//番剧标题
    String number;//当前是第几话
    String url;//番剧的链接

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
