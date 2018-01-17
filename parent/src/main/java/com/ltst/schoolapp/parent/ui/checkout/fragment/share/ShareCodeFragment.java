package com.ltst.schoolapp.parent.ui.checkout.fragment.share;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.checkout.CheckoutScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class ShareCodeFragment extends CoreFragment implements ShareCodeContract.View {

    @Inject ShareCodePresenter presenter;
    @BindView(R.id.share_code_code_field) TextView codeField;
    @BindView(R.id.share_code_header) TextView headerField;

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_share_code;
    }

    @Override
    protected void onCreateComponent(HasSubComponents rootComponent) {
        CheckoutScope.CheckoutComponent component = (CheckoutScope.CheckoutComponent) rootComponent.getComponent();
        component.shareComponent(new ShareCodeScope.ShareCodeModule(this, getArguments())).inject(this);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.info_checkout_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
    }

    @OnClick(R.id.share_code_button)
    void onShareClick() {
        presenter.share();
    }

    @Override
    public void bindView(String code, String header) {
        int codeLength = code.length();
        StringBuilder builder = new StringBuilder();
        builder.append(code.substring(0, codeLength / 2));
        builder.append(StringUtils.SPACE);
        builder.append(code.substring(codeLength / 2, codeLength));
        codeField.setText(builder.toString());
        headerField.setText(header);
    }

    public static final class Screen extends FragmentScreen {

        static final String CODE_KEY = "ShareCodeScreen.CodeKey";
        static final String SHARE_SCREEN_HEADER_KEY = "ShareCodeScreen.ShareScreenHeader";
        static final String SHARE_SCREEN_NAMES_KEY = "ShareCodeScreen.ShareNames";
        static final String SHARE_SCREEN_SCHOOL_TITLE_KEY = "ShareCodeScreen.SchoolTitle";

        private final String code;
        private final String shareScreenHeader;
        private final String childrenNames;
        private final String schoolTitle;

        public Screen(String code, String shareScreenHeader, String childrenNames, String schoolTitle) {
            this.code = code;
            this.shareScreenHeader = shareScreenHeader;
            this.childrenNames = childrenNames;
            this.schoolTitle = schoolTitle;
        }

        @Override
        protected void onAddArguments(Bundle arguments) {
            super.onAddArguments(arguments);
            arguments.putString(CODE_KEY, code);
            arguments.putString(SHARE_SCREEN_HEADER_KEY, shareScreenHeader);
            arguments.putString(SHARE_SCREEN_NAMES_KEY, childrenNames);
            arguments.putString(SHARE_SCREEN_SCHOOL_TITLE_KEY, schoolTitle);
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        protected Fragment createFragment() {
            return new ShareCodeFragment();
        }
    }
}
