package com.ltst.schoolapp.teacher.ui.child.viewchild;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.ui.TwoLineTextViewWithIcon;
import com.ltst.core.util.CalendarUtil;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.child.ChildScope;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

public class ViewChildFragment extends CoreFragment implements ViewChildContract.View {

    @Inject
    ViewChildPresenter presenter;
    @Inject
    DialogProvider dialogProvider;
    @BindView(R.id.view_child_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.view_child_avatar)
    AvatarView avatarView;
    @BindView(R.id.view_child_first_name)
    TextView fistNameField;
    @BindView(R.id.view_child_last_name)
    TextView lastNameField;
    @BindView(R.id.view_child_age)
    TextView ageField;
    @BindView(R.id.view_child_birthday)
    TextView birthdayField;
    @BindView(R.id.view_child_family_field)
    TwoLineTextViewWithIcon familyField;
    @BindView(R.id.view_child_blood_medical_container)
    ViewGroup medicalContainer;
    @BindView(R.id.view_child_blood_group)
    TextView bloodGroupField;
    @BindView(R.id.view_child_blood_genotype)
    TextView genotypeField;
    @BindView(R.id.view_child_blood_allergies)
    TextView allergiesView;
    @BindView(R.id.view_child_information_container)
    ViewGroup informationContainer;
    @BindView(R.id.view_child_information)
    TextView informationField;
//    @BindView(R.id.view_child_groups_container)
//    RecyclerView groupsRecyclerView;
    @BindView(R.id.view_child_root_groups_container)
    LinearLayout groupsContainer;
    ViewGroup rootView;
    @BindString(R.string.view_child_missed)
    String missed;
    private Toolbar toolbar;


    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_new_view_child;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        ChildScope.ChildComponent component = (ChildScope.ChildComponent) rootComponent.getComponent();
        component.viewChildComponent(new ViewChildScope.ViewChildModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
//        LinearLayoutManager groupsLayoutManager = new LinearLayoutManager(getContext());
//        groupsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        groupsRecyclerView.setLayoutManager(groupsLayoutManager);
        avatarView.setClickAvatarCallBack(() -> presenter.openAvatarPhoto());
        return view;
    }

//    @Override public void setGroupsAdapter(RecyclerBindableAdapter groupsAdapter) {
//        groupsRecyclerView.setAdapter(groupsAdapter);
//    }

    @OnClick(R.id.view_child_family_field)
    void onFamilyClick() {
        presenter.openFamily();
    }

    @OnClick(R.id.view_child_add_activity)
    void onAddActivityClick() {
        presenter.openAddActivity();
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void setAvatar(String avatarUrl) {
        avatarView.setAvatar(avatarUrl);
    }

    @Override
    public void setFirstName(String name) {
        fistNameField.setText(name);
    }

    @Override
    public void setLastName(String lastName) {
        lastNameField.setText(lastName);
    }

    @BindDimen(R.dimen.view_child_item_group_avatar_size)
    int groupAvatarSize;

    @Override
    public void setGroups(List<Group> groups) {
        groupsContainer.removeAllViews();
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
    public void setAgeAndGender(String birthday, String gender) {
        Timber.d(birthday);
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
            String formatted = displayedGender +
                    StringUtils.SPACE +
                    age;
            ageField.setText(formatted);
            ageField.setVisibility(View.VISIBLE);
        } else {
            hideAgeField();
        }
    }

    @Override
    public void hideAgeField() {
        ageField.setVisibility(View.GONE);
    }

    @Override
    public void hideBirthdayField() {
        birthdayField.setVisibility(View.GONE);
    }

    @Override
    public void setBirthday(String birthday) {
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
            hideBirthdayField();
        }
    }

    @Override
    public void setAllergies(String allergies) {
        String allergiesFormat = getString(R.string.view_child_allergies_format);
        String text = String.format(allergiesFormat, StringUtils.isBlank(allergies) ? missed : allergies);
        allergiesView.setText(text);
    }

    @Override
    public void setBloodInfo(String bloodGroup, String genotype) {
        String bloodGroupFormat = getString(R.string.view_child_blood_group_format);
        String bloodText = String.format(bloodGroupFormat, StringUtils.isBlank(bloodGroup) ? missed : bloodGroup);
        bloodGroupField.setText(bloodText);
        String genotypeFormat = getString(R.string.view_child_genotype_format);
        String genotypeText = String.format(genotypeFormat, StringUtils.isBlank(genotype) ? missed : genotype);
        genotypeField.setText(genotypeText);
    }

    @Override
    public void setInfo(String additional) {
        String informationFormat = getString(R.string.view_child_additional_format);
        String text = String.format(informationFormat, StringUtils.isBlank(additional) ? missed : additional);
        informationField.setText(text);
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void networkError() {
        dialogProvider.showNetError(getContext());
    }

    @Override
    public void setFamilyPeopleCount(int count, boolean hasNewMembers) {
        if (!hasNewMembers) {
            String format = getString(R.string.view_child_family_count_format);
            familyField.setMainText(String.format(format, count));
        } else {
            String newFamilyMember = getString(R.string.view_child_family_new_member);
            SpannableString span = new SpannableString(newFamilyMember);
            span.setSpan(new StyleSpan(Typeface.BOLD), 0, span.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            familyField.setMainText(span);
        }
    }


    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {

            return getClass().getSimpleName();
        }

        @Override
        protected Fragment createFragment() {

            return new ViewChildFragment();
        }
    }
}
