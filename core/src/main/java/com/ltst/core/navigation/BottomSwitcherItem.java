package com.ltst.core.navigation;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ltst.core.R;

public class BottomSwitcherItem extends RelativeLayout {

    private String tag;

    private ImageView icon;

    private @DrawableRes int selectedIcon;
    private @DrawableRes int unSelectedIcon;


    public BottomSwitcherItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        icon = ((ImageView) findViewById(R.id.navigator_item_icon));
    }

    public void setData(BottomNavigationFragmentScreen screen) {
        selectedIcon = screen.selectedIconId();
        unSelectedIcon = screen.unselectedIconId();
        icon.setImageDrawable(ContextCompat.getDrawable(getContext(), unSelectedIcon));
        tag = screen.getName();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected){
            setIcon(selectedIcon);
            setBackgroundColor(ContextCompat.getColor(getContext(),R.color.bottom_navigator_selected));
        }
        else {
            setIcon(unSelectedIcon);
            setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.transparent));
        }

    }

    public String getTagName() {
        return tag;
    }

    private void setIcon(@DrawableRes int iconRes) {
        icon.setImageDrawable(ContextCompat.getDrawable(getContext(), iconRes));
    }
}
