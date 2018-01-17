package com.ltst.schoolapp.parent.ui.school.item.fragment;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemScope {

    @Module
    class ItemModule {
        private final ItemContract.View view;

        public ItemModule(ItemContract.View view) {
            this.view = view;
        }

        @Provides
        @ItemScope
        ItemContract.View provideView() {
            return this.view;
        }
    }

    @ItemScope
    @Subcomponent(modules = ItemScope.ItemModule.class)
    interface ItemComponent {

        void inject(ItemFragment itemFragment);
    }
}
