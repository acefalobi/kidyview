package com.ltst.schoolapp.parent.ui.child.edit.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
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
import com.ltst.schoolapp.parent.data.model.ParentChild;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class EditChildPresenter implements EditChildContract.Presenter {

    private final EditChildContract.View view;
    private final Child parentChild;
    private final Child originalChild;
    private final long schoolId;
    private final ApplicationSwitcher applicationSwitcher;
    private final GalleryPictureLoader galleryPictureLoader;
    private final ActivityScreenSwitcher activityScreenSwitcher;
    private final ParentApplication context;
    private final DataService dataService;
    private final PermissionsHandler permissionsHandler;
    private File cacheFile;
    private Uri photoUri;
    private CompositeSubscription subscription;

    @Inject
    public EditChildPresenter(EditChildContract.View view,
                              ParentChild parentChild,
                              ApplicationSwitcher applicationSwitcher,
                              GalleryPictureLoader galleryPictureLoader,
                              ActivityScreenSwitcher activityScreenSwitcher,
                              ParentApplication context,
                              DataService dataService,
                              PermissionsHandler permissionsHandler) {
        this.view = view;
        this.originalChild = parentChild.getChild();
        this.permissionsHandler = permissionsHandler;
        this.parentChild = parentChild.getChild().clone();
        this.schoolId = parentChild.getSchoolId();
        this.applicationSwitcher = applicationSwitcher;
        this.galleryPictureLoader = galleryPictureLoader;
        this.activityScreenSwitcher = activityScreenSwitcher;
        this.context = context;
        this.dataService = dataService;
    }

    @Override
    public void start() {
        subscription = new CompositeSubscription();
    }

    @Override
    public void stop() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    @Override
    public void setFirstName(String firstName) {
        parentChild.setName(firstName);
    }

    @Override
    public void setLastName(String lastName) {
        parentChild.setLastName(lastName);
    }

    @Override
    public void setGender(boolean male) {
        parentChild.setGender(male ? Child.MALE : Child.FEMALE);
    }

    @Override
    public void setBloodGroup(String bloodGroup) {
        parentChild.setBloodGroup(bloodGroup);
    }

    @Override
    public void setGenotype(String genotype) {
        parentChild.setGenotype(genotype);
    }

    @Override
    public void setAllergies(String allergies) {
        parentChild.setAllergies(allergies);
    }

    @Override
    public void setAdditionalInfo(String info) {
        parentChild.setAdditional(info);
    }

    @Override
    public void done() {
        if (photoUri == null && originalChild.equals(parentChild)) {
            goBack();
            return;
        }
        Map<ValidateType, String> needValidate = new HashMap<>();
        needValidate.put(ValidateType.NAME, parentChild.getFirstName());
        needValidate.put(ValidateType.LAST_NAME, parentChild.getLastName());
        FieldsValidator.validate(needValidate)
                .subscribe(new Action1<Map<ValidateType, String>>() {
                    @Override
                    public void call(Map<ValidateType, String> validateTypeStringMap) {
                        updateChild();
                    }
                }, throwable -> {
                    ValidationThrowable validationThrowable = (ValidationThrowable) throwable;
                    Set<ValidateType> validateTypes = validationThrowable.keySet();
                    if (validateTypes.contains(ValidateType.NAME)) {
                        view.nameValidateError();
                    } else if (validateTypes.contains(ValidateType.LAST_NAME)) {
                        view.lastNameValidateError();
                    }
                });
    }

    private void updateChild() {
        view.startLoad();
        subscription.add(dataService.updateChild(parentChild, schoolId, cacheFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(child1 -> {
                    view.stopLoad();
                    this.originalChild.updateAll(child1.getChild());
                    goBack();
                }, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetErrorException) {
                        view.netError();
                    }
                }));
    }

    @Override public void checkWriteExternalPermission() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                view::photoWay);
    }

    @Override public void checkCameraPermission() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.CAMERA,
                this::openCamera);
    }

    private File getFile() {
        cacheFile = FilePathUtil.getCacheDir(context);
        photoUri = Uri.fromFile(cacheFile);
        return cacheFile;
    }

    @Override
    public void openCamera() {
        applicationSwitcher.openCamera(getFile());
    }

    @Override public void openGallery() {
        applicationSwitcher.openGallery();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ApplicationSwitcher.CAMERA_REQUEST) {
            PhotoRotator.rotatePhotoFile(context, photoUri);
            view.setAvatar(photoUri);
        } else if (requestCode == ApplicationSwitcher.GALLERY_REQUEST) {
            cacheFile = galleryPictureLoader.getFileWithRotate(data, GalleryPictureLoader.MAX_PHOTO_SIZE);
            photoUri = Uri.fromFile(cacheFile);
            view.setAvatar(photoUri);
        }
    }

    @Override
    public void goBack() {
        activityScreenSwitcher.goBack();
    }


    @Override
    public void firstStart() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                setBirthDay(year, monthOfYear, dayOfMonth);
            }
        };
        view.bindView(parentChild, dateSetListener);
        initBirthDate();
    }

    private void setBirthDay(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat(Child.BIRTHDAY_FORMAT);
        String birthday = dateFormat.format(calendar.getTime());
        parentChild.setBirthDay(birthday);
        view.setBirthDate(calendar);
    }

    private void initBirthDate() {
        Calendar calendar = Calendar.getInstance();
        String birthDay = parentChild.getBirthDay();
        if (!StringUtils.isBlank(birthDay)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Child.SERVER_FORMAT);
            try {
                String displayedDay;
                if (birthDay.contains("T")) {
                    displayedDay = birthDay.substring(0, birthDay.indexOf("T"));
                } else {
                    displayedDay = birthDay;
                }
                Date parse = dateFormat.parse(displayedDay);
                calendar.setTime(parse);
                view.setBirthDate(calendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

}
