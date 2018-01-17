package com.ltst.schoolapp.teacher.ui.main.feed;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface FeedScope {

    @FeedScope
    @Subcomponent(modules = FeedModule.class)
    public interface FeedComponent {
        void inject(FeedFragment feedFragment);
    }

    @Module
    public class FeedModule {
        private FeedContract.View view;
        private boolean isMain;

        public FeedModule(FeedContract.View view, boolean isMain) {
            this.view = view;
            this.isMain = isMain;
        }

        @Provides
        @FeedScope
        FeedContract.View provideFeedView() {
            return this.view;
        }

        @Provides
        @FeedScope
        Boolean isMain() {
            return isMain;
        }
    }
}
