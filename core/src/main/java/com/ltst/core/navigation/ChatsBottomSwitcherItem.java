package com.ltst.core.navigation;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ltst.core.R;

public class ChatsBottomSwitcherItem extends BottomSwitcherItem {

    private ImageView unreadIcon;

    public ChatsBottomSwitcherItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        unreadIcon = ((ImageView) findViewById(R.id.chats_unread_icon));
    }

    public void setUnreadIconVisible(boolean visible) {
        unreadIcon.setVisibility(visible ? VISIBLE : GONE);
    }
}
