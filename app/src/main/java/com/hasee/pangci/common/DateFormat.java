package com.hasee.pangci.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateFormat {

    public static int differentDaysByMillisecond(String startTime, String endTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dateStart = format.parse(startTime);
            Date dateEnd = format.parse(endTime);
            int days = (int) ((dateEnd.getTime() - dateStart.getTime()) / (1000 * 3600 * 24));
            return days;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
