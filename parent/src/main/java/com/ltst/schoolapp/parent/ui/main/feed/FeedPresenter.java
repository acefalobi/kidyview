package com.ltst.schoolapp.parent.ui.main.feed;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.NetworkModule;
import com.ltst.core.data.model.ChildActivity;
import com.ltst.core.data.model.ChildInGroup;
import com.ltst.core.data.model.Image;
import com.ltst.core.data.model.Post;
import com.ltst.core.data.uimodel.FeedType;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.adapter.FeedRecyclerAdapter;
import com.ltst.core.ui.simple.image.SimpleImageFragment;
import com.ltst.core.util.SharingService;
import com.ltst.core.util.SimpleDataObserver;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.dated.feed.DatedFeedActivity;
import com.ltst.schoolapp.parent.ui.main.ChildInGroupHelper;
import com.ltst.schoolapp.parent.ui.report.ReportActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class FeedPresenter implements FeedContract.Presenter,
        FeedRecyclerAdapter.FeedItemListener, SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener, Toolbar.OnMenuItemClickListener, DatePickerDialog.OnDateSetListener, ChildInGroupHelper.ChildInGroupChangeListener {
    public static final String KEY_ADDED_POST = "AddPostPresenter.added.post";

    public static final String DATE_PICKER_TAG = "DatePicker";
    private static final int ADD_POST_RC = 1341;

    private final FeedContract.View view;
    private final DataService dataService;
    private final ActivityScreenSwitcher activitySwitcher;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final SharingService sharingService;
    private final Boolean isMain;
    private final Calendar calendar;
    private final String date;
    private final Application application;
    private final ChildInGroupHelper spinnerHelper;

    private FeedRecyclerAdapter feedRecyclerAdapter;
    private CompositeSubscription subscriptions;
    private String query;
    private Post addedPost;
    private ChildInGroup selectedChildInGroup;

    @Inject
    public FeedPresenter(FeedContract.View view,
                         DataService dataService,
                         ActivityScreenSwitcher activitySwitcher,
                         FragmentScreenSwitcher fragmentSwitcher,
                         SharingService sharingService,
                         Boolean isMain,
                         Bundle screenParams,
                         ParentApplication application,
                         ChildInGroupHelper spinnerHelper) {
        this.view = view;
        this.dataService = dataService;
        this.activitySwitcher = activitySwitcher;
        this.fragmentSwitcher = fragmentSwitcher;
        this.sharingService = sharingService;
        this.isMain = isMain;
        this.calendar = ((Calendar) screenParams.getSerializable(DatedFeedActivity.Screen.KEY_DATE));
        this.spinnerHelper = spinnerHelper;
        if (!isMain) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setCalendar(calendar);
            date = dateFormat.format(new Date(calendar.getTimeInMillis()));
            selectedChildInGroup = screenParams.getParcelable(DatedFeedActivity.Screen.KEY_CHILD_IN_GROUP);
        } else {
            date = null;
        }
        this.application = application;
        feedRecyclerAdapter = new FeedRecyclerAdapter(true).setFeedItemListener(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_POST_RC) {
            if (data != null) {
                if (data.getExtras().containsKey(KEY_ADDED_POST)) {
                    addedPost = data.getExtras().getParcelable(KEY_ADDED_POST);
                }
            }
        }
    }

    @Override
    public void firstStart() {

    }

    @Override
    public void start() {
        spinnerHelper.showSpinner(true);
        subscriptions = new CompositeSubscription();
        feedRecyclerAdapter.registerAdapterDataObserver(new SimpleDataObserver() {
            @Override
            public void onAnythingChanges() {
                if (feedRecyclerAdapter.getItems().size() == 0) {
                    view.showEmpty();
                } else {
                    view.showContent();
                }
            }
        });
        initToolbar();

        if (isMain) {
            spinnerHelper.setChildInGroupChangeListener(this);
            if (spinnerHelper.isChildInGroupChecked()) {
                selectedChildInGroup = spinnerHelper.getSelectedChildInGroup();
                if (feedRecyclerAdapter.getItemCount() == 0) {
                    reloadData();
                }

            } else if (!spinnerHelper.hasItems()) {
                reloadData();
            }


        } else {
            if (feedRecyclerAdapter.getItemCount() == 0) {
                reloadData();
            }

        }
        if (addedPost != null) {
            reloadData();
            addedPost = null;
        }
        view.bindData(feedRecyclerAdapter,
                this, this,
                onSearchCollapse -> reloadData(),
                onLoadMore());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////   TOOLBAR   /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void initToolbar() {
        int icon = isMain ? 0 : R.drawable.ic_arrow_back_white_24dp;
        String title = null;
        boolean showSpinner = true;
        if (!isMain) {
            showSpinner = false;
            String[] months = application.getResources().getStringArray(R.array.months);

            title = calendar.get(Calendar.DAY_OF_MONTH) +
                    StringUtils.SPACE +
                    months[calendar.get(Calendar.MONTH)] +
                    StringUtils.SPACE +
                    calendar.get(Calendar.YEAR);
        } else {
            if (!spinnerHelper.hasItems()) {
                showSpinner = false;
            }
        }
        view.initToolbar(icon,
                v -> {
                    activitySwitcher.goBack();
                }, this, showSpinner,
                title);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.feed_menu_search:
                break;
            case R.id.feed_menu_calendar:
                showCalendar();
                break;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////   CALENDAR  /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void showCalendar() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMaxDate(Calendar.getInstance());
        datePickerDialog.setAccentColor(ContextCompat.getColor(application, R.color.toolbar_color_blue));
        datePickerDialog.show(fragmentSwitcher.getFragmentManager(), DATE_PICKER_TAG);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        activitySwitcher.open(new DatedFeedActivity.Screen(calendar, selectedChildInGroup));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////   DATA    ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void reloadData() {
        if (selectedChildInGroup == null) {
            selectedChildInGroup = spinnerHelper.getSelectedChildInGroup();
        }
        if (selectedChildInGroup == null) {
            view.showEmpty();
            return;
        }
        long groupId = selectedChildInGroup.getGroupId();
        long childId = selectedChildInGroup.getChildId();
        view.showLoading();
        view.expandSearch(false);
        subscriptions.add(dataService.getPosts(date, null, null, null,
                FeedType.FEED, groupId, childId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> {
                    feedRecyclerAdapter.clear();
                    feedRecyclerAdapter.addAll(posts);
                    view.setPaginationIsEnd(false);
                }, Throwable::printStackTrace));
    }

    private Action1<Integer> onLoadMore() {
        return integer -> {
            if (feedRecyclerAdapter.getRealItemCount() == 0) return;
            long lastId = feedRecyclerAdapter.getItem(0).getId();
            //TODO
            Observable<List<Post>> request = query == null
                    ? dataService.getPosts(date, null, lastId, null,
                    FeedType.FEED, selectedChildInGroup.getGroupId(), selectedChildInGroup.getChildId())
                    : dataService.getPostsForQuery(date, null, lastId, null,
                    FeedType.FEED, query, selectedChildInGroup.getGroupId(), selectedChildInGroup.getChildId());
            subscriptions.add(request
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(posts -> {
                        if (posts.size() == 0) {
                            view.setPaginationIsEnd(true);
                        } else {
                            // FIXME: 02.11.16 duplicate of posts on screen after add new post
                            List<Post> items = feedRecyclerAdapter.getItems();
                            List<Post> needAdd = new ArrayList<Post>();
                            needAdd.addAll(posts);
                            for (Post post : posts) {
                                for (Post fromAdapter : items) {
                                    if (post.getId() == fromAdapter.getId()) {
                                        needAdd.remove(post);
                                    }
                                }
                            }
                            feedRecyclerAdapter.addAll(needAdd);
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                    }));
        };
    }


    @Override
    public void onRefresh() {
        reloadData();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////   ITEMS   ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPhotoClick(String url, View photo) {
        fragmentSwitcher.showDialogFragment(new SimpleImageFragment.Screen(url, true));
    }

    @Override
    public void onShareClick(Post post) {
        List<String> resourceTokens = new ArrayList<>();
        if (post.getImages().size() == 0) {
            String shareContent = prepareShareContentWithActivity(post);
            sharingService.sharePost(shareContent, Collections.emptyList(), post.getTitle());
            sharingService.sharePost(shareContent, Collections.emptyList(), post.getTitle());
        } else {
            view.showLoading();
            for (Image image : post.getImages()) {
                resourceTokens.add(image.getResourceToken());
            }
            subscriptions.add(dataService.generateShortUrls(resourceTokens)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(shortUrls -> {
                        view.showContent();
                        String childrenNames = post.getTitle();
                        sharingService.sharePost(post.getContent(), shortUrls, childrenNames);
                    }, throwable -> {
                        throwable.printStackTrace();
                        view.showContent();
                    }));
        }

    }

    @NonNull private String prepareShareContentWithActivity(Post post) {
        ChildActivity activity = post.getActivity();
        String activityTitle = activity.getTitle();

        String postActivityIcon = NetworkModule.ACTICITY_ICONS_PATH
                + activityTitle
                + StringUtils.PNG_FORMAT;

        return postActivityIcon
                + StringUtils.CARRET
                + activityTitle
                + StringUtils.COLON
                + StringUtils.SPACE
                + post.getContent();
    }


    @Override
    public void onIconClick(Post post) {

    }

    @Override public void onReportClick(Post post) {
        activitySwitcher.open(new ReportActivity.Screen(post));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   SEARCH    ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onQueryTextSubmit(String query) {
        this.query = query;
        loadDataForQuery(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void loadDataForQuery(String query) {
        view.showLoading();
        view.setPaginationIsEnd(false);
        ChildInGroup selectedChildInGroup = spinnerHelper.getSelectedChildInGroup();
        subscriptions.add(dataService.getPostsForQuery(null, null, null, null,
                FeedType.FEED, query, selectedChildInGroup.getGroupId(), selectedChildInGroup.getChildId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> {
                    feedRecyclerAdapter.clear();
                    feedRecyclerAdapter.addAll(posts);
                    view.setPaginationIsEnd(false);
                }, throwable -> {
                    throwable.printStackTrace();
                }));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   LIFECYCLE   /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static final String KEY_FEED_ITEMS = "FeedPresenter.feedRecyclerAdapter.items";
    private static final String KEY_SELECTED_CHILD_IN_GROUP = "FeedPresenter.SelectedChildINGroup";

    @Override
    public void onRestore(@NonNull Bundle bundle) {
        ArrayList<Post> posts = bundle.getParcelableArrayList(KEY_FEED_ITEMS);
        if (posts != null) {
            if (feedRecyclerAdapter.getRealItemCount() == 0) {
                feedRecyclerAdapter.getItems().addAll(posts);
            }
        }
        if (bundle.containsKey(KEY_SELECTED_CHILD_IN_GROUP)) {
            selectedChildInGroup = bundle.getParcelable(KEY_SELECTED_CHILD_IN_GROUP);
        }
    }

    @Override
    public void onSave(@NonNull Bundle outState) {
        ArrayList<Post> posts = (ArrayList<Post>) feedRecyclerAdapter.getItems();
        outState.putParcelableArrayList(KEY_FEED_ITEMS, posts);
        outState.putParcelable(KEY_SELECTED_CHILD_IN_GROUP, selectedChildInGroup);
    }

    @Override
    public void stop() {
        spinnerHelper.removeListener(this);
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }

    @Override public void childInGroupChanged(ChildInGroup childInGroup) {
        if (selectedChildInGroup != null &&
                childInGroup.getChildId() == selectedChildInGroup.getChildId()
                && childInGroup.getGroupId() == selectedChildInGroup.getGroupId()) {
            return;
        }
        selectedChildInGroup = childInGroup;
        reloadData();
    }
}
