package com.ltst.schoolapp.parent.ui.main;


import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.ltst.core.data.model.ChildInGroup;
import com.ltst.schoolapp.parent.data.DataService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ChildInGroupHelper {

    private DataService dataService;
    private CompositeSubscription subscription;
    private ChildInGroupAdapter adapter;
    private WeakReference<Spinner> spinnerWeakReference;
    private List<ChildInGroupChangeListener> listeners;
    private Integer selectedItem = null;
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
                    if (selectedItem == null || selectedItem != position) {
                        ChildInGroup childInGroup = (ChildInGroup) adapter.getItem(position);
                        dataService.setSelectedChildInGroup(childInGroup.getChildId(), childInGroup.getGroupId());
                        if (listeners != null && !listeners.isEmpty()) {
                            for (ChildInGroupChangeListener listener : listeners) {
                                listener.childInGroupChanged(childInGroup);
                            }
                        }
                        selectedItem = position;
                    }

                }

                @Override public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
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

    public boolean hasItems() {
        return adapter != null && adapter.getCount() > 0;
    }

    public void subscribe() {
        spinnerWeakReference.get().setSelection(selectedItem != null ? selectedItem : 0);
        subscription = new CompositeSubscription();
        subscription.add(Observable.concat(dataService.getCachedChildrenInGroups()
                , dataService.updateChildrenInGroups())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bindSpinnerData, Throwable::printStackTrace));
    }

    private Action1<List<ChildInGroup>> bindSpinnerData = childInGroups -> {
        Spinner spinner = spinnerWeakReference.get();
        if (spinner == null) {
            return;
        }
        if (spinner.getAdapter() == null) {
            adapter = new ChildInGroupAdapter(spinnerWeakReference);
        }
        adapter.addAll(childInGroups);
        spinner.setAdapter(adapter);
        if (selectedItem != null) {
            if (adapter.getCount() - 1 < selectedItem) {
                selectedItem = adapter.getCount() - 1;
                if (adapter.getCount() != 0) {
                    ChildInGroup item = ((ChildInGroup) adapter.getItem(selectedItem));
                    for (ChildInGroupChangeListener listener : listeners) {
                        listener.childInGroupChanged(item);
                    }
                    spinner.setAdapter(adapter);
                }
            }
        }
        spinner.setEnabled(adapter.getCount() >= 2);
        spinnerWeakReference.get().setSelection(selectedItem != null ? selectedItem : 0);
        if (showSpinner) {
            spinner.setVisibility(adapter.getCount() == 0 ? View.GONE : View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    };


    public boolean isChildInGroupChecked() {
        return adapter != null && !adapter.isEmpty();
    }

    public void unsubscribe() {
        selectedItem = spinnerWeakReference.get().getSelectedItemPosition();
        subscription.unsubscribe();
    }

    public void removeListener(ChildInGroupChangeListener listener) {
        if (listeners != null && !listeners.isEmpty()) {
            ChildInGroupChangeListener needRemove = null;
            for (ChildInGroupChangeListener addedListener : listeners) {
                if (addedListener == listener) {
                    needRemove = addedListener;
                }
            }
            if (needRemove != null) {
                listeners.remove(needRemove);
            }
        }
    }

    public void setChildInGroupChangeListener(ChildInGroupChangeListener childInGroupChangeListener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(childInGroupChangeListener);
    }

    public ChildInGroup getSelectedChildInGroup() {
        if (adapter == null) {
            return null;
        }
        if (adapter.getCount() > 0) {
            return ((ChildInGroup) adapter.getItem(selectedItem != null ? selectedItem : 0));
        } else return null;

    }

    public interface ChildInGroupChangeListener {
        void childInGroupChanged(ChildInGroup childInGroup);
    }
}
