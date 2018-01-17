package com.ltst.schoolapp.parent.ui.checkout.select.school.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.data.model.ChildInGroupInSchool;
import com.ltst.schoolapp.parent.ui.checkout.select.school.SelectChildInSchoolActivity;

import java.util.List;

import javax.inject.Inject;

public class SelectChildInSchoolPresenter implements SelectChildInSchoolContract.Presenter, BindableViewHolder.ActionListener<ChildInGroupInSchool> {

    private final SelectChildInSchoolContract.View view;
    private final List<ChildInGroupInSchool> objects;
    private final ActivityScreenSwitcher activitySwitcher;
    private final SimpleBindableAdapter<ChildInGroupInSchool> adapter =
            new SimpleBindableAdapter<>(R.layout.viewholder_select_person_in_group_item, ChildInGroupViewHolder.class);

    @Inject
    public SelectChildInSchoolPresenter(SelectChildInSchoolContract.View view,
                                        Bundle screenParams,
                                        ActivityScreenSwitcher activitySwitcher) {
        this.view = view;
        this.objects = screenParams.getParcelableArrayList(SelectChildInSchoolActivity.Screen.KEY_CHILD_IN_GROUP_IN_SCHOOL);
        this.activitySwitcher = activitySwitcher;
    }

    @Override public void start() {

    }

    @Override public void stop() {

    }

    @Override public void firstStart() {
        view.setNextButtonEnabled(false);
        view.setAdapter(adapter);
        for (ChildInGroupInSchool object : objects) {
            if (object.isSelected()) {
                view.setNextButtonEnabled(true);
            }
        }
        adapter.addAll(objects);
        adapter.setActionListener(this);


    }

    @Override public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override public void onSave(@NonNull Bundle outState) {

    }

    @Override public void OnItemClickListener(int position, ChildInGroupInSchool item) {
        for (ChildInGroupInSchool object : adapter.getItems()) {
            object.setSelected(false);
        }
        item.setSelected(true);
        view.setNextButtonEnabled(true);
        adapter.notifyDataSetChanged();
    }

    @Override public void goBack() {
        activitySwitcher.goBack();
    }

    @Override public void setResultAndClose() {
        ChildInGroupInSchool selectedObject = null;
        for (ChildInGroupInSchool object : objects) {
            if (object.isSelected()) {
                selectedObject = object;
                break;
            }
        }
        Intent intent = new Intent();
        intent.putExtra(SelectChildInSchoolActivity.Screen.KEY_SELECTED_OBJECT, selectedObject);
        activitySwitcher.setResultAndGoBack(intent);
    }
}
