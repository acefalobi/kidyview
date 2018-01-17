package com.ltst.schoolapp.parent.ui.checkout.fragment.share;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.util.SharingService;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.ui.main.BottomScreen;
import com.ltst.schoolapp.parent.ui.main.MainActivity;

import javax.inject.Inject;

public class ShareCodePresenter implements ShareCodeContract.Presenter {

    private final ShareCodeContract.View view;
    private final ActivityScreenSwitcher activityScreenSwitcher;
    private final SharingService sharingService;
    private final ParentApplication context;
    private final String code;
    private final String header;
    private final String names;
    private final String schoolTitle;

    @Inject
    public ShareCodePresenter(ShareCodeContract.View view,
                              ActivityScreenSwitcher activityScreenSwitcher,
                              Bundle screenParams,
                              SharingService sharingService,
                              ParentApplication context) {
        this.view = view;
        this.activityScreenSwitcher = activityScreenSwitcher;
        this.sharingService = sharingService;
        this.context = context;
        this.code = screenParams.getString(ShareCodeFragment.Screen.CODE_KEY);
        this.header = screenParams.getString(ShareCodeFragment.Screen.SHARE_SCREEN_HEADER_KEY);
        this.names = screenParams.getString(ShareCodeFragment.Screen.SHARE_SCREEN_NAMES_KEY);
        this.schoolTitle = screenParams.getString(ShareCodeFragment.Screen.SHARE_SCREEN_SCHOOL_TITLE_KEY);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void firstStart() {
        view.bindView(code, header);
    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    @Override
    public void goBack() {
        activityScreenSwitcher.open(new MainActivity.Screen(BottomScreen.CHECKS));
    }

    @Override
    public void share() {
        String format = context.getString(R.string.share_code_text_format);
        String shareText = String.format(format, names, schoolTitle, code);
        sharingService.shareCode(shareText);
    }
}
