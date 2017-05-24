package com.hewj.library.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: pj
 * Date: 14-3-7
 * Time: 上午10:44
 * To change this template use File | Settings | File Templates.
 */
public class DateUtil {
    public static final String TO_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String TO_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String TO_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String TO_SHORT_YYYY_MM_DD_HH_MM = "yyyy/MM/dd HH:mm";
    public static final String TO_SHORTpYYYYpMMpDD_HH_MM = "yyyy.MM.dd HH:mm";
    public static final String TO_HH_MM_SS = "HH:mm:ss";
    public static final String TO_HH_MM = "HH:mm";
    public static final String FROM_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String FROM_HHMMSS = "HHmmss";

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(tz);
        long unixTime = calendar.getTimeInMillis();
        long unixTimeGMT = unixTime;
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date date = new Date(unixTimeGMT);
        String getTime = timeFormat.format(date);
        return getTime;
    }

    public static String getCurrentTime(String format) {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(tz);
        long unixTime = calendar.getTimeInMillis();
        long unixTimeGMT = unixTime;
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                format);
        Date date = new Date(unixTimeGMT);
        String getTime = timeFormat.format(date);
        return getTime;
    }

    public static String getAPMCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(tz);
        long unixTime = calendar.getTimeInMillis();
        long unixTimeGMT = unixTime;
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                "aa HH:mm");
        Date date = new Date(unixTimeGMT);
        String getTime = timeFormat.format(date);
        return getTime;
    }

    public static Date formatDate(String parttern, String dateString) {
        if (dateString == null || "".equals(dateString)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(parttern);
        try {
            Date date = sdf.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String formatTime(String parttern, String timeString) {
        if (timeString == null || "".equals(timeString)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(FROM_HHMMSS);
        try {
            Date date = sdf.parse(timeString);
            return format(parttern, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long toSecond(String timeString) {
        if (timeString == null) {
            return 0;
        }
        String[] timeSplits = timeString.split(":");
        if (timeSplits.length < 3) {
            return 0;
        }

        int hour = Integer.valueOf(timeSplits[0]);
        int min = Integer.valueOf(timeSplits[1]);
        int sec = Integer.valueOf(timeSplits[2]);
        long millsecond = hour * 60 * 60 + min * 60 + sec;
        return millsecond;
    }

    public static String format(String parttern, Date date) {
        DateFormat format = new SimpleDateFormat(parttern);
        return format.format(date);
    }

    /**
     * 转换为时间
     *
     * @param mss 要转换的毫秒数
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatTime(long mss) {
        long hours = mss / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds);
    }

    /**
     * 转换为时间
     *
     * @param mss 要转换的毫秒数
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatTime2(long mss) {
        long hours = mss / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        String sHours = hours > 0 ? hours + "小时" : "";
        String sMinutes = minutes > 0 ? minutes + "分" : "";
        String sSeconds = seconds > 0 ? seconds + "秒" : "";

        return sHours + sMinutes + sSeconds;
    }

    /**
     * 比较时间大小
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int compare_date(String date1, String date2) {
        if (date1.length() < 11) {
            date1 += " 00:00:00";
        }
        if (date2.length() < 11) {
            date1 += " 00:00:00";
        }
        int result = 0;
        try {
            if (DateUtil.toDate(date1).after(DateUtil.toDate(date2))) {

                result = 1;
            } else if (DateUtil.toDate(date1).before(DateUtil.toDate(date2))) {

                result = -1;
            } else {
                result = 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;

    }

    /**
     * 把String类型的值变为Date类型
     */
    public static Date toDate(String value) {
        if (value.length() == 16)
            value += ":00";

        SimpleDateFormat sdf = new SimpleDateFormat(TO_YYYY_MM_DD_HH_MM_SS);
        Date date = null;
        try {
            date = sdf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    }

    /**
     * 把String类型的值转换为指定的String类型
     *
     * @param format
     * @param value
     * @return
     */
    public static String toStringFormat(String format, String value) {
        try {
            Date date = toDate(value);
            return format(format, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 把String类型的值变为Date类型
     */
    public static Date toDate(String parttern, String value) {
        if (value.length() == 16)
            value += ":00";

        SimpleDateFormat sdf = new SimpleDateFormat(parttern);
        Date date = null;
        try {
            date = sdf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    }


    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 字符串的日期格式的计算
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }


    public static void main(String[] strs) {
//        System.out.println(formatDate(TO_YYYY_MM_DD_HH_MM_SS, "20140101111111"));
//        System.out.println(formatTime(1000*60*60));
        System.out.println("date getTime = " + getAPMCurrentTime());
    }


    /**
     * 根据开始时间和结束时间返回时间段内的时间集合
     *
     * @param beginDate
     * @param endDate
     * @return List
     */
    public static List<Date> getDatesBetweenTwoDate(Date beginDate, Date endDate) {
        List lDate = new ArrayList();
        lDate.add(beginDate);//把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        //使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            //根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);//把结束时间加入集合
        return lDate;
    }


    /**
     * 获取某月的最后一天
     *
     * @throws
     * @Title:getLastDayOfMonth
     * @Description:
     * @param:@param year
     * @param:@param month
     * @param:@return
     * @return:String
     */

    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfMonth = sdf.format(cal.getTime());

        return lastDayOfMonth;
    }

    public static Date getDateLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        return cal.getTime();
    }

}
