package com.hewj.bangumi.ui.webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.hewj.bangumi.R;
import com.hewj.bangumi.base.BaseActivity;
import com.hewj.bangumi.common.App;


/**
 * 网页浏览页面的activity实现
 * @author hwj
 * @version 1.0, 2017/5/11
 */

public class WebViewActivity extends BaseActivity {
    public static WebViewActivity instance;
    static String mURL;
    static String mTitle;
    WebViewFragment webViewFragment=new WebViewFragment();
    public static void launch(Context context,String URL,String Title) {
        mTitle=Title;
        mURL=URL;
        Intent intent = new Intent(context, WebViewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_common_layout;
    }

    @Override
    protected void onInitTitle() {

        App.getInstance().addActivity(WebViewActivity.this);
        instance=WebViewActivity.this;

        mAppBar.setTitleText(mTitle);
        mAppBar.canFinishActivity();
        mAppBar.setVisibility(View.GONE);
        mAppBar.setNavigationIcon(R.drawable.icon_tabbar_arrow);

        mAppBar.setNavigationOnClickListener(v -> {
            webViewFragment.back();
        });
    }

    @Override
    protected void onResolveIntent(Intent intent) {

    }

    @Override
    protected void onInitFragment() {
        Bundle urlBundle=new Bundle();
        urlBundle.putString("url",mURL);
        webViewFragment.setArguments(urlBundle);
        showContent(webViewFragment, R.id.Layout);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            webViewFragment.back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
