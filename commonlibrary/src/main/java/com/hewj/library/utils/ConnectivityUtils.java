package com.hewj.library.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;


/**
 * 
 * @author niutf
 * 
 */
public class ConnectivityUtils {

    private static final String TAG = ConnectivityUtils.class.getSimpleName();
    
    // Loop time while waiting (stopgap in case we don't get a broadcast)
    private static final int CONNECTIVITY_WAIT_TIME = 10*60*1000;

    // Sentinel value for "no active network"
    public static final int NO_ACTIVE_NETWORK = -1;

    private static boolean sMobileConnected;
    private static boolean sConnected;
    
    private static boolean sRoaming;

    public static boolean isConnected() {
        return sConnected;
    }

    public static boolean isNonMobileConnected() {
        return sConnected && !sMobileConnected;
    }
    
    public static boolean isMobileConnected() {
        return sMobileConnected;
    }
    
    public static boolean forceCheckConnectivityState(){
        ConnectivityManager connectMgr = EnvironmentUtils.getConnectivityManager();
        NetworkInfo activeNetInfo = connectMgr.getActiveNetworkInfo();
        if(activeNetInfo==null){
            sConnected = false;
            sMobileConnected =false;
            sRoaming = false;
        }else{
            sConnected = activeNetInfo.isConnected();
            sMobileConnected = sConnected && isNetworkTypeMobile(activeNetInfo.getType());
            sRoaming = activeNetInfo.isRoaming();
        }
        
        return isConnected();
    }

    public static class ConnectivityChangeReceiver extends BroadcastReceiver {

        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            boolean originConnected = isConnected();
            boolean nowConnected = forceCheckConnectivityState();



//            if(originConnected != nowConnected){
//                UnitedAccount.getInstance().notifyConnectivityChanged();
//            }
        }
    }

    public static int getActiveNetworkType() {
        NetworkInfo info = EnvironmentUtils.getConnectivityManager().getActiveNetworkInfo();
        if (info == null) return NO_ACTIVE_NETWORK;
        return info.getType();
    }

    public static String getActiveNetworkTypeDescription(){
        NetworkInfo activeNetwork = EnvironmentUtils.getConnectivityManager().getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isAvailable()) {
            return "none";
        }
        int netType = activeNetwork.getType();
        switch (netType){
            case ConnectivityManager.TYPE_ETHERNET:
                return "ETHERNET";
            case ConnectivityManager.TYPE_WIFI:
                return "wifi";
            case ConnectivityManager.TYPE_MOBILE:
                if (isFastMobileNetwork()) {
                    return "fast";
                } else {
                    return "slow";
                }
            default:
                return "unknown: " + netType;

        }
    }


    private static boolean isFastMobileNetwork() {
        int networkType = EnvironmentUtils.getTelephonyManager().getNetworkType();
  
        switch (networkType) {  
        case TelephonyManager.NETWORK_TYPE_1xRTT:  
            return false; // ~ 50-100 kbps  
        case TelephonyManager.NETWORK_TYPE_CDMA:  
            return false; // ~ 14-64 kbps  
        case TelephonyManager.NETWORK_TYPE_EDGE:  
            return false; // ~ 50-100 kbps  
        case TelephonyManager.NETWORK_TYPE_EVDO_0:  
            return true; // ~ 400-1000 kbps  
        case TelephonyManager.NETWORK_TYPE_EVDO_A:  
            return true; // ~ 600-1400 kbps  
        case TelephonyManager.NETWORK_TYPE_GPRS:  
            return false; // ~ 100 kbps  
        case TelephonyManager.NETWORK_TYPE_HSDPA:  
            return true; // ~ 2-14 Mbps  
        case TelephonyManager.NETWORK_TYPE_HSPA:  
            return true; // ~ 700-1700 kbps  
        case TelephonyManager.NETWORK_TYPE_HSUPA:  
            return true; // ~ 1-23 Mbps  
        case TelephonyManager.NETWORK_TYPE_UMTS:  
            return true; // ~ 400-7000 kbps  
        case TelephonyManager.NETWORK_TYPE_EHRPD:  
            return true; // ~ 1-2 Mbps  
        case TelephonyManager.NETWORK_TYPE_EVDO_B:  
            return true; // ~ 5 Mbps  
        case TelephonyManager.NETWORK_TYPE_HSPAP:  
            return true; // ~ 10-20 Mbps  
        case TelephonyManager.NETWORK_TYPE_IDEN:  
            return false; // ~25 kbps  
        case TelephonyManager.NETWORK_TYPE_LTE:  
            return true; // ~ 10+ Mbps  
        case TelephonyManager.NETWORK_TYPE_UNKNOWN:  
            return false;  
        default:  
            return false;  
  
        }  
    }
    
    /**
     * Copy from {@link android.net.ConnectivityManager}.
     * Checks if a given type uses the cellular data connection.
     * This should be replaced in the future by a network property.
     * @param networkType the type to check
     * @return a boolean - {@code true} if uses cellular network, else {@code false}
     */
    public static boolean isNetworkTypeMobile(int networkType) {
        switch (networkType) {
            case ConnectivityManager.TYPE_MOBILE:
            case ConnectivityManager.TYPE_MOBILE_MMS:
            case ConnectivityManager.TYPE_MOBILE_SUPL:
            case ConnectivityManager.TYPE_MOBILE_DUN:
            case ConnectivityManager.TYPE_MOBILE_HIPRI:
            //the below types are hide
//            case ConnectivityManager.TYPE_MOBILE_FOTA:
//            case ConnectivityManager.TYPE_MOBILE_IMS:
//            case ConnectivityManager.TYPE_MOBILE_CBS:
//            case ConnectivityManager.TYPE_MOBILE_IA:
                return true;
            default:
                return false;
        }
    }

    public static boolean isRoaming() {
        return sRoaming;
    }
}
