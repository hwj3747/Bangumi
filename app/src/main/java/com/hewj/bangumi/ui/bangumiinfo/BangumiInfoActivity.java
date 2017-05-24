package com.hewj.bangumi.ui.bangumiinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hewj.bangumi.R;
import com.hewj.bangumi.base.BaseActivity;
/**
 * Created by hewj on 2017/5/10.
 * 番剧详情
 */

public class BangumiInfoActivity extends BaseActivity {

    BangumiInfoFragment bangumiInfoFragment=new BangumiInfoFragment();
    public static BangumiInfoActivity instance;
    static String name;

    public static void launch(Context context,Bundle bundle) {
        Intent intent = new Intent(context, BangumiInfoActivity.class);
        name=bundle.getString("name");
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_common_layout;
    }

    @Override
    protected void onInitTitle() {
        instance = BangumiInfoActivity.this;
        if(name!=null)
            mAppBar.setTitleText(name);
        mAppBar.setNavigationIcon(R.drawable.icon_tabbar_arrow);
        mAppBar.canFinishActivity();
    }

    @Override
    protected void onResolveIntent(Intent intent) {
        bangumiInfoFragment.setArguments(intent.getExtras());
    }

    @Override
    protected void onInitFragment() {
        showContent(bangumiInfoFragment, R.id.Layout);
    }
}
