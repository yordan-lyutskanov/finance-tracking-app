package com.yordan.finance.utils;



public class FilterSortUtils {
    public static final String FILTER_DATE_TODAY = "WHERE date > " + DateUtils.beginningOfTodayAsInt();
    public static final String FILTER_DATE_YESTERDAY = "WHERE date > " + DateUtils.yesterdayAsInt();
    public static final String FILTER_DATE_LAST_WEEK = "WHERE date > " + DateUtils.sevenDaysAgoDateAsInt();
    public static final String FILTER_DATE_LAST_MONTH = "WHERE date > " + DateUtils.thirtyDaysAgoAsInt();
    public static final String FILTER_DATE_LAST_YEAR = "WHERE date > " + DateUtils.yearAgoAsInt();
    public static final String FILTER_DATE_ALL = "";


    public static final String FILTER_NO_FILTER = "";
    public static final String FILTER_CATEGORY_HOUSING = "WHERE category LIKE '1%'";
    public static final String FILTER_CATEGORY_UTILITIES = "WHERE category LIKE '2%'";
    public static final String FILTER_CATEGORY_GROCERIES = "WHERE category LIKE '3%'";
    public static final String FILTER_CATEGORY_TRANSPORTATION = "WHERE category LIKE '4%'";
    public static final String FILTER_CATEGORY_PERSONAL = "WHERE category LIKE '5%'";
    public static final String FILTER_CATEGORY_ENTERTAINMENT = "WHERE category LIKE '6%'";
    public static final String FILTER_CATEGORY_HEALTH = "WHERE category LIKE '7%'";
    public static final String FILTER_CATEGORY_SAVINGS = "WHERE category LIKE '8%'";
    public static final String FILTER_CATEGORY_OTHERS = "WHERE category LIKE '9%'";

    public static final String ORDER_BY_DATE_ASC = "date ASC";
    public static final String ORDER_BY_DATE_DESC = "date DESC";

    public static final String ORDER_BY_CATEGORY_ASC = "category ASC";
    public static final String ORDER_BY_CATEGORY_DESC = "category DESC";

    public static final String ORDER_BY_AMOUNT_ASC = "amount ASC";
    public static final String ORDER_BY_AMOUNT_DESC = "amount DESC";

    private static String activeFilter;
    private static String activeSort;

    public static void setActiveSort(String activeSort) {
        FilterSortUtils.activeSort = activeSort;
    }

    public static void setActiveFilter(String activeFilter) {
        FilterSortUtils.activeFilter = activeFilter;
    }

    public static String getActiveSort() {
        if(activeSort == null){
            return ORDER_BY_DATE_DESC;
        }
        return activeSort;
    }

    public static String getActiveFilter() {
        if(activeFilter == null){
            return FILTER_NO_FILTER;
        }
        return activeFilter;
    }

}
