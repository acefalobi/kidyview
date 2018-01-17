package com.ltst.schoolapp.parent.ui.main.events;


import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.BaseView;
import com.ltst.core.data.model.Event;
import com.ltst.core.ui.calendar.CalendarPageAdapter;

import java.util.Calendar;

public interface EventsContract {

    interface View extends BaseView<Presenter> {

        void setAdapter(SimpleBindableAdapter<Event> adapter);

        void showNetworkError();
    }

    interface Presenter extends BasePresenter {

        void getEvents(Calendar calendar);

        void loadPoints(Calendar from, Calendar to, CalendarPageAdapter.DrawPointsListener drawPointsListener);
    }
}
