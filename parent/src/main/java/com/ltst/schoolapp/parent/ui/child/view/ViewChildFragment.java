package com.ltst.schoolapp.parent.ui.child.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Group;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.util.CalendarUtil;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.data.model.ParentChild;
import com.ltst.schoolapp.parent.ui.child.ChildScope;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewChildFragment extends CoreFragment implements ViewChildContract.View {

    @Inject
    ViewChildPresenter presenter;

    private Toolbar toolbar;

    @BindView(R.id.child_root_view) ViewGroup rootView;
    @BindView(R.id.view_child_avatar) AvatarView avatarView;
    @BindView(R.id.view_child_name) TextView nameField;
    @BindView(R.id.view_child_age) TextView ageField;
    @BindView(R.id.view_child_birthday) TextView birthdayField;
    @BindView(R.id.view_child_blood_group) TextView bloodField;
    @BindView(R.id.view_child_blood_genotype) TextView genotypeField;
    @BindView(R.id.view_child_blood_allergies) TextView allergiesField;
    @BindView(R.id.view_child_information) TextView informationField;
    @BindView(R.id.view_child_root_groups_container) LinearLayout groupsContainer;
    @BindView(R.id.child_parents_list) RecyclerView familyRecyclerView;
    @BindString(R.string.view_child_missed)
    String missed;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_coordinator_view_child;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        ChildScope.ChildComponent component = (ChildScope.ChildComponent) rootComponent.getComponent();
        component.viewChildComponent(new ViewChildScope.ViewChildModule(this)).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        avatarView.setClickAvatarCallBack(() -> presenter.openAvatar());
        Context context = getContext();
        LinearLayoutManager familyLayoutManager = new LinearLayoutManager(context);
        familyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        familyRecyclerView.setLayoutManager(familyLayoutManager);
        return view;
    }

    @Override
    public void bindData(ParentChild parentChild, boolean canEditChild) {
        Child child = parentChild.getChild();
        avatarView.setAvatar(child.getAvatarUrl());
        nameField.setText(child.getFirstName() + StringUtils.SPACE + child.getLastName());
        setAgeAndGender(child.getBirthDay(), child.getGender());
        setBirthday(child.getBirthDay());
        setBloodGroup(child.getBloodGroup());
        setGenotype(child.getGenotype());
        setAllergies(child.getAllergies());
        setAdditional(child.getAdditional());
        if (!canEditChild) {
            Menu menu = this.toolbar.getMenu();
            MenuItem actionItem = menu.findItem(R.id.action_edit);
            actionItem.setVisible(false);
        }

    }

    @BindDimen(R.dimen.view_child_item_group_avatar_size)
    int groupAvatarSize;

    @Override
    public void setGroups(List<Group> groups) {

        int existingChildCount = groupsContainer.getChildCount();
        if (existingChildCount == groups.size()) {
            groupsContainer.removeAllViews();
        }
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        for (Group group : groups) {
            View view = layoutInflater.inflate(R.layout.viewholder_item_group_container, groupsContainer, false);
            ImageView groupAvatar = (ImageView) view.findViewById(R.id.view_child_item_group_avatar);
            AvatarView.ImageViewTarget imageTarget = new AvatarView.ImageViewTarget(groupAvatar);
            Glide.with(groupAvatar.getContext())
                    .load(group.getAvatarUrl())
                    .asBitmap()
                    .override(groupAvatarSize, groupAvatarSize)
                    .centerCrop()
                    .placeholder(R.drawable.ic_cave)
                    .error(R.drawable.ic_cave)
                    .into(imageTarget);
            TextView groupTitle = (TextView) view.findViewById(R.id.view_child_item_group_title);
            groupTitle.setText(group.getTitle());
            groupsContainer.addView(view);
        }
    }


    @Override
    protected int getBackgroundColorId() {
        return R.color.pale_gray;
    }

    @Override
    public void setFamilyAdapter(RecyclerBindableAdapter familyAdapter, boolean canEditChild) {
        RecyclerView.Adapter adapter = familyRecyclerView.getAdapter();
        if (adapter == null || adapter == familyAdapter) {
            familyRecyclerView.setAdapter(familyAdapter);
        }
        if (familyAdapter.getFootersCount() == 0 && canEditChild) {
            addSendRequestFooter(familyAdapter);
        }

    }

    private void addSendRequestFooter(RecyclerBindableAdapter familyAdapter) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View footer = inflater.inflate(R.layout.layout_view_child_footer, rootView, false);
        View requestButton = ButterKnife.findById(footer, R.id.child_send_request_button);
        requestButton.setOnClickListener(v -> presenter.familyRequest());
        familyAdapter.addFooter(footer);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    private void setBloodGroup(String bloodGroup) {
        String bloodGroupFormat = getString(R.string.view_child_blood_group_format);
        String text = String.format(bloodGroupFormat, StringUtils.isBlank(bloodGroup) ? missed : bloodGroup);
        bloodField.setText(text);
    }

    private void setGenotype(String genotype) {
        String genotypeFormat = getString(R.string.view_child_genotype_format);
        String text = String.format(genotypeFormat, StringUtils.isBlank(genotype) ? missed : genotype);
        genotypeField.setText(text);
    }

    private void setAllergies(String allergies) {
        String allergiesFormat = getString(R.string.view_child_allergies_format);
        String text = String.format(allergiesFormat, StringUtils.isBlank(allergies) ? missed : allergies);
        allergiesField.setText(text);
    }

    private void setAdditional(String additional) {
        String informationFormat = getString(R.string.view_child_additional_format);
        String text = String.format(informationFormat, StringUtils.isBlank(additional) ? missed : additional);
        informationField.setText(text);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(getString(R.string.view_child_title));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
        toolbar.inflateMenu(R.menu.menu_edit);
        toolbar.setOnMenuItemClickListener(item -> {
            presenter.openEditChildScreen();
            return true;
        });
    }

    private void setAgeAndGender(String birthday, String gender) {
        String age = StringUtils.EMPTY;
        if (!StringUtils.isBlank(birthday)) {
            Calendar birthdayCalendar = CalendarUtil.parseDateString(Child.SERVER_FORMAT, birthday);
            if (birthdayCalendar != null) {
                age = CalendarUtil.getDiffAge(birthdayCalendar, Calendar.getInstance());
            }
        }
        if (gender != null) {
            String displayedGender = gender.equals(Child.MALE) ?
                    getString(R.string.child_boy) :
                    getString(R.string.child_girl);
            String formatted = displayedGender
                    + StringUtils.SPACE
                    + age;
            ageField.setText(formatted);
            ageField.setVisibility(View.VISIBLE);
        } else {
            ageField.setVisibility(View.GONE);
        }
    }

    private void setBirthday(String birthday) {
//        try {
//            Calendar calendar = CalendarUtil.parseDateString(Child.SERVER_FORMAT, birthday);
//            String format = getString(R.string.view_child_birtdate_format);
//            String displayedBirthday = String.format(format,
//                    calendar.get(Calendar.DAY_OF_MONTH),
//                    calendar.get(Calendar.MONTH),
//                    calendar.get(Calendar.YEAR));
//            birthdayField.setText(displayedBirthday);
//        } catch (Exception e) {
//            e.printStackTrace();
//            birthdayField.setVisibility(View.GONE);
//        }
        if (StringUtils.isBlank(birthday)) {
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(Child.SERVER_FORMAT);

        try {
            Date parse = dateFormat.parse(birthday);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            dateFormat.applyPattern(Child.BIRTHDAY_FORMAT);
            birthdayField.setText(dateFormat.format(calendar.getTime()));
//            birthdayField.setText(displayedBirthday);
        } catch (Exception e) {
            e.printStackTrace();
            birthdayField.setVisibility(View.GONE);
        }
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new ViewChildFragment();
        }
    }
}
