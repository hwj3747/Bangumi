package com.hewj.bangumi.common;

import android.app.Activity;
import android.app.Application;

import com.hewj.library.utils.EnvironmentUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hewj on 2017/5/10.
 */
public class App extends Application {
    private AppComponent component;


    //运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();
    //为了实现每次使用该类时不创建新的对象而创建的静态对象
    private static App instance;
    //实例化一次
    public synchronized static App getInstance(){
        if (null == instance) {
            instance = new App();
        }
        return instance;
    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    //关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity:mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    //杀进程
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    protected AppModule getApplicationModule() {
        return new AppModule(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
       // refWatcher = LeakCanary.install(this);
        EndObserver.init(getApplicationContext());
        EnvironmentUtils.init(this);
        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        ComponentHolder.setAppComponent(appComponent);
    }
}
