package com.ltst.core.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ltst.core.R;
import com.ltst.core.data.realm.model.ChildCheckScheme;
import com.ltst.core.ui.holder.ChecksViewHolder;
import com.ltst.core.util.DateUtils;

import java.util.Calendar;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

public class ChecksAdapter extends RealmBasedRecyclerViewAdapter<ChildCheckScheme, ChecksViewHolder> {

    public ChecksAdapter(Context context, RealmResults realmResults) {
        super(context, realmResults, true, true, true, "datetime");
    }

    @Override
    public ChecksViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int type) {
        View itemView = inflater.inflate(R.layout.viewholder_checks_item, viewGroup, false);
        return new ChecksViewHolder(itemView);
    }

    @Override
    public void onBindRealmViewHolder(ChecksViewHolder checksViewHolder, int position) {
        int realPos = checksViewHolder.getAdapterPosition();
        int previousViewType = realPos > 1 ? getItemViewType(realPos - 1) : -5;
        int nextViewType = realPos < getItemCount() - 1 ? getItemViewType(realPos + 1) : -5;
        boolean previousIsHeader = previousViewType != 0;
        boolean nextIsHeader = nextViewType != 0;
        checksViewHolder.bindView(realmResults.get(position), null, previousIsHeader, nextIsHeader);
    }

    @Override
    public RealmViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        View view = this.inflater.inflate(R.layout.fragment_checks_header, viewGroup, false);
        return new RealmViewHolder((TextView) view);
    }

    @Override
    public String createHeaderFromColumnValue(Object columnValue) {
        return DateUtils.getDayOfTextMonthString((String) columnValue, getContext());
    }


    @Override
    public Object getLastItem() {
        if (realmResults.size() > 0) {
            return super.getLastItem();
        } else {
            String date = DateUtils.getFullDateTimeString(Calendar.getInstance(), getContext());
            return new ChildCheckScheme(date);
        }
    }
}
