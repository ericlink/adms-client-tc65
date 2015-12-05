package net.diabetech.util;

import java.util.Calendar;
import net.diabetech.lang.StringHelper;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class CalendarHelper {

    private CalendarHelper() {
    }

    public static String getFormattedMonth(Calendar cal) {
        return StringHelper.format(
                String.valueOf(cal.get(Calendar.MONTH) + 1),
                "##");
    }

    public static String getFormattedDayOfMonth(Calendar cal) {
        return StringHelper.format(
                String.valueOf(cal.get(Calendar.DAY_OF_MONTH)),
                "##");
    }

    public static String getFormattedYear(Calendar cal) {
        return StringHelper.format(
                String.valueOf(cal.get(Calendar.YEAR) - 2000),
                "##");
    }

    public static String getFormattedHourOfDay(Calendar cal) {
        return StringHelper.format(
                String.valueOf(cal.get(Calendar.HOUR_OF_DAY)),
                "##");
    }

    public static String getFormattedMinute(Calendar cal) {
        return StringHelper.format(
                String.valueOf(cal.get(Calendar.MINUTE) + 1),
                "##");
    }
}
