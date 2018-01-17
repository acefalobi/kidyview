package com.ltst.schoolapp.teacher.ui.addchild.fragment;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class SelectedDialogView extends RecyclerView {
    public SelectedDialogView(Context context) {
        super(context);
    }

    public SelectedDialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        setLayoutManager(layoutManager);
    }


}
