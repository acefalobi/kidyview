package com.ltst.schoolapp.teacher.ui.main.checks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Group;
import com.ltst.core.data.realm.model.ChildCheckScheme;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.adapter.ChecksAdapter;
import com.ltst.core.util.DateUtils;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.checks.check.the.code.CheckTheCodeActivity;
import com.ltst.schoolapp.teacher.ui.checks.dated.DatedCheckActivity;
import com.ltst.schoolapp.teacher.ui.main.ChangeGroupHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ltst.core.util.DateUtils.TWO_DAYS_MILLIS;

public class ChecksPresenter implements ChecksContract.Presenter, Toolbar.OnMenuItemClickListener,
        RealmRecyclerView.OnLoadMoreListener, DatePickerDialog.OnDateSetListener, SwipeRefreshLayout.OnRefreshListener, ChangeGroupHelper.GroupChangedListener {

    private static final int MAX_COUNT_EXCEPTIONS = 20;

    private final ChecksContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final TeacherApplication application;
    private final DataService dataService;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final Bundle datedChecksScreenParams;
    private final ChangeGroupHelper changeGroupHelper;

    private ChecksAdapter checksAdapter;
    private CompositeSubscription subscriptions;
    private Realm realm;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private int countExceptions;
    private boolean fromDatedChecks = false;
    private String selectGroupTitle;


    @Inject
    public ChecksPresenter(ChecksContract.View view,
                           ActivityScreenSwitcher activitySwitcher,
                           TeacherApplication application,
                           DataService dataService,
                           FragmentScreenSwitcher fragmentSwitcher,
                           Bundle datedChecksScreenParams, ChangeGroupHelper changeGroupHelper) {
        this.view = view;
        this.activitySwitcher = activitySwitcher;
        this.application = application;
        this.dataService = dataService;
        this.fragmentSwitcher = fragmentSwitcher;
        this.datedChecksScreenParams = datedChecksScreenParams;
        this.changeGroupHelper = changeGroupHelper;
        initStartCalendar(datedChecksScreenParams);
    }

    private void initStartCalendar(Bundle datedChecksScreenParams) {
        int startYear = datedChecksScreenParams.getInt(DatedCheckActivity.Screen.SELECTED_YEAR, 0);
        if (startYear != 0) {
            startCalendar = Calendar.getInstance();
            int startMonth = datedChecksScreenParams.getInt(DatedCheckActivity.Screen.SELECTED_MONTH);
            int startDay = datedChecksScreenParams.getInt(DatedCheckActivity.Screen.SELECTED_DAY);
            startCalendar.set(Calendar.YEAR, startYear);
            startCalendar.set(Calendar.MONTH, startMonth);
            startCalendar.set(Calendar.DAY_OF_MONTH, startDay);
            fromDatedChecks = true;
        } else {
            startCalendar = Calendar.getInstance();
        }
    }


    @Override
    public void firstStart() {

    }

    @Override
    public void start() {
        changeGroupHelper.showSpinner(true);
        subscriptions = new CompositeSubscription();
        view.showEmpty(fromDatedChecks);
        countExceptions = 0;
        realm = Realm.getDefaultInstance();
        changeGroupHelper.setGroupChangedListener(this);
        uploadNotSynced();
        if (fromDatedChecks) {
            final String datePatern = "MMMM d yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePatern);
            view.initDatedChecksToolbar(simpleDateFormat.format(startCalendar.getTime()), fromDatedChecks);
        }
    }

    private View.OnClickListener onFabClickListener() {
        return onFabClick -> openCheckTheCodeScreen();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   TOOLBAR   ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.feed_menu_calendar) {
            openSortChecksCalendar();
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   DATA   //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void uploadNotSynced() {
        subscriptions.add(dataService.syncChecksIfNeeded()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(childChecks -> {
                            loadItems();
                        },
                        throwable -> {
                            loadItems();
                            throwable.printStackTrace();
                        }));
    }

    private void loadItems() {
//        startCalendar = Calendar.getInstance();
        endCalendar = ((Calendar) startCalendar.clone());
        if (!fromDatedChecks) {
            startCalendar.setTimeInMillis(startCalendar.getTimeInMillis() - DateUtils.TWO_DAYS_MILLIS);
        }
        loadItemsFromDb();
        view.bindListeners(onFabClickListener(), this, this, this);
    }

    private void loadItemsFromDb() {
        if (fromDatedChecks) {
            StringBuilder startCalendarDate = new StringBuilder();
            int month = startCalendar.get(Calendar.MONTH) + 1;
            String stringMonth;
            if (month < 10) {
                stringMonth = "0" + String.valueOf(month);
            } else {
                stringMonth = String.valueOf(month);
            }
            String stringDay = null;
            int day = startCalendar.get(Calendar.DAY_OF_MONTH);
            if (day < 10) {
                stringDay = "0" + String.valueOf(day);
            } else {
                stringDay = String.valueOf(day);
            }
            startCalendarDate.append(startCalendar.get(Calendar.YEAR))
                    .append(StringUtils.DASH)
                    .append(stringMonth)
                    .append(StringUtils.DASH)
                    .append(stringDay)
                    .append("T");
            reCreateAdapter(startCalendarDate.toString());
        } else {
            reCreateAdapter(null);
        }
    }

    private void reCreateAdapter(String startCalendarDate) {

        dataService.getSelectedGroup()
                .subscribe(group -> {
//                    view.stopRefresh();
                    this.selectGroupTitle = group.getTitle();
                    RealmResults<ChildCheckScheme> models;
                    if (startCalendarDate != null) {
                        models = realm
                                .where(ChildCheckScheme.class)
                                .contains("datetime", startCalendarDate)
                                .equalTo("groupId", group.getId())
                                .findAllSorted("datetime", Sort.DESCENDING);
                    } else {
                        models = realm.where(ChildCheckScheme.class)
                                .equalTo("groupId", group.getId())
                                .findAllSorted("datetime", Sort.DESCENDING);
                    }
                    if (models.size() > 0) view.showContent();
                    checksAdapter = new ChecksAdapter(application, models);
                    view.bindAdapter(checksAdapter);
                });

    }

    private void updateItemsFromDb() {
        dataService.getSelectedGroup()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(group -> {
                    RealmResults<ChildCheckScheme> models = realm
                            .where(ChildCheckScheme.class)
                            .equalTo("groupId", group.getId())
                            .findAllSorted("datetime", Sort.DESCENDING);
                    checksAdapter.updateRealmResults(models);
                    if (checksAdapter.getItemCount() < 1) {
                        view.showEmpty(fromDatedChecks);
                    }
                });

    }

    @Override
    public void onLoadMore(Object o) {
        if (checksAdapter.getItemCount() <= 0) return;
        ChildCheckScheme lastItem = (ChildCheckScheme) checksAdapter.getLastItem();
        endCalendar = DateUtils.getCalendar(lastItem.getDatetime(), application);
        if (!fromDatedChecks) {
            startCalendar.setTimeInMillis(endCalendar.getTimeInMillis() - TWO_DAYS_MILLIS);
        }
        loadDataFromServer();
    }

    private void loadDataFromServer() {
        if (countExceptions >= MAX_COUNT_EXCEPTIONS) {
            view.enableLoadMore(false);
            updateItemsFromDb();

            return;
        }
        if (fromDatedChecks) {
            view.stopRefresh();
//            view.enableLoadMore(false);
            return;
        }
        String startDate = DateUtils.getYearMonthDayString(startCalendar, application);
        String endDate = startDate;
        if (!fromDatedChecks) {
            endDate = DateUtils.getYearMonthDayString(endCalendar, application);
        }
        if (checksAdapter == null) {
            reCreateAdapter(null);
        }
        view.enableLoadMore(true);
        subscriptions.add(dataService.getStates(startDate, endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(childChecks -> {
                    view.stopRefresh();
                    if (childChecks.size() != 0) {
                        view.showContent();
                        if (checksAdapter.getItemCount() == 0) {
                            loadItemsFromDb();
                        }
                        updateItemsFromDb();
                    }
                    endCalendar.setTimeInMillis(endCalendar.getTimeInMillis() - TWO_DAYS_MILLIS);
                    startCalendar.setTimeInMillis(startCalendar.getTimeInMillis() - TWO_DAYS_MILLIS);
                    loadDataFromServer();
                    countExceptions++;
                }, throwable -> {
                    view.enableLoadMore(false);
                    throwable.printStackTrace();
                }));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   NAVIGATION   //////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void openCheckTheCodeScreen() {
        dataService.getSelectedGroup().subscribe(group -> {
            activitySwitcher.open(new CheckTheCodeActivity.Screen(group.getTitle()));
        });

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   LIFECYCLE   /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void stop() {
        changeGroupHelper.removeListener(this);
        subscriptions.unsubscribe();
        realm.close();
    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    public static final String DATE_PICKER_TAG = "DatePicker";

    @Override public void openSortChecksCalendar() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMaxDate(calendar);
        datePickerDialog.setAccentColor(ContextCompat.getColor(application, R.color.toolbar_color_blue));
        datePickerDialog.show(fragmentSwitcher.getFragmentManager(), DATE_PICKER_TAG);
    }

    @Override public void goBack() {
        activitySwitcher.goBack();
    }

    @Override public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        activitySwitcher.open(new DatedCheckActivity.Screen(year, monthOfYear, dayOfMonth));
    }

    @Override
    public void onRefresh() {
        countExceptions = 0;
        loadDataFromServer();
    }

    @Override public void groupChanged(Group group) {
        startCalendar = Calendar.getInstance();
        endCalendar = ((Calendar) startCalendar.clone());
        countExceptions = 0;
        loadDataFromServer();
    }
}
