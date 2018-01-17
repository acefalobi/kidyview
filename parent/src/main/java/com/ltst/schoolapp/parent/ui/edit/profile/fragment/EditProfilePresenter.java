package com.ltst.schoolapp.parent.ui.edit.profile.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Profile;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.permission.PermissionsHandler;
import com.ltst.core.util.FilePathUtil;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.core.util.PhotoRotator;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.core.util.validator.ValidationThrowable;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.ui.checkout.fragment.info.ParentProfile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class EditProfilePresenter implements EditProfileContract.Presenter {

    private final EditProfileContract.View view;
    private final DataService dataService;
    private final ApplicationSwitcher applicationSwitcher;
    private final ActivityScreenSwitcher activityScreenSwitcher;
    private final ParentApplication context;
    private final GalleryPictureLoader galleryPictureLoader;
    private final PermissionsHandler permissionsHandler;
    private ParentProfile profile;
    private ParentProfile savedProfile;
    private CompositeSubscription subscription;
    private File tempPhotoFile;
    private Uri photoPath;
    private boolean fromGallery = false;

    @Inject
    public EditProfilePresenter(EditProfileContract.View view,
                                DataService dataService,
                                ApplicationSwitcher applicationSwitcher,
                                ActivityScreenSwitcher activityScreenSwitcher,
                                ParentApplication context,
                                GalleryPictureLoader galleryPictureLoader,
                                PermissionsHandler permissionsHandler) {
        this.view = view;
        this.dataService = dataService;
        this.applicationSwitcher = applicationSwitcher;
        this.activityScreenSwitcher = activityScreenSwitcher;
        this.context = context;
        this.galleryPictureLoader = galleryPictureLoader;
        this.permissionsHandler = permissionsHandler;
    }

    @Override
    public void start() {
        if (!fromGallery) {
            addPhoto();
            fromGallery = false;
        }

        subscription = new CompositeSubscription();
        if (profile == null) {
            subscription.add(dataService.getProfileFromDataBase()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(parentProfile -> {
                        this.profile = parentProfile;
                        this.savedProfile = profile.clone();
                        view.bindData(parentProfile);
                    }));
        }

    }

    @Override
    public void stop() {

        subscription.unsubscribe();
        subscription = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ApplicationSwitcher.CAMERA_REQUEST) {
            if (photoPath != null) {
                PhotoRotator.rotatePhotoFile(context, photoPath);
                addPhoto();
            }
        } else if (requestCode == ApplicationSwitcher.GALLERY_REQUEST) {
            tempPhotoFile = galleryPictureLoader.getFileWithRotate(data, GalleryPictureLoader.MAX_PHOTO_SIZE);
            photoPath = Uri.fromFile(tempPhotoFile);
            fromGallery = true;
            addPhoto();
        }
    }

    private void addPhoto() {
        if (photoPath != null && new File(photoPath.getEncodedPath()).length() > 0) {
            view.setPhoto(photoPath);
        }
    }

    @Override
    public void setFirstName(String firstName) {
        if (profile != null) {
            Profile profile = this.profile.getProfile();
            if (profile != null) {
                profile.setFirstName(firstName);
            }
        }


    }

    @Override
    public void setLastName(String lastName) {
        if (profile != null && profile.getProfile() != null) {
            profile.getProfile().setLastName(lastName);
        }

    }

    @Override
    public void setPhone(String phoneNumber) {
        if (profile != null && profile.getProfile() != null) {
            profile.getProfile().setPhone(phoneNumber);
        }
    }

    @Override
    public void setSecondPhone(String secondPhone) {
        if (profile != null && profile.getProfile() != null) {
            profile.getProfile().setAdditionalPhone(secondPhone);
        }

    }

    @Override
    public void validateAndUpdate() {
        if (savedProfile.equals(profile) && photoPath == null) {
            view.stopLoad();
            goBack();
            return;
        }
        Map<ValidateType, String> needValidate = new HashMap<>();
        Profile profile = this.profile.getProfile();
        String phone = profile.getPhone() != null ? profile.getPhone() : StringUtils.EMPTY;
        needValidate.put(ValidateType.PERSONAL_PHONE, phone);
        String additionalPhone = profile.getAdditionalPhone();
        if (!StringUtils.isBlank(additionalPhone)) {
            needValidate.put(ValidateType.SECOND_PHONE, additionalPhone);
        }
        needValidate.put(ValidateType.NAME, profile.getFirstName());
        needValidate.put(ValidateType.LAST_NAME, profile.getLastName());
        FieldsValidator.validate(needValidate)
                .subscribe(validateTypeStringMap -> {
                    updateOnServer();
                }, throwable -> {
                    if (throwable instanceof ValidationThrowable) {
                        ValidationThrowable notValid = (ValidationThrowable) throwable;
                        Set<ValidateType> validateTypes = notValid.keySet();
                        for (ValidateType type : validateTypes) {
                            if (type.equals(ValidateType.PERSONAL_PHONE)) {
                                view.personalPhoneValidateError();
                            } else if (type.equals(ValidateType.NAME)) {
                                view.nameValidateError();
                            } else if (type.equals(ValidateType.LAST_NAME)) {
                                view.lastNameValidateError();
                            } else if (type.equals(ValidateType.SECOND_PHONE)) {
                                view.secondPhoneValidateError();
                            }
                        }
                    }
                });

    }

    private void updateOnServer() {
        view.startLoad();
        subscription.add(dataService.updateProfile(profile, tempPhotoFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profile1 -> {
                    view.stopLoad();
                    goBack();
                }, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetErrorException) {
                        view.netwrorkError();
                    }
                }));
    }

    @Override
    public void goBack() {
        activityScreenSwitcher.goBack();
    }

    @Override
    public void photoFromCamera() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, this::checkCameraPermission);

    }

    private void checkCameraPermission() {
        permissionsHandler.requestPermission(PermissionsHandler.CAMERA_REQUEST,
                Manifest.permission.CAMERA, this::openCamera);
    }

    private void openCamera() {
        tempPhotoFile = FilePathUtil.getCacheDir(context);
        photoPath = Uri.fromFile(tempPhotoFile);
        applicationSwitcher.openCamera(tempPhotoFile);
    }

    @Override
    public void photoFromGallery() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, this::openGallery);

    }

    private void openGallery() {
        applicationSwitcher.openGallery();
    }

    @Override
    public void firstStart() {

    }

    private static final String KEY_STATE_PROFILE = "EditProfilePresenter.ProfileState";
    private static final String KEY_PHOTO_URI = "EditProfilePresenter.ProfilePhotoPath";
    private static final String KEY_SAVED_PROFILE = "EditProfilePresenter.SavedProfile";

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_STATE_PROFILE)) {
            this.profile = savedInstanceState.getParcelable(KEY_STATE_PROFILE);
        }
        if (savedInstanceState.containsKey(KEY_PHOTO_URI)) {
            this.photoPath = Uri.parse(savedInstanceState.getString(KEY_PHOTO_URI));
        }
        if (savedInstanceState.containsKey(KEY_SAVED_PROFILE)) {
            this.savedProfile = savedInstanceState.getParcelable(KEY_SAVED_PROFILE);
        }
    }

    @Override
    public void onSave(@NonNull Bundle outState) {
        if (profile != null) {
            outState.putParcelable(KEY_STATE_PROFILE, profile);
        }
        if (photoPath != null) {
            outState.putString(KEY_PHOTO_URI, photoPath.toString());
        }
        if (savedProfile != null) {
            outState.putParcelable(KEY_SAVED_PROFILE, savedProfile);
        }
    }
}
