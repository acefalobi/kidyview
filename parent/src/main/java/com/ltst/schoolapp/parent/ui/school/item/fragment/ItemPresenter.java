package com.ltst.schoolapp.parent.ui.school.item.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.ui.school.SchoolInfoWrapper;

import java.util.List;

import javax.inject.Inject;

public class ItemPresenter implements ItemContract.Presenter {

    private final ItemContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final ApplicationSwitcher applicationSwitcher;
    private final SchoolInfoWrapper wrapper;
    private final ParentApplication context;
    private final String SCHOOL_PREFIX;
    private final String TEACHER_PREFIX;

    @Inject
    public ItemPresenter(ItemContract.View view,
                         ActivityScreenSwitcher activitySwitcher,
                         ApplicationSwitcher applicationSwitcher,
                         SchoolInfoWrapper wrapper,
                         ParentApplication context) {
        this.view = view;
        this.activitySwitcher = activitySwitcher;
        this.applicationSwitcher = applicationSwitcher;
        this.wrapper = wrapper;
        this.context = context;
        SCHOOL_PREFIX = context.getResources().getString(R.string.school);
        TEACHER_PREFIX = context.getResources().getString(R.string.teacher);

    }

    @Override public void start() {
        view.setTitle(wrapper.title);
        view.setNames(getNames(wrapper.names, wrapper.isTeacher));
        view.setAvatar(wrapper.avatarUrl, wrapper.isTeacher ? R.drawable.ic_profile : R.drawable.ic_cave);
        workOnAddress();
        workOnPhone();
        workOnAdditionalPhone();
        workOnEmail();

    }

    private String getNames(List<String> names, boolean isTeacher) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < names.size(); x++) {
            builder.append(names.get(x));
            if (x != names.size() - 1) {
                builder.append(StringUtils.COMMA);
                builder.append(StringUtils.SPACE);
            }
        }
        builder.append(StringUtils.APOSTROPHE_S);
        builder.append(StringUtils.SPACE);
        builder.append(isTeacher ? TEACHER_PREFIX : SCHOOL_PREFIX);
        return builder.toString();
    }

    private void workOnAddress() {
        if (wrapper.isTeacher) {
            view.hideAddressField();
        } else {
            view.setAddress(wrapper.address, context.getString(R.string.item_info_school_address));
        }
    }

    private void workOnPhone() {
        String phoneDescription = context.getString(wrapper.isTeacher ? R.string.item_info_personal_phone
                : R.string.item_info_school_phone);
        view.setPhone(wrapper.phoneNumber, phoneDescription);
    }

    private void workOnAdditionalPhone() {
        String additionalPhone = wrapper.additionalPhoneNumber;
        if (!StringUtils.isBlank(additionalPhone)) {
            view.setAdditionalPhone(additionalPhone);
        } else {
            view.hideAdditionalPhoneField();
        }
    }

    private void workOnEmail() {
        String emailDescription = context.getString(wrapper.isTeacher ? R.string.item_info_personal_email
                : R.string.item_info_school_email);
        view.setEmail(wrapper.email, emailDescription);
    }


    @Override public void stop() {

    }

    @Override public void firstStart() {

    }

    @Override public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override public void onSave(@NonNull Bundle outState) {

    }

    @Override public void goBack() {
        activitySwitcher.goBack();
    }

    @Override public void openDialer() {
        applicationSwitcher.openDial(wrapper.phoneNumber);
    }

    @Override public void openEmailClient() {
        applicationSwitcher.openEmailApplication(wrapper.email);
    }
}
