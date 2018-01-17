package com.ltst.schoolapp.parent.ui.main.events;


import android.view.View;
import android.widget.TextView;

import com.ltst.core.ui.holder.EventsViewHolder;

public class ParentEventVewHolder extends EventsViewHolder {

    public ParentEventVewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void spanEmptyText(String emptyEventString, TextView emptyEventField) {
        emptyEventField.setText(emptyEventString); // don`t span for parent app
    }
}
