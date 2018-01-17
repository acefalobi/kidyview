package com.ltst.schoolapp.parent.ui.main.checks;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.danil.recyclerbindableadapter.library.RecyclerBindableAdapter;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.util.DateUtils;
import com.ltst.schoolapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChecksAdapter extends RecyclerBindableAdapter<ChecksAdapter.ChecksWrapper, ParentChecksViewHolder> {

    @Override
    protected void onBindItemViewHolder(ParentChecksViewHolder viewHolder, int position, int type) {
        boolean showBottomDivider;
        int size = getItems().size();
        if (position + 1 < size) {
            ChecksWrapper nextItem = getItem(position + 1);
            if (StringUtils.isBlank(nextItem.getDate())) {
                showBottomDivider = false;
            } else showBottomDivider = true;
        } else showBottomDivider = true;
        viewHolder.bindView(position, getItem(position), showBottomDivider);
    }

    @Override
    protected ParentChecksViewHolder viewHolder(View view, int type) {
        return new ParentChecksViewHolder(view);
    }

    @Override
    protected int layoutId(int type) {
        return R.layout.viewholder_parent_checks;
    }

    public static class ChecksWrapper implements Parcelable {

        private ChildCheck childCheck;
        private String date;

        public ChecksWrapper(ChildCheck childCheck, String date) {
            this.childCheck = childCheck;
            this.date = date;
        }

        public ChildCheck getChildCheck() {
            return childCheck;
        }

        public String getDate() {
            return date;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.childCheck, flags);
            dest.writeString(this.date);
        }

        protected ChecksWrapper(Parcel in) {
            this.childCheck = in.readParcelable(ChildCheck.class.getClassLoader());
            this.date = in.readString();
        }

        public static final Parcelable.Creator<ChecksWrapper> CREATOR = new Parcelable.Creator<ChecksWrapper>() {
            @Override
            public ChecksWrapper createFromParcel(Parcel source) {
                return new ChecksWrapper(source);
            }

            @Override
            public ChecksWrapper[] newArray(int size) {
                return new ChecksWrapper[size];
            }
        };
    }

    public static List<ChecksWrapper> fromChecks(Context context, List<ChildCheck> checks) {
        Collections.reverse(checks);
        String date = null;
        List<ChecksWrapper> result = new ArrayList<>(checks.size());
        for (ChildCheck check : checks) {
            String itemDate = null;
            String checkDate = DateUtils.getDayOfTextMonthString(check.getDatetime(), context);
            if (date == null || !checkDate.equals(date)) {
                date = checkDate;
                itemDate = checkDate;
            }
            result.add(new ChecksWrapper(check, itemDate));
        }
        return result;
    }
}
