package com.ltst.schoolapp.teacher.ui.editprofile.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.layer.atlas.util.Log;
import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Profile;
import com.ltst.core.data.preferences.qualifiers.IsAdmin;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.permission.PermissionsHandler;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.FilePathUtil;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.core.util.PhotoRotator;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.core.util.validator.ValidationThrowable;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.main.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileFragment.PROFILE;
import static com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileFragment.SCHOOL;
import static com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileFragment.SCREEN_MODE;
import static com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileScope.EditProfileModule.FROM_ENTER;
import static com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileScope.EditProfileModule.FROM_PROFILE;
import static com.ltst.schoolapp.teacher.ui.editprofile.fragment.EditProfileScope.EditProfileModule.FROM_SCREEN;

public class EditProfilePresenter implements EditProfileContract.Presenter {

    private final EditProfileContract.View view;
    private final DataService dataService;
    private final Profile profile;
    private final GalleryPictureLoader galleryPictureLoader;
    private final ActivityScreenSwitcher activitySwitcher;
    private final int fromScreen;
    private final DialogProvider dialogProvider;
    private final TeacherApplication applicationContext;
    private final BooleanPreference isAdmin;
    private final PermissionsHandler permissionsHandler;
    private final ApplicationSwitcher applicationSwitcher;
    private final int screenMode;
    private final Profile copyOfExistProfile;

    private CompositeSubscription compositeSubscription;
    private Uri photoPath;
    private Subscription updateProfileSubscription;
    private File file;

    @Inject
    public EditProfilePresenter(EditProfileContract.View view,
                                DataService dataService,
                                Profile profile,
                                GalleryPictureLoader galleryPictureLoader,
                                ActivityScreenSwitcher activitySwitcher,
                                @Named(FROM_SCREEN) int fromScreen,
                                @Named(SCREEN_MODE) int screenMode,
                                DialogProvider dialogProvider,
                                TeacherApplication context,
                                @IsAdmin BooleanPreference isAdmin,
                                PermissionsHandler permissionsHandler, ApplicationSwitcher applicationSwitcher) {
        this.view = view;
        this.applicationContext = context;
        this.dataService = dataService;
        this.profile = profile;
        this.copyOfExistProfile = profile.clone();
        this.galleryPictureLoader = galleryPictureLoader;
        this.activitySwitcher = activitySwitcher;
        this.fromScreen = fromScreen;
        this.dialogProvider = dialogProvider;
        this.isAdmin = isAdmin;
        this.permissionsHandler = permissionsHandler;
        this.applicationSwitcher = applicationSwitcher;
        this.screenMode = screenMode;
    }

    @Override
    public void firstStart() {
        boolean needBlockEmail = fromScreen == FROM_ENTER;
        view.bindData(profile, needBlockEmail, isAdmin.get(), screenMode);
    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    @Override
    public void start() {
        compositeSubscription = new CompositeSubscription();
        initToolbar();
    }

    private void initToolbar() {
        switch (fromScreen) {
            case FROM_ENTER:
                view.setToolbarTittle(R.string.create_profile_title);
                break;
            case FROM_PROFILE:
                view.setToolbarTittle(R.string.edit_profile_title);
                view.setToolbarNavigationIcon(R.drawable.ic_clear_white_24dp, v -> {
                    goBack();
                });
                break;
        }
    }

    @Override
    public void stop() {
        compositeSubscription.unsubscribe();
        if (updateProfileSubscription != null) {
            updateProfileSubscription.unsubscribe();
        }
    }

    @Override
    public void photoFromCamera() {
        if (photoPath != null) {
            PhotoRotator.rotatePhotoFile(applicationContext, photoPath);
            view.setPhoto(photoPath);
        }
    }

    @NonNull
    private File getFile() {
        file = FilePathUtil.getCacheDir(applicationContext);
        photoPath = Uri.fromFile(file);
        return file;
    }

    @Override
    public void photoFromGallery(Uri data) {
        try {
            Bitmap bitmap = galleryPictureLoader.getBitmap(data, GalleryPictureLoader.MAX_PHOTO_SIZE);
            File file = getFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            photoPath = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        view.setPhoto(photoPath);
    }

    @Override
    public void validateAndUpdate(Map<ValidateType, String> needValidate, String additionalPhone) {
        FieldsValidator.validate(needValidate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(validateTypeStringMap -> updateProfile(validateTypeStringMap, additionalPhone),
                        throwable -> {
                            Log.d("PROFILE UPDATE OTHER ERROR");
                            if (throwable instanceof ValidationThrowable) {
                                Set<ValidateType> notValidatedTypes = ((ValidationThrowable) throwable)
                                        .notValidatedParams.keySet();
                                if (notValidatedTypes.contains(ValidateType.NAME)) {
                                    view.nameValidateError();
                                    return;
                                }
                                if (notValidatedTypes.contains(ValidateType.LAST_NAME)) {
                                    view.lastNameValidateError();
                                    return;
                                }
                                if (notValidatedTypes.contains(ValidateType.PERSONAL_PHONE)) {
                                    view.personalPhoneValidateError();
                                    return;
                                }
                                if (notValidatedTypes.contains(ValidateType.SECOND_PHONE)) {
                                    view.additionalPhoneValidateError();
                                }
                                if (notValidatedTypes.contains(ValidateType.PERSONAL_EMAIL)) {
                                    view.personalEmailValidateError();
                                    return;
                                }
                                if (notValidatedTypes.contains(ValidateType.SCHOOL_TITLE)) {
                                    view.schoolTitleValidateError();
                                    return;
                                }
                                if (notValidatedTypes.contains(ValidateType.SCHOOL_ADDRESS)) {
                                    view.schoolAddressValidateError();
                                    return;
                                }
                                if (notValidatedTypes.contains(ValidateType.SCHOOL_PHONE)) {
                                    view.schoolPhoneValidateError();
                                    return;
                                }
                                if (notValidatedTypes.contains(ValidateType.SCHOOL_ADDITIONAL_PHONE)){
                                    view.schoolAdditionalPhoneError();
                                    return;
                                }
                                if (notValidatedTypes.contains(ValidateType.SCHOOL_EMAIL)) {
                                    view.schoolEmailValidateError();
                                    return;
                                }
                            }
                        });
    }

    private void updateProfile(Map<ValidateType, String> validated, String additionalPhone) {
        view.load();
        if (additionalPhone != null) {
            if (additionalPhone.length() > 0 && additionalPhone.length() < FieldsValidator.MIN_NUMBER_COUNT) {
                view.additionalPhoneValidateError();
                view.showContent();
                return;
            }
        }
        final Profile profile = Profile.fromValidatedFields(validated);
        profile.setAdditionalPhone(additionalPhone == null ? StringUtils.EMPTY : additionalPhone);
        if (fromScreen == FROM_PROFILE) {
            if (photoPath == null && profile.equals(copyOfExistProfile)) {
                goBack();
                return;
            }
        }
        String filePath = photoPath != null ? photoPath.getPath() : null;
        updateProfileSubscription = dataService.updateProfile(profile, filePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profile1 -> {
                    Log.d("PROFILE UPDATED");
//                    if (file != null && file.exists()) {
//                        file.delete();
//                    }
                    switch (fromScreen) {
                        case FROM_ENTER:
                            activitySwitcher.open(new MainActivity.Screen());
                            break;
                        case FROM_PROFILE:
                            goBack();
                            break;
                    }
                }, throwable -> {
                    Log.d("PROFILE UPDATE NET ERROR");
                    if (throwable instanceof NetErrorException) {
                        dialogProvider.showNetError(applicationContext);
                    }
                    view.showContent();
                });
    }

    @Override
    public void goBack() {
        if (screenMode == PROFILE) {
            activitySwitcher.goBack();
        } else if (screenMode == SCHOOL) {
            Intent emptyIntent = new Intent();
            activitySwitcher.setResultAndGoBack(emptyIntent);
        }

    }

    @Override
    public void checkWriteExternalPermission() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                view::choicePhotoWay);
    }

    @Override public void checkCameraPermission() {
        permissionsHandler.requestPermission(PermissionsHandler.CAMERA_REQUEST,
                Manifest.permission.CAMERA,
                this::openCamera);
    }

    @Override public void openGallery() {
        applicationSwitcher.openGallery();
    }

    @Override public void openCamera() {
        applicationSwitcher.openCamera(getFile());
    }

}
