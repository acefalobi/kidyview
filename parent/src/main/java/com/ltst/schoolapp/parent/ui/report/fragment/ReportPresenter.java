package com.ltst.schoolapp.parent.ui.report.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.view.View;

import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.data.model.ChildState;
import com.ltst.core.data.model.ChildStateType;
import com.ltst.core.data.model.Post;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.adapter.FeedRecyclerAdapter;
import com.ltst.core.ui.simple.image.SimpleImageFragment;
import com.ltst.core.util.DateUtils;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.data.DataService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ReportPresenter implements ReportContract.Presenter, FeedRecyclerAdapter.FeedItemListener {

    private static final int DEFAULT_CALENDAR_YEAR = 1977;

    private final ReportContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final DataService dataService;
    private final Post reportPost;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final ParentApplication context;
    private final Calendar reportCalendarDate;


    private final int[] headerIcons = {R.drawable.ic_deciduous_tree, R.drawable.ic_brick,
            R.drawable.ic_party_baloon, R.drawable.ic_prop_plane, R.drawable.ic_rocket};
    private final Random random = new Random();

    private final FeedRecyclerAdapter adapter = new FeedRecyclerAdapter(false);

    private CompositeSubscription subscription;

    @Inject
    public ReportPresenter(ReportContract.View view,
                           ActivityScreenSwitcher activitySwitcher,
                           DataService dataService,
                           Post reportPost, FragmentScreenSwitcher fragmentSwitcher,
                           ParentApplication context) {
        this.view = view;
        this.activitySwitcher = activitySwitcher;
        this.dataService = dataService;
        this.reportPost = reportPost;
        this.fragmentSwitcher = fragmentSwitcher;
        this.context = context;
        reportCalendarDate = DateUtils.getCalendarByDate(reportPost.getCreatedAt(), DEFAULT_CALENDAR_YEAR);
    }

    @Override public void firstStart() {
        view.bindData(adapter, headerIcons[random.nextInt(headerIcons.length)],
                reportPost.getChildFirstName());
    }

    @Override public void start() {
        if (subscription == null || subscription.isUnsubscribed()) {
            subscription = new CompositeSubscription();
            if (adapter.getItemCount() == 1) {
                getScreenData();
            }
        }
    }

    private void getScreenData() {
        String currentDate = DateUtils.getYearMonthDayString(reportCalendarDate, context);
        subscription.add(Observable.zip(dataService.getCheckoutReport(reportPost.getId()),
                dataService.getStates(currentDate, currentDate),
                ReportWrapper::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reportSubscription, Throwable::printStackTrace));
    }

    private Action1<ReportWrapper> reportSubscription = reportWrapper -> {
        addPostsToAdapter(reportWrapper.posts);
        addChecksInfo(reportWrapper.checks);
    };

    private void addPostsToAdapter(List<Post> posts) {
        if (adapter.getItemCount() == 1) {
            adapter.addAll(posts);
        }
    }

    private void addChecksInfo(List<ChildCheck> checks) {
        removeOtherChildChecks(checks);
        List<ChildState> states = ChildState.allFromChecks(checks);
        removeFutureStates(states);
        findAndBindReportChecks(states);
    }


    private void removeOtherChildChecks(List<ChildCheck> checks) {
        Iterator<ChildCheck> iterator = checks.iterator();
        while (iterator.hasNext()) {
            ChildCheck item = iterator.next();
            if (needDeleteAsOtherCildCheck(item)) {
                iterator.remove();
            }
        }
    }

    private void removeFutureStates(List<ChildState> states) {
        Iterator<ChildState> iterator = states.iterator();
        while (iterator.hasNext()) {
            ChildState item = iterator.next();
            if (needDeleteAsFutureState(item)) {
                iterator.remove();
            }
        }
    }

    private boolean needDeleteAsFutureState(ChildState childState) {
        Calendar stateCalendar = DateUtils.getCalendarByDate(childState.getDatetime(), DEFAULT_CALENDAR_YEAR);
        return stateCalendar.getTime().getTime() > reportCalendarDate.getTime().getTime();
    }

    private boolean needDeleteAsOtherCildCheck(ChildCheck childCheck) {
        String itemChildFirstName = childCheck.getChild().getFirstName();
        String itemChildLastName = childCheck.getChild().getLastName();
        String reportChildFirstName = reportPost.getChildFirstName();
        String reportChildLastName = reportPost.getChildLastName();
        return !itemChildFirstName.equals(reportChildFirstName) ||
                !itemChildLastName.equals(reportChildLastName);
    }

    private void findAndBindReportChecks(List<ChildState> states) {
        Collections.reverse(states);
        ChildState checkIn = null;
        ChildState checkOut = null;
        boolean needCheckIn = false;
        for (ChildState state : states) {
            ChildStateType stateType = state.getType();
            if (checkOut == null && stateType.toString().equals(ChildStateType.CHECKOUT.toString())) {
                checkOut = state;
                needCheckIn = true;
            } else if (stateType.toString().equals(ChildStateType.CHECKIN.toString()) && needCheckIn) {
                checkIn = state;
            }
        }
        if (checkOut != null) {
            view.bindCheckOut(checkOut);
        }
        if (checkIn != null) {
            view.bindCheckIn(checkIn);
        }
    }

    @Override public void stop() {
        subscription.unsubscribe();
        subscription = null;
    }


    private final class ReportWrapper {
        private List<Post> posts;
        private List<ChildCheck> checks;

        public ReportWrapper(List<Post> posts, List<ChildCheck> checks) {
            this.posts = posts;
            this.checks = checks;
        }
    }


    @Override public void onPhotoClick(String url, View photo) {
        fragmentSwitcher.showDialogFragment(new SimpleImageFragment.Screen(url));
    }

    @Override public void onShareClick(Post post) {
        //stare view is invisible on this screen
    }

    @Override public void onIconClick(Post post) {
        //nothing for this screen
    }

    @Override public void onReportClick(Post post) {
        //no reports on this screen
    }

    private static final String KEY_RESTORE_LIST = "ReportPresenter.RestorePosts";

    @Override public void onRestore(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_RESTORE_LIST)) {
            ArrayList<Post> savedPosts = savedInstanceState.getParcelableArrayList(KEY_RESTORE_LIST);
            if (adapter.getItems().size() < 2) { //header
                adapter.clear();
                adapter.addAll(savedPosts);
            }

        }
    }

    @Override public void onSave(@NonNull Bundle outState) {
        outState.putParcelableArrayList(KEY_RESTORE_LIST, new ArrayList<>(adapter.getItems()));
    }

    @Override public void goBack() {
        activitySwitcher.goBack();
    }
}
