package com.ltst.schoolapp.parent.firebase.token;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.layer.sdk.services.LayerFcmInstanceIdService;
import com.livetyping.utils.preferences.StringPreference;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.preferences.qualifiers.FirebaseDeviceToken;
import com.ltst.core.data.preferences.qualifiers.ServerToken;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.data.DataService;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RefreshFirebaseTokenService extends LayerFcmInstanceIdService {

    @Inject DataService dataService;
    @Inject @FirebaseDeviceToken StringPreference fireBaseToken;
    @Inject @ServerToken StringPreference serverToken;

    @Override public void onCreate() {
        super.onCreate();
        ParentApplication applicationContext = (ParentApplication) getApplicationContext();
        ParentScope.ParentComponent component = applicationContext.getComponent();
        component.refreshFBTokenComponent().inject(this);
    }

    @Override public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        fireBaseToken.set(token);
        if (!StringUtils.isBlank(serverToken.get())) {
            dataService.updateFireBaseToken(token)
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
