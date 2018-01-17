package com.ltst.schoolapp.parent.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.Spinner;

import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChange;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.query.Predicate;
import com.layer.sdk.query.Query;
import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.preferences.qualifiers.IsFirstStart;
import com.ltst.core.navigation.ActivityScreen;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.BottomNavigationFragmentScreen;
import com.ltst.core.navigation.BottomNavigator;
import com.ltst.core.navigation.HasFragmentContainer;
import com.ltst.core.navigation.HasSubComponents;
import com.ltst.core.util.Foreground;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.ParentActivity;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends ParentActivity implements HasSubComponents<MainScope.MainComponent>,
        HasFragmentContainer {

    @Inject ActivityScreenSwitcher activitySwitcher;

    @Inject @IsFirstStart BooleanPreference isFirstStart;
    @Inject DataService dataService;
    @Inject LayerClient layerClient;

    @BindView(R.id.main_bottom_navigator) BottomNavigator bottomNavigator;
    @BindView(R.id.feed_toolbar_spinner) Spinner spinner;

    private ChildInGroupHelper spinnerHelper;
    private MainScope.MainComponent component;
    private Subscription layerConnectSubscription;
    private String screenTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(Screen.KEY_MAIN_ACTIVITY_FIRST_SCREEN)) {
                screenTag = extras.getString(Screen.KEY_MAIN_ACTIVITY_FIRST_SCREEN);
            }
        }

        ButterKnife.bind(this);
        spinnerHelper.init(dataService, new WeakReference<>(spinner));
    }


    @Override protected void onExtractParams(Bundle params) {
        super.onExtractParams(params);
        if (params.containsKey(Screen.KEY_MAIN_ACTIVITY_FIRST_SCREEN)) {
            screenTag = params.getString(Screen.KEY_MAIN_ACTIVITY_FIRST_SCREEN);

        }
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent); // check intent for change screen
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(Screen.KEY_MAIN_ACTIVITY_FIRST_SCREEN)) {
                screenTag = extras.getString(Screen.KEY_MAIN_ACTIVITY_FIRST_SCREEN);
                if (Foreground.get().isForeground()) {
                    for (BottomNavigationFragmentScreen bottomNavigationFragmentScreen : BottomScreen.getScreens()) {
                        if (bottomNavigationFragmentScreen.getName().equals(screenTag)) {
                            bottomNavigator.openScreen(bottomNavigationFragmentScreen);
                            screenTag = null;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void addToParentComponent(ParentScope.ParentComponent component) {
        spinnerHelper = new ChildInGroupHelper();
        this.component = DaggerMainScope_MainComponent.builder()
                .mainModule(new MainScope.MainModule(getDialogProvider(),
                        getFragmentScreenSwitcher(),
                        spinnerHelper,
                        getActivityProvider()))
                .parentComponent(component)
                .build();
        this.component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        spinnerHelper.subscribe();
        bottomNavigator.init(getFragmentScreenSwitcher(), BottomScreen.getScreens(), isFirstStart.get());
//        isFirstStart.set(false); // set false on ProfileFragment
        activitySwitcher.attach(this);
        if (screenTag != null) {
            for (BottomNavigationFragmentScreen bottomNavigationFragmentScreen : BottomScreen.getScreens()) {
                if (bottomNavigationFragmentScreen.getName().equals(screenTag)) {
                    bottomNavigator.openScreen(bottomNavigationFragmentScreen);
                    screenTag = null;
                }
            }
        }
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
        spinnerHelper.unsubscribe();
        activitySwitcher.detach(this);
        if (layerConnectSubscription != null) {
            layerConnectSubscription.unsubscribe();
            layerConnectSubscription = null;
        }
        super.onStop();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected Toolbar getToolbar() {
        return ((Toolbar) findViewById(R.id.main_toolbar));
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.main_fragment_container;
    }

    @Override
    public MainScope.MainComponent getComponent() {
        return this.component;
    }

    public static class Screen extends ActivityScreen {

        public static final String KEY_MAIN_ACTIVITY_FIRST_SCREEN = "MainActivity.Screen.FragmentTag";

        private String screenTag;

        public Screen() {
        }

        public Screen(BottomScreen screen) {
            this.screenTag = screen.toString();
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
