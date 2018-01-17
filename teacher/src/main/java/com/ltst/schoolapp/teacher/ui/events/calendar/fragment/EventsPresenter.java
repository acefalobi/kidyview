package com.ltst.schoolapp.teacher.ui.events.calendar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.ltst.core.data.model.EmptyEvent;
import com.ltst.core.data.model.Event;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.ui.calendar.CalendarPageAdapter;
import com.ltst.core.ui.calendar.CalendarView;
import com.ltst.core.ui.holder.EventsViewHolder;
import com.ltst.core.ui.simple.image.SimpleImageFragment;
import com.ltst.core.util.ActivityProvider;
import com.ltst.core.util.IntentsUtil;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.events.add.AddEventActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class EventsPresenter implements EventsContract.Presenter, CalendarPageAdapter.LoadPointsListener {

    public static final int ADD_EVENT_REQUEST_CODE = 505;

    private final EventsContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final DataService dataService;
    private CalendarView.PointDate selectedDate;
    private final TeacherApplication appContext;

    private SimpleBindableAdapter<Event> adapter;
    private Subscription subscription;
    private CompositeSubscription loadPointsSubscription;
    private List<Event> emptyList;

    @Inject
    public EventsPresenter(EventsContract.View view,
                           ActivityScreenSwitcher activitySwitcher,
                           DataService dataService,
                           ActivityProvider activityProvider,
                           TeacherApplication appContext,
                           FragmentScreenSwitcher fragmentSwitcher) {
        this.view = view;
        this.activitySwitcher = activitySwitcher;
        this.dataService = dataService;
        this.appContext = appContext;
        adapter = new SimpleBindableAdapter<>(R.layout.viewholder_event, EventsViewHolder.class);
        adapter.setActionListener(new EventsViewHolder.EventsClickListener() {
            @Override
            public void onPhotoClick(String photoUri) {
                fragmentSwitcher.showDialogFragment(new SimpleImageFragment.Screen(photoUri));
            }

            @Override
            public void onDocumentClick(String documentUri) {
                Intent fileDownloadIntent = IntentsUtil.getFileDownloadIntent(documentUri);
                activityProvider.getContext().startActivity(fileDownloadIntent);
            }

            @Override
            public void OnItemClickListener(int position, Event item) {
                //nothing
            }
        });


    }

    @Override
    public void start() {
        view.setAdapter(adapter, this);
        if (emptyList == null || emptyList.size() < 1) {
            Event emptyEvent = new EmptyEvent(appContext.getString(R.string.events_empty_list_text));
            emptyList = new ArrayList<>(1);
            emptyList.add(emptyEvent);
        }
    }

    @Override
    public void stop() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        if (loadPointsSubscription != null) {
            loadPointsSubscription.unsubscribe();
            loadPointsSubscription = null;
        }
    }

    @Override
    public void firstStart() {

    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    @Override
    public void goToAddEvent() {
        AddEventActivity.Screen screen = new AddEventActivity.Screen(selectedDate.year, selectedDate.month, selectedDate.day);
        activitySwitcher.startForResult(screen, ADD_EVENT_REQUEST_CODE);
    }

    @Override
    public void getEvents(Calendar calendar) {
        view.startLoad();
        if (selectedDate == null) {
            selectedDate = new CalendarView.PointDate();
        }
        selectedDate.year = calendar.get(Calendar.YEAR);
        selectedDate.month = calendar.get(Calendar.MONTH);
        selectedDate.day = calendar.get(Calendar.DAY_OF_MONTH);
        subscription = dataService.getEvents(calendar, calendar)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Event>>() {
                    @Override
                    public void call(List<Event> events) {
                        adapter.clear();
                        if (events.size() > 0) {
                            adapter.addAll(events);
                        } else {
                            adapter.addAll(emptyList);
                        }
                        view.stopLoad();

                    }
                }, throwable -> {
                    if (throwable instanceof NetErrorException) {
                        view.showNetworkError();
                        view.stopLoad();
                    }
                });
    }

    @Override
    public void loadPoints(Calendar from, Calendar to, final CalendarPageAdapter.DrawPointsListener drawPointsListener) {
        if (loadPointsSubscription == null || loadPointsSubscription.isUnsubscribed()) {
            loadPointsSubscription = new CompositeSubscription();
        }
        loadPointsSubscription.add(dataService.getPoints(from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(drawPointsListener::dawPoints,
                        throwable -> view.showNetworkError()));
    }

    public void returnFromAddEvent(Intent data, Calendar selectedDay) {
        int year = data.getIntExtra(AddEventActivity.Screen.SELECTED_YEAR, 0);
        int month = data.getIntExtra(AddEventActivity.Screen.SELECTED_MONTH, 0);
        int day = data.getIntExtra(AddEventActivity.Screen.SELECTED_DAY, 0);
        if (year == selectedDay.get(Calendar.YEAR) &&
                month == selectedDay.get(Calendar.MONTH) &&
                day == selectedDay.get(Calendar.DAY_OF_MONTH)) {
            getEvents(selectedDay);
        }

    }

    @Override public void goBack() {
        activitySwitcher.goBack();
    }

}
