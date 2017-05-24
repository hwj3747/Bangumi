package com.hewj.library.absBase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.hewj.library.loading.ILoadingDelegate;
import com.hewj.library.loading.VaryViewHelperController;

import compartment.BaseView;

/**
 * Base Fragment that implements logging for fragment's life cycle.
 * Base Fragment also implements a framework for relation of activity
 *
 * @author cuij
 */
public abstract class AbsBaseFragment extends android.support.v4.app.Fragment implements ILoadingDelegate, BaseView {
    private boolean mActive = false;
    private boolean mIgnoredWhilePopBack = false;
    protected AbsBaseActivity mActivity;
    private VaryViewHelperController mVaryViewHelperController;
    protected View mView;

    private void logInfo(String msg) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        logInfo("onCreate");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        logInfo("onCreateOptionsMenu");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        logInfo("onPrepareOptionsMenu");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        logInfo("onSaveInstanceState");
    }

    @Override
    public void onResume() {
        super.onResume();
        logInfo("onResume");
        mActive = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        logInfo("onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        logInfo("onStop");

    }

    @Override
    public void onPause() {
        super.onPause();
        logInfo("onPause");
        mActive = false;
    }

    @Override
    public void onDestroy() {
        logInfo("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logInfo("onActivityResult");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logInfo("onCreateView");
        return inflater.inflate(getLayoutID(), container, false);
    }

    protected abstract int getLayoutID();

    protected <T extends View> T findById(int id) {
        return (T) mView.findViewById(id);
    }

    private void onInitViewLoading() {
        if (null != getLoadingTargetView()) {
            mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
        }
    }

    protected abstract View getLoadingTargetView();

    /**
     * Activity 往 fragment 装填数据使用
     *
     * @param objects
     */
    public void onSetupData(Object... objects) {
    }

    @Override
    public void onDestroyView() {
        logInfo("onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        logInfo("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        onInitViewLoading();
        onViewInit();
    }

    protected abstract void onViewInit();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        logInfo("onActivityCreated");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        logInfo("onAttach");
        if (!(activity instanceof AbsBaseActivity)) {
            throw new ClassCastException("activity must extends BaseActivity of " + this.getClass().toString());
        }
        try {
            mActivity = (AbsBaseActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("activity must implements delegate of " + this.getClass().toString());
        }
    }

    @Override
    public void onDetach() {
        logInfo("onDetach");
        super.onDetach();
    }

    /**
     * @return
     * @author cuij
     */
    public boolean onBackPressed() {
        logInfo("onBackPressed");
        return false;
    }

    public boolean isIgnoredWhilePopBack() {
        return mIgnoredWhilePopBack;
    }

    public void setIgnoredWhilePopBack(boolean ignored) {
        mIgnoredWhilePopBack = ignored;
    }

    /**
     * can override this method to handle keydown event pass by baseActivity
     *
     * @param keyCode
     * @param event
     * @return if consume this event
     * @author cuij
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
    }

    public boolean isActive() {
        return mActive;
    }


    @Override
    public void showError(String msg,View.OnClickListener onClickListener) {
        toggleShowError(true, msg, onClickListener);
    }

    @Override
    public void showException(String msg, View.OnClickListener onClickListener) {
        toggleShowError(true, msg, onClickListener);
    }

    @Override
    public void showNetError(View.OnClickListener onClickListener) {
        toggleNetworkError(true, onClickListener);
    }

    @Override
    public void showEmpty(View.OnClickListener onClickListener) {
        toggleShowEmpty(true, null, onClickListener);
    }

    @Override
    public void showLoading(String msg) {
        if ("".equals(msg) || null == msg) {
            msg = "数据加载中...";
        }
        toggleShowLoading(true, msg);
    }


    @Override
    public void hideLoading() {
        toggleShowLoading(false, null);
    }

    /**
     * toggle show loading
     *
     * @param toggle
     */
    protected void toggleShowLoading(boolean toggle, String msg) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showLoading(msg);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * toggle show empty
     *
     * @param toggle
     */
    protected void toggleShowEmpty(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showEmpty(msg, onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * toggle show error
     *
     * @param toggle
     */
    protected void toggleShowError(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showError(msg, onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * toggle show network error
     *
     * @param toggle
     */
    protected void toggleNetworkError(boolean toggle, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showNetworkError(onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    @Override
    public Context getBaseContext() {
        return mActivity;
    }

    @Override
    public Context getApplicationContext(){
        return mActivity.getApplicationContext();
    }
    protected void hideSoftInput(View v) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            View view = mActivity.getWindow().peekDecorView();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    protected void showSoftInput() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}