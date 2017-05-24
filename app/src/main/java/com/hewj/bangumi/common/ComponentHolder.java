package com.hewj.bangumi.common;

/**
 * Created by hewj on 2017/5/10.
 */
public class ComponentHolder {
    private static AppComponent sAppComponent;

    public static void setAppComponent(AppComponent appComponent) {
        sAppComponent = appComponent;
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}