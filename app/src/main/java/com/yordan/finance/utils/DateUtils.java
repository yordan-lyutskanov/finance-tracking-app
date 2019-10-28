package com.yordan.finance.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {



    public static int currentDate(){
        return (int) (System.currentTimeMillis() / 1000L);
    }

    public static String intDateToString(int intDate){
            String date = (new Date(intDate * 1000L)).toString();
            return (date.substring(4, 10)) + (date.substring(29));
    }

    public static String longDateToString(long longDate){
        String date = (new Date(longDate)).toString();
        return (date.substring(3, 10)) + (date.substring(29));
    }

    static Date intToDate(int intDate){
        return new Date(intDate * 1000L);
    }

    public static int longDateToInt(long date){
        return (int) (date / 1000L);
    }

    public static int sevenDaysAgoDateAsInt(){
        return currentDate() - secToday() - 6 * 24 * 60 * 60;
    }

    public static int thirtyDaysAgoAsInt(){
        return currentDate() - secToday() - 29 * 24 * 60 * 60;
    }

    public static int beginingOfADay(int date){
        Date d1 = intToDate(date);
        String strDate = d1.toString().replaceFirst("\\d\\d:\\d\\d:\\d\\d", "00:00:00");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date d2 = new Date();
        try{
            d2 = dateFormat.parse(strDate);
        }catch (ParseException e){
            e.printStackTrace();
        }

        return d2 != null ? (int) (d2.getTime() / 1000L) : -1;
    }

    public static int yearAgoAsInt(){
        return currentDate() - secToday() - 364 * 24 * 60 * 60;
    }

    public static int beginningOfTodayAsInt(){
        return currentDate() - secToday();
    }

    static int yesterdayAsInt() {
        return beginningOfTodayAsInt() - 24 * 60 * 60;
    }

    private static int secToday(){
        String now =  (new Date().toString());
        String hours = now.substring(11, 13);
        String mins = now.substring(14, 16);
        String secs = now.substring(17, 19);

        return Integer.parseInt(hours) * 3600 + Integer.parseInt(mins) * 60 + Integer.parseInt(secs);
    }

    public static int aDay(){
        return 24 * 60 * 60;
    }

    public static int[] parseDate(int intDate){
        Date date = DateUtils.intToDate(intDate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new int[]{year, month, day};
    }

    public static Date parseIntArray(int[] dateArray){
        Calendar c = Calendar.getInstance();
        c.set(dateArray[0], dateArray[1], dateArray[2]);
        return c.getTime();
    }



}
