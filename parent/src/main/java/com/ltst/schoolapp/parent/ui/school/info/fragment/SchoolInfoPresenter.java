package com.ltst.schoolapp.parent.ui.school.info.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.data.model.SchoolInfo;
import com.ltst.schoolapp.parent.ui.school.SchoolInfoWrapper;
import com.ltst.schoolapp.parent.ui.school.item.SchoolItemActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SchoolInfoPresenter implements SchoolInfoContract.Presenter {

    private final SchoolInfoContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final DataService dataService;
    private final SimpleBindableAdapter<SchoolInfoWrapper> adapter =
            new SimpleBindableAdapter<>(R.layout.viewholder_school_info, SchoolInfoViewHolder.class);

    private CompositeSubscription compositeSubscription;

    @Inject
    public SchoolInfoPresenter(SchoolInfoContract.View view,
                               ActivityScreenSwitcher activitySwitcher,
                               DataService dataService) {
        this.view = view;
        this.activitySwitcher = activitySwitcher;
        this.dataService = dataService;
    }

    @Override public void start() {
        configureOnClickAdapter();
        view.setAdapter(adapter);
        if (adapter.getItemCount() == 0) {
            compositeSubscription = new CompositeSubscription();
            compositeSubscription.add(dataService.getSchoolInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getInfoSubscription, throwableSubscription));
        }

    }

    private void configureOnClickAdapter() {
        adapter.setActionListener((position, item) -> {
            activitySwitcher.open(new SchoolItemActivity.Screen(item));
        });
    }

    private Action1<SchoolInfo> getInfoSubscription = schoolInfo -> {
        List<SchoolInfo.InfoSchool> schools = schoolInfo.getSchools();
        List<SchoolInfo.Teacher> teachers = schoolInfo.getTeachers();
        List<SchoolInfo.Child> children = schoolInfo.getChildren();
        List<SchoolInfoWrapper> items = new ArrayList<>(schools.size() + teachers.size());
        for (SchoolInfo.InfoSchool school : schools) {
            SchoolInfoWrapper schoolWrapper = SchoolInfoWrapper.fromSchool(school);
            schoolWrapper.names = new ArrayList<>();
            for (SchoolInfo.Child child : children) {
                List<Long> schoolIds = child.getSchoolIds();
                if (schoolIds.contains(school.getId())) {
                    schoolWrapper.names.add(child.getFirstName());
                }
            }
            items.add(schoolWrapper);
            for (SchoolInfo.Teacher teacher : teachers) {
                if (teacher.getSchoolId() == school.getId()) {
                    SchoolInfoWrapper teacherWrapper = SchoolInfoWrapper.fromTeacher(teacher);
                    teacherWrapper.names = new ArrayList<>();
                    List<Long> teacherGroupIds = teacher.getGroupIds();
                    for (SchoolInfo.Child child : children) {
                        List<Long> childGroupIds = child.getGroupIds();
                        for (Long childGroupId : childGroupIds) {
                            if (teacherGroupIds.contains(childGroupId)) {
                                teacherWrapper.names.add(child.getFirstName());
                            }
                        }
                    }
                    items.add(teacherWrapper);
                }
            }
        }
        adapter.addAll(items);

    };

    private Action1<Throwable> throwableSubscription = throwable -> {
        if (throwable instanceof NetErrorException) {
            SchoolInfoPresenter.this.view.netError();
        }
    };

    @Override public void stop() {
        compositeSubscription.unsubscribe();
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
}
