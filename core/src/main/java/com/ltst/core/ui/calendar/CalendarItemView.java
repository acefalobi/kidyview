package com.ltst.core.ui.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.ltst.core.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarItemView extends LinearLayout implements CalendarPageAdapter.DrawPointsListener {


    private List<CalendarItemDay> calendarItemDays;
    private ClearMonthListener clearMonthListener;
    private CalendarPageAdapter.LoadPointsListener loadPointsListener;

    private ProgressBar progressBar;

    public CalendarItemView(Context context) {
        super(context);
    }

    public CalendarItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.calendar_item_view, this);
        initCleanListener();
    }


    public void fillMonth(Calendar calendar,
                          Calendar selectedDayCalendar,
                          CalendarView.DaySelectedListener daySelectedListener,
                          CalendarPageAdapter.InnerDaySelectedListener innerDaySelectedListener,
                          CalendarPageAdapter.LoadPointsListener loadPointsListener) {
        this.loadPointsListener = loadPointsListener;
        long startCalendarTime = calendar.getTimeInMillis();
        int startCalendarMonth = calendar.get(Calendar.MONTH);
//        SimpleDateFormat dateFormat = new SimpleDateFormat(MONTH_FORMAT);
//        String month = dateFormat.format(calendar.getTime());
//        ((TextView) findViewById(R.id.calendar_item_view_month_title)).setText(month);

        LinearLayout rootLinearLayout = ((LinearLayout) getChildAt(0));
        progressBar = ((ProgressBar) rootLinearLayout.getChildAt(1));
        TableLayout tableLayout = ((TableLayout) rootLinearLayout.getChildAt(2));
        calendarItemDays = new ArrayList<>(42); //count of rows in tableLayout

        for (int child = 1; child < tableLayout.getChildCount(); child++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(child);
            for (int x = 0; x < 7; x++) {
                LinearLayout dayLayout = (LinearLayout) tableRow.getChildAt(x);
                calendarItemDays.add(
                        new CalendarItemDay(dayLayout,
                                clearMonthListener,
                                selectedDayCalendar,
                                daySelectedListener,
                                innerDaySelectedListener));
            }
        }

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (firstDayOfWeek == 0) {
            firstDayOfWeek = 7; /*because first day of Calendar.class is SUNDAY*/
        }

        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek);
        for (int monthDay = 0; monthDay < calendarItemDays.size(); monthDay++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendarItemDays.get(monthDay).setDate(calendar, startCalendarMonth, selectedDayCalendar);
        }
        calendar.setTimeInMillis(startCalendarTime);
        loadPoints();
    }

    private void loadPoints() {
        if (progressBar != null) {
            progressBar.setVisibility(VISIBLE);
        }
        int year;
        int month;
        int day;
        CalendarItemDay firstVisibleDay = calendarItemDays.get(0);
        year = firstVisibleDay.getYear();
        month = firstVisibleDay.getMonth();
        day = firstVisibleDay.getDay();
        Calendar from = Calendar.getInstance();
        from.set(year, month, day);

        CalendarItemDay lastVisibleDay = calendarItemDays.get(calendarItemDays.size() - 1);
        year = lastVisibleDay.getYear();
        month = lastVisibleDay.getMonth();
        day = lastVisibleDay.getDay();
        Calendar to = Calendar.getInstance();
        to.set(year, month, day);
        if (loadPointsListener != null) {
            loadPointsListener.loadPoints(from, to, this);
        }

    }

    public void updatePoints() {
        if (calendarItemDays != null) {
            loadPoints();
        }
    }

    private void initCleanListener() {
        clearMonthListener = new ClearMonthListener() {
            @Override
            public void cleanMonth() {
                if (calendarItemDays != null) {
                    for (CalendarItemDay day : calendarItemDays) {
                        day.reset();
                    }
                }

            }
        };
    }

    public ClearMonthListener getClearMonthListener() {
        return clearMonthListener;
    }

    @Override
    public void dawPoints(List<CalendarView.PointDate> points) {
        if (progressBar != null) {
            progressBar.setVisibility(INVISIBLE);
        }
        if (points != null && points.size() > 0) {
            for (int x = 0; x < calendarItemDays.size(); x++) {
                CalendarItemDay itemDay = calendarItemDays.get(x);
                itemDay.hidePoint();
                int year = itemDay.getYear();
                int month = itemDay.getMonth();
                int day = itemDay.getDay();
                for (int y = 0; y < points.size(); y++) {
                    if (year == points.get(y).year &&
                            month == points.get(y).month &&
                            day == points.get(y).day) {
                        itemDay.showPoint();
                    }
                }
            }
        }
    }

    interface ClearMonthListener {
        void cleanMonth();
    }


}
