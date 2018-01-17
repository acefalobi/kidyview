package com.ltst.schoolapp.teacher.ui.events.add.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.danil.recyclerbindableadapter.library.SimpleBindableAdapter;
import com.danil.recyclerbindableadapter.library.view.BindableViewHolder;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Group;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.ApplicationSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.ServerDataBaseException;
import com.ltst.core.permission.PermissionsHandler;
import com.ltst.core.ui.DialogProvider;
import com.ltst.core.util.FilePathUtil;
import com.ltst.core.util.FileUtils;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;
import com.ltst.core.util.PhotoRotator;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.teacher.data.DataService;
import com.ltst.schoolapp.teacher.ui.addchild.fragment.SelectableGroup;
import com.ltst.schoolapp.teacher.ui.checks.select.child.fragment.ChooseGroupHolder;
import com.ltst.schoolapp.teacher.ui.events.add.AddEventActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AddEventPresenter implements AddEventContract.Presenter, DialogProvider
        .PhotoWayCallBack, Toolbar.OnMenuItemClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, BindableViewHolder.ActionListener<SelectableGroup> {

    public static final String DATE_PICKER_TAG = "AddEventPresenter.DatePicker";
    public static final String TIME_PICKER_TAG = "AddEventPresenter.TimePicker";
    public static final int MAX_COUNT_FILES = 1;
    public static final int MAX_COUNT_PHOTOS = 1;
    private final AddEventContract.View view;
    private final ActivityScreenSwitcher activitySwitcher;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final DataService dataService;
    private final TeacherApplication application;
    private final GalleryPictureLoader galleryPictureLoader;
    private final ApplicationSwitcher applicationSwitcher;
    private final PermissionsHandler permissionsHandler;
    private final Bundle screenParams;
    private final TeacherApplication appContext;
    private CompositeSubscription subscriptions;
    private boolean isAdding = false;
    private Uri currentPhotoPath;
    private List<String> photos = new ArrayList<>();
    private Uri currentFilePath;
    private File photo;
    private File document;
    private List<String> files = new ArrayList<>();
    private Calendar eventCalendar = Calendar.getInstance();
    private SimpleBindableAdapter<SelectableGroup> groupsAdapter =
            new SimpleBindableAdapter<>(R.layout.viewholder_choose_child_group_item, ChooseGroupHolder.class);
    private Group tempGroup;
    private long selectedGroupId;

    @Inject
    public AddEventPresenter(AddEventContract.View view,
                             ActivityScreenSwitcher screenSwitcher,
                             FragmentScreenSwitcher fragmentScreenSwitcher,
                             DataService dataService,
                             TeacherApplication application,
                             GalleryPictureLoader galleryPictureLoader,
                             ApplicationSwitcher applicationSwitcher,
                             PermissionsHandler permissionsHandler,
                             Bundle screenParams,
                             TeacherApplication appContext) {
        this.permissionsHandler = permissionsHandler;
        this.appContext = appContext;
        this.view = view;
        this.activitySwitcher = screenSwitcher;
        this.fragmentSwitcher = fragmentScreenSwitcher;
        this.dataService = dataService;
        this.application = application;
        this.galleryPictureLoader = galleryPictureLoader;
        this.applicationSwitcher = applicationSwitcher;
        this.screenParams = screenParams;
        feelStartEventCalendar(screenParams);
    }

    private void feelStartEventCalendar(Bundle screenParams) {
        eventCalendar.clear();
        eventCalendar.set(Calendar.YEAR, screenParams.getInt(AddEventActivity.Screen.SELECTED_YEAR));
        eventCalendar.set(Calendar.MONTH, screenParams.getInt(AddEventActivity.Screen.SELECTED_MONTH));
        eventCalendar.set(Calendar.DAY_OF_MONTH, screenParams.getInt(AddEventActivity.Screen.SELECTED_DAY));
        eventCalendar.set(Calendar.HOUR_OF_DAY, 0);
        eventCalendar.set(Calendar.MINUTE, 0);
        eventCalendar.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ApplicationSwitcher.FILE_REQUEST) {
            if (data == null) return;
            currentFilePath = data.getData();
            if (currentFilePath == null) return;
            addFile();
        } else if (requestCode == ApplicationSwitcher.CAMERA_REQUEST) {
            addPhoto();
        } else if (requestCode == ApplicationSwitcher.GALLERY_REQUEST) {
            photo = galleryPictureLoader.getFileWithRotate(data, GalleryPictureLoader.MAX_PHOTO_SIZE);
            currentPhotoPath = Uri.fromFile(photo);
            addPhoto();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  FIRST START  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void firstStart() {
        subscriptions = new CompositeSubscription();
        view.setDate(eventCalendar);
        dataService.getCachedGroups()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bindSelectedGroup, Throwable::printStackTrace);
    }

    private Action1<List<Group>> bindSelectedGroup = groups -> {
        for (Group group : groups) {
            if (group.isSelected()) {
                AddEventPresenter.this.view.setGroupTitle(group.getTitle());
                selectedGroupId = group.getId();
                tempGroup = group;
            }
        }
        groupsAdapter.clear();
        groupsAdapter.addAll(SelectableGroup.fromGroups(groups));
        if (groups.size() <= 1) {
            AddEventPresenter.this.view.oneGroupMode();
        }

    };

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////  START  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void start() {
        subscriptions = new CompositeSubscription();
        initToolbar();
        view.bindData(onPhotoClickListener(),
                onPinFileClickListener());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  INIT TOOLBAR ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void initToolbar() {
        view.initToolbar(R.drawable.ic_clear_white_24dp,
                onNavigationClickListener(),
                this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        String content = view.getContent();
        if (StringUtils.isBlank(content) && photos.isEmpty() && files.isEmpty()) {
            view.emptyEventError();
            return false;
        }
        if (eventCalendar.get(Calendar.HOUR) == 0 && eventCalendar.get(Calendar.MINUTE) == 0
                && eventCalendar.get(Calendar.MILLISECOND) == 0 && eventCalendar.get(Calendar.AM_PM) == 0) {
            view.timeError();
            return false;
        }
        if (isAdding) return false;
        isAdding = true;
        view.showLoading(true);
        subscriptions.add(dataService.addEvent(selectedGroupId, eventCalendar, content, photos, files)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addedPost -> {
                    view.showLoading(true);
                    isAdding = false;
//                    if (photo != null && photo.exists()) {
//                        photo.delete();
//                    }
//                    if (document != null && document.exists()) {
//                        document.delete();
//                    }
                    Intent intent = new Intent();
                    intent.putExtra(AddEventActivity.Screen.SELECTED_YEAR, eventCalendar.get(Calendar.YEAR));
                    intent.putExtra(AddEventActivity.Screen.SELECTED_MONTH, eventCalendar.get(Calendar.MONTH));
                    intent.putExtra(AddEventActivity.Screen.SELECTED_DAY, eventCalendar.get(Calendar.DAY_OF_MONTH));
                    activitySwitcher.setResultAndGoBack(intent);
                }, this::createEventError));
        return false;
    }

    private void createEventError(Throwable throwable) {
        view.showLoading(false);
        isAdding = false;
        throwable.printStackTrace();
        if (throwable instanceof ServerDataBaseException){
            view.showDateOfEventError();
        }
    }

    private View.OnClickListener onNavigationClickListener() {
        return v -> activitySwitcher.goBack();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   PHOTOS  /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener onPhotoClickListener() {
        return v -> view.chosePhotoWay(this);
    }

    private void addPhoto() {
        PhotoRotator.rotatePhotoFile(appContext, currentPhotoPath);
        String photo = currentPhotoPath.getPath();
        photos.add(photo);
        view.addPhoto(currentPhotoPath, v -> {
            view.removePhoto(v);
            photos.remove(photo);
            view.setPhotoButtonEnabled(photos.size() < MAX_COUNT_PHOTOS);
        });
        view.setPhotoButtonEnabled(photos.size() < MAX_COUNT_PHOTOS);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   FILES  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener onPinFileClickListener() {
        return v -> files();
    }

    private void addFile() {
        if (currentFilePath.toString().contains("content")) {
            downloadFileFromCloud();
            return;
        }
        String file = null;
        if (currentFilePath.toString().contains(appContext.getExternalCacheDir().getPath())) {
            file = currentFilePath.toString();
        } else {
            file = FileUtils.getPath(application, currentFilePath);
        }
        if (!StringUtils.isBlank(file)) {
            files.add(file);
            int lastSlash = file.lastIndexOf(StringUtils.SLASH);
            String fileName = file.substring(lastSlash + 1);
            final String finalFile = file;
            view.addFile(fileName, v -> {
                view.removeFile(v);
                files.remove(finalFile);
                view.setFileButtonEnabled(files.size() < MAX_COUNT_FILES);
            });
            view.setFileButtonEnabled(files.size() < MAX_COUNT_FILES);
        }

    }

    private void downloadFileFromCloud() {
        getFileFromDrive(appContext, currentFilePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uri -> {
                    currentFilePath = uri;
                    addFile();
                });
    }

    public Observable<Uri> getFileFromDrive(Context context, Uri filePath) {
        ContentResolver contentResolver = context.getContentResolver();
        Observable<Uri> downloadObservable = null;
        try {
            InputStream inputStream = contentResolver.openInputStream(filePath);
            String mimeType = contentResolver.getType(filePath);
            Cursor returnCursor = contentResolver.query(filePath, null, null, null, null);
            if (returnCursor != null) {
                returnCursor.moveToFirst();
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String fileName = returnCursor.getString(nameIndex);
                downloadObservable = Observable.create(new Observable.OnSubscribe<Uri>() {
                    @Override
                    public void call(Subscriber<? super Uri> subscriber) {
                        BufferedInputStream bis = new BufferedInputStream(inputStream);
                        document = FilePathUtil.getFileForDrive(appContext, fileName);
                        if (!document.exists()) {
                            try {
                                document.createNewFile();
                                document.setWritable(true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            int count;
                            OutputStream output = new FileOutputStream(document.getPath());
                            byte[] data = new byte[1024];
                            long total = 0;
                            try {
                                while ((count = bis.read(data)) != -1) {
                                    total += count;
                                    output.write(data, 0, count);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // flushing output
                            try {
                                output.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // closing streams
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                bis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        subscriber.onNext(Uri.parse(document.getAbsolutePath()));
                        subscriber.onCompleted();

                    }
                });

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return downloadObservable;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////   DATE AND TIME   /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
//    private View.OnClickListener onPickDateAndTimeClickListener() {
//        return v -> openDatePicker();
//    }

    @Override
    public void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, screenParams.getInt(AddEventActivity.Screen.SELECTED_YEAR));
        calendar.set(Calendar.MONTH, screenParams.getInt(AddEventActivity.Screen.SELECTED_MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, screenParams.getInt(AddEventActivity.Screen.SELECTED_DAY));
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setAccentColor(ContextCompat.getColor(appContext, R.color.toolbar_color_blue));
        datePickerDialog.setMinDate(Calendar.getInstance());
        datePickerDialog.show(fragmentSwitcher.getFragmentManager(), DATE_PICKER_TAG);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        eventCalendar.set(year, monthOfYear, dayOfMonth);
        this.view.setDate(eventCalendar);
    }

    @Override
    public void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.setAccentColor(ContextCompat.getColor(appContext, R.color.toolbar_color_blue));
        timePickerDialog.show(fragmentSwitcher.getFragmentManager(), TIME_PICKER_TAG);
    }


    @Override
    public void onTimeSet(RadialPickerLayout picker, int hourOfDay, int minute, int second) {
        if (hourOfDay == 12) {
            eventCalendar.set(Calendar.AM_PM, 1);
        } else {
            eventCalendar.set(Calendar.HOUR, hourOfDay);
        }

        eventCalendar.set(Calendar.MINUTE, minute);
        eventCalendar.set(Calendar.MILLISECOND, 10); //for determinate was time seted or not
        view.setTime(eventCalendar);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  GROUP_CHOOSER  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override public void openGroupChooser() {
        if (groupsAdapter.getItemCount() <= 1) {
            return;
        }
        view.showGroupChooser(groupsAdapter);
        groupsAdapter.setActionListener(AddEventPresenter.this);
    }

    @Override public void changeGroup() {
        if (tempGroup.getId() == selectedGroupId) {
            return;
        }
        view.setGroupTitle(tempGroup.getTitle());
        selectedGroupId = tempGroup.getId();
    }

//    private Action1<List<SelectableGroup>> addDataToAdapter = items -> {
//        groupsAdapter.addAll(items);
//
//        AddEventPresenter.this.view.showGroupChooser(groupsAdapter);
//    };

    @Override public void OnItemClickListener(int position, SelectableGroup item) {
        for (SelectableGroup selectableGroup : groupsAdapter.getItems()) {
            Group group = selectableGroup.getGroup();
            if (group.getId() != item.getGroup().getId()) {
                selectableGroup.setSelected(false);
            } else {
                selectableGroup.setSelected(true);
                tempGroup = item.getGroup();
            }
        }
        groupsAdapter.notifyDataSetChanged();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  PERMISSIONS  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void camera() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                this::checkCameraPermission);
    }

    private void checkCameraPermission() {
        permissionsHandler.requestPermission(PermissionsHandler.CAMERA_REQUEST,
                Manifest.permission.CAMERA,
                this::openCamera);
    }

    private void openCamera() {
        photo = FilePathUtil.getCacheDir(appContext);
        currentPhotoPath = applicationSwitcher.openCamera(photo);
    }

    @Override
    public void gallery() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                applicationSwitcher::openGallery);
    }

    public void files() {
        permissionsHandler.requestPermission(PermissionsHandler.WRITE_STORAGE_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                this::openFileManagerIfExist);
    }


    private void openFileManagerIfExist() {
        if (applicationSwitcher.fileManagerExist()) {
            applicationSwitcher.openFileManager();
        } else {
            view.showFileManagerError();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   LIFECICLE   ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onSave(@NonNull Bundle outState) {

    }

    @Override
    public void stop() {
        subscriptions.unsubscribe();
    }


}
