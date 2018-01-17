package com.ltst.core.ui.calendar;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ltst.core.R;

import java.util.Calendar;

public class CalendarItemDay {

    private TextView dayTextView;
    private FrameLayout clickableContainer;
    private Calendar selectedDayCalendar;
    private CalendarView.DaySelectedListener daySelectedListener;
    private CalendarPageAdapter.InnerDaySelectedListener innerDaySelectedListener;

    private PrimaryStatus primaryStatus;
    private SecondaryStatus secondaryStatus;
    private ViewGroup point;

    private int year;
    private int month;
    private int day;

    public CalendarItemDay(LinearLayout dayLayout,
                           final CalendarItemView.ClearMonthListener clearMonthListener,
                           final Calendar selectedDayCalendar,
                           final CalendarView.DaySelectedListener daySelectedListener,
                           final CalendarPageAdapter.InnerDaySelectedListener innerDaySelectedListener) {
        clickableContainer = ((FrameLayout) dayLayout.getChildAt(0));
        point = ((ViewGroup) dayLayout.getChildAt(1));
        this.selectedDayCalendar = selectedDayCalendar;
        this.daySelectedListener = daySelectedListener;
        this.innerDaySelectedListener = innerDaySelectedListener;
        clickableContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMonthListener.cleanMonth();
//                selectedDayCalendar.set(year, month, day);
//                if (daySelectedListener != null) {
//                    daySelectedListener.daySelected(selectedDayCalendar);
//                }
//                if (innerDaySelectedListener != null) {
//                    innerDaySelectedListener.daySelected();
//                }
                Context context = dayTextView.getContext();
                setSelected(context);

            }
        });
        dayTextView = ((TextView) clickableContainer.getChildAt(0));
    }

    private void setSelected(Context context) {
        int whiteColor = ContextCompat.getColor(context, android.R.color.white);
        dayTextView.setTextColor(whiteColor);
        dayTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_calendar_day_selected));
        selectedDayCalendar.set(year, month, day);
        if (daySelectedListener != null) {
            daySelectedListener.daySelected(selectedDayCalendar);
        }
        if (innerDaySelectedListener != null) {
            innerDaySelectedListener.daySelected();
        }
    }

    public void setDate(Calendar calendar, int currentMonth, Calendar selectedDayCalendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (calendar.get(Calendar.MONTH) == currentMonth) {
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                primaryStatus = PrimaryStatus.IN_MONTH_HOLIDAY;
            } else {
                primaryStatus = PrimaryStatus.IN_MONTH_WORKDAY;
            }
        } else {
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                primaryStatus = PrimaryStatus.OUT_MONTH_HOLIDAY;
            } else {
                primaryStatus = PrimaryStatus.OUT_MONTH_WORKDAY;
            }
        }
        dayTextView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        reset();
        if (selectedDayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                && selectedDayCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && selectedDayCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
            setSelected(dayTextView.getContext());
        }


    }

    void reset() {
        int colorResId = primaryStatus == PrimaryStatus.IN_MONTH_HOLIDAY ? R.color.pale_red :
                primaryStatus == PrimaryStatus.IN_MONTH_WORKDAY ? android.R.color.black :
                        primaryStatus == PrimaryStatus.OUT_MONTH_HOLIDAY ? R.color.opacity_red :
                                R.color.bluish_grey;
        Context context = dayTextView.getContext();
        dayTextView.setTextColor(ContextCompat.getColor(context, colorResId));
        dayTextView.setBackground(null);
        Calendar todayCalendar = CalendarPageAdapter.TODAY_CALENDAR;
        if (todayCalendar.get(Calendar.MONTH) == month
                && todayCalendar.get(Calendar.DAY_OF_MONTH) == day
                && todayCalendar.get(Calendar.YEAR) == year) {
            dayTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_calendar_day_today));
        }
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public void showPoint() {
        point.setVisibility(View.VISIBLE);
    }

    public void hidePoint() {
        point.setVisibility(View.INVISIBLE);
    }

    public enum PrimaryStatus {
        IN_MONTH_HOLIDAY,
        IN_MONTH_WORKDAY,
        OUT_MONTH_HOLIDAY,
        OUT_MONTH_WORKDAY
    }

    public enum SecondaryStatus {
        TODAY,
        NOT_TODAY
    }

}
