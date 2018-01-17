package com.ltst.schoolapp.teacher.ui.addchild.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.Group;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.permission.PermissionsHandler;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.ActivityProvider;
import com.ltst.core.util.FilePathUtil;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.core.util.IntentsUtil;
import com.ltst.core.util.PhotoRotator;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.teacher.data.DataService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AddChildPresenter implements AddChildContract.Presenter {

    private final AddChildContract.View view;
    private final ActivityProvider activityProvider;
    private final GalleryPictureLoader galleryPictureLoader;
    private final DataService dataService;
    private final ActivityScreenSwitcher activitySwitcher;
    private final int childId;
    private final int screenMode;
    private final TeacherApplication appContext;
    private final PermissionsHandler permissionsHandler;
    private final DialogProvider dialogProvider;
    private final ApplicationSwitcher applicationSwitcher;

    private Child child;
    private Uri photoPath;
    private ActivityProvider.ActivityResultListener activityResultListener;
    private CompositeSubscription subscriptions;
    private File photoFileForDelete;
    private List<SelectableGroup> childGroups;
    private List<SelectableGroup> tempSelectedChildGroups;
    private Child copyOfExistChild;

    @Inject
    public AddChildPresenter(AddChildContract.View view,
                             ActivityProvider activityProvider,
                             GalleryPictureLoader galleryPictureLoader,
                             DataService dataService,
                             ActivityScreenSwitcher activitySwitcher,
                             int childId,
                             TeacherApplication appContext,
                             PermissionsHandler permissionsHandler,
                             DialogProvider dialogProvider, ApplicationSwitcher applicationSwitcher) {
        this.appContext = appContext;
        this.view = view;
        this.activityProvider = activityProvider;
        this.galleryPictureLoader = galleryPictureLoader;
        this.dataService = dataService;
        this.activitySwitcher = activitySwitcher;
        this.childId = childId;
        this.permissionsHandler = permissionsHandler;
        this.dialogProvider = dialogProvider;
        this.applicationSwitcher = applicationSwitcher;
        if (childId == 0) {
            screenMode = AddChildFragment.Screen.ADD_CHILD;
        } else {
            screenMode = AddChildFragment.Screen.EDIT_CHILD;
        }
        setupActivityResultListener();
    }

    private void setupActivityResultListener() {
        activityResultListener = (requestCode, resultCode, data) -> {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            if (requestCode == ApplicationSwitcher.CAMERA_REQUEST) {
                PhotoRotator.rotatePhotoFile(appContext, photoPath);
                view.setPhoto(photoPath);
            } else if (requestCode == ApplicationSwitcher.GALLERY_REQUEST) {
                photoFromGallery(data.getData());
            }
        };
    }

    @Override
    public void start() {
        activityProvider.setActivityResultListener(activityResultListener);
        if (subscriptions == null) {
            subscriptions = new CompositeSubscription();
        }
    }

    @Override
    public void stop() {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
            subscriptions = null;
        }
    }

    @Override
    public void firstStart() {
        subscriptions = new CompositeSubscription();
        String screenTitle =
                screenMode == AddChildFragment.Screen.ADD_CHILD
                        ? appContext.getString(R.string.add_child_title)
                        : appContext.getString(R.string.edit_child_title);
        view.setTitle(screenTitle);
        if (screenMode == AddChildFragment.Screen.ADD_CHILD) {
            dataService.getSelectedGroup()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(group -> {
                        child = new Child();
                        child.setFullAccessMembers(new ArrayList<>());
                        ArrayList<Long> groupIds = new ArrayList<>();
                        groupIds.add(group.getId());
                        child.setGroupIds(groupIds);
                        prepareGroupChooser();
                    });
        }

        if (screenMode == AddChildFragment.Screen.EDIT_CHILD) {
            //EDIT EXISTING CHILD
            view.startLoad();
            Subscription getChildSubscription = dataService.getChildById(childId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(child1 -> {
                        view.stopLoad();
                        AddChildPresenter.this.child = child1;
                        this.copyOfExistChild = child.clone();
                        view.bindChild(child1);
                        prepareGroupChooser();
                    }, throwable -> {
                        if (throwable instanceof NetErrorException) {
                            view.showNetError();
                        }
                    });
            subscriptions.add(getChildSubscription);
        }
    }

    private void prepareGroupChooser() {
        childGroups = new ArrayList<>();
        subscriptions.add(dataService.getCachedGroups()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groups -> {
                    for (Group group : groups) {
                        childGroups.add(new SelectableGroup(group, false));
                    }
                    List<Long> groupIds = child.getGroupIds();
                    for (Long groupId : groupIds) {
                        for (SelectableGroup selectableGroup : childGroups) {
                            if (groupId == selectableGroup.getGroup().getId()) {
                                selectableGroup.setSelected(true);
                            }
                        }
                    }
                    view.setGroups(SelectableGroup.getSelectedGroups(childGroups));
                }));
    }

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    @Override
    public void cancel() {

    }

    private static final int WRITE_STORAGE_PERMISSION = 5456;

    @Override
    public void checkWriteExternalPermission() {
        permissionsHandler.requestPermission(WRITE_STORAGE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                view::chosePhotoWay);
    }


    @Override
    public void checkPermissionAndOpenCamera() {
        permissionsHandler.requestPermission(PermissionsHandler.CAMERA_REQUEST,
                Manifest.permission.CAMERA, this::openCamera);
    }

    private void photoFromGallery(Uri data) {
        try {
            Bitmap bitmap = galleryPictureLoader.getBitmap(data, GalleryPictureLoader.MAX_PHOTO_SIZE);
            photoFileForDelete = FilePathUtil.getCacheDir(appContext);
            FileOutputStream out = new FileOutputStream(photoFileForDelete);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            photoPath = Uri.fromFile(photoFileForDelete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        view.setPhoto(photoPath);
    }

    @Override
    public void openAppSettings() {
        AppCompatActivity context = activityProvider.getContext();
        context.startActivity(IntentsUtil.getAppSettingsIntent(context));
    }

    @Override
    public void openGallery() {
        applicationSwitcher.openGallery();
    }

    @Override
    public void setName(String name) {
        child.setName(name);
    }

    @Override
    public void setLastName(String lastName) {
        child.setLastName(lastName);
    }

    @Override
    public void setGender(boolean switchEnabled) {
        child.setGender(switchEnabled ? Child.FEMALE : Child.MALE);
    }

    @Override
    public void setBirthDay(int year, int mouth, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, mouth);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat(Child.BIRTHDAY_FORMAT);
        Date time = calendar.getTime();
        String birthDate = dateFormat.format(time);
        child.setBirthDay(birthDate);
        view.setBirthDate(birthDate);
    }

    @Override
    public void setBloodGroup(String group) {
        child.setBloodGroup(group);
    }

    @Override
    public void setGenotype(String genotype) {
        child.setGenotype(genotype);
    }

    @Override
    public void setAllergies(String allergies) {
        child.setAllergies(allergies);
    }

    @Override
    public void setAdditional(String additional) {
        child.setAdditional(additional);
    }

    @Override
    public void done() {
        if (screenMode == AddChildFragment.Screen.EDIT_CHILD) {
            if (child.equals(copyOfExistChild) && photoPath==null) {
                goBack();
                return;
            }
        }
        String name = child.getFirstName();
        if (StringUtils.isBlank(name)) {
            view.emptyName();
            return;
        } else {
            String lastName = child.getLastName();
            if (StringUtils.isBlank(lastName)) {
                view.emptyLastName();
                return;
            }
        }
        if (child.getBirthDay() == null) {
            view.emptyBirthDate();
            return;
        }

        if (child.getGender() == null) {
            view.emptyGender();
            return;
        }

        if (child.getBloodGroup() == null) {
            child.setBloodGroup(StringUtils.EMPTY);
        }
        if (child.getGenotype() == null) {
            child.setGenotype(StringUtils.EMPTY);
        }
        if (child.getAllergies() == null) {
            child.setAllergies(StringUtils.EMPTY);
        }
        if (child.getAdditional() == null) {
            child.setAdditional(StringUtils.EMPTY);
        }
        view.startLoad();
        if (screenMode == AddChildFragment.Screen.ADD_CHILD) {
            createNewChild();
        } else {
            updateExistingChild();
        }


    }

    private void updateExistingChild() {
        String path = photoPath != null ? photoPath.getPath() : null;
        File file = path != null ? new File(path) : null;
        view.startLoad();
        subscriptions.add(dataService.editChild(child, file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(child1 -> {
                    view.stopLoad();
                    goBack();
                }, throwable -> {
                    if (throwable instanceof NetErrorException) {
                        view.showNetError();
                    }
                }));
    }

    private void createNewChild() {
        String path = photoPath != null ? photoPath.getPath() : null;
        subscriptions.add(dataService.createChild(child, path != null ? new File(path) : null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(child1 -> {
                    view.stopLoad();
//                    if (photoFileForDelete != null && photoFileForDelete.exists()) {
//                        photoFileForDelete.delete();
//                    }
                    goBack();
                }, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetErrorException) {
                        view.showNetError();
                    }
                }));
    }

    @Override
    public void goBack() {
        activitySwitcher.goBack();
    }

    @Override public void selectChildGroup() {
        tempSelectedChildGroups = new ArrayList<>(childGroups);
        SimpleBindableAdapter adapter =
                new SimpleBindableAdapter(R.layout.viewholder_selected_child_group_item, SelectGroupViewHolder.class);
        adapter.setActionListener((position, item) -> {
            for (SelectableGroup selectableGroup : tempSelectedChildGroups) {
                SelectableGroup tempSelectableGroup = (SelectableGroup) item;
                if (selectableGroup.getGroup().getId() == tempSelectableGroup.getGroup().getId()) {
                    selectableGroup.setSelected(tempSelectableGroup.isSelected());
                }
            }
        });
        adapter.addAll(childGroups);
        view.openSelectGroupDialog(adapter);


    }

    @Override
    public void confirmTempSelectedGroups() {
        List<Long> newGroupIds = new ArrayList<>();
        for (SelectableGroup selectableGroup : tempSelectedChildGroups) {
            if (selectableGroup.isSelected()) {
                newGroupIds.add(selectableGroup.getGroup().getId());
            }
        }
        if (newGroupIds.size() < 1) {
            showEmptyGroupsWarning();
        } else {
            child.setGroupIds(newGroupIds);
            List<Group> selectedGroups = SelectableGroup.getSelectedGroups(tempSelectedChildGroups);
            view.setGroups(selectedGroups);
        }
    }

    private void showEmptyGroupsWarning() {
        String warningMessage;
        if (screenMode == AddChildFragment.Screen.EDIT_CHILD) {
            warningMessage = activityProvider.getContext()
                    .getString(R.string.add_child_remove_groups_waring);
            dialogProvider.emptyGroupsWarning(warningMessage, true, (dialog, which) -> {
                child.setGroupIds(new ArrayList<>());
                tempSelectedChildGroups = null;
                view.setGroups(null);

            });
        } else if (screenMode == AddChildFragment.Screen.ADD_CHILD) {
            warningMessage = activityProvider.getContext()
                    .getString(R.string.add_child_create_groups_empty);
            dialogProvider.emptyGroupsWarning(warningMessage, false, (dialog, which) -> {
                tempSelectedChildGroups = null;
                prepareGroupChooser();
            });
        }

    }

    @Override
    public void cancelTempSelectedGtoups() {
        tempSelectedChildGroups = null;
    }

    private void openCamera() {
        photoFileForDelete = FilePathUtil.getCacheDir(appContext);
        photoPath = Uri.fromFile(photoFileForDelete);
        applicationSwitcher.openCamera(photoFileForDelete);
    }


}
