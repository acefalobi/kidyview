package com.ltst.core;

import android.content.Context;

import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.preferences.StringPreference;
import com.ltst.core.data.preferences.qualifiers.NeedShowLogoutPopup;
import com.ltst.core.data.preferences.qualifiers.ServerToken;
import com.ltst.core.util.TokenExceptionHandler;

import dagger.Module;
import dagger.Provides;

@Module
public class CoreAppModule {
    private final Context context;


    public CoreAppModule(Context context) {
        this.context = context;
    }


    @Provides
    @CoreScope
    Context provideAppContext() {
        return this.context;
    }

    @Provides
    @CoreScope
    TokenExceptionHandler provideTokenExceptionHandler (Context context,
                                                        @ServerToken StringPreference tokenPreference,
                                                        @NeedShowLogoutPopup BooleanPreference needShowPopup){
        return new TokenExceptionHandler(context,tokenPreference, needShowPopup);
    }


}
