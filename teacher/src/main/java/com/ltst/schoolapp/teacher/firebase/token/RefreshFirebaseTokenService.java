package com.ltst.schoolapp.teacher.firebase.token;


import com.google.firebase.iid.FirebaseInstanceId;
import com.layer.atlas.util.Log;
import com.layer.sdk.services.LayerFcmInstanceIdService;
import com.livetyping.utils.preferences.StringPreference;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.preferences.qualifiers.FirebaseDeviceToken;
import com.ltst.core.data.preferences.qualifiers.ServerToken;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.data.DataService;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RefreshFirebaseTokenService extends LayerFcmInstanceIdService {

    @Inject DataService dataService;
    @Inject @ServerToken StringPreference serverToken;
    @Inject @FirebaseDeviceToken StringPreference fireBaseToken;

    @Override public void onCreate() {
        super.onCreate();
        TeacherApplication application = (TeacherApplication) getApplicationContext();
        TeacherComponent teacherComponent = application.getTeacherComponent();
        teacherComponent.refreshFireBaseTokenComponent().inject(this);

    }

    @Override public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("NEW FIREBASE TOKEN : " + token);
        fireBaseToken.set(token);
        if (!StringUtils.isBlank(serverToken.get())) {
            dataService.updateFireBaseProfile(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override public void call(Void aVoid) {
                            // nothing for this action
                        }
                    }, Throwable::printStackTrace);
        }
    }



}
