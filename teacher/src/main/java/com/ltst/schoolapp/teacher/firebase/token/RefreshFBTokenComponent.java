package com.ltst.schoolapp.teacher.firebase.token;


import dagger.Subcomponent;

@Subcomponent
public interface RefreshFBTokenComponent {
    void inject(RefreshFirebaseTokenService service);
}
