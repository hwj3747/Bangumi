package com.hewj.bangumi.presenter;


import android.widget.Toast;

import com.hewj.bangumi.common.EndObserver;
import com.hewj.bangumi.common.SchedulerProvider;
import com.hewj.bangumi.data.AbsReturn;
import com.hewj.bangumi.data.AbsService;
import com.hewj.bangumi.entity.BangumiEntity;
import com.hewj.bangumi.view.MainView;
import com.hewj.library.utils.ConnectivityUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import compartment.BasePresenter;
import compartment.BaseView;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by hewj on 2017/5/10.
 */
public class MainPresenter extends BasePresenter<MainView, BaseView> {
    AbsService mAbsService;
    SchedulerProvider mSchedulerProvider;

    @Inject
    public MainPresenter(AbsService absService, SchedulerProvider schedulerProvider) {
        this.mAbsService = absService;
        this.mSchedulerProvider = schedulerProvider;
    }

    private Subscription mTestSubscription = Subscriptions.empty();

    /**
     *获取番剧列表
     * */
    public void getBangumiList(){
        getBaseView().showLoading("正在加载");
        if(!ConnectivityUtils.forceCheckConnectivityState()){
            getBaseView().showError("网络错误，请检查网络连接",
                    v-> {
                        getBaseView().hideLoading();
                        Toast.makeText(getBaseView().getBaseContext(),"网络错误，请检查网络连接",Toast.LENGTH_LONG).show();
                        getBangumiList();});
        }
        else {

            mAbsService.getBangumiList().compose(mSchedulerProvider.applySchedulers()).subscribe(MainObserver);
        }
    }

    private Observer<AbsReturn<ArrayList<ArrayList<BangumiEntity>>>> MainObserver = new EndObserver<AbsReturn<ArrayList<ArrayList<BangumiEntity>>>>() {
        @Override
        public void onHideLoding() {
            getBaseView().hideLoading();
        }

        @Override
        public void onMyNext(AbsReturn<ArrayList<ArrayList<BangumiEntity>>> entity) {
            getView().setData(entity.getData());
        }
    };
}
