package com.ltst.core.util;

import android.support.annotation.IntDef;

import com.livetyping.utils.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {

    public static Calendar parseDateString(String dateFormat, String date) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            Calendar calendar = Calendar.getInstance();
            Date parse = format.parse(date);
            calendar.setTime(parse);
            int month = calendar.get(Calendar.MONTH);
//            month++;
            calendar.set(Calendar.MONTH, month);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getDiffAge(Calendar birthdayCalendar, Calendar currentCalendar) {
        int diffYears = getDiffYears(birthdayCalendar, currentCalendar);
        if (diffYears > 0) {
            return formatCountOfDateUnits(diffYears, YEARS);
        } else if (diffYears == 0) {
            int diffMonths = getDiffMonthInOneYear(birthdayCalendar, currentCalendar);
            if (diffMonths > 0) {
                return formatCountOfDateUnits(diffMonths, MONTHS);
            }
        }
        return StringUtils.EMPTY;
    }

    private static final int YEAR = Calendar.YEAR;
    private static final int MONTH = Calendar.MONTH;
    private static final int DAY_OF_MONTH = Calendar.DAY_OF_MONTH;
    private static final int COUNT_OF_MONTH_IN_YEAR = 12;

    private static int getDiffYears(Calendar birthdayCalendar, Calendar currentCalendar) {
        int diff = currentCalendar.get(YEAR) - birthdayCalendar.get(YEAR);
        if (birthdayCalendar.get(MONTH) > currentCalendar.get(MONTH) ||
                (birthdayCalendar.get(MONTH) == currentCalendar.get(MONTH) &&
                        birthdayCalendar.get(DAY_OF_MONTH) > currentCalendar.get(DAY_OF_MONTH))) {
            diff--;
        }
        return diff;
    }

    private static int getDiffMonthInOneYear(Calendar birthdayCalendar, Calendar currentCalendar) {
        int diffMonths;
        int birthdayMonth = birthdayCalendar.get(MONTH);
        int currentMonth = currentCalendar.get(MONTH);
        if (currentMonth >= birthdayMonth) {
            diffMonths = currentMonth - birthdayMonth;
        } else {
            diffMonths = COUNT_OF_MONTH_IN_YEAR + currentMonth - birthdayMonth;
        }

        if (birthdayCalendar.get(DAY_OF_MONTH) > currentCalendar.get(DAY_OF_MONTH)) {
            diffMonths--;
        }
        return diffMonths;
    }

    private static final int YEARS = 0;
    private static final int MONTHS = 1;

    @IntDef({YEARS, MONTHS})
    private @interface DayUnits {
    }

    private static final String DIFF_AGE_YEAR = "year";
    private static final String DIFF_AGE_YEARS = "years";
    private static final String DIFF_AGE_MONTH = "month";
    private static final String DIFF_AGE_MOTHS = "months";
    private static final String DIFF_AGE_FORMAT = "%d %s old";


    private static String formatCountOfDateUnits(int count, @DayUnits int dayUnits) {
        boolean oneUnit = count == 1;
        String unitsForFormat = dayUnits == YEARS ?
                oneUnit ? DIFF_AGE_YEAR : DIFF_AGE_YEARS :
                oneUnit ? DIFF_AGE_MONTH : DIFF_AGE_MOTHS;
        return String.format(DIFF_AGE_FORMAT, count, unitsForFormat);
    }
}
