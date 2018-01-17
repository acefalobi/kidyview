package com.ltst.schoolapp.parent.firebase.message;


import dagger.Subcomponent;

@Subcomponent
public interface FireBaseMessageComponent {
    void inject(ParentFireBaseMessageService service);
}
