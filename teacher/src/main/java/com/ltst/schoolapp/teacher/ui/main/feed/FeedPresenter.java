package com.ltst.schoolapp.teacher.ui.main.feed;

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
import com.ltst.core.data.model.Group;
import com.ltst.core.data.model.Image;
import com.ltst.core.data.model.Post;
import com.ltst.core.data.uimodel.FeedType;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.adapter.FeedRecyclerAdapter;
import com.ltst.core.ui.simple.image.SimpleImageFragment;
import com.ltst.core.util.SharingService;
import com.ltst.core.util.SimpleDataObserver;
import com.ltst.core.util.SimpleItemSelectedListener;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.activities.add.AddPostActivity;
import com.ltst.schoolapp.teacher.ui.activities.dated.feed.DatedFeedActivity;
import com.ltst.schoolapp.teacher.ui.events.calendar.CalendarActivity;
import com.ltst.schoolapp.teacher.ui.main.ChangeGroupHelper;
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

import static com.ltst.schoolapp.teacher.ui.activities.add.fragment.AddPostPresenter.KEY_ADDED_POST;

public class FeedPresenter extends SimpleItemSelectedListener implements FeedContract.Presenter,
        FeedRecyclerAdapter.FeedItemListener, SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener, Toolbar.OnMenuItemClickListener, DatePickerDialog.OnDateSetListener, ChangeGroupHelper.GroupChangedListener {

    private static final String KEY_FEED_ITEMS = "FeedPresenter.feedRecyclerAdapter.items";
    public static final String DATE_PICKER_TAG = "DatePicker";
    private static final int ADD_POST_RC = 1341;
    private final FeedContract.View mainView;
    private final DataService dataService;
    private final ActivityScreenSwitcher activitySwitcher;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final SharingService sharingService;
    private final Boolean isMain;
    private final Calendar calendar;
    private final String date;
    private final Application application;
    private final ChangeGroupHelper changeGroupHelper;
    //    private FeedSpinnerAdapter feedSpinnerAdapter;
    private FeedRecyclerAdapter feedRecyclerAdapter;
    private CompositeSubscription subscriptions;
    private String query;
    private Post addedPost;
//    private int lastSpinnerPosition = FeedType.FEED.ordinal();

    @Inject
    public FeedPresenter(FeedContract.View view,
                         DataService dataService,
                         ActivityScreenSwitcher activitySwitcher,
                         FragmentScreenSwitcher fragmentSwitcher,
                         SharingService sharingService,
                         Boolean isMain,
                         Calendar calendar,
                         TeacherApplication application,
                         ChangeGroupHelper changeGroupHelper) {
        this.mainView = view;
        this.dataService = dataService;
        this.activitySwitcher = activitySwitcher;
        this.fragmentSwitcher = fragmentSwitcher;
        this.sharingService = sharingService;
        this.isMain = isMain;
        this.calendar = calendar;
        this.changeGroupHelper = changeGroupHelper;
        if (!isMain) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setCalendar(calendar);
            date = dateFormat.format(new Date(calendar.getTimeInMillis()));
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
        changeGroupHelper.showSpinner(true);
        subscriptions = new CompositeSubscription();
        feedRecyclerAdapter.registerAdapterDataObserver(new SimpleDataObserver() {
            @Override
            public void onAnythingChanges() {
                if (feedRecyclerAdapter.getItems().size() == 0) {
                    mainView.showEmpty();
                } else {
                    mainView.showContent();
                }
            }
        });
        initToolbar();
        mainView.bindData(feedRecyclerAdapter,
                this, this,
                onSearchCollapse -> reloadData(),
                fab -> {
                    activitySwitcher.startForResult(new AddPostActivity.Screen(), ADD_POST_RC);
                },
                onLoadMore());
        if (isMain) {
            if (changeGroupHelper.isGroupChecked() && feedRecyclerAdapter.getItemCount() == 0) {
                reloadData();
            }
            changeGroupHelper.setGroupChangedListener(this);
////            setSpinnerPosition -> SimpleSpinnerListener -> this -> onItemSelected
//            mainView.setSpinnerPosition(lastSpinnerPosition);
        } else {
            onItemSelected(0);
        }
        if (addedPost != null) {
            reloadData();
            addedPost = null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////   TOOLBAR   /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void initToolbar() {
        int icon = isMain ? 0 : R.drawable.ic_arrow_back_white_24dp;
        String title = null;
        if (!isMain) {
            String[] months = application.getResources().getStringArray(R.array.months);
            title = calendar.get(Calendar.DAY_OF_MONTH) +
                    StringUtils.SPACE +
                    months[calendar.get(Calendar.MONTH)] +
                    StringUtils.SPACE +
                    calendar.get(Calendar.YEAR);
        }
        mainView.initToolbar(icon,
                v -> {
                    activitySwitcher.goBack();
                }, this,
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
            case R.id.feed_menu_events:
                openEvents();
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
        activitySwitcher.open(new DatedFeedActivity.Screen(calendar));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////   DATA    ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onItemSelected(int position) {
        if (feedRecyclerAdapter.getRealItemCount() != 0) return;
//        lastSpinnerPosition = position;
        reloadData();
    }

    private void reloadData() {
        mainView.showLoading();
        mainView.expandSearch(false);
        subscriptions.add(dataService.getPosts(date, null, null, null,
                FeedType.FEED)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> {
                    feedRecyclerAdapter.clear();
                    feedRecyclerAdapter.addAll(posts);
                    mainView.setPaginationIsEnd(false);
                }, Throwable::printStackTrace));
    }

    private Action1<Integer> onLoadMore() {
        return integer -> {
            if (feedRecyclerAdapter.getRealItemCount() == 0) return;
            long lastId = feedRecyclerAdapter.getItem(0).getId();
            //TODO
            Observable<List<Post>> request = query == null
                    ? dataService.getPosts(date, null, lastId, null,
                    FeedType.FEED)
                    : dataService.getPostsForQuery(date, null, lastId, null,
                    FeedType.FEED, query);
            subscriptions.add(request
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(posts -> {
                        if (posts.size() == 0) {
                            mainView.setPaginationIsEnd(true);
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
                    }, Throwable::printStackTrace));
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
        } else {
            mainView.showLoading();
            for (Image image : post.getImages()) {
                resourceTokens.add(image.getResourceToken());
            }
            subscriptions.add(dataService.generateShortUrls(resourceTokens)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(shortUrls -> {
                        mainView.showContent();
                        String childrenNames = post.getTitle();
                        sharingService.sharePost(post.getContent(), shortUrls, childrenNames);
                    }, throwable -> {
                        throwable.printStackTrace();
                        mainView.showContent();
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
        //nothing for teacher app
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
        mainView.showLoading();
        mainView.setPaginationIsEnd(false);
        subscriptions.add(dataService.getPostsForQuery(null, null, null, null,
                FeedType.FEED, query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> {
                    feedRecyclerAdapter.clear();
                    feedRecyclerAdapter.addAll(posts);
                    mainView.setPaginationIsEnd(false);
                }, Throwable::printStackTrace));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   LIFECYCLE   /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override public void openEvents() {
        activitySwitcher.open(new CalendarActivity.Screen());
    }

    @Override
    public void onRestore(@NonNull Bundle bundle) {
        ArrayList<Post> posts = bundle.getParcelableArrayList(KEY_FEED_ITEMS);
        if (posts != null) {
            if (feedRecyclerAdapter.getRealItemCount() == 0) {
                feedRecyclerAdapter.getItems().addAll(posts);
            }
        }
    }

    @Override
    public void onSave(@NonNull Bundle outState) {
        ArrayList<Post> posts = (ArrayList<Post>) feedRecyclerAdapter.getItems();
        outState.putParcelableArrayList(KEY_FEED_ITEMS, posts);
    }

    @Override
    public void stop() {
        changeGroupHelper.removeListener(this);
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }

    @Override public void groupChanged(Group group) {
        reloadData();
    }
}
