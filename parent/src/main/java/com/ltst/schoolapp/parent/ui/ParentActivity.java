package com.ltst.schoolapp.parent.ui;

import com.ltst.core.base.CoreActivity;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.ParentScope;

public abstract class ParentActivity extends CoreActivity {

    @Override
    protected void onCreateComponent() {
        ParentApplication application = ParentApplication.get(this);
        ParentScope.ParentComponent component = application.getComponent();
        addToParentComponent(component);
    }

    protected abstract void addToParentComponent(ParentScope.ParentComponent component);
}
