package com.ltst.schoolapp.parent.ui.report.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.BasePresenter;
import com.ltst.core.base.CoreFragment;
import com.ltst.core.data.model.ChildState;
import com.ltst.core.data.model.ChildStateType;
import com.ltst.core.navigation.FragmentScreen;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.util.DateUtils;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ui.report.ReportScope;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportFragment extends CoreFragment implements ReportContract.View {

    @Inject ReportPresenter presenter;

    @BindView(R.id.report_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.report_progress_bar) ProgressBar progressBar;
    private View headerView;

    @Override protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override protected int getResLayoutId() {
        return R.layout.fragment_report;
    }

    @Override protected void onCreateComponent(HasSubComponents rootComponent) {
        ReportScope.ReportComponent component = (ReportScope.ReportComponent) rootComponent.getComponent();
        component.subComponent(new ReportFragmentScope.ReportFragmentModule(this)).inject(this);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @BindString(R.string.report_title) String title;

    @Override protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
    }


    @BindString(R.string.report_header_name_format) String headerNameFormat;

    @Override public void bindData(RecyclerBindableAdapter adapter,
                                   @DrawableRes int headerIconResId,
                                   String childName) {
        RecyclerView.Adapter existingAdapter = recyclerView.getAdapter();
        if (existingAdapter == null || existingAdapter != adapter) {
            recyclerView.setAdapter(adapter);
        }
        if (adapter.getHeadersCount() == 0) {
            View header = getView(headerIconResId, childName);
            headerView = header;
            adapter.addHeader(header);

        }
    }

    @NonNull private View getView(@DrawableRes int headerIconResId, String childName) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View header = layoutInflater.inflate(R.layout.header_report, recyclerView, false);
        ImageView headerIcon = ButterKnife.findById(header, R.id.report_header_image);
        TextView firstText = ButterKnife.findById(header, R.id.report_header_first_line);
        headerIcon.setImageResource(headerIconResId);
        firstText.setText(String.format(headerNameFormat, childName));
        return header;
    }

    @Override public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void stopLoad() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void bindCheckOut(ChildState checkOut) {
        ViewGroup checkInContainer = ButterKnife.findById(headerView, R.id.header_checout_container);
        checkInContainer.setVisibility(View.VISIBLE);
        TextView checkInTextField = ButterKnife.findById(headerView, R.id.header_checkout_state_text);
        fillStateText(checkInTextField, checkOut);
        TextView checkInTimeField = ButterKnife.findById(headerView, R.id.header_checkout_state_time);
        fillStateTime(checkInTimeField, checkOut);
    }


    @Override public void bindCheckIn(ChildState checkIn) {
        ViewGroup checkInContainer = ButterKnife.findById(headerView, R.id.header_checkin_container);
        checkInContainer.setVisibility(View.VISIBLE);
        TextView checkOutTextField = ButterKnife.findById(headerView, R.id.header_checkin_state_text);
        fillStateText(checkOutTextField, checkIn);
        TextView checkOutTimeField = ButterKnife.findById(headerView, R.id.header_checkin_state_time);
        fillStateTime(checkOutTimeField, checkIn);

    }

    private void fillStateText(TextView textView, ChildState state) {
        String stateType = state.getType().toString();
        String format = getContext().getString(stateType.equals(ChildStateType.CHECKIN.toString())
                ? com.ltst.core.R.string.checks_dropped_of_format
                : com.ltst.core.R.string.checks_picked_by_format);
        String visibleName = state.getFirstName() + StringUtils.SPACE + state.getLastName();
        String text = String.format(format, visibleName);
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), text.length() - visibleName.length(), text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }

    private void fillStateTime(TextView timeView, ChildState childState) {
        String hourMinuteString = DateUtils.getHourMinuteString(childState.getDatetime(), timeView.getContext());
        timeView.setText(hourMinuteString);
    }


    public static final class Screen extends FragmentScreen {

        @Override public String getName() {
            return getClass().getName();
        }

        @Override protected Fragment createFragment() {
            return new ReportFragment();
        }
    }
}
