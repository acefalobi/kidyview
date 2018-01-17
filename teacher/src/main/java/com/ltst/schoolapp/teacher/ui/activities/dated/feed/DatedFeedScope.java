package com.ltst.schoolapp.teacher.ui.activities.dated.feed;

import com.ltst.core.base.CoreActivity;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.main.ChangeGroupHelper;
import com.ltst.schoolapp.teacher.ui.main.feed.FeedScope;

import java.util.Calendar;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface DatedFeedScope {

    @DatedFeedScope
    @Component(dependencies = {TeacherComponent.class}, modules = {DatedFeedModule.class})
    interface DatedFeedComponent {
        void inject(DatedFeedActivity activity);

        FeedScope.FeedComponent feedComponent(FeedScope.FeedModule feedModule);

    }

    @Module
    public class DatedFeedModule {
        private DialogProvider dialogProvider;
        private GalleryPictureLoader galleryPictureLoader;
        private Calendar date;

        public DatedFeedModule(CoreActivity coreActivity, Calendar date) {
            this.dialogProvider = coreActivity.getDialogProvider();
            this.galleryPictureLoader = new GalleryPictureLoader(coreActivity);
            this.date = date;
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
        Calendar provideDate() {
            return date;
        }

        @Provides
        //need for work of FeedPresenter, don`t used in Dated Feed Screen
        ChangeGroupHelper provideFAKEchangeGroupHelper(){
            return new ChangeGroupHelper();
        }
    }
}
