package com.ltst.schoolapp.teacher.ui.settings.editgroup;

import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.schoolapp.TeacherComponent;
import com.ltst.schoolapp.teacher.ui.settings.editgroup.fragment.EditGroupScope;

import javax.inject.Scope;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

@Scope
public @interface EditGroupActivityScope {

    @EditGroupActivityScope
    @Component(dependencies = {TeacherComponent.class}, modules = {EditGroupActivityModule.class})
    interface EditGroupActivityComponent {
        void inject(EditGroupActivity activity);

        EditGroupScope.EditGroupComponent editGroupComponent(EditGroupScope.EditGroupModule module);
    }

    @Module
    class EditGroupActivityModule {
        private final DialogProvider dialogProvider;
        private final GalleryPictureLoader pictureLoader;
        private final long groupId;

        public EditGroupActivityModule(DialogProvider dialogProvider,
                                       GalleryPictureLoader galleryPictureLoader, long groupId) {
            this.dialogProvider = dialogProvider;
            this.pictureLoader = galleryPictureLoader;
            this.groupId = groupId;
        }


        @Provides
        @EditGroupActivityScope
        DialogProvider provideDialogProvider() {
            return this.dialogProvider;
        }


        @Provides
        @EditGroupActivityScope
        GalleryPictureLoader provideGalleryPictureLoader() {
            return this.pictureLoader;
        }

        @Provides
        @EditGroupActivityScope
        long provideGroupId() {
            return groupId;
        }
    }
}
