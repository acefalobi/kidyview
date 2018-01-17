package com.ltst.schoolapp.teacher.ui.settings.settings.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreEnterFragment;
import com.ltst.core.data.model.Group;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.settings.settings.SettingsActivityScope.SettingsActivityComponent;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class SettingsFragment extends CoreEnterFragment implements SettingsContract.View {

    @Inject
    SettingsPresenter presenter;

    @BindView(R.id.settings_group_list) LinearLayout groupListView;

    private Toolbar toolbar;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getBackgroundColorId() {
        return android.R.color.white;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        SettingsActivityComponent component = (SettingsActivityComponent) rootComponent.getComponent();
        component.settingsComponent(new SettingsScope.SettingsModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.toolbar.setVisibility(View.VISIBLE);
        this.toolbar.setTitle(R.string.settings_title);
    }

    @Override
    public void setToolbarNavigationIcon(int icon, View.OnClickListener onClickListener) {
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(onClickListener);
    }

    @BindDimen(R.dimen.view_child_item_group_avatar_size) int avatarSize;

    @Override public void bindGroups(List<Group> groups) {
        groupListView.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (Group group : groups) {
            View view = inflater.inflate(R.layout.layout_settings_group_item, groupListView, false);
            ImageView groupAvatarView = (ImageView) view.findViewById(R.id.view_child_item_group_avatar);
            TextView groupTitleView = ((TextView) view.findViewById(R.id.view_child_item_group_title));
            AvatarView.ImageViewTarget groupAvatarTarget = new AvatarView.ImageViewTarget(groupAvatarView);

            groupTitleView.setText(group.getTitle());
            Glide.with(getContext())
                    .load(group.getAvatarUrl())
                    .asBitmap()
                    .override(avatarSize, avatarSize)
                    .error(R.drawable.ic_cave)
                    .into(groupAvatarTarget);

            view.setOnClickListener(v -> {
                presenter.onEditGroup(group.getId());
            });
            groupListView.addView(view);
        }
    }

//    @OnClick(R.id.settings_edit_name_group)
//    void onEditGroup() {
//        presenter.onEditGroup();
//    }

    @OnClick(R.id.settings_change_password)
    void onChangePasswordViewClick() {
        presenter.onChangePasswordViewClick();
    }

    @OnClick(R.id.settings_log_out)
    void onLogOutViewClick() {
        presenter.onLogOutViewClick();
    }

    public static class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new SettingsFragment();
        }
    }
}
