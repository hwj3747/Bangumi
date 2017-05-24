package com.hewj.bangumi.entity;

/**
 * Created by hewj on 2017/5/10.
 */

public class BangumiInfoEntity {
    String name;//名字
    String cover;//封面
    String all;//全集
    String autor;//作者
    String type;//类型
    String state;//状态
    String version;//版本

    @Override
    public String toString() {
        return "BangumiInfoEntity{" +
                "name='" + name + '\'' +
                ", cover='" + cover + '\'' +
                ", all='" + all + '\'' +
                ", autor='" + autor + '\'' +
                ", type='" + type + '\'' +
                ", state='" + state + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
