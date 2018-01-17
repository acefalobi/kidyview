package com.ltst.schoolapp.teacher.ui.main.children;

import com.ltst.core.ui.holder.ChildrenViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

import javax.inject.Qualifier;
import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ChildrenScope {

    @ChildrenScope
    @Subcomponent(modules = ChildrenModule.class)
    interface ChildrenComponent {
        void inject(ChildrenFragment childrenFragment);
        void inject (ChildrenViewHolder viewHolder);
    }

    @Module
    class ChildrenModule {

        private final ChildrenContract.View view;

        public ChildrenModule(ChildrenContract.View view) {
            this.view = view;
        }

        @Provides
        @ChildrenScope
        ChildrenContract.View provideChildrenView() {
            return this.view;
        }


        @Provides
        @ChildrenScope
        @CurrentCalendar
        Calendar provideCurrentCalendar() {
            return Calendar.getInstance();
        }

        @Provides
        @ChildrenScope
        @BirthdayCalendar
        Calendar provideBirthdayCalendar() {
            return Calendar.getInstance();
        }
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface CurrentCalendar {

    }


    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface BirthdayCalendar {
    }
}
