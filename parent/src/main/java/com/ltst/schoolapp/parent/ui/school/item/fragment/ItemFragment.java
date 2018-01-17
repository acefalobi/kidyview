package com.ltst.schoolapp.parent.ui.school.item.fragment;


import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.ui.TwoLineTextViewWithIcon;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.school.item.SchoolItemScope;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class ItemFragment extends CoreFragment implements ItemContract.View {

    @Inject ItemPresenter presenter;
    private Toolbar toolbar;

    @BindView(R.id.school_info_holder_avatar) ImageView avatarView;
    @BindView(R.id.school_info_holder_title) TextView titleField;
    @BindView(R.id.school_info_holder_names) TextView namesField;
    @BindView(R.id.item_school_info_location_field) TwoLineTextViewWithIcon locationField;
    @BindView(R.id.item_school_info_phone_field) TwoLineTextViewWithIcon phoneField;
    @BindView(R.id.item_school_info_additional_phone_field) TwoLineTextViewWithIcon additionalPhoneField;
    @BindView(R.id.item_school_info_email_field) TwoLineTextViewWithIcon emailField;


    @Override protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override protected int getResLayoutId() {
        return R.layout.fragment_item_school_info;
    }

    @Override protected void onCreateComponent(HasSubComponents rootComponent) {
        SchoolItemScope.SchoolItemComponent component = (SchoolItemScope.SchoolItemComponent) rootComponent.getComponent();
        component.subcomponent(new ItemScope.ItemModule(this)).inject(this);
    }

    @Override protected void initToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> {
            presenter.goBack();
        });
    }

    @OnClick(R.id.item_school_info_phone_field)
    void onPhoneFieldClick() {
        presenter.openDialer();
    }

    @OnClick(R.id.item_school_info_email_field)
    void onEmailFieldClick() {
        presenter.openEmailClient();
    }

    @Override public void setTitle(String title) {
        toolbar.setTitle(title);
        titleField.setText(title);
    }

    @Override public void setNames(String names) {
        namesField.setText(names);
    }

    @BindDimen(R.dimen.avatar_view_avatar_size) int avatarSize;

    @Override public void setAvatar(String avatarUrl, @IdRes int holderResId) {
        Glide.with(getContext())
                .load(avatarUrl)
                .asBitmap()
                .override(avatarSize, avatarSize)
                .centerCrop()
                .error(holderResId)
                .into(new AvatarView.ImageViewTarget(avatarView));
    }

    @Override public void setAddress(String address, String secondaryText) {
        locationField.setMainText(address);
        locationField.setSecondaryText(secondaryText);
    }

    @BindDimen(R.dimen.activity_vertical_margin_16) int verticalMargin;

    @Override public void hideAddressField() {
        locationField.setVisibility(View.GONE);
        ViewGroup.MarginLayoutParams phoneFieldParams = ((ViewGroup.MarginLayoutParams) phoneField.getLayoutParams());
        phoneFieldParams.setMargins(0, verticalMargin, 0, 0);
        phoneField.setLayoutParams(phoneFieldParams);

    }

    @Override public void setPhone(String phone, String descripton) {
        phoneField.setMainText(phone);
        phoneField.setSecondaryText(descripton);
    }

    @Override public void setAdditionalPhone(String additionalPhone) {
        additionalPhoneField.setMainText(additionalPhone);
        additionalPhoneField.setSecondaryText(getString(R.string.item_ifo_additional_phone));
    }

    @Override public void hideAdditionalPhoneField() {
        additionalPhoneField.setVisibility(View.GONE);
    }

    @Override public void setEmail(String email, String description) {
        emailField.setMainText(email);
        emailField.setSecondaryText(description);
    }


    public static final class Screen extends FragmentScreen {

        @Override public String getName() {
            return getClass().getName();
        }

        @Override protected Fragment createFragment() {
            return new ItemFragment();
        }
    }
}
