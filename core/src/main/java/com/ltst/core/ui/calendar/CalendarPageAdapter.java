package com.ltst.core.ui.calendar;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ltst.core.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CalendarPageAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private LayoutInflater inflater;
    static final int VIEW_COUNT = 1000;
    int currentPosition = VIEW_COUNT / 2;

    private CalendarView.DaySelectedListener daySelectedListener;
    private InnerDaySelectedListener innerDaySelectedListener;
    private LoadPointsListener loadPointsListener;
    private CalendarView.ChangeMonthListener changeMonthListener;


    private Calendar calendar = Calendar.getInstance();
    private Calendar selectedCalendar = ((Calendar) calendar.clone());
    public static final Calendar TODAY_CALENDAR = Calendar.getInstance();

    public CalendarPageAdapter(Context context, InnerDaySelectedListener innerDaySelectedListener) {
        this.inflater = LayoutInflater.from(context);
        this.innerDaySelectedListener = innerDaySelectedListener;
    }

    @Override
    public int getCount() {
        return VIEW_COUNT;
    }

    public void addDaySelectedListener(CalendarView.DaySelectedListener listener) {
        this.daySelectedListener = listener;
    }

    Calendar getSelectedCalendar() {
        return selectedCalendar;
    }

    Calendar getCurrentCalendar(){
        return this.calendar;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        CalendarItemView view = (CalendarItemView) inflater
                .inflate(R.layout.calendar_dynamic_item, container, false);
        view.setTag(position);
        container.addView(view);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Calendar calendarClone = ((Calendar) calendar.clone());
        CalendarItemView itemById = (CalendarItemView) container.findViewWithTag(position);
        if (position < currentPosition) {
            calendarClone.add(Calendar.MONTH, -1);
            fillMonth(itemById, calendarClone);
        } else if (position > currentPosition) {
            calendarClone.add(Calendar.MONTH, 1);
            fillMonth(itemById, calendarClone);
        } else {
            fillMonth(itemById, calendar);
        }
        return view;
    }

    public void setChangeMonthListener(CalendarView.ChangeMonthListener changeMonthListener) {
        this.changeMonthListener = changeMonthListener;
    }

    private void fillMonth(CalendarItemView view, Calendar calendar) {
        view.fillMonth(calendar, selectedCalendar,
                daySelectedListener,
                innerDaySelectedListener,
                loadPointsListener);
    }

    public void setLoadPointsListener(LoadPointsListener loadPointsListener) {
        this.loadPointsListener = loadPointsListener;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((CalendarItemView) object));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    public static final String MONTH_FORMAT = "MMMM yyyy";

    @Override
    public void onPageSelected(int position) {
        if (currentPosition > position) {
            calendar.add(Calendar.MONTH, -1);
        } else {
            calendar.add(Calendar.MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        currentPosition = position;
        if (changeMonthListener != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(MONTH_FORMAT);
            String month = dateFormat.format(calendar.getTime());
            changeMonthListener.monthChanged(month);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface InnerDaySelectedListener {
        void daySelected();
    }

    public interface LoadPointsListener {
        void loadPoints(Calendar from, Calendar to, DrawPointsListener drawPointsListener);
    }

    public interface DrawPointsListener {
        void dawPoints(List<CalendarView.PointDate> points);
    }
}
