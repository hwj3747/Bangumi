package com.hewj.bangumi.common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hewj.bangumi.data.AbsService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
/**
 * Created by hewj on 2017/5/10.
 */
@Module
public class AppModule {
    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    public SchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.DEFAULT;
    }

    @Provides
    public AbsService provideApi() {
        return AbsService.getInstance();
    }

    @Provides
    public AbsService.AbsApi provideAbsApi() {
        return AbsService.getService();
    }
}
