package com.ltst.core.ui.calendar;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ltst.core.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarView extends LinearLayout implements CalendarPageAdapter.InnerDaySelectedListener {

    private ViewPager viewPager;
    private CalendarPageAdapter adapter;
    private static final int OFFSET = 1;

//    private Calendar calendar = Calendar.getInstance();
//    private Calendar currentDayCalendar = ((Calendar) calendar.clone());

    private DaySelectedListener daySelectedListener;
    private CalendarPageAdapter.LoadPointsListener loadPointsListener;


    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.calendar_view, this);
    }

    public CalendarView(Context context) {
        super(context);

    }

    public void addDaySelectedListener(DaySelectedListener listener) {
        this.daySelectedListener = listener;
        this.adapter.addDaySelectedListener(daySelectedListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        viewPager = (ViewPager) findViewById(R.id.calendar_view_pager);
        adapter = new CalendarPageAdapter(getContext(), this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(OFFSET);
        viewPager.setCurrentItem(adapter.currentPosition);
        viewPager.addOnPageChangeListener(adapter);
        LayoutTransition layoutTransition = new LayoutTransition();
        setLayoutTransition(layoutTransition);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        }
    }

    @Override
    public void daySelected() {
        int currentItem = viewPager.getCurrentItem();

        if (viewPager.findViewWithTag(currentItem + OFFSET) != null) {
            ((CalendarItemView) viewPager.findViewWithTag(currentItem + OFFSET))
                    .getClearMonthListener().cleanMonth();
        }
        if (viewPager.findViewWithTag(currentItem - OFFSET) != null) {
            ((CalendarItemView) viewPager.findViewWithTag(currentItem - OFFSET))
                    .getClearMonthListener().cleanMonth();
        }

    }

    public void setLoadPointsListener(CalendarPageAdapter.LoadPointsListener loadPointsListener) {
        this.loadPointsListener = loadPointsListener;
        adapter.setLoadPointsListener(loadPointsListener);
    }

    public void updatePoints() {
        int currentItem = viewPager.getCurrentItem();
        if (viewPager.findViewWithTag(currentItem) != null) {
            ((CalendarItemView) viewPager.findViewWithTag(currentItem)).updatePoints();
        }
        if (viewPager.findViewWithTag(currentItem + OFFSET) != null) {
            ((CalendarItemView) viewPager.findViewWithTag(currentItem + OFFSET)).updatePoints();
        }
        if (viewPager.findViewWithTag(currentItem - OFFSET) != null) {
            ((CalendarItemView) viewPager.findViewWithTag(currentItem - OFFSET)).updatePoints();
        }
    }

    public String getCurrentMonthTitle() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(CalendarPageAdapter.MONTH_FORMAT);
        String month = dateFormat.format(adapter.getCurrentCalendar().getTime());
        return month;
    }

    public void setChangeMonthListener(ChangeMonthListener listener) {
        adapter.setChangeMonthListener(listener);
    }

    public Calendar getSelectedDay() {
        return adapter.getSelectedCalendar();
    }

    public interface DaySelectedListener {
        void daySelected(Calendar calendar);
    }

    public interface ChangeMonthListener {
        void monthChanged(String monthTitle);
    }

    public static class PointDate {
        public int year;
        public int month;
        public int day;
    }


}
