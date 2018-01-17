package com.ltst.core.util;

import android.content.Context;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.R;
import com.ltst.core.data.model.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static final int SECOND_MILLIS = 1000;
    public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final int TWO_DAYS_MILLIS = 2 * DAY_MILLIS;

    public static final String RAILS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static Calendar getCalendar(String dateTime, Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);
        Date parsed = null;
        try {
            parsed = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsed);
        return calendar;
    }

    public static String getYearMonthDayString(Calendar calendar, Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        SimpleDateFormat needFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
        return needFormat.format(calendar.getTime());
    }

    public static String getFullDateTimeString(Calendar calendar, Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        SimpleDateFormat needFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);
        return needFormat.format(calendar.getTime());
    }

    public static String getHourMinuteString(String dateTime, Context context) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Locale locale = context.getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);
        dateFormat.setTimeZone(timeZone);
        SimpleDateFormat needFormat = new SimpleDateFormat("HH:mm", locale);
//        needFormat.setTimeZone(timeZone);
        try {
            Date date = dateFormat.parse(dateTime);
            return needFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDayOfTextMonthString(String dateTime, Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar created = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);
        try {
            Date date = dateFormat.parse(dateTime);
            created.setTimeInMillis(date.getTime());
            String[] month = context.getResources().getStringArray(R.array.months);
            return created.get(Calendar.DAY_OF_MONTH) +
                    StringUtils.SPACE +
                    "of" +
                    StringUtils.SPACE +
                    month[created.get(Calendar.MONTH)];
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long getTimeMilliseconds(String dateTime, Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar created = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);
        try {
            Date date = dateFormat.parse(dateTime);
            return date.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    ;

    public static String getTime() {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone timezone = TimeZone.getTimeZone("UTC");
        dateTimeFormat.setTimeZone(timezone);
        String dateTime = dateTimeFormat.format(new Date(Calendar.getInstance().getTimeInMillis()));
        return dateTime;
    }

    public static String getPostTime(Context context, Post post) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat innerDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        innerDateFormat.setTimeZone(timeZone);
        SimpleDateFormat neededDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        try {
            Date date = innerDateFormat.parse(post.getCreatedAt());
            return neededDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }

    public static Calendar getCalendarByDate(String serverFormatDate, int defaultYear) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(RAILS_DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Calendar calendar = Calendar.getInstance();

            Date date = dateFormat.parse(serverFormatDate);
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, defaultYear);
            e.printStackTrace();
            return calendar;
        }
    }

}
