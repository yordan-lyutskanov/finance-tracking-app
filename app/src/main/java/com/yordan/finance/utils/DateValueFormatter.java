package com.yordan.finance.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Calendar;

public class DateValueFormatter implements IAxisValueFormatter{

    public static final int WEEKLY = 0;
    private static final int MONTHLY = 1;
    private static final int QUARTERLY = 2;
    private static final int YEARLY = 3;
    private static final int BEGINNING_OF_TIME = 4;

    private String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    
    private static DateValueFormatter formatter;
    private static int since;
    
    public static DateValueFormatter getInstance(int sinceConstant){
        if(formatter == null){
            formatter = new DateValueFormatter();
        }
        since = sinceConstant;
        return formatter;
    }
    

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        switch (since) {
            case WEEKLY:

                Calendar c = Calendar.getInstance();
                c.setTime(DateUtils.intToDate(DateUtils.currentDate()));
                int dayOfTheWeek = c.get(Calendar.DAY_OF_WEEK) - 2;

                switch ((int) value){
                    case 6:
                        return "Today";
                    case 5:
                        return "Yesterday";
                    case 4:
                        int index = 2;
                        return daysOfWeek[dayOfTheWeek - index >= 0 ? dayOfTheWeek - index : dayOfTheWeek - index + 7 ];
                    case 3:
                        index = 3;
                        return daysOfWeek[dayOfTheWeek - index >= 0 ? dayOfTheWeek - index : dayOfTheWeek - index + 7 ];
                    case 2:
                        index = 4;
                        return daysOfWeek[dayOfTheWeek - index >= 0 ? dayOfTheWeek - index : dayOfTheWeek - index + 7 ];
                    case 1:
                        index = 5;
                        return daysOfWeek[dayOfTheWeek - index >= 0 ? dayOfTheWeek - index : dayOfTheWeek - index + 7 ];
                    case 0:
                        index = 6;
                        return daysOfWeek[dayOfTheWeek - index >= 0 ? dayOfTheWeek - index : dayOfTheWeek - index + 7 ];
                }

            case MONTHLY:
                return "Yet to be implemented";
            case QUARTERLY:
                return "Yet to be implemented";
            case YEARLY:
                return "Yet to be implemented";
            case BEGINNING_OF_TIME:
                return "Yet to be implemented";
                default:
                    return "Default";
        }
    }
}
