package com.ltst.schoolapp.teacher.ui.enter.code;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeScope {

    @CodeScope
    @Subcomponent (modules = CodeModule.class)
    interface CodeComponent {
        void inject(CodeFragment fragment);
    }

    @Module
    class CodeModule {
        private final CodeContract.View view;

        public CodeModule(CodeContract.View view) {
            this.view = view;
        }

        @Provides
        @CodeScope
        CodeContract.View provideCodeView() {
            return this.view;
        }
    }
}
