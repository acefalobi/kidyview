package com.ltst.core.util;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Danil on 23.09.2016.
 */

public abstract class SimpleDataObserver extends RecyclerView.AdapterDataObserver {

    @Override
    public final void onChanged() {
        onAnythingChanges();
    }

    @Override
    public final void onItemRangeChanged(int positionStart, int itemCount) {
        onAnythingChanges();
    }

    @Override
    public final void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        onAnythingChanges();
    }

    @Override
    public final void onItemRangeInserted(int positionStart, int itemCount) {
        onAnythingChanges();
    }

    @Override
    public final void onItemRangeRemoved(int positionStart, int itemCount) {
        onAnythingChanges();
    }

    @Override
    public final void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        onAnythingChanges();
    }

    public abstract void onAnythingChanges();
}
