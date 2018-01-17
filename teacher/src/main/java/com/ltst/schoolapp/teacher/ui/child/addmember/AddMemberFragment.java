package com.ltst.schoolapp.teacher.ui.child.addmember;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.data.model.Member;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.teacher.ui.child.ChildScope;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class AddMemberFragment extends CoreFragment implements AddMemberContract.View {

    @Inject AddMemberPresenter presenter;
    @Inject DialogProvider dialogProvider;

    @BindView(R.id.add_member_avatar_view) AvatarView avatarView;
    @BindView(R.id.add_member_name) MaterialEditText nameField;
    @BindView(R.id.add_member_last_name) MaterialEditText lastNameField;
    @BindView(R.id.add_member_phone) MaterialEditText phoneField;
    @BindView(R.id.add_member_mail) MaterialEditText emailField;
    @BindView(R.id.add_member_secod_phone) MaterialEditText secondPhoneField;
    @BindView(R.id.add_member_status) MaterialEditText statusField;
    @BindView(R.id.add_member_proress_bar) ProgressBar progressBar;

    @BindView(R.id.add_member_scroll_view) ScrollView scrollView;

    private Toolbar toolbar;


    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_add_member;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        ChildScope.ChildComponent component = (ChildScope.ChildComponent) rootComponent.getComponent();
        component.addMemberComponent(new AddMemberScope.AddMemberModule(this, this, getArguments())).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        avatarView.setClickAvatarCallBack(() -> presenter.checkWriteStoragePermission());
        return view;
    }

    @Override
    public void chosePhotoWay() {
        dialogProvider.showPhotoWay(new DialogProvider.PhotoWayCallBack() {
            @Override
            public void camera() {
                presenter.checkPermissionAndOpenCamera();
            }

            @Override
            public void gallery() {
                presenter.openGallery();
            }

        });
    }

    @Override
    public void setPhoto(Uri photoPath) {
        avatarView.setAvatar(photoPath);
    }

    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
        toolbar.getMenu().findItem(R.id.action_done).setVisible(false);

    }

    @Override
    public void stopLoad() {
        progressBar.setVisibility(View.GONE);
        toolbar.getMenu().findItem(R.id.action_done).setVisible(true);
    }

    @Override
    public void netError() {
        dialogProvider.showNetError(getContext());
    }

    @Override public void bindExistMember(Member member) {
        avatarView.setClickAvatarCallBack(null);
        avatarView.setAvatar(member.getAvatarUrl());
        avatarView.showUnderAvatar(false);
        nameField.setText(member.getFirstName());
        nameField.setEnabled(false);
        lastNameField.setText(member.getLastName());
        lastNameField.setEnabled(false);
        phoneField.setText(member.getPhone());
        phoneField.setEnabled(false);
        emailField.setText(member.getEmail());
        emailField.setEnabled(false);
        secondPhoneField.setText(member.getSecondPhone());
        secondPhoneField.setEnabled(false);
    }

    @Override public void bindNewMember(Member member) {
        emailField.setText(member.getEmail());
        emailField.setEnabled(false);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.add_family_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
        toolbar.inflateMenu(R.menu.menu_done);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_done) {
                presenter.done();
            }
            return false;
        });
        this.toolbar = toolbar;
    }

    @OnTextChanged(R.id.add_member_name)
    void onFirstNameChanged(CharSequence text, int start, int before, int count) {
        presenter.setName(text.toString());
    }

    @OnTextChanged(R.id.add_member_last_name)
    void onLastNameChanged(CharSequence text, int start, int before, int count) {
        presenter.setLastName(text.toString());
    }

    @OnTextChanged(R.id.add_member_status)
    void onStatusChanged(CharSequence text, int start, int before, int count) {
        presenter.setStatus(text.toString());
    }

    @OnTextChanged(R.id.add_member_phone)
    void onPhoneChanged(CharSequence text, int start, int before, int count) {
        presenter.setPhone(text.toString());
    }

    @OnTextChanged(R.id.add_member_secod_phone)
    void onSecondPhoneChanged(CharSequence text, int start, int before, int count) {
        presenter.setSecondPhone(text.toString());
    }

    @OnTextChanged(R.id.add_member_mail)
    void onEmailChanged(CharSequence text, int start, int before, int count) {
        presenter.setEmail(text.toString());
    }

    @OnFocusChange({R.id.add_member_name,
                           R.id.add_member_last_name,
                           R.id.add_member_status,
                           R.id.add_member_phone,
                           R.id.add_member_secod_phone,
                           R.id.add_member_mail})
    void onFieldsFocusChanged(MaterialEditText editText, boolean isFocus) {
        if (isFocus) {
            editText.setError(null);
        }
    }

    @Override
    public void phoneError() {
        phoneField.setError(getString(R.string.regex_phone_error));
        focusOnView(phoneField);
    }

    @Override
    public void emailError() {
        emailField.setError(getString(R.string.regex_email_error));
        focusOnView(emailField);
    }

    @Override
    public void nameError() {
        nameField.setError(getString(R.string.empty_field_error));
        focusOnView(nameField);
    }

    @Override
    public void lastNameError() {
        lastNameField.setError(getString(R.string.empty_field_error));
        focusOnView(lastNameField);
    }

    @Override
    public void secondPhoneError() {
        secondPhoneField.setError(getString(R.string.regex_phone_error));
        focusOnView(secondPhoneField);
    }

    @Override
    public void statusError() {
        statusField.setError(getString(R.string.empty_field_error));
        focusOnView(statusField);
    }

    private final void focusOnView(final View view) {
        scrollView.post(() -> {
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }
            scrollView.smoothScrollTo(0, view.getTop() - 2 * actionBarHeight);
        });
    }

    public static class Screen extends FragmentScreen {

        public static final String KEY_FAMILY_MEMBER = "AddMemberFragment.Screen.Member";
        public static final String KEY_SCREEN_MODE = "AddMemberFragment.Screen.ScreenMode";

        private final Member member;
        private final int screenMode;


        public Screen(Member member, @AddMemberScope.AddMEmberScreenMode int screenMode) {
            this.member = member;
            this.screenMode = screenMode;
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new AddMemberFragment();
        }

        @Override protected void onAddArguments(Bundle arguments) {
            super.onAddArguments(arguments);
            arguments.putParcelable(KEY_FAMILY_MEMBER, member);
            arguments.putInt(KEY_SCREEN_MODE, screenMode);
        }
    }
}
