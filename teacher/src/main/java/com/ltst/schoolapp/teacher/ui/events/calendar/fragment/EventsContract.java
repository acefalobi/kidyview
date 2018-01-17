package com.ltst.schoolapp.teacher.ui.events.calendar.fragment;

import android.content.Intent;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.ui.calendar.CalendarPageAdapter;

import java.util.Calendar;

public interface EventsContract  {

    interface Presenter extends BasePresenter {

        void goToAddEvent();

        void getEvents(Calendar calendar);

        void returnFromAddEvent(Intent data, Calendar selectedDay);

        void goBack();
    }

    interface View extends BaseView<Presenter>{

        void setAdapter(RecyclerBindableAdapter adapter,CalendarPageAdapter.LoadPointsListener loadPointsListener);

        void startLoad();

        void stopLoad();

        void showNetworkError();
    }
}
