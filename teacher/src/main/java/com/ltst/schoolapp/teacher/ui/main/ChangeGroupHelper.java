package com.ltst.schoolapp.teacher.ui.main;


import android.database.DataSetObserver;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Group;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.ltst.core.util.ForegroundImageView;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.data.DataService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ChangeGroupHelper {

    private DataService dataService;
    private CompositeSubscription compositeSubscription;
    private GroupsAdapter adapter;
    WeakReference<Spinner> spinnerWeakReference;
    private List<GroupChangedListener> listeners;
    private Long selectedGroupId;
    private boolean showSpinner;


    void init(DataService dataService, WeakReference<Spinner> spinnerWeakReference) {
        this.dataService = dataService;
        this.spinnerWeakReference = spinnerWeakReference;
        initOnIltemClick(spinnerWeakReference);
    }

    private void initOnIltemClick(WeakReference<Spinner> spinnerWeakReference) {
        Spinner spinner = spinnerWeakReference.get();
        if (spinner != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    if (selectedGroupId != null) {
                    Group item = (Group) adapter.getItem(position);
                    long selectedGroup = item.getId();
                    if (selectedGroupId == null || selectedGroupId != selectedGroup) {
                        dataService.changeSelectedGroup(selectedGroup);
                        if (listeners != null && !listeners.isEmpty()) {
                            for (GroupChangedListener listener : listeners) {
                                listener.groupChanged(item);
                            }
                        }
                        selectedGroupId = selectedGroup;
                    }
                }

                @Override public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void subscribe() {
        int selection = 0;
        if (selectedGroupId != null) {
            List<Group> items = adapter.getItems();
            for (int x = 0; x < items.size(); x++) {
                if (items.get(x).getId() == selectedGroupId) {
                    selection = x;
                }
            }
        }
//        spinnerWeakReference.get().setVisibility(View.VISIBLE);
        if (adapter != null && adapter.getCount() != 0) {
            spinnerWeakReference.get().setSelection(selection);
        }

        compositeSubscription = new CompositeSubscription();
        compositeSubscription.add(dataService.getGroups()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groups -> {
                    Spinner spinner = spinnerWeakReference.get();
                    if (spinner == null) {
                        return;
                    }
                    if (spinner.getAdapter() == null) {
                        adapter = new GroupsAdapter(spinnerWeakReference);
                    }
                    adapter.addAll(groups);
                    spinner.setAdapter(adapter);
                    int spinnerSelection = 0;
                    if (selectedGroupId != null) {
                        for (int x = 0; x < groups.size(); x++) {
                            if (groups.get(x).getId() == selectedGroupId) {
                                spinnerSelection = x;
                            }
                        }
                    }
                    if (adapter != null && adapter.getCount() > 0) {
                        spinner.setSelection(spinnerSelection);
                    }

                    adapter.notifyDataSetChanged();
                    if (groups.size() == 1) {
                        spinner.setEnabled(false);
                    } else spinner.setEnabled(true);
                    if (showSpinner) {
                        spinner.setVisibility(adapter.getCount() == 0 ? View.GONE : View.VISIBLE);
                    }
                }, Throwable::printStackTrace));
    }

    public void unsubscribe() {
        int selectedPosition = spinnerWeakReference.get().getSelectedItemPosition();
        if (adapter != null && adapter.getCount() != 0) {
            selectedGroupId = ((Group) adapter.getItem(selectedPosition)).getId();
        }

        compositeSubscription.unsubscribe();

    }

    public void removeListener(GroupChangedListener listener) {
        if (listeners != null && !listeners.isEmpty()) {
            GroupChangedListener needRemove = null;
            for (GroupChangedListener addedListener : listeners) {
                if (addedListener == listener) {
                    needRemove = addedListener;
                }
            }
            if (needRemove != null) {
                listeners.remove(needRemove);
            }
        }
    }

    public void setGroupChangedListener(GroupChangedListener groupChangedListener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(groupChangedListener);
    }

    public void showSpinner(boolean showSpinner) {
        this.showSpinner = showSpinner;
        if (spinnerWeakReference != null) {
            Spinner spinner = spinnerWeakReference.get();
            if (spinner != null && adapter != null) {
                if (showSpinner) {
                    spinner.setVisibility(adapter.getCount() == 0 ? View.GONE : View.VISIBLE);
                } else {
                    spinner.setVisibility(View.GONE);
                }
            }
        }
    }

    public boolean isGroupChecked() {
        return adapter != null && !adapter.isEmpty();
    }

    private static class GroupsAdapter extends BaseAdapter implements SpinnerAdapter {

        private final static int COUNT_OF_TITLE_CHARS = 15;


        private List<Group> groups = new ArrayList<>();
        private WeakReference<Spinner> spinnerWeakReference;

        GroupsAdapter(WeakReference<Spinner> spinnerWeakReference) {
            this.spinnerWeakReference = spinnerWeakReference;
        }

        List<Group> getItems() {
            return groups;
        }


        @Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View item = LayoutInflater.from(spinnerWeakReference.get().getContext())
                    .inflate(R.layout.spinner_dropdown_item, parent, false);
            Group group = groups.get(position);
            String iconUrl = group.getAvatarUrl();
            ForegroundImageView grouIconView = ((ForegroundImageView) item.findViewById(R.id.spinner_item_icon));
            if (iconUrl != null && iconUrl.equals(SelectPersonModel.GROUP_HOLDER)) {
                Glide.with(item.getContext())
                        .load(com.ltst.core.R.drawable.ic_cave)
                        .into(grouIconView);
            } else {
                Glide.with(item.getContext())
                        .load(iconUrl)
                        .thumbnail(0.2f)
                        .error(com.ltst.core.R.drawable.ic_cave)
                        .into(grouIconView);
            }
            String title = group.getTitle();
            title = StringUtils.cutLongText(title, COUNT_OF_TITLE_CHARS);
            TextView groupTitleView = (TextView) item.findViewById(R.id.spinner_item_title);
            groupTitleView.setTextColor(ContextCompat.getColor(groupTitleView.getContext(), android.R.color.black));
            groupTitleView.setText(title);
            return item;
        }

        @Override public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        public void addAll(List<Group> groups) {
            this.groups = groups;
            notifyDataSetChanged();
            for (Group group : groups) {
                if (group.isSelected()) {
                    Spinner spinner = spinnerWeakReference.get();
                    if (spinner != null) {
                        spinner.setSelection(groups.indexOf(group));
                    }
                    break;
                }
            }

        }

        @Override public int getCount() {
            return groups.size();
        }

        @Override public Object getItem(int position) {
            return groups.get(position);
        }

        @Override public long getItemId(int position) {
            return groups.get(position).getId();
        }

        @Override public boolean hasStableIds() {
            return false;
        }


        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View resultView;
            Spinner spinner = spinnerWeakReference.get();
            if (spinner != null) {
                LayoutInflater layoutInflater = LayoutInflater.from(spinner.getContext());
                if (convertView == null) {
                    resultView = layoutInflater.inflate(R.layout.spiner_item, parent, false);
                } else {
                    resultView = convertView;
                }
                Group group = groups.get(position);
                String title = group.getTitle();
                title = StringUtils.cutLongText(title, COUNT_OF_TITLE_CHARS);
                TextView titleView = (TextView) resultView.findViewById(R.id.spinner_item_title);
                titleView.setTextColor(ContextCompat.getColor(spinner.getContext(), android.R.color.white));
                titleView.setText(title);
                return resultView;
            }
            return null;
        }


        @Override public int getItemViewType(int position) {
            return 1;
        }

        @Override public int getViewTypeCount() {
            return 1;
        }

        @Override public boolean isEmpty() {
            return getCount() == 0;
        }

    }

    public interface GroupChangedListener {
        void groupChanged(Group group);
    }
}
