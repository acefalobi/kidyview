package com.ltst.schoolapp.parent.ui.checkout.fragment.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;
import com.ltst.core.navigation.ActivityScreenSwitcher;
import com.ltst.core.navigation.FragmentScreenSwitcher;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.util.validator.FieldsValidator;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.core.util.validator.ValidationThrowable;
import com.ltst.schoolapp.R;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.data.DataService;
import com.ltst.schoolapp.parent.data.model.ChildInGroupInSchool;
import com.ltst.schoolapp.parent.data.model.ParentChild;
import com.ltst.schoolapp.parent.ui.checkout.fragment.share.ShareCodeFragment;
import com.ltst.schoolapp.parent.ui.checkout.select.school.SelectChildInSchoolActivity;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class InfoPresenter implements InfoContract.Presenter, TimePickerDialog.OnTimeSetListener {

    private final InfoContract.View view;
    private final DataService dataService;
    private final FragmentScreenSwitcher fragmentSwitcher;
    private final ActivityScreenSwitcher activityScreenSwitcher;
    private final ParentApplication appContext;
    //    private ParentProfile parentProfile;
    //    private ArrayList<Long> ids;
    private ArrayList<ChildInGroupInSchool> objects;
    SimpleDateFormat requestDateFormat = new SimpleDateFormat(Child.SERVER_FORMAT);
    SimpleDateFormat viewDateFormat = new SimpleDateFormat("HH:mm");
    private String status = StringUtils.EMPTY;
    private String firstName = StringUtils.EMPTY;
    private String lastName = StringUtils.EMPTY;
    private String time = StringUtils.EMPTY;
    private String visibleTime;
    private CompositeSubscription subscription;
    private boolean afterActivityResult;
    private ChildInGroupInSchool selectedObject;
    private ArrayList<ChildInGroupInSchool> allObjects;

    @Inject
    public InfoPresenter(InfoContract.View view,
                         DataService dataService,
                         FragmentScreenSwitcher fragmentSwitcher,
                         ActivityScreenSwitcher activityScreenSwitcher,
                         ParentApplication appContext) {
        this.view = view;
        this.dataService = dataService;
        this.fragmentSwitcher = fragmentSwitcher;
        this.activityScreenSwitcher = activityScreenSwitcher;
        this.appContext = appContext;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        afterActivityResult = true;
        Bundle extras = data.getExtras();
        if (extras.containsKey(SelectChildInSchoolActivity.Screen.KEY_SELECTED_OBJECT)) {
            selectedObject = extras.getParcelable(SelectChildInSchoolActivity.Screen.KEY_SELECTED_OBJECT);
            fillNameField();
        }
    }

    private void fillNameField() {
        String childrenNames = getObjectText();
        if (!StringUtils.isBlank(childrenNames)) {
            view.setObjectName(childrenNames.toString());
        } else {
            view.setEmptyChildField();
        }

    }

    @NonNull
    private String getObjectText() {
        if (selectedObject == null) {
            return StringUtils.EMPTY;
        } else {
            return ChildInGroupInSchool.getObjectTitle(selectedObject);
        }
    }

    @Override
    public void firstStart() {
        view.startLoad();
        dataService.getProfile()
                .flatMap(new Func1<ParentProfile, Observable<List<ChildInGroupInSchool>>>() {
                    @Override public Observable<List<ChildInGroupInSchool>> call(ParentProfile parentProfile) {
                        List<ParentChild> childList = parentProfile.getChildList();
                        String parentEmail = parentProfile.getProfile().getEmail();
                        return dataService.fromParentChildren(childList, parentEmail);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObjects, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetErrorException) {
                        view.networkError();
                    }
                });
    }

    private Action1<List<ChildInGroupInSchool>> getObjects = childInGroupInSchools -> {
        InfoPresenter.this.view.stopLoad();
        if (childInGroupInSchools == null) {

            return;
        }
        allObjects = new ArrayList<>(childInGroupInSchools);
        if (allObjects != null || !allObjects.isEmpty()) {
            if (allObjects.size() == 1) {
                String objectName = ChildInGroupInSchool.getObjectTitle(allObjects.get(0));
//                InfoPresenter.this.view.setObjectName(objectName);
                InfoPresenter.this.view.oneChildMode(objectName);
                selectedObject = allObjects.get(0);
            } else if (allObjects.size() == 0) {
                InfoPresenter.this.view.emptyChildListMode();
            } else {
                InfoPresenter.this.view.manyChildrenMode();
            }
        }
    };

    @Override
    public void setRelativeStatus(String string) {
        status = string;
    }

    @Override
    public void setRelativeFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setRelativeLastName(String lastName) {
        this.lastName = lastName;
    }

    public static final String TIME_PICKER_TAG = "AddEventPresenter.TimePicker";

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
    public void goBack() {
        activityScreenSwitcher.goBack();
    }

    @Override
    public void validateFields() {
        if (selectedObject == null) {
            view.emptyChildIdError();
            return;
        }
        Map<ValidateType, String> needValidate = new HashMap<>();
        needValidate.put(ValidateType.NAME, firstName);
        needValidate.put(ValidateType.LAST_NAME, lastName);
        needValidate.put(ValidateType.STATUS, status);
        FieldsValidator.validate(needValidate)
                .subscribe(validateTypeStringMap -> {
                    checkTime();
                }, throwable -> {
                    ValidationThrowable validationThrowable = (ValidationThrowable) throwable;
                    Set<ValidateType> validateTypes = validationThrowable.keySet();
                    for (ValidateType validateType : validateTypes) {
                        if (validateType.equals(ValidateType.STATUS)) {
                            view.statusValidateError();
                        } else if (validateType.equals(ValidateType.NAME)) {
                            view.firstNameValidateError();
                        } else if (validateType.equals(ValidateType.LAST_NAME)) {
                            view.lastNameValidateError();
                        }
                    }
                });
    }

    private void checkTime() {
        if (StringUtils.isBlank(time)) {
            view.timeEmptyError();
        } else generateCode();
    }

    private void generateCode() {
        view.startLoad();
        subscription = new CompositeSubscription();
        long childId = selectedObject.getChild().getServerId();
        long groupId = selectedObject.getGroupId();
        int schoolId = selectedObject.getSchoolId();
        subscription.add(dataService.generateCode(childId, groupId, schoolId, status, firstName, lastName, time)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(code -> {
                    view.stopLoad();
                    openShareCodeScreen(code);

                }, throwable -> {
                    view.stopLoad();
                    if (throwable instanceof NetErrorException) {
                        view.networkError();
                    }
                }));
    }

    private void openShareCodeScreen(String code) {
        String shareHeaderFormat = appContext.getString(R.string.share_code_header_format);
        String childName = selectedObject.getChild().getFirstName();
        String schoolTitle = selectedObject.getSchoolTitle();
        String shareScreenHeader = String.format(shareHeaderFormat, status, firstName, lastName,
                childName, visibleTime);
        fragmentSwitcher.openWithClearStack(new ShareCodeFragment.Screen(code, shareScreenHeader, childName, schoolTitle));
    }

    private static final int REQUEST_CODE = 776;

    @Override
    public void selectChild() {
        if (this.allObjects.size() > 1) {
            if (selectedObject != null) {
                for (ChildInGroupInSchool object : allObjects) {
                    if (selectedObject.equals(object)) {
                        object.setSelected(true);
                    } else {
                        object.setSelected(false);
                    }
                }
            }
            activityScreenSwitcher.startForResult(new SelectChildInSchoolActivity.Screen(allObjects), REQUEST_CODE);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        time = requestDateFormat.format(calendar.getTime());
        visibleTime = viewDateFormat.format(calendar.getTime());
        this.view.setTime(visibleTime);

    }

    private static final String KEY_ALL_OBJECTS = "InfoPresenter.AllObjects";

    @Override
    public void onRestore(@NonNull Bundle savedInstanceState) {
//        if (afterActivityResult) {
//            selectedObject = savedInstanceState.getParcelable(SelectChildInSchoolActivity.Screen.KEY_SELECTED_OBJECT);
//            view.setObjectName(ChildInGroupInSchool.getObjectTitle(selectedObject));
//        }
        if (savedInstanceState.containsKey(KEY_ALL_OBJECTS)) {
            allObjects = savedInstanceState.getParcelableArrayList(KEY_ALL_OBJECTS);
        }
    }

    @Override
    public void onSave(@NonNull Bundle outState) {
        outState.putParcelableArrayList(KEY_ALL_OBJECTS, allObjects);
    }

}
