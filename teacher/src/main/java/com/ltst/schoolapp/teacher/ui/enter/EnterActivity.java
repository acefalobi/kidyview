package com.ltst.schoolapp.teacher.ui.enter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EnterActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<EnterScope.EnterComponent> {

    private EnterScope.EnterComponent component;
    private Screen.Params screenParams;

    @Inject ActivityScreenSwitcher activityScreenSwitcher;

    @Inject EnterPresenter enterPresenter;

    @Inject ApplicationSwitcher applicationSwitcher;

    @BindView(R.id.default_toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_EnterTheme);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void onExtractParams(Bundle params) {
        this.screenParams = params.getParcelable(Screen.KEY_PARAMS);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.default_activity_blue;
    }

    @Override
    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void addToTeacherComponent(TeacherComponent teacherComponent) {
        Profile enterProfile = new Profile();
        component = DaggerEnterScope_EnterComponent.builder()
                .teacherComponent(teacherComponent)
                .enterModule(new EnterScope.EnterModule(
                        getFragmentScreenSwitcher(),
                        getDialogProvider(),
                        enterProfile,
                        new GalleryPictureLoader(this),
                        screenParams)).build();
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        applicationSwitcher.attach(this);
        activityScreenSwitcher.attach(this);
        enterPresenter.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onStop() {
        enterPresenter.stop();
        applicationSwitcher.detach(this);
        activityScreenSwitcher.detach(this);
        super.onStop();
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.default_fragment_container;
    }

    @Override
    public EnterScope.EnterComponent getComponent() {
        return component;
    }

    public static class Screen extends ActivityScreen {
        public static final String KEY_PARAMS = "EnterActivity.Screen.screenParams";
        private Params screenParams;

        public Screen() {
            screenParams = new Params(Params.EnterFragment.NONE);
        }

        public Screen(Params.EnterFragment enterFragment) {
            screenParams = new Params(enterFragment);
        }

        public Screen(Params screenParams) {
            this.screenParams = screenParams;
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            if (screenParams.getFragment() == Params.EnterFragment.NONE) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            intent.putExtra(KEY_PARAMS, screenParams);
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return EnterActivity.class;
        }

        public static class Params implements Parcelable {
            private EnterFragment fragment;

            public Params(EnterFragment fragment) {
                this.fragment = fragment;
            }


            public EnterFragment getFragment() {
                return fragment;
            }

            public enum EnterFragment {
                NONE,
                RESTORE_PASSWORD;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.fragment == null ? -1 : this.fragment.ordinal());
            }

            protected Params(Parcel in) {
                int tmpFragment = in.readInt();
                this.fragment = tmpFragment == -1 ? null : EnterFragment.values()[tmpFragment];
            }

            public static final Parcelable.Creator<Params> CREATOR = new Parcelable.Creator<Params>() {
                @Override
                public Params createFromParcel(Parcel source) {
                    return new Params(source);
                }

                @Override
                public Params[] newArray(int size) {
                    return new Params[size];
                }
            };
        }
    }


}
