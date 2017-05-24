package com.hewj.bangumi.presenter;


import android.widget.Toast;

import com.hewj.bangumi.common.EndObserver;
import com.hewj.bangumi.common.SchedulerProvider;
import com.hewj.bangumi.data.AbsReturn;
import com.hewj.bangumi.data.AbsService;
import com.hewj.bangumi.entity.BangumiGroupEntity;
import com.hewj.bangumi.entity.BangumiInfoEntity;
import com.hewj.bangumi.view.BangumiInfoView;
import com.hewj.library.utils.ConnectivityUtils;

import javax.inject.Inject;

import compartment.BasePresenter;
import compartment.BaseView;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by hewj on 2017/5/10.
 */
public class BangumiInfoPresenter extends BasePresenter<BangumiInfoView, BaseView> {
    AbsService mAbsService;
    SchedulerProvider mSchedulerProvider;

    @Inject
    public BangumiInfoPresenter(AbsService absService, SchedulerProvider schedulerProvider) {
        this.mAbsService = absService;
        this.mSchedulerProvider = schedulerProvider;
    }

    private Subscription mTestSubscription = Subscriptions.empty();

    /**
     * 获取番剧详情
     * */
    public void getBangumiInfo(String URL){
        getBaseView().showLoading("正在加载");
        if(!ConnectivityUtils.forceCheckConnectivityState()){
            getBaseView().showError("网络错误，请检查网络连接",
                    v-> {
                        getBaseView().hideLoading();
                        Toast.makeText(getBaseView().getBaseContext(),"网络错误，请检查网络连接",Toast.LENGTH_LONG).show();
                        getBangumiInfo(URL);});
        }
        else {

            mAbsService.getItemInfo(URL).compose(mSchedulerProvider.applySchedulers()).subscribe(BangumiInfoObserve);
        }
    }

    /**
     * 获取不同来源的每一集
     * */
    public void getItemList(String URL){
        getBaseView().showLoading("正在加载");
        if(!ConnectivityUtils.forceCheckConnectivityState()){
            getBaseView().showError("网络错误，请检查网络连接",
                    v-> {
                        getBaseView().hideLoading();
                        Toast.makeText(getBaseView().getBaseContext(),"网络错误，请检查网络连接",Toast.LENGTH_LONG).show();
                        getItemList(URL);});
        }
        else {

            mAbsService.getItemList(URL).compose(mSchedulerProvider.applySchedulers()).subscribe(BangumiItemListObserve);
        }
    }


    private Observer<AbsReturn<BangumiInfoEntity>> BangumiInfoObserve = new EndObserver<AbsReturn<BangumiInfoEntity>>() {
        @Override
        public void onHideLoding() {
            getBaseView().hideLoading();
        }

        @Override
        public void onMyNext(AbsReturn<BangumiInfoEntity> entity) {
            getView().setData(entity.getData());
        }
    };

    private Observer<AbsReturn<BangumiGroupEntity>> BangumiItemListObserve = new EndObserver<AbsReturn<BangumiGroupEntity>>() {
        @Override
        public void onHideLoding() {
            getBaseView().hideLoading();
        }

        @Override
        public void onMyNext(AbsReturn<BangumiGroupEntity> entity) {
            getView().setList(entity.getData());
        }
    };
}
