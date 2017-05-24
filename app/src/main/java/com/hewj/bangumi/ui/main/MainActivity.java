package com.hewj.bangumi.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hewj.bangumi.R;
import com.hewj.bangumi.base.BaseActivity;
import com.hewj.bangumi.util.TimeUtil;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewj on 2017/5/10.
 * 番剧列表
 */
public class MainActivity extends BaseActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener {
    private long mExitTime;
    String[] weeks = {"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
    public static MainActivity instance;
    public static void launch(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
//        initToolbar();
        initMenuFragment();

    }

    /**
     * 初始化右边菜单栏
     */
    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    /**
     * 初始化菜单栏各个按钮
     */
    private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject monday = new MenuObject("星期一");
        monday.setResource(R.drawable.monday);

        MenuObject tuesday = new MenuObject("星期二");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.tuesday);
        tuesday.setBitmap(b);

        MenuObject wednesday = new MenuObject("星期三");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.wednesday));
        wednesday.setDrawable(bd);

        MenuObject thursday = new MenuObject("星期四");
        thursday.setResource(R.drawable.thursday);

        MenuObject friday = new MenuObject("星期五");
        friday.setResource(R.drawable.friday);

        MenuObject sunday = new MenuObject("星期六");
        sunday.setResource(R.drawable.sunday);

        MenuObject saturday = new MenuObject("星期日");
        saturday.setResource(R.drawable.saturday);

        menuObjects.add(close);
        menuObjects.add(monday);
        menuObjects.add(tuesday);
        menuObjects.add(wednesday);
        menuObjects.add(thursday);
        menuObjects.add(friday);
        menuObjects.add(sunday);
        menuObjects.add(saturday);

        return menuObjects;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    /**
     * 初始化标题栏，获取当前星期显示
     */
    @Override
    protected void onInitTitle() {
        instance=MainActivity.this;
        mAppBar.setTitleText(TimeUtil.getWeek());
        mAppBar.addMenu(getBaseContext(),R.drawable.btn_add);
        ((ImageView)mAppBar.getMenu(1)).setPadding(0,0,50,0);
        mAppBar.getMenu(1).setOnClickListener(l->{
            if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
            }
        });
    }

    @Override
    protected void onResolveIntent(Intent intent) {

    }

    /**
     * 初始化默认显示的fragment
     */
    @Override
    protected void onInitFragment() {
        MainFragment mainFragment=new MainFragment();
        Bundle bundle=new Bundle();
        int currentTime= TimeUtil.currentWeekOfDay();
        bundle.putInt("arg",currentTime);
        Log.i("week",""+currentTime);
        mainFragment.setArguments(bundle);
        showContent(mainFragment,R.id.container);
    }

    @Override
    protected void onRestoreData(Bundle savedInstanceState) {

    }

    @Override
    protected void onPrepareData() {

    }

    @Override
    protected void onSaveData(Bundle outState) {

    }

    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            finish();
        }
    }

    /**
     * 菜单栏点击事件，跳过第一个X按钮
     */
    @Override
    public void onMenuItemClick(View clickedView, int position) {
        if(position>0) {
            MainFragment mainFragment = new MainFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("arg", position - 1);
            mainFragment.setArguments(bundle);
            showContent(mainFragment, R.id.container);
            mAppBar.setTitleText(weeks[position-1]);
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
