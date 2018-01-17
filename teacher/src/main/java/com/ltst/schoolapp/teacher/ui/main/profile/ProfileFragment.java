package com.ltst.schoolapp.teacher.ui.main.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.main.BottomScreen;
import com.ltst.schoolapp.teacher.ui.main.MainScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileFragment extends CoreEnterFragment implements ProfileContract.View {

    @Inject
    ProfilePresenter presenter;

    @Inject
    DialogProvider dialogProvider;

    @BindView(R.id.profile_avatar_view)
    AvatarView avatarView;

    @BindView(R.id.profile_name_view)
    TextView nameField;

    @BindView(R.id.profile_personal_email)
    TextView personalEmailField;

    @BindView(R.id.profile_phones_field)
    TextView phonesField;

    @BindView(R.id.profile_progress_bar)
    ProgressBar progressBar;

    private Toolbar toolbar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        avatarView.setClickAvatarCallBack(new AvatarView.ClickAvatarCallBack() {
            @Override
            public void onAvatarClick() {
                presenter.openAvatar();
            }
        });
        return view;
    }

    @Override
    protected int getBackgroundColorId() {
        return android.R.color.white;
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.new_fragment_profile;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        MainScope.MainComponent component = (MainScope.MainComponent) rootComponent.getComponent();
        component.addProfileComponent(new ProfileScope.ProfileModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.setTitle(R.string.profile_title);
        this.toolbar.inflateMenu(R.menu.menu_profile);
//        Spinner spinner = (Spinner) toolbar.findViewById(R.id.feed_toolbar_spinner);
//        spinner.setVisibility(View.GONE);
        ImageView groupIcon = ((ImageView) toolbar.findViewById(R.id.main_toolbar_icon));
        groupIcon.setVisibility(View.GONE);
        this.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile_menu_edit:
                    presenter.goToEditProfile();
                    break;
                case R.id.profile_menu_settings:
                    presenter.goToSettings();
                    break;
            }
            return false;
        });
    }

    @OnClick(R.id.profile_school_info_button)
    void onSchoolInfoClick() {
        presenter.openSchoolInfo();
    }

    @OnClick(R.id.profile_events_button)
    void onEventsClick() {
        presenter.openEvents();
    }

    @Override
    public void load() {
        progressBar.setVisibility(View.VISIBLE);
        toolbar.getMenu().findItem(R.id.action_done).setVisible(false);
    }

    @Override
    public void showContent() {
        progressBar.setVisibility(View.GONE);
        toolbar.getMenu().findItem(R.id.action_done).setVisible(true);
    }

    @Override
    public void bindData(Profile profile) {
        nameField.setText(profile.getFullName());
        personalEmailField.setText(profile.getEmail());
        phonesField.setText(StringUtils.isBlank(profile.getAdditionalPhone())
                ? profile.getPhone()
                : profile.getPhone()
                + StringUtils.COMMA
                + StringUtils.SPACE
                + profile.getAdditionalPhone());
        avatarView.setAvatar(profile.getAvatarUrl());
    }

    public static final class Screen extends BottomNavigationFragmentScreen {

        @Override
        public String getName() {
            return BottomScreen.PROFILE.toString();
        }

        @Override
        protected Fragment createFragment() {
            return new ProfileFragment();
        }

        @Override
        public int unselectedIconId() {
            return R.drawable.ic_profile_unselected;
        }

        @Override
        public int selectedIconId() {
            return R.drawable.ic_profile_selected;
        }

    }
}
