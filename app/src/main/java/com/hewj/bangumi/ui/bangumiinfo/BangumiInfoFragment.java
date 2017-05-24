package com.hewj.bangumi.ui.bangumiinfo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hewj.bangumi.R;
import com.hewj.bangumi.base.BaseFragment;
import com.hewj.bangumi.common.ComponentHolder;
import com.hewj.bangumi.entity.BangumiGroupEntity;
import com.hewj.bangumi.entity.BangumiInfoEntity;
import com.hewj.bangumi.presenter.BangumiInfoPresenter;
import com.hewj.bangumi.view.BangumiInfoView;

import javax.inject.Inject;

import butterknife.ButterKnife;


/**
 * Created by hewj on 2017/5/10.
 * 番剧详情
 */
public class BangumiInfoFragment extends BaseFragment implements BangumiInfoView {

    @Inject
    BangumiInfoPresenter presenter;
    String url;

    BangumiInfoEntity bangumiInfoEntity;
    BangumiGroupEntity bangumiGroupEntity;
    ImageView cover;
    TextView name;
    TextView all;
    TextView autor;
    TextView version;
    TextView state;
    TextView type;
    ExpandableListView expandableListView;
    public BangumiInfoFragment() {
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
        if(getArguments()!=null){
            url=getArguments().getString("url");
            presenter.getBangumiInfo(url);
            presenter.getItemList(url);
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
        View rootView = inflater.inflate(R.layout.fragment_bangumi_info, container, false);

        ButterKnife.inject(this, rootView);

        cover= (ImageView) rootView.findViewById(R.id.cover);
        name= (TextView) rootView.findViewById(R.id.name);
        all= (TextView) rootView.findViewById(R.id.all);
        state= (TextView) rootView.findViewById(R.id.state);
        version= (TextView) rootView.findViewById(R.id.version);
        autor= (TextView) rootView.findViewById(R.id.autor);
        type= (TextView) rootView.findViewById(R.id.type);
        expandableListView=(ExpandableListView)rootView.findViewById(R.id.episode_list);
        return rootView;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_bangumi_info;
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
    public void setData(BangumiInfoEntity entity) {
        bangumiInfoEntity=entity;
        initTop();
    }

    @Override
    public void setList(BangumiGroupEntity entity) {
        bangumiGroupEntity=entity;
        initBottom();
    }

    public void initBottom(){
        if(bangumiGroupEntity!=null){
            int size=bangumiGroupEntity.getGroup().size()-1;
            bangumiGroupEntity.getGroup().remove(size);
            ExpandableListAdapter expandableListAdapter=new ExpandableListAdapter(getBaseContext(),bangumiGroupEntity.getGroup(),bangumiGroupEntity.getChild());
            expandableListView.setAdapter(expandableListAdapter);

        }
    }
    public void initTop() {
        if (bangumiInfoEntity != null) {
           Glide.with(this).load(bangumiInfoEntity.getCover()).into(cover);
            all.setText(bangumiInfoEntity.getAll());
            autor.setText(bangumiInfoEntity.getAutor());
            name.setText(bangumiInfoEntity.getName());
            state.setText(bangumiInfoEntity.getState());
            type.setText(bangumiInfoEntity.getType());
            version.setText(bangumiInfoEntity.getVersion());

        }
    }
}
