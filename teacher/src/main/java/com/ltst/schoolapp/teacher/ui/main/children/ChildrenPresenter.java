package com.ltst.schoolapp.teacher.ui.main.children;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Filter;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Group;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.adapter.ChildrenAdapter;
import com.ltst.core.ui.holder.ChildrenViewHolder;
import com.ltst.core.ui.simple.image.SimpleImageFragment;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.addchild.AddChildActivity;
import com.ltst.schoolapp.teacher.ui.child.ChildActivity;
import com.ltst.schoolapp.teacher.ui.main.ChangeGroupHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ChildrenPresenter implements ChildrenContract.Presenter, ChangeGroupHelper.GroupChangedListener {

    private final ChildrenContract.View view;
    private final DataService dataService;
    private final ActivityScreenSwitcher activitySwitcher;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final ChildrenAdapter adapter;
    private final ChangeGroupHelper changeGroupHelper;
    private Integer clickedPosition = null;
    private String filterRequest = null;
    private Subscription getChildrenSubscription;
    private Subscription removeChildSubscription;
    private long needDeleteChild;


    List<Child> children;

    @Inject
    public ChildrenPresenter(ChildrenContract.View view,
                             DataService dataService,
                             ActivityScreenSwitcher activitySwitcher,
                             FragmentScreenSwitcher fragmentSwitcher,
                             ChangeGroupHelper changeGroupHelper) {
        this.view = view;
        this.dataService = dataService;
        this.activitySwitcher = activitySwitcher;
        this.fragmentSwitcher = fragmentSwitcher;
        this.changeGroupHelper = changeGroupHelper;
        this.adapter = new ChildrenAdapter(getActionListener(), true);
    }

    private ChildrenViewHolder.ChildrenClickListener getActionListener() {
        return new ChildrenViewHolder.ChildrenClickListener() {
            @Override
            public void onItemClickListener(int position, Child item) {
                clickedPosition = position;
                onListItemClick(item);
            }

            @Override
            public void onEditChildListener(int position, Child item) {
                clickedPosition = position;
                openEditChildScreen(item);
            }

            @Override
            public void onDeleteChildListener(int position, Child item) {
                showWarning(item.getServerId(), item);
            }

            @Override
            public void onPhotoClickListener(String avatarUrl) {
                fragmentSwitcher.showDialogFragment(new SimpleImageFragment.Screen(avatarUrl));
            }

            @Override
            public void OnItemClickListener(int position, ChildrenAdapter.ChildWrapper item) {
                clickedPosition = position;
                onListItemClick(item.getChild());
            }
        };
    }

    private void showWarning(long serverId, Child item) {
        needDeleteChild = serverId;
        String childName = item.getFirstName() + StringUtils.SPACE + item.getLastName();
        view.deleteChildWarning(childName);
    }

    private void openEditChildScreen(Child item) {
        activitySwitcher.open(new AddChildActivity.Screen(item.getId()));
    }

    @Override
    public void start() {
        changeGroupHelper.setGroupChangedListener(this);
        changeGroupHelper.showSpinner(true);
        if (changeGroupHelper.isGroupChecked()) {
            view.showLoad();
            view.setAdapter(adapter);
            getChildrenSubscription = dataService.getChildren()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(children -> {
                        if (!children.isEmpty()) {
                            List<ChildrenAdapter.ChildWrapper> childWrappers =
                                    ChildrenAdapter.ChildWrapper.wrapChildren(children);
                            if (this.adapter.getItems() == null) {
                                adapter.addAll(childWrappers);
                            } else {
                                adapter.getItems().clear();
                                adapter.getItems().addAll(childWrappers);
                                if (clickedPosition != null) {
                                    Filter filter = adapter.getFilter();
                                    if (filterRequest != null) {
                                        filter.filter(filterRequest);
                                    } else {
                                        filter.filter(StringUtils.EMPTY);
                                    }

                                } else {
                                    adapter.getFilter().filter(StringUtils.EMPTY);
                                }

                            }
                            this.children = children;
                            view.showList(clickedPosition);
                        } else view.showEmpty();
                    }, Throwable::printStackTrace);
        }

    }

    private Action1<List<Child>> getChildrenAction = children -> {

    };

    @Override
    public void stop() {
        changeGroupHelper.removeListener(this);
        if (getChildrenSubscription != null) {
            getChildrenSubscription.unsubscribe();
        }
        if (removeChildSubscription != null) {
            removeChildSubscription.unsubscribe();
        }
    }

    @Override
    public void firstStart() {

    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {
        ArrayList<ChildrenAdapter.ChildWrapper> children =
                savedInstanceState.getParcelableArrayList(KEY_CHILDREN_ITEMS);
        if (children != null) {
            adapter.getItems().addAll(children);
        }
    }

    private static final String KEY_CHILDREN_ITEMS = "ChildrenPresenter.Items";

    @Override
    public void onSave(@NonNull Bundle outState) {
        ArrayList<ChildrenAdapter.ChildWrapper> items = (ArrayList<ChildrenAdapter.ChildWrapper>) adapter.getItems();
        if (items != null) {
            outState.putParcelableArrayList(KEY_CHILDREN_ITEMS, items);
        }
    }


    @Override
    public void goToAddChildScreen() {
        activitySwitcher.open(new AddChildActivity.Screen());
    }

    @Override
    public void setSearch(String query) {
        if (query != null) {
            adapter.getFilter().filter(query);
            filterRequest = query;
        } else {
            adapter.getFilter().filter("");
        }
    }

    @Override
    public void onListItemClick(Child item) {
        int childId = item.getId();
        String firstName = item.getFirstName();
        String lastName = item.getLastName();
        activitySwitcher.open(new ChildActivity.Screen(childId, firstName, lastName));
    }

    @Override
    public void deleteSelectedChild() {
        if (needDeleteChild != 0) {
            view.showLoad();
            removeChildSubscription = dataService.deleteChild(needDeleteChild)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(children -> {
                        this.children = children;
                        if (!children.isEmpty()) {
                            needDeleteChild = 0;
                            adapter.getItems().clear();
                            adapter.getItems().addAll(ChildrenAdapter.ChildWrapper.wrapChildren(children));
                            adapter.getFilter().filter(StringUtils.EMPTY);
                            view.showList(clickedPosition);
                        } else {
                            adapter.getItems().clear();
                            needDeleteChild = 0;
                            view.showEmpty();
                        }
                    }, throwable -> {
                        needDeleteChild = 0;
                        view.showList(clickedPosition);
                        view.networkError();
                    });
        }

    }


    @Override public void groupChanged(Group group) {
        getChildrenSubscription = dataService
                .getChildren()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(children1 -> {
                    if (children1.isEmpty()) {
                        view.showEmpty();
                    } else {
                        List<ChildrenAdapter.ChildWrapper> childWrappers =
                                ChildrenAdapter.ChildWrapper.wrapChildren(children1);
                        adapter.clear();
                        adapter.addAll(childWrappers);
                        view.showList(clickedPosition);
                    }
                });
    }
}
