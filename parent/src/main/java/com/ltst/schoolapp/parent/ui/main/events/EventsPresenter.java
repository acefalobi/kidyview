package com.ltst.schoolapp.parent.ui.main.events;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.ltst.core.data.model.EmptyEvent;
import com.ltst.core.data.model.Event;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.calendar.CalendarPageAdapter;
import com.ltst.core.ui.calendar.CalendarView;
import com.ltst.core.ui.holder.EventsViewHolder;
import com.ltst.core.ui.simple.image.SimpleImageFragment;
import com.ltst.core.util.ActivityProvider;
import com.ltst.core.util.IntentsUtil;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.main.ChildInGroupHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class EventsPresenter implements EventsContract.Presenter {

    private final EventsContract.View view;
    private final ParentApplication application;
    private final SimpleBindableAdapter<Event> adapter;
    private final DataService dataService;
    private final ChildInGroupHelper spinnerHelper;

    private List<Event> emptyList;
    private CompositeSubscription subscriptions;


    @Inject
    public EventsPresenter(EventsContract.View view,
                           ParentApplication application,
                           FragmentScreenSwitcher fragmentSwithcer, DataService dataService, ChildInGroupHelper spinnerHelper, ActivityProvider activityProvider) {
        this.view = view;
        this.application = application;
        this.dataService = dataService;
        this.spinnerHelper = spinnerHelper;
        adapter = new SimpleBindableAdapter<>(R.layout.viewholder_event, ParentEventVewHolder.class);
        adapter.setActionListener(new EventsViewHolder.EventsClickListener() {
            @Override public void onPhotoClick(String photoUri) {
                fragmentSwithcer.showDialogFragment(new SimpleImageFragment.Screen(photoUri));
            }

            @Override public void onDocumentClick(String documentUri) {
                Intent fileDownloadIntent = IntentsUtil.getFileDownloadIntent(documentUri);
                activityProvider.getContext().startActivity(fileDownloadIntent);
            }

            @Override public void OnItemClickListener(int position, Event item) {

            }
        });
    }


    @Override public void start() {
        spinnerHelper.showSpinner(false);
        subscriptions = new CompositeSubscription();
        view.setAdapter(adapter);
        if (emptyList == null || emptyList.size() < 1) {
            Event emptyEvent = new EmptyEvent(application.getString(R.string.empty_event_message));
            emptyList = new ArrayList<>(1);
            emptyList.add(emptyEvent);
        }
    }

    @Override public void stop() {
        subscriptions.unsubscribe();
        subscriptions = null;
    }

    @Override public void firstStart() {

    }

    @Override public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override public void onSave(@NonNull Bundle outState) {

    }

    @Override public void loadPoints(Calendar from, Calendar to, CalendarPageAdapter.DrawPointsListener drawPointsListener) {
        subscriptions.add(dataService.getPoints(from, to).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(drawPointsListener::dawPoints, throwable -> {
                    view.showNetworkError();
                }));
    }

    @Override public void getEvents(Calendar calendar) {
        subscriptions.add(dataService.getEvents(calendar, calendar)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> {
                    adapter.clear();
                    if (events.size() > 0) {
                        adapter.addAll(events);
                    } else adapter.addAll(emptyList);
                }));
    }
}
