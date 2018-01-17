package com.ltst.schoolapp.teacher;

import com.ltst.core.base.CoreActivity;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.TeacherComponent;

public abstract class TeacherActivity extends CoreActivity {

    @Override
    protected void onCreateComponent() {
        TeacherApplication application = TeacherApplication.get(this);
        TeacherComponent teacherComponent = application.getTeacherComponent();
        addToTeacherComponent(teacherComponent);
    }

    protected abstract void addToTeacherComponent(TeacherComponent teacherComponent);
}
