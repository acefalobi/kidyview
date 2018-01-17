package com.ltst.schoolapp.parent.ui.dated.feed;

import android.os.Bundle;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.ui.main.ChildInGroupHelper;
import com.ltst.schoolapp.parent.ui.main.feed.FeedScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface DatedFeedScope {

    @DatedFeedScope
    @Component(dependencies = {ParentScope.ParentComponent.class}, modules = {DatedFeedModule.class})
    interface DatedFeedComponent {
        void inject(DatedFeedActivity activity);

        FeedScope.FeedComponent feedComponent(FeedScope.FeedModule feedModule);

    }

    @Module
    public class DatedFeedModule {
        private final DialogProvider dialogProvider;
        private final GalleryPictureLoader galleryPictureLoader;
        private final Bundle screenParams;

        public DatedFeedModule(CoreActivity coreActivity, Bundle screenParams) {
            this.dialogProvider = coreActivity.getDialogProvider();
            this.galleryPictureLoader = new GalleryPictureLoader(coreActivity);
            this.screenParams = screenParams;
        }

        @Provides
        DialogProvider provideDialogProvider() {
            return dialogProvider;
        }

        @Provides
        GalleryPictureLoader provideGalleryPictureLoader() {
            return galleryPictureLoader;
        }

        @Provides
        FragmentScreenSwitcher fragmentScreenSwitcher() {
            return new FragmentScreenSwitcher();
        }

        @Provides
        Bundle provideScreenParams(){
            return this.screenParams;
        }

//        @Provides
//        Calendar provideDate() {
//            return date;
//        }

        @Provides
            //need for work of FeedPresenter, don`t used in Dated Feed Screen
        ChildInGroupHelper provideFAKEchangeGroupHelper(){
            return new ChildInGroupHelper();
        }

//        @Provides
//        ChildInGroup provideSelectedChildInGroup(){
//            return this.selectedChildInGroup;
//        }
    }
}
