package com.ltst.schoolapp.parent.ui.checkout.fragment.info;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.DialogProvider;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.checkout.CheckoutScope;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class InfoFragment extends CoreFragment implements InfoContract.View {

    @Inject InfoPresenter presenter;
    @Inject DialogProvider dialogProvider;
    @BindView(R.id.info_checkout_name_field) TextView childNameField;
    @BindView(R.id.info_checkout_name_container) ViewGroup childNameContainer;
    @BindView(R.id.info_checkout_name_arrow) ImageView childNameArrow;
    @BindView(R.id.info_checkout_status_field) MaterialEditText statusField;
    @BindView(R.id.info_checkout_relative_name_field) MaterialEditText relativeNameField;
    @BindView(R.id.info_checkout_last_name_field) MaterialEditText relativeLastNameField;
    @BindView(R.id.info_checkout_time_field) TextView timeField;
    @BindView(R.id.info_checkout_progress_bar) ProgressBar progressBar;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_checkout_info;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        CheckoutScope.CheckoutComponent component = (CheckoutScope.CheckoutComponent) rootComponent.getComponent();
        component.infoComponent(new InfoScope.InfoModule(this)).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.info_checkout_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> {
            presenter.goBack();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
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
    public void setObjectName(String childName) {
        childNameField.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_grey_blue));
        childNameField.setText(childName);
    }

    @OnClick(R.id.info_checkout_name_container)
    void onChildNameClick() {
        presenter.selectChild();
    }

    @OnClick(R.id.info_checkout_time_container)
    void onChildTimeClick() {
        presenter.openTimePicker();
    }

    @OnClick(R.id.info_checkout_next_button)
    void onNextClick() {
        presenter.validateFields();
    }

    @OnTextChanged(R.id.info_checkout_status_field)
    void onStatusChanged(CharSequence text, int start, int before, int count) {
        if (!StringUtils.isBlank(text)) {
            presenter.setRelativeStatus(text.toString());
        }
    }

    @OnTextChanged(R.id.info_checkout_relative_name_field)
    void onNameChanged(CharSequence text, int start, int before, int count) {
        if (!StringUtils.isBlank(text)) {
            presenter.setRelativeFirstName(text.toString());
        }
    }

    @OnTextChanged(R.id.info_checkout_last_name_field)
    void onLastNameChanged(CharSequence text, int start, int before, int count) {
        if (!StringUtils.isBlank(text)) {
            presenter.setRelativeLastName(text.toString());
        }
    }

    @OnFocusChange({R.id.info_checkout_relative_name_field, R.id.info_checkout_last_name_field})
    void onNameFieldsFocusChanged(MaterialEditText editText, boolean focus) {
        if (focus) {
            editText.setError(null);
        }
    }

    @Override
    public void setTime(String time) {
        timeField.setTextColor(ContextCompat.getColor(getContext(), R.color.apple_green));
        timeField.setText(time);
    }

    @Override
    public void networkError() {
        dialogProvider.showNetError(getContext());
    }

    @Override
    public void emptyChildIdError() {
        childNameField.setTextColor(ContextCompat.getColor(getContext(), R.color.error_color));
    }

    @Override
    public void statusValidateError() {
        statusField.setError(getString(R.string.empty_field_error));
    }

    @Override
    public void firstNameValidateError() {
        relativeNameField.setError(getString(R.string.empty_field_error));
    }

    @Override
    public void lastNameValidateError() {
        relativeLastNameField.setError(getString(R.string.empty_field_error));
    }

    @Override
    public void timeEmptyError() {
        timeField.setTextColor(ContextCompat.getColor(getContext(), R.color.error_color));
    }

    @Override
    public void setEmptyChildField() {
        childNameField.setText(getString(R.string.info_checkout_name_filed_hint));

    }

    @Override public void emptyChildListMode() {
        initNameContainer(getString(R.string.info_checkout_no_children), false);
        initEditableFields(false);
    }


    @Override public void oneChildMode(String childName) {
        initNameContainer(childName, false);
        initEditableFields(true);
    }

    @Override public void manyChildrenMode() {
        initNameContainer(getString(R.string.info_checkout_name_filed_hint), true);
        initEditableFields(true);
    }

    private void initNameContainer(String text, boolean active) {
        childNameField.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_grey_blue));
        childNameField.setText(text);
        childNameContainer.setClickable(active);
        childNameArrow.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    private void initEditableFields(boolean enable) {
        statusField.setEnabled(enable);
        relativeNameField.setEnabled(enable);
        relativeLastNameField.setEnabled(enable);
        timeField.setEnabled(enable);
        Button nextButton = ButterKnife.findById(getView(), R.id.info_checkout_next_button);
        nextButton.setEnabled(enable);
        ViewGroup timeContainer = ButterKnife.findById(getView(), R.id.info_checkout_time_container);
        timeContainer.setClickable(enable);
        TextView timeHintView = ButterKnife.findById(getView(), R.id.info_checkout_time_hint_text);
        timeHintView.setTextColor(ContextCompat.getColor(getContext(), enable ?
                R.color.dark_grey_blue : R.color.bluish_grey));
    }


    public static final class Screen extends FragmentScreen {

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new InfoFragment();
        }
    }
}
