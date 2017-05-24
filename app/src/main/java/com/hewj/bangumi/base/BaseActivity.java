package com.hewj.bangumi.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import com.hewj.library.absBase.AbsBaseActivity;
import com.hewj.library.utils.SharedPreferencesUtil;

import javax.inject.Inject;


/**
 * 实现所有activity的公共操作
 * Created by hewj on 2017/5/10.
 */
public abstract class BaseActivity extends AbsBaseActivity {
    @Inject
    @Nullable
    protected LayoutInflaterFactory layoutInflaterHook;
    private Fragment mActiveFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (layoutInflaterHook != null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            LayoutInflaterCompat.setFactory(layoutInflater, layoutInflaterHook);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestoreData(Bundle savedInstanceState) {
        mActiveFragment = (BaseFragment) getSupportFragmentManager().getFragment(
                savedInstanceState, BaseFragment.class.getName());

    }

    @Override
    protected void onPrepareData() {

    }

    @Override
    protected void onSaveData(Bundle outState) {
        getSupportFragmentManager()
                .putFragment(outState, BaseFragment.class.getName(), mActiveFragment);

    }

    /**
     * 设置要显示的fragment
     *
     * @param fragment 要显示的fragment
     * @param id       fragment覆盖的控件id
     * @return void
     */
    public void showContent(BaseFragment fragment, int id) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();

        ft.replace(id, fragment);
//        if (!fragment.isAdded()) {
//            ft.add(id, fragment);
//            if (null != mActiveFragment) {
//                ft.hide(mActiveFragment);
//            }
//        } else {
//            ft.hide(getActiveFragment()).show(fragment);
//        }
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
        mActiveFragment = fragment;
        SharedPreferencesUtil.getInstance(this).putInt("mActiveFragment", mActiveFragment.getId());
    }

    public void showMainContent(Fragment fragment, int id) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();

        if (!fragment.isAdded()) {
            ft.add(id, fragment);
            if (null != mActiveFragment) {
                ft.hide(mActiveFragment);
            }
        } else {
            ft.hide(getActiveFragment()).show(fragment);
        }
        ft.commitAllowingStateLoss();
        mActiveFragment = fragment;
        SharedPreferencesUtil.getInstance(this).putInt("mActiveFragment", mActiveFragment.getId());
    }

    public Fragment getActiveFragment() {
        if (null == mActiveFragment) {
            int id = SharedPreferencesUtil.getInstance(this).getInt("mActiveFragment", -1);
            mActiveFragment = (BaseFragment) mFragmentManager.findFragmentById(id);
        }
        return mActiveFragment;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {

                this.finish();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
