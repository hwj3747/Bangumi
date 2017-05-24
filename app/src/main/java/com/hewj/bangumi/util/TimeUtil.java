package com.hewj.bangumi.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * 时间处理的工具类
 * @author hwj
 * @version 1.0, 2017/5/11
 */
public class TimeUtil {

    /**
     * 获取当前日期所对应的星期的数值 <br>
     * 1、获取当前时间<br>
     * 2、通过Calendar.DAY_OF_WEEK得到当前星期<br>

     * @return mWeek
     */
    public static int currentWeekOfDay(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int mWeek = c.get(Calendar.DAY_OF_WEEK);
        if(mWeek==1) return 6;
        if(mWeek==7) return 5;
        return mWeek-2;
    }

    /**
     * 获取当前日期所对应的星期的中文名 <br>
     * 1、获取当前时间<br>
     * 2、通过Calendar.DAY_OF_WEEK得到当前星期<br>

     * @return String
     */
    public static String getWeek(){
        String[] weeks = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
        Calendar cal = Calendar.getInstance();
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
            week_index = 0;
        }
        return weeks[week_index];
    }

    public static void main(String[] args){
        System.out.print(currentWeekOfDay());
    }
}
