package com.ltst.schoolapp.teacher.firebase.message;


import dagger.Subcomponent;

@Subcomponent
public interface FireBaseMessageComponent {
    void inject(TeacherFireBaseMessageService service);
}
