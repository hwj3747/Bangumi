package com.hewj.library.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hewj.library.R;
import com.hewj.library.utils.EnvironmentUtils;

import java.lang.reflect.Field;

//import android.support.v7.internal.widget.TintTypedArray;

/**
 * @author Limj
 * @date 2015/7/25
 */
public class AppBar extends Toolbar {

    private static final int START = 0xabc;

    private Resources res;
    private TextView mTitleView;

    private TextView mTitleView1;

    private int state = 0;

    public TextView getmTitleView() {
        return mTitleView;
    }

    public void setmTitleView(TextView mTitleView) {
        this.mTitleView = mTitleView;
    }

    public TextView getmTitleView1() {
        return mTitleView1;
    }

    public void setmTitleView1(TextView mTitleView1) {
        this.mTitleView1 = mTitleView1;
    }

    public AppBar(Context context) {
        this(context, null);
    }

    public AppBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.appBarStyle);
    }

    public AppBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        res = getResources();
        init(context, attrs, defStyleAttr);
    }

    private String getStr(int id) {
        try {
            String str = res.getString(id);
            if (str.indexOf("res") == 0) {
                // 是图片
                return null;
            } else {
                return str;
            }
        } catch (Resources.NotFoundException ex) {
            return null;
        }
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, R.styleable.AppBar, defStyleAttr, 0);

        int menu01Id = a.getResourceId(R.styleable.AppBar_menu1, 0);
        int menu02Id = a.getResourceId(R.styleable.AppBar_menu2, 0);
        int menu03Id = a.getResourceId(R.styleable.AppBar_menu3, 0);
        int menu04Id = a.getResourceId(R.styleable.AppBar_menu4, 0);
        int menu05Id = a.getResourceId(R.styleable.AppBar_menu5, 0);

        int centerId = a.getResourceId(R.styleable.AppBar_centerTitle, 0);

        a.recycle();
        int[] menuIds = {menu01Id, menu02Id, menu03Id, menu04Id, menu05Id};
        for (int menuId : menuIds) {
            if (menuId == 0) {
                continue;
            }
            addMenu(context, menuId);
        }
        if (centerId != 0)
            initCenterTitle(context, centerId);
    }

    public void initTitle(Context context){
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mTitleView = new TextView(context, null, R.attr.menuTextStyle);
        mTitleView.setText("");
        mTitleView.setLayoutParams(params);
        addView(mTitleView);
    }
    private void initCenterTitle(Context context, int centerId) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mTitleView1 = new TextView(context, null, R.attr.menuTextStyle);
        mTitleView1.setText("");
        mTitleView1.setLayoutParams(params);
        mTitleView = new TextView(context, null, R.attr.menuTextStyle);
        mTitleView.setText(getStr(centerId));
        mTitleView.setId(centerId);
        mTitleView.setLayoutParams(params);
        addView(mTitleView);
        addView(mTitleView1);
        mTitleView1.setVisibility(View.GONE);
    }

    public void addMenu(Context context, int menuId) {
        View menuV;
        //设定布局的各种参数
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, Gravity.RIGHT);
        String text;
        if ((text = getStr(menuId)) != null) {
            menuV = new TextView(context, null, R.attr.menuTextStyle);
            ((TextView) menuV).setText(text);
            ((TextView) menuV).setTextColor(getResources().getColor(R.color.black));
        } else {
            menuV = new ImageView(context, null, R.attr.menuImageStyle);
            ((ImageView) menuV).setImageResource(menuId);
            ((ImageView) menuV).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
        int pos = -1;
        for (int i = 1; i <= 5; i++) {
            if (null == findViewById(START + i)) {
                menuV.setId(START + i);
                menuV.setLayoutParams(params);
                addView(menuV);
                return;
            }
        }

        if (pos == -1) {
            Toast.makeText(getContext(), "最多5个，已无法再添加menu", Toast.LENGTH_LONG).show();
        }
    }


    //    -------------------------自添加ZSF-----------------------------------

    public void addMenu(Context context, int menuId,String menuText,String location) {//文字加图片菜单
        View menuV;
        //设定布局的各种参数
        LayoutParams params;
        if(location.equals("left"))
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, Gravity.LEFT);
        else
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, Gravity.RIGHT);
        menuV = new TextView(context, null, R.attr.menuTextStyle);
        ((TextView) menuV).setText(menuText);
        ((TextView) menuV).setTextColor(Color.parseColor("#818181"));
        ((TextView) menuV).setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(menuId), null);
        int pos = -1;
        for (int i = 1; i <=5; i++) {

            if (null == findViewById(START + i)) {
                menuV.setId(START + 1);
                menuV.setLayoutParams(params);
                addView(menuV);
                return;
            }
        }

        if (pos == -1) {
            Toast.makeText(getContext(), "已无法再添加menu", Toast.LENGTH_LONG).show();
        }
    }


    //    -------------------------------自添加---------------------------------

    public void canFinishActivity() {
        setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getMenu(int pos) {
        return (T) findViewById(START + pos);
    }


    public TextView getTitleView() {
        return (TextView) getSubView("mTitleTextView");
    }

    public TextView getSubtitleView() {
        return ((TextView) getSubView("mSubtitleTextView"));
    }

    public ImageButton getNavButton() {
        return (ImageButton) getSubView("mNavButtonView");
    }

    private View getSubView(String name) {
        Field field;
        try {
            field = Toolbar.class.getDeclaredField(name);
            field.setAccessible(true);
            View v = (View) field.get(this);
            field.setAccessible(false);
            return v;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setTitleText(String title) {
        mTitleView.setText(title);
    }

    public void setTitleTextColor(@ColorInt int color) {
        if (mTitleView != null) {
            mTitleView.setTextColor(color);
        }
    }

    public void setTitleId(Context context, int menuId) {
        String text;
        if ((text = getStr(menuId)) != null) {
            mTitleView.setText(text);
        } else {
            mTitleView.setBackgroundResource(menuId);
        }
    }

    public void setTitleCompoundDrawables(Context context, String text
            , int left, int top, int right, int bottom) {
        mTitleView.setText(text);
        mTitleView.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        mTitleView.setCompoundDrawablePadding(EnvironmentUtils.dip2px(5));
    }
}