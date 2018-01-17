package com.ltst.schoolapp.parent.ui.main.events;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.data.model.Event;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.ui.calendar.CalendarPageAdapter;
import com.ltst.core.ui.calendar.CalendarView;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.main.BottomScreen;
import com.ltst.schoolapp.parent.ui.main.MainScope;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;

public class EventsFragment extends CoreFragment implements EventsContract.View, CalendarPageAdapter.LoadPointsListener {

    @Inject EventsPresenter presenter;
    @Inject DialogProvider dialogProvider;

    @BindView(R.id.events_recycler_view) RecyclerView eventsRecyclerView;
    @BindView(R.id.events_calendar_view) CalendarView calendarView;
    private Toolbar toolbar;


    @Override protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override protected int getResLayoutId() {
        return R.layout.fragment_events;
    }

    @Override protected void onCreateComponent(HasSubComponents rootComponent) {
        MainScope.MainComponent component = (MainScope.MainComponent) rootComponent.getComponent();
        component.eventsComponent(new EventsScope.EventsModule(this)).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eventsRecyclerView.setLayoutManager(layoutManager);
        calendarView.addDaySelectedListener(calendar -> {
            presenter.getEvents(calendar);
        });
        calendarView.setChangeMonthListener(monthTitle -> {
            toolbar.setTitle(monthTitle);
        });
        calendarView.setLoadPointsListener(this);
        return view;
    }

    @Override protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setAdapter(SimpleBindableAdapter<Event> adapter) {
        eventsRecyclerView.setAdapter(adapter);
        calendarView.updatePoints();
        if (StringUtils.isBlank(toolbar.getTitle())) {
            String month = calendarView.getCurrentMonthTitle();
            toolbar.setTitle(month);
        }

    }

    @Override public void showNetworkError() {
        dialogProvider.showNetError(getContext());
    }

    @Override public void loadPoints(Calendar from, Calendar to, CalendarPageAdapter.DrawPointsListener drawPointsListener) {
        presenter.loadPoints(from, to, drawPointsListener);
    }

    public static class Screen extends BottomNavigationFragmentScreen {

        @Override public String getName() {
            return BottomScreen.EVENTS.toString();
        }

        @Override protected Fragment createFragment() {
            return new EventsFragment();
        }

        @Override public int unselectedIconId() {
            return R.drawable.ic_event_unselected;
        }

        @Override public int selectedIconId() {
            return R.drawable.ic_event_selected;
        }
    }
}
