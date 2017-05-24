package com.hewj.bangumi.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hewj.bangumi.R;
import com.hewj.bangumi.base.BaseFragment;
import com.hewj.bangumi.common.ComponentHolder;
import com.hewj.bangumi.entity.BangumiEntity;
import com.hewj.bangumi.presenter.MainPresenter;
import com.hewj.bangumi.ui.bangumiinfo.BangumiInfoActivity;
import com.hewj.bangumi.view.MainView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by hewj on 2017/5/10.
 * 番剧列表
 */
public class MainFragment extends BaseFragment implements MainView {

    @Inject
    MainPresenter presenter;

    @InjectView(R.id.bangumi_list)
    RecyclerView bangumiList;
    private LinearLayoutManager mLayoutManager;
    private MainListAdapter mAdapter;
    static ArrayList<ArrayList<BangumiEntity>> bangumiEntities=new ArrayList<>();//存储番剧列表的list
    int index;
    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Icepick.restoreInstanceState(this, savedInstanceState);
        ComponentHolder.getAppComponent().inject(this);

        presenter.bindView(this);
        presenter.bindBaseView(this);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    protected void onViewInit() {
        if(bangumiEntities.size()==0)//数据为空的情况，加载番剧列表数据
            presenter.getBangumiList();
        if(getArguments()!=null) {//数据不为空，加载列表页面
            index=getArguments().getInt("arg");
            if(bangumiEntities.size()!=0) {
                creatList();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save state of all @State annotated members
        //Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.inject(this, rootView);

        return rootView;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_main;
    }

    @Override
    protected View getLoadingTargetView() {
        return findById(R.id.Layout);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void setData(ArrayList<ArrayList<BangumiEntity>> entities) {
        bangumiEntities=entities;
        creatList();
    }


    public void creatList(){
//        ImageView imageView = (ImageView) findViewById(R.id.my_image_view);
//
//        Glide.with(this).load("http://goo.gl/gEgYUd").into(imageView);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(getBaseContext());
        bangumiList.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        bangumiList.setHasFixedSize(true);
        //创建并设置Adapter
        mAdapter = new MainListAdapter(getBaseContext(),bangumiEntities.get(index),(v,p)->{
            Bundle bundle=new Bundle();
            bundle.putString("name",bangumiEntities.get(index).get(p).getTitle());
            bundle.putString("url",bangumiEntities.get(index).get(p).getUrl());
            BangumiInfoActivity.launch(getBaseContext(),bundle);
        });
        bangumiList.setAdapter(mAdapter);
    }
//    @Override
//    public void show(AbsReturn<TestEntity> test) {
//        text.setText(test.getData().getPwd());
//    }
}
