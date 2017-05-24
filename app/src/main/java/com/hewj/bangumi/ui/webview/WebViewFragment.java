package com.hewj.bangumi.ui.webview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hewj.bangumi.R;
import com.hewj.bangumi.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 网页浏览页面的fragment实现
 * @author hwj
 * @version 1.0, 2017/5/11
 */

public class WebViewFragment extends BaseFragment {


    @InjectView(R.id.webView)
    WebView wv;
    @InjectView(R.id.loading_progress)
    ProgressBar loadingProgress;

    public WebViewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    protected void onViewInit() {
        wv.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        wv.getSettings().setSupportZoom(true);
// 设置出现缩放工具
        wv.getSettings().setBuiltInZoomControls(true);
//扩大比例的缩放
        wv.getSettings().setUseWideViewPort(true);
//自适应屏幕
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wv.getSettings().setLoadWithOverviewMode(true);

        wv.loadUrl(getArguments().getString("url"));
        wv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                                view.loadUrl("javascript:function setTop(){var x=document.getElementsByTagName(\"div\");" +
                        "for (var i=8;i<x.length;i++){x[i].style.display=\"none\";}}setTop();");
            }//注入js代码，将播放器后面的div都隐藏掉
        });
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                loadingProgress.setVisibility(View.VISIBLE);
                loadingProgress.setProgress(newProgress);
                view.loadUrl("javascript:function setTop(){var x=document.getElementsByTagName(\"div\");" +
                        "for (var i=8;i<x.length;i++){x[i].style.display=\"none\";}}setTop();");
                //注入js代码，将播放器后面的div都隐藏掉
                if(newProgress == 100)
                    loadingProgress.setVisibility(View.GONE);
            }
        });
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
        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);

        ButterKnife.inject(this, rootView);

        return rootView;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_webview;
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

    public void back() {
        if (wv.canGoBack()) {
            wv.goBack();//返回上一页面
        } else {
            WebViewActivity.instance.finish();
        }
    }
}
