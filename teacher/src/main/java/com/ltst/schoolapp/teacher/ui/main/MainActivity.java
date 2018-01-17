package com.ltst.schoolapp.teacher.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.model.Group;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.BottomNavigator;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.ui.AvatarView;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.TeacherActivity;
import com.ltst.schoolapp.teacher.data.DataService;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends TeacherActivity implements HasFragmentContainer,
        HasSubComponents<MainScope.MainComponent>, ChangeGroupHelper.GroupChangedListener {

    @Inject ActivityScreenSwitcher screenSwitcher;
    @Inject ApplicationSwitcher applicationSwitcher;
    @Inject DataService dataService;
    @Inject LayerClient layerClient;

    @BindView(R.id.main_bottom_navigator) BottomNavigator bottomNavigator;
    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.feed_toolbar_spinner) Spinner spinner;
    @BindView(R.id.main_toolbar_icon) ImageView groupIcon;

    private MainScope.MainComponent component;
    private ChangeGroupHelper changeGroupHelper;
    private Subscription getSelectedGroupSubscription;
    private AvatarView.ImageViewTarget imageViewTarget;
    private Subscription layerConnectSubscription;
    private String screenTag;

    @Override
    protected void addToTeacherComponent(TeacherComponent teacherComponent) {
        changeGroupHelper = new ChangeGroupHelper();
        component = DaggerMainScope_MainComponent.builder()
                .mainModule(new MainScope.MainModule(
                        getFragmentScreenSwitcher(),
                        getActivityProvider(),
                        getDialogProvider(),
                        new GalleryPictureLoader(this), changeGroupHelper))
                .teacherComponent(teacherComponent)
                .build();
        component.inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        changeGroupHelper.init(dataService, new WeakReference<>(spinner));
        imageViewTarget = new AvatarView.ImageViewTarget(groupIcon);
    }

    @Override
    protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        if (params.containsKey(Screen.KEY_MAIN_ACTIVITY_FIRST_SCREEN)) {
            screenTag = params.getString(Screen.KEY_MAIN_ACTIVITY_FIRST_SCREEN);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataService.enableTokenExceptionHandler();
        changeGroupHelper.subscribe();
        if (changeGroupHelper.isGroupChecked()) {
            setSelectedGroupAvatar();
        }
        changeGroupHelper.setGroupChangedListener(this);
        bottomNavigator.init(getFragmentScreenSwitcher(), BottomScreen.getScreens());
        if (screenTag != null) {
            for (BottomNavigationFragmentScreen bottomNavigationFragmentScreen : BottomScreen.getScreens()) {
                if (bottomNavigationFragmentScreen.getName().equals(screenTag)) {
                    bottomNavigator.openScreen(bottomNavigationFragmentScreen);
                    screenTag = null;
                }
            }
        }
        screenSwitcher.attach(this);
        applicationSwitcher.attach(this);
        addLayerChangeListener();
    }

    private void addLayerChangeListener() {
        layerConnectSubscription = dataService.layerConnect()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(layerConnect);
    }

    private Action1<Boolean> layerConnect = aBoolean -> {
        if (aBoolean) {
            getUnreadMessages();
            layerClient.registerEventListener(layerChangeEvent -> {
                List<LayerChange> changes = layerChangeEvent.getChanges();
                for (LayerChange change : changes) {
                    String attributeName = change.getAttributeName();
                    if (!StringUtils.isBlank(attributeName) && attributeName.equals("totalUnreadMessageCount")) {
                        getUnreadMessages();
                        return;
                    }
                }

            });
        }
    };

    private void getUnreadMessages() {
        Query query = Query.builder(Conversation.class)
                .predicate(new Predicate(Conversation.Property.HAS_UNREAD_MESSAGES, Predicate.Operator.EQUAL_TO, true))
                .build();
        List resultArray = layerClient.executeQuery(query, Query.ResultType.OBJECTS);
        bottomNavigator.setUnreadMessagesIndicator(resultArray.size());
    }

    @Override
    protected void onStop() {
        changeGroupHelper.unsubscribe();
        applicationSwitcher.detach(this);
        screenSwitcher.detach(this);
        super.onStop();
        if (getSelectedGroupSubscription != null) {
            getSelectedGroupSubscription.unsubscribe();
        }
        if (layerConnectSubscription != null) {
            layerConnectSubscription.unsubscribe();
        }
        changeGroupHelper.removeListener(this);
    }

    private void setSelectedGroupAvatar() {
        getSelectedGroupSubscription = dataService.getSelectedGroup()
                .subscribe(group -> {
                    Glide.with(MainActivity.this)
                            .load(group.getAvatarUrl())
                            .asBitmap()
                            .thumbnail(0.2f)
                            .placeholder(R.drawable.ic_cave)
//                            .error(com.ltst.core.R.drawable.ic_cave)
                            .into(imageViewTarget);
                });
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.main_fragment_container;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public MainScope.MainComponent getComponent() {
        return component;
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

    @Override public void groupChanged(Group group) {
        setSelectedGroupAvatar();
    }

    public static class Screen extends ActivityScreen {

        static final String KEY_MAIN_ACTIVITY_FIRST_SCREEN = "MainActivity.Screen.FragmentTag";
        private String screenTag;

        public Screen() {
        }

        public Screen(BottomScreen bottomScreen) {
            this.screenTag = bottomScreen.toString();
        }

        @Override
        protected void configureIntent(@NonNull Intent intent) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (screenTag != null) {
                intent.putExtra(KEY_MAIN_ACTIVITY_FIRST_SCREEN, screenTag);
            }
        }

        @Override
        protected Class<? extends CoreActivity> activityClass() {
            return MainActivity.class;
        }
    }


}
