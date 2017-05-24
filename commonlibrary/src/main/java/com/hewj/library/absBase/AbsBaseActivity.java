package com.hewj.library.absBase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.LinearLayout;

import com.hewj.library.R;
import com.hewj.library.view.AppBar;

import java.util.ArrayList;

/**
 * Base Activity that implements logging for activity's life cycle.
 * Base Activity also implements a framework for relation of fragment
 * <p/>
 * <p/>
 * Activity的处理非常关键：
 * Activity和Fragment的生命周期有三种情况：
 * 1、Acitity和Fragment都是初次创建
 * 这种情况无需做任何处理
 * 判断方法：savedInstanceState为空
 * 2、Activity被销毁后恢复，但Fragment仍是原来那个
 * （这种情况多现于旋转屏幕等操作，且Fragment设置了setRetainInstance)
 * 这种情况Activity重新创建过，在Activity恢复数据后，要将自己保存的mFragment与原来的那几个Fragment
 * 对应上，并且将那几个原始的Fragment的Callback进行重新设置，关联到本Activity来
 * 判断方法：savedInstanceState不为空，并且fragment含有Callback
 * 3、Activity和Fragment都是被销毁后恢复的
 * (这种情况多现于按Home键或者切换到其他应用，然后长时间黑屏后点亮屏幕或者lauch本App）
 * 这种情况Acitivity恢复数据后，要将自己保存的mFragment与系统重新创建的那几个fragment进行对应，
 * 并且由于那几个系统恢复的fragment只做了空构造函数，于是还需要对他们进行init，然后再为它们设置Callback
 * 判断方法：savedInstanceState不为空，并且fragment不含有Callback
 *
 * @author cuij
 */
public abstract class AbsBaseActivity extends AppCompatActivity {

    protected FragmentManager mFragmentManager;

    protected AppBar mAppBar;

    private LinearLayout mContentLayout;
    private LayoutInflater mInflater;



    protected boolean mIsDestroyed = false;
    protected boolean mIsStarted = false;

    private ArrayList<Fragment> mFragmentItemList = new ArrayList<Fragment>();

    public void setTitle(String title){
        mAppBar.setTitleText(title);
    }
    private void logInfo(String msg) {

    }

    /**
     * If the sub class is an normal activity(not full screen , has actionbar), then no need to override onRequestWindowFeature and onSetContentView.
     * initial method will by called in the order below.
     * <li>onResolveBundle</li>
     * <li>getPrefTheme and set theme for this activity</li>
     * <li>onRequestWindowFeature</li>
     * <li>onSetContentView</li>
     * <li>setUpActionBar</li>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logInfo("onCreate");
        adjustScreenOritation();
        // ywx注释了，别这样用，否则页面切换效果就没有了
//        mThemeId = getPrefTheme();
//        setTheme(mThemeId);
        onRequestWindowFeature();
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            onResolveIntent(getIntent());
        } else {
            onRestoreData(savedInstanceState);
        }
        onSetContentView();
        onPrepareData();
        if (savedInstanceState == null) {
            onInitFragment();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof DialogFragment)
            return;
        try {
            Fragment baseFragment = fragment;
            if (!mFragmentItemList.contains(baseFragment)) {
                mFragmentItemList.add(baseFragment);
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("fragment must extends AbsBaseFragment " + fragment.getClass().toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        logInfo("onCreateOptionsMenu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        logInfo("onPrepareOptionsMenu");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
//            case R.id.home_more:
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Read the saved theme selection from shared-preferences.
     * @return the theme id we should use.
     */
/*    protected int getPrefTheme() {
        boolean enableNight = SharedPreferencesUtil.getInstance(this).getBoolean(Preferences.ENABLE_NIGHT_MODE,false);
        if (enableNight) {
            return R.style.NightTheme;
        } else {
            return R.style.NightTheme;
        }
    }*/

    /**
     * This will be called in onCreate method.
     * Override this and call {@code requestWindowFeature} to customize window feature.
     * eg. requestWindowFeature(Window.FEATURE_NO_TITLE)
     */
    protected void onRequestWindowFeature() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
    }

    /**
     * This will be called in onCreate method. Never call this outside {@code onCreate}.
     * Override this and call {@code setConentView}.
     */
    protected void onSetContentView() {
        setContentView(R.layout.base_activity_layout);

        mAppBar = (AppBar) findViewById(R.id.app_bar);
       // mAppBar.setBackgroundColor(Color.argb(247, 247, 247, 247));
        mAppBar.setTitleTextColor(Color.WHITE);
        mContentLayout = (LinearLayout) findViewById(R.id.contentLayout);

        int layoutID = getLayoutID();
        if (layoutID != 0) {


            onInitTitle();
            try {
                mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mContentLayout.addView(mInflater.inflate(layoutID, null), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            } catch (Exception e) {
                Log.i("MyException",e.toString());
                //LogUtils.e(LogUtils.LogCategory.LIFECYCLE, "mContentLayout.addView", e);
            }
        }
    }

    /**
     * 设置布局的id
     * @return
     */
    protected abstract int getLayoutID();

    /**
     * 初始化标题
     */
    abstract protected void onInitTitle();

    /**
     * This will be called in onCreate method when savedInstanceState is null
     * Override this to read extras from intent.
     */
    abstract protected void onResolveIntent(Intent intent);

    /**
     * This will be called in onCreate after all data prepared
     * will be called only first init(not restore by framework)
     * you can create first fragment hear
     *
     * @author cuij
     */
    abstract protected void onInitFragment();

    /**
     * This will be called in onCreate method when savedInstanceState is not null
     * Override this to read data from savedInstanceState
     *
     * @author cuij
     */
    abstract protected void onRestoreData(Bundle savedInstanceState);

    /**
     * This will be called in onCreate after all data and fragment prepared
     * you can init static data and prepare view、adapter or other static widget here
     */
    abstract protected void onPrepareData();

    @Override
    final protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onSaveData(outState);
        logInfo("onSaveInstanceState");
    }

    /**
     * This will be called in onSaveInstaceState
     * Override this to save Init Data in bundle
     * eg: init Account、Mailbox or MessageId
     *
     * @param outState
     * @author cuij
     */
    abstract protected void onSaveData(Bundle outState);

    /**
     * This will be called after onStart
     */
    @Override
    final protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        logInfo("onRestoreInstanceState");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logInfo("onResume");
    }

    /**
     * If want to override onStart, must call super.onStart() at first
     */
    @Override
    protected void onStart() {
        super.onStart();
//        int theme = getPrefTheme();
        mIsStarted = true;
//        if (mThemeId != theme) {
//            logInfo("theme changed: recreate activity");
//            recreate();
//        }
        logInfo("onStart");
    }

    /**
     * If want to override onStart, must call super.onStop() at first
     */
    @Override
    protected void onStop() {
        logInfo("onStop");
        mIsStarted = false;
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        logInfo("onPause");
    }

    @Override
    protected void onDestroy() {
        logInfo("onDestroy");
        mIsDestroyed = true;
        super.onDestroy();
    }

    public boolean isDestroyed() {
        return mIsDestroyed;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logInfo("onRestart");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logInfo("onActivityResult");
    }

    @Override
    public void onBackPressed() {
//        logInfo("onBackPressed");
//        if (mFragmentItemList.size() > 0) {
//            Fragment lastFragment = mFragmentItemList.get(mFragmentItemList.size() - 1);
//            for (Fragment fragment : mFragmentItemList) {
//            }
//            if (lastFragment.onBackPressed()) {
//                return;
//            } else {
//                boolean allIgnored = true;
//                for (int i = 0; i < mFragmentItemList.size() - 1; i++) {
//                    if (!mFragmentItemList.get(i).isIgnoredWhilePopBack()) {
//                        allIgnored = false;
//                        break;
//                    }
//                }
//                if (allIgnored) {
//                    finish();
//                    return;
//                }
//
//                mFragmentItemList.remove(mFragmentItemList.size() - 1);
//                mFragmentManager.popBackStack();
//
//                int i = mFragmentItemList.size() - 1;
//                while (i >= 0) {
//                    if (mFragmentItemList.get(i).isIgnoredWhilePopBack()) {
//                        mFragmentItemList.remove(i);
//                        mFragmentManager.popBackStack();
//                        i--;
//                    } else {
//                        break;
//                    }
//                }
//
//                if (mFragmentItemList.size() <= 0) {
//                    finish();
//                }
//            }
//        } else {
//            finish();
//        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        /*if(KeyEvent.KEYCODE_MENU==keyCode && mOverflowMenu!=null){
            View v = findViewById(R.id.home_more);
            if(!mOverflowMenu.isShowing()&&v!=null){
                mOverflowMenu.show(v);
                return true;
            }
        }
        for (AbsBaseFragment baseFragment : mFragmentItemList) {
            if (baseFragment != null && baseFragment.isActive()) {
                if (baseFragment.onKeyDown(keyCode, event)) {
                    return true;
                }
            }
        }*/
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
    }

    private void adjustScreenOritation() {
//        EnvironmentUtils.ScreenType screenType = EnvironmentUtils.getScreenType();
//        switch (screenType) {
//            case TYPE_PHONE:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                break;
//            case TYPE_TABLET_7_INCH:
//                break;

//            case TYPE_TABLET_10_INCH:
//                break;
//        }
//        EnvironmentUtils.updateScreenParams();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

}
