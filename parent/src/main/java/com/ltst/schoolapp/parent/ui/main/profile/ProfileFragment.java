package com.ltst.schoolapp.parent.ui.main.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.data.model.Profile;
import com.ltst.core.data.preferences.qualifiers.IsFirstStart;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.main.BottomScreen;
import com.ltst.schoolapp.parent.ui.main.MainScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileFragment extends CoreFragment implements ProfileContract.View {

    @Inject
    ProfilePresenter presenter;

    @Inject
    DialogProvider dialogProvider;

    @Inject
    @IsFirstStart BooleanPreference isFirstStart;

    @BindView(R.id.profile_child_list)
    RecyclerView childList;

    @BindView(R.id.profile_progress_bar) ProgressBar progressBar;

    /*Header*/
    @BindView(R.id.profile_header_avatar_view) AvatarView avatarView;
    @BindView(R.id.profile_header_name_field) TextView nameField;
    @BindView(R.id.profile_email_field) TextView emailField;
    @BindView(R.id.profile_phones_field) TextView phonesField;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_view_profile;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        MainScope.MainComponent component = (MainScope.MainComponent) rootComponent.getComponent();
        component.profileComponent(new ProfileScope.ProfileModule(this)).inject(this);
    }

    @OnClick(R.id.profile_header_school)
    void onSchoolInfoClick() {
        presenter.openSchoolScreen();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        childList.setLayoutManager(layoutManager);
        avatarView.setClickAvatarCallBack(() -> presenter.openAvatarPhoto());
        return view;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(isFirstStart.get() ? R.string.profile_first_start_title : R.string.profile_title);
        isFirstStart.set(false);
        toolbar.inflateMenu(R.menu.menu_profile);
        toolbar.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_edit) {
                        presenter.openEditProfile();
                    } else if (item.getItemId() == R.id.action_logout) {
                        dialogProvider.logoutAlert(getContext(), (dialog, which) -> presenter.logout());
                    }
                    return false;
                }
        );
    }

    @Override
    public void setAdapter(RecyclerBindableAdapter adapter) {
        RecyclerView.Adapter existingAdapter = childList.getAdapter();
        if (existingAdapter == adapter) {
            return;
        }
        childList.setAdapter(adapter);
    }

    private static final String NAME_FORMAT = "%s %s";

    @Override
    public void bindProfileData(Profile profile) {
        avatarView.setAvatar(profile.getAvatarUrl());
        nameField.setText(String.format(NAME_FORMAT, profile.getFirstName(), profile.getLastName()));
        emailField.setText(profile.getEmail());
        StringBuilder phonesBuilder = new StringBuilder();
        phonesBuilder.append(profile.getPhone());
        String additionalPhone = profile.getAdditionalPhone();
        if (!StringUtils.isBlank(additionalPhone)) {
            phonesBuilder.append(StringUtils.COMMA)
                    .append(StringUtils.SPACE)
                    .append(additionalPhone);
        }
        phonesField.setText(phonesBuilder.toString());
    }

    @Override
    public void networkError() {
        dialogProvider.showNetError(getContext());
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
    }

    public static class Screen extends BottomNavigationFragmentScreen {

        @Override
        public int unselectedIconId() {
            return R.drawable.ic_profile_unselected;
        }

        @Override
        public int selectedIconId() {
            return R.drawable.ic_profile_selected;
        }

        @Override
        public String getName() {
            return BottomScreen.PROFILE.toString();
        }

        @Override
        protected Fragment createFragment() {
            return new ProfileFragment();
        }
    }
}
