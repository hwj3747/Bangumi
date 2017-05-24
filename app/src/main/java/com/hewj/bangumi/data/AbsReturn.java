package com.hewj.bangumi.data;

/**
 * Created by hewj on 2017/5/10.
 */
public class AbsReturn<V> {
    String message;
    V data;
    int  code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
