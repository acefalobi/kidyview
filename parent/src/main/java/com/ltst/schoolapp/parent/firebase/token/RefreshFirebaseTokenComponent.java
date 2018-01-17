package com.ltst.schoolapp.parent.firebase.token;


import dagger.Subcomponent;

@Subcomponent
public interface RefreshFirebaseTokenComponent {
    void inject (RefreshFirebaseTokenService service);
}
