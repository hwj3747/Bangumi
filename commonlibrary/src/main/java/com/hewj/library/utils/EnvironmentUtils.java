package com.hewj.library.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;

import java.io.File;
public class EnvironmentUtils {

    private static EnvironmentUtils sInstance;
    private static TelephonyManager mTelephonyManager;
    private static PackageInfo mPackageInfo;
    private static XmlResourceParser mSubjectTextColor;
    private static LayoutInflater mInflater;
    private Context mContext;
    private static ConnectivityManager mConnectivityManager;
    private static Resources mResources;
    private static long mMainThreadId;
    private static int mHeight;
    private static int mWidth;
    private static int mStatusHeight;
    private static int mActionbarHeight;

    public static int screenWidth;
    public static int screenHeight;
    public static float density;
    public static float scaleDensity;
    public static float xdpi;
    public static float ydpi;
    public static int   densityDpi;
    public static int screenMin;

    public enum ScreenType {
        TYPE_PHONE(0),
        TYPE_TABLET_7_INCH(1),
        TYPE_TABLET_10_INCH(2);

        final int mValue;

        ScreenType(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }

        static ScreenType valueOf(int value){
            for (ScreenType type: ScreenType.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return TYPE_PHONE;
        }

        @Override
        public String toString() {
            String string="";
            switch (mValue){
                case 0:
                    string = "TYPE_PHONE";
                    break;
                case 1:
                    string = "TYPE_TABLET_7_INCH";
                    break;
                case 2:
                    string = "TYPE_TABLET_10_INCH";
                    break;
            }
            return string;
        }
    }

    public synchronized static EnvironmentUtils init(Context context) {
        if(null == sInstance){
            sInstance = new EnvironmentUtils(context);
            mMainThreadId = Thread.currentThread().getId();

            WindowManager wm = (WindowManager) sInstance.mContext.getSystemService(Context.WINDOW_SERVICE);
            Point outSize = new Point();
            mHeight = outSize.y;
            mWidth = outSize.x;
            mStatusHeight = -1;
            mActionbarHeight = -1;

            DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
            screenMin = (screenWidth > screenHeight) ? screenHeight : screenWidth;
            density = dm.density;
            scaleDensity = dm.scaledDensity;
            xdpi = dm.xdpi;
            ydpi = dm.ydpi;
            densityDpi=dm.densityDpi;
//
//            // start service
//            startJadeService(context);
        }
        return sInstance;
    }

    public static void updateScreenParams(){
        WindowManager wm = (WindowManager) sInstance.mContext.getSystemService(Context.WINDOW_SERVICE);
        Point outSize = new Point();
        wm.getDefaultDisplay().getSize(outSize);
        mHeight = outSize.y;
        mWidth = outSize.x;
    }

    private EnvironmentUtils(Context context) {
        mContext = context;
    }
    
    //synchronized
    public static Context getAppContext() {
        return sInstance.mContext;
    }

    public static CharSequence getAppLabel() {
        return sInstance.mContext.getPackageManager().getApplicationLabel(sInstance.mContext.getApplicationInfo());
    }
    
    public static TelephonyManager getTelephonyManager() {
        if (mTelephonyManager == null)
            mTelephonyManager = (TelephonyManager)sInstance.mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyManager;
    }
    
    public static PackageInfo getPackageInfo() {
        if (mPackageInfo == null) {
            try {
                mPackageInfo = sInstance.mContext.getPackageManager().getPackageInfo(sInstance.mContext.getPackageName(), 0);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return mPackageInfo;
    }

    public static String getPackageName() {
        return sInstance.mContext.getPackageName();
    }

    public static int getUid() {
        return sInstance.mContext.getApplicationInfo().uid;
    }
    
    public static ConnectivityManager getConnectivityManager() {
        if (mConnectivityManager == null) {
            mConnectivityManager =
                    (ConnectivityManager)sInstance.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        return mConnectivityManager;
    }
    
    public static Resources getResources() {
        if (mResources == null)
            mResources = sInstance.mContext.getResources();
        return mResources;
    }
    
    public static File getFilePath(String fileName) {
        return sInstance.mContext.getFileStreamPath(fileName);
    }
    
    public static File getAppDataPath() {
        return sInstance.mContext.getFilesDir();
    }
    
    public static File getAppCachePath() {
        return sInstance.mContext.getCacheDir();
    }
    
    public static File getExternalDataPath() {
        return sInstance.mContext.getExternalFilesDir(null);
    }
    
    public static File getExternalCachePath() {
        return sInstance.mContext.getExternalCacheDir();
    }
    
    public static long getMainThreadId() {
        return mMainThreadId;
    }
    
    public static int getScreenHeight(){
        return mHeight;
    }
    
    public static int getScreenWidth(){
        return mWidth;
    }

    public static int getStatusHeight() {
        return mStatusHeight;
    }

    public static void setStatusHeight(int statusHeight) {
        mStatusHeight = statusHeight;
    }

    public static int getActionbarHeight() {
        return mActionbarHeight;
    }

    public static void setActionbarHeight(int actionbarHeight) {
        mActionbarHeight = actionbarHeight;
    }

    public static int getScreenTop(){
        return mStatusHeight+mActionbarHeight;
    }
//    public static String getDeviceId() {
//        return getTelephonyManager().getDeviceId();
//    }
//    
//    public static String getAppVersion(){
//        return getPackageInfo().versionCode + ":" + getPackageInfo().versionName;
//    }


    public static boolean hasJellyBeanMR1(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * dipתpx
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) { //#0001-
        final float scale = EnvironmentUtils.density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue){
        final float scale = EnvironmentUtils.density;
        return (int)((pxValue-0.5)/scale);
    }
}
