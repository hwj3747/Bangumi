package com.hewj.bangumi.entity;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Created by hewj on 2017/5/10.
 */
public class TestEntity implements Parcelable {
    String name;
    String pwd;
    public static Creator<TestEntity> getCREATOR() {
        return CREATOR;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.pwd);
    }

    public TestEntity() {
    }

    protected TestEntity(Parcel in) {
        this.name = in.readString();
        this.pwd = in.readString();
    }

    public static final Creator<TestEntity> CREATOR = new Creator<TestEntity>() {
        @Override
        public TestEntity createFromParcel(Parcel source) {
            return new TestEntity(source);
        }

        @Override
        public TestEntity[] newArray(int size) {
            return new TestEntity[size];
        }
    };
}
