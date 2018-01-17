package com.ltst.schoolapp.parent.ui.main.checks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;

import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.util.DateUtils;
import com.ltst.core.util.SimpleDataObserver;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.checkout.CheckoutActivity;
import com.ltst.schoolapp.parent.ui.dated.checks.DatedCheckActivity;
import com.ltst.schoolapp.parent.ui.main.ChildInGroupHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ChecksPresenter implements ChecksContract.Presenter, DatePickerDialog.OnDateSetListener, SwipeRefreshLayout.OnRefreshListener {

    private final ChecksContract.View view;
    private final DataService dataService;
    private final ParentApplication context;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final ActivityScreenSwitcher activitySwitcher;
    private final Bundle datedScreenParams;
    private final ChildInGroupHelper spinnerHelper;
    private ChecksAdapter adapter = new ChecksAdapter();
    private CompositeSubscription subscription;
    private Calendar lastCalendar;
    private int EMPTY_COUNTS = 0;
    private boolean fromDatedChecks = false;
    private Calendar defaultCalendar;

    @Inject
    public ChecksPresenter(ChecksContract.View view,
                           DataService dataService,
                           ParentApplication context,
                           FragmentScreenSwitcher fragmentSwitcher,
                           ActivityScreenSwitcher activitySwitcher, Bundle datedScreenParams, ChildInGroupHelper spinnerHelper) {
        this.view = view;
        this.dataService = dataService;
        this.context = context;
        this.fragmentSwitcher = fragmentSwitcher;
        this.activitySwitcher = activitySwitcher;
        this.datedScreenParams = datedScreenParams;
        this.spinnerHelper = spinnerHelper;
        initStartCalendar(datedScreenParams);
    }

    @Override
    public void firstStart() {

    }

    private void initStartCalendar(Bundle datedScreenParams) {
        int startYear = datedScreenParams.getInt(DatedCheckActivity.Screen.SELECTED_YEAR, 0);
        if (startYear != 0) {
            lastCalendar = Calendar.getInstance();
            int startMonth = datedScreenParams.getInt(DatedCheckActivity.Screen.SELECTED_MONTH);
            int startDay = datedScreenParams.getInt(DatedCheckActivity.Screen.SELECTED_DAY);
            lastCalendar.set(Calendar.YEAR, startYear);
            lastCalendar.set(Calendar.MONTH, startMonth);
            lastCalendar.set(Calendar.DAY_OF_MONTH, startDay);
            fromDatedChecks = true;

        } else {
            lastCalendar = Calendar.getInstance();
        }
        defaultCalendar = ((Calendar) lastCalendar.clone());

    }

    @Override
    public void start() {
        if (fromDatedChecks) {
            final String datePattern = "MMMM d yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
            view.initDatedToolbar(simpleDateFormat.format(lastCalendar.getTime()));
            view.disableSwipeToRefresh();
        }
        spinnerHelper.showSpinner(false);
        if (subscription == null) {
            subscription = new CompositeSubscription();
        }
        view.init(adapter, onLoadMore(), this);
        if (adapter.getItemCount() == 0) {
            firstLoad();
        }
        adapter.registerAdapterDataObserver(new SimpleDataObserver() {
            @Override public void onAnythingChanges() {
                if (adapter.getItemCount() < 1) {
                    view.showEmptyScreen();
                } else view.hideEmptyScreen();
            }
        });
    }

    private void firstLoad() {
        if (lastCalendar == null) {
            lastCalendar = Calendar.getInstance();
        }
        loadStates(lastCalendar, false);
    }

    private static final int ONE_DAY = 1;

    private Action1<Integer> onLoadMore() {
        return integer -> {
            if (!fromDatedChecks) {
                loadNext(ONE_DAY);
            }

        };
    }

    private void loadNext(int days) {
        lastCalendar.add(Calendar.DAY_OF_MONTH, -days);
        loadStates(lastCalendar, false);
    }

    private static final int TEN_DAYS = 10;

    private void loadStates(Calendar calendar, boolean needClearAdapter) {
        view.startLoad();
        Calendar startCalendar = ((Calendar) calendar.clone());
        Calendar endCalendar = ((Calendar) startCalendar.clone());
        if (!fromDatedChecks) {
            endCalendar.add(Calendar.DAY_OF_MONTH, -TEN_DAYS);
        }
        if (this.lastCalendar == null) {
            lastCalendar = Calendar.getInstance();
        }
        this.lastCalendar.setTime(endCalendar.getTime());

        subscription.add(dataService.getStates(DateUtils.getYearMonthDayString(endCalendar, context),
                DateUtils.getYearMonthDayString(startCalendar, context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(childChecks -> {
                    if (!fromDatedChecks) {
                        if (childChecks == null || childChecks.isEmpty()) {
                            if (EMPTY_COUNTS == 5) {
                                view.stopRefresh();
                                view.stopLoad();
                                if (adapter.getItemCount() == 0) {
                                    view.showEmptyScreen();
                                }
                                return;
                            }
                            loadNext(TEN_DAYS);
                            EMPTY_COUNTS++;
                        } else {
                            fiilAdapter(needClearAdapter, childChecks);
                            view.stopLoad();
                            view.stopRefresh();
                        }
                    } else {
                        if (needClearAdapter) {
                            adapter.clear();
                            view.init(adapter, onLoadMore(), this);
                        }
                        view.stopRefresh();
                        view.stopLoad();
                        if (childChecks.isEmpty()) {
                            view.showEmptyScreen();
                        } else {
                            fiilAdapter(needClearAdapter, childChecks);
                        }
                    }

                }, throwable -> {
                    if (throwable instanceof NetErrorException) {
                        view.showNetError();
                    }
                }));
    }

    private void fiilAdapter(boolean needClearAdapter, List<ChildCheck> childChecks) {
        if (needClearAdapter) {
            adapter.clear();
        }
        adapter.addAll(ChecksAdapter.fromChecks(context, childChecks));
        view.stopRefresh();
    }

    @Override
    public void stop() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    private static final String RESTORE_LIST_KEY = "ChecksPresenter.WrapperList";
    private static final String RESTORE_CALENDAR_KEY = "ChecksPresenter.EdnCalendar";
    private static final String RESTORE_DEF_CALENDAR_KEY = "ChecksPresenter.DefaultCalendar";

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {
        if (adapter.getItemCount() != 0) {
            return;
        }
        if (savedInstanceState.containsKey(RESTORE_CALENDAR_KEY)) {
            lastCalendar = ((Calendar) savedInstanceState.getSerializable(RESTORE_CALENDAR_KEY));
        }
        if (savedInstanceState.containsKey(RESTORE_DEF_CALENDAR_KEY)) {
            defaultCalendar = ((Calendar) savedInstanceState.getSerializable(RESTORE_DEF_CALENDAR_KEY));
        }
        ArrayList<ChecksAdapter.ChecksWrapper> children =
                savedInstanceState.getParcelableArrayList(RESTORE_LIST_KEY);
        if (children != null) {
            adapter.addAll(children);
        }


    }

    @Override
    public void onSave(@NonNull Bundle outState) {
        ArrayList<ChecksAdapter.ChecksWrapper> wrappers = ((ArrayList<ChecksAdapter.ChecksWrapper>) adapter.getItems());
        if (wrappers != null) {
            outState.putParcelableArrayList(RESTORE_LIST_KEY, wrappers);
        }
        outState.putSerializable(RESTORE_CALENDAR_KEY, lastCalendar);
        outState.putSerializable(RESTORE_DEF_CALENDAR_KEY, defaultCalendar);
    }

    public static final String DATE_PICKER_TAG = "DatePicker";

    @Override
    public void openDatedChecksScreen() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMaxDate(calendar);
        datePickerDialog.setAccentColor(ContextCompat.getColor(context, R.color.toolbar_color_blue));
        datePickerDialog.show(fragmentSwitcher.getFragmentManager(), DATE_PICKER_TAG);
    }

    @Override
    public void goBack() {
        activitySwitcher.goBack();
    }

    @Override
    public void openGenerateCode() {
        activitySwitcher.open(new CheckoutActivity.Screen());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        activitySwitcher.open(new DatedCheckActivity.Screen(year, monthOfYear, dayOfMonth));
    }

    @Override
    public void onRefresh() {
        EMPTY_COUNTS = 0;
        loadStates(defaultCalendar, true);
    }
}
