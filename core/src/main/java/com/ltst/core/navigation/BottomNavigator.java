package com.ltst.core.navigation;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ltst.core.R;
import com.ltst.core.layer.LayerModule;

import java.util.List;

public class BottomNavigator extends LinearLayout implements View.OnClickListener {

    private FragmentScreenSwitcher fragmentScreenSwitcher;
    private List<BottomNavigationFragmentScreen> screens;
    private int selectedChild;
    private ChatsBottomSwitcherItem chatsItem;

    public BottomNavigator(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOrientation(HORIZONTAL);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bottom_navigator_background));
    }

    public void init(FragmentScreenSwitcher fragmentScreenSwitcher,
                     List<BottomNavigationFragmentScreen> screens) {
        init(fragmentScreenSwitcher, screens, false);
    }

    public void init(FragmentScreenSwitcher fragmentScreenSwitcher,
                     List<BottomNavigationFragmentScreen> screens, boolean isFirstStart) {
        // TODO: 10.03.17 (alexeenkoff)  need refactoring. Don`t used init isFirstStart on Main Activity of Teacher application
        if (getChildCount() != 0) {
            return;
        }
        this.fragmentScreenSwitcher = fragmentScreenSwitcher;
        this.screens = screens;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (BottomNavigationFragmentScreen screen : screens) {
            View item;
            if (screen.getName().equals(LayerModule.CHATS_SCREEN_NAME)) {
                item = inflater.inflate(R.layout.chats_bottom_switch_item, this, false);
                chatsItem = ((ChatsBottomSwitcherItem) item);
            } else {
                item = inflater.inflate(R.layout.bottom_switcher_item, this, false);
            }
            BottomSwitcherItem bottomSwitcherItem = (BottomSwitcherItem) item;
            bottomSwitcherItem.setData(screen);

            item.setOnClickListener(this);
            addView(item);
        }
        if (!fragmentScreenSwitcher.hasFragments()) {
            int selectScreen = isFirstStart ? screens.size() - 1 : 0;
            BottomNavigationFragmentScreen startScreen = screens.get(selectScreen);
            fragmentScreenSwitcher.open(startScreen);
            getChildAt(selectScreen).setSelected(true);
        }
    }

    @Override
    public void onClick(View v) {
        unselectAll();
        BottomSwitcherItem switcherItem = (BottomSwitcherItem) v;
        String tag = switcherItem.getTagName();
        for (int x = 0; x < screens.size(); x++) {
            if (screens.get(x).getName().equals(tag)) {
                fragmentScreenSwitcher.open(screens.get(x), false);
                switcherItem.setSelected(true);
                selectedChild = x;
            }
        }
    }

    public void openScreen(BottomNavigationFragmentScreen screen) {
        fragmentScreenSwitcher.open(screen, false);
        unselectAll();
        int childCount = getChildCount();
        for (int x = 0; x < childCount; x++) {
            BottomSwitcherItem bottomSwitcherItem = (BottomSwitcherItem) getChildAt(x);
            if (bottomSwitcherItem.getTagName().equals(screen.getName())) {
                bottomSwitcherItem.setSelected(true);
                selectedChild = x;
            }
        }
    }

    private static final String SUPER_STATE = "BottomNavigator.State";
    private static final String SELECTED_ITEM = "BottomNavigator.SelectedItem";

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE, parcelable);
        bundle.putInt(SELECTED_ITEM, selectedChild);
        return bundle;

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            Parcelable superState = bundle.getParcelable(SUPER_STATE);
            selectedChild = bundle.getInt(SELECTED_ITEM);
            BottomSwitcherItem childAt = (BottomSwitcherItem) getChildAt(selectedChild);
            childAt.setSelected(true);
            super.onRestoreInstanceState(superState);
        } else super.onRestoreInstanceState(state);

    }

    private void unselectAll() {
        for (int x = 0; x < screens.size(); x++) {
            BottomSwitcherItem childAt = (BottomSwitcherItem) getChildAt(x);
            childAt.setSelected(false);
            childAt.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        }
    }

    public void setUnreadMessagesIndicator(long unreaedMessagesCount) {
        chatsItem.setUnreadIconVisible(unreaedMessagesCount > 0);
    }
}
