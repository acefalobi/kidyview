package com.ltst.schoolapp.teacher.ui.events.calendar.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.ui.calendar.CalendarPageAdapter;
import com.ltst.core.ui.calendar.CalendarView;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.events.calendar.CalendarScope;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;

public class EventsFragment extends CoreFragment implements EventsContract.View {

    @Inject EventsPresenter presenter;
    @Inject DialogProvider dialogProvider;

    @BindView(R.id.events_recycler_view) RecyclerView recyclerView;
    private ProgressBar eventsProgressBar;
    private ViewGroup header;
    private Toolbar toolbar;
    private boolean networkErrorPopupIsShowed;


    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_events;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        CalendarScope.CalendarComponent component = ((CalendarScope.CalendarComponent) rootComponent.getComponent());
        component.eventsFragment(new EventsScope.EventsModule(this)).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initRecyclerView();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == EventsPresenter.ADD_EVENT_REQUEST_CODE) {
            CalendarView calendarView = (CalendarView) header.findViewById(R.id.calendar_view);
            calendarView.updatePoints();
            Calendar selectedDay = calendarView.getSelectedDay();
            presenter.returnFromAddEvent(data, selectedDay);
        }
    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setVisibility(View.VISIBLE);
        toolbar.inflateMenu(R.menu.menu_plus);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_plus) {
                presenter.goToAddEvent();
            }
            return false;
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
    }

    @Override
    public void setAdapter(RecyclerBindableAdapter adapter, CalendarPageAdapter.LoadPointsListener loadPointsListener) {
        CalendarView calendarView;
        if (adapter.getHeadersCount() < 1) {
            header = ((ViewGroup) LayoutInflater.from(getContext())
                    .inflate(R.layout.header_events, recyclerView, false));
            calendarView = ((CalendarView) header.findViewById(R.id.calendar_view));
            calendarView.addDaySelectedListener(calendar -> {
                presenter.getEvents(calendar);
            });
            calendarView.setLoadPointsListener(loadPointsListener);
            eventsProgressBar = ((ProgressBar) header.findViewById(R.id.calendar_progress_bar));
            adapter.addHeader(header);

            calendarView.setChangeMonthListener(monthTitle -> {
                if (toolbar != null) {
                    toolbar.setTitle(monthTitle);
                }
            });
            if (StringUtils.isBlank(toolbar.getTitle())) {
                String month = calendarView.getCurrentMonthTitle();
                toolbar.setTitle(month);
            }
        } else {
            calendarView = ((CalendarView) header.findViewById(R.id.calendar_view));
            calendarView.updatePoints();
        }
        recyclerView.setAdapter(adapter);
        if (StringUtils.isBlank(toolbar.getTitle())) {
            String month = calendarView.getCurrentMonthTitle();
            toolbar.setTitle(month);
        }
    }

    @Override
    public void startLoad() {
        eventsProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoad() {
        eventsProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNetworkError() {
        if (!networkErrorPopupIsShowed) {
            dialogProvider.showNetError(getContext(), (dialog, which) -> {
                dialog.dismiss();
                networkErrorPopupIsShowed = false;
            });
            networkErrorPopupIsShowed = true;
        }
    }

    public static class Screen extends BottomNavigationFragmentScreen {

        @Override
        public int unselectedIconId() {
            return R.drawable.ic_event_unselected;
        }

        @Override
        public int selectedIconId() {
            return R.drawable.ic_event_selected;
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new EventsFragment();
        }
    }
}
