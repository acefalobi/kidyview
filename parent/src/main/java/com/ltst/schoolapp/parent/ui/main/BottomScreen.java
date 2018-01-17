package com.ltst.schoolapp.parent.ui.main;

import com.ltst.core.layer.LayerModule;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.schoolapp.parent.ui.main.chats.ChatsFragment;
import com.ltst.schoolapp.parent.ui.main.checks.ChecksFragment;
import com.ltst.schoolapp.parent.ui.main.events.EventsFragment;
import com.ltst.schoolapp.parent.ui.main.feed.FeedFragment;
import com.ltst.schoolapp.parent.ui.main.profile.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

public enum BottomScreen {
    FEED(FeedFragment.Screen.class,"feed"),
    CHECKS(ChecksFragment.Screen.class,"checks"),
    EVENTS(EventsFragment.Screen.class, "events"),
    CHATS(ChatsFragment.Screen.class, LayerModule.CHATS_SCREEN_NAME),
    PROFILE(ProfileFragment.Screen.class, "profile");

    private Class<? extends BottomNavigationFragmentScreen> fragmentClass;

    private String screenTag;

    BottomScreen(Class<? extends BottomNavigationFragmentScreen> fragmentClass, String screenTag) {
        this.fragmentClass = fragmentClass;
        this.screenTag = screenTag;
    }

    @Override public String toString() {
        return screenTag;
    }

    private BottomNavigationFragmentScreen getInstance() throws IllegalAccessException, InstantiationException {
        return fragmentClass.newInstance();
    }

    public static List<BottomNavigationFragmentScreen> getScreens() {
        List<BottomNavigationFragmentScreen> screens = new ArrayList<>(values().length);
        for (BottomScreen bottomScreen : values()) {
            try {
                screens.add(bottomScreen.getInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return screens;
    }
}
