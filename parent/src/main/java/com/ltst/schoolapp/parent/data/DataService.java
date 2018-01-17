package com.ltst.schoolapp.parent.data;

import android.support.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerAuthenticationListener;
import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.preferences.StringPreference;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.DataBaseService;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.data.model.ChildInGroup;
import com.ltst.core.data.model.Event;
import com.ltst.core.data.model.Group;
import com.ltst.core.data.model.Member;
import com.ltst.core.data.model.Post;
import com.ltst.core.data.model.Profile;
import com.ltst.core.data.preferences.qualifiers.FirebaseDeviceToken;
import com.ltst.core.data.preferences.qualifiers.IsFirstStart;
import com.ltst.core.data.preferences.qualifiers.ServerToken;
import com.ltst.core.data.realm.model.ProfileScheme;
import com.ltst.core.data.request.GenerateCodeRequest;
import com.ltst.core.data.request.GenerateShortUrlsRequest;
import com.ltst.core.data.request.GetPostsOptions;
import com.ltst.core.data.request.LoginRequest;
import com.ltst.core.data.request.ProfileRequest;
import com.ltst.core.data.request.RecoveryPasswordRequest;
import com.ltst.core.data.request.ResetPasswordRequest;
import com.ltst.core.data.request.UpdateFirebaseTokenRequest;
import com.ltst.core.data.response.GenerateCodeResponse;
import com.ltst.core.data.response.ProfileResponse;
import com.ltst.core.data.uimodel.FeedType;
import com.ltst.core.ui.adapter.dialog.DialogItem;
import com.ltst.core.ui.calendar.CalendarView;
import com.ltst.schoolapp.parent.ParentScope;
import com.ltst.schoolapp.parent.data.model.ChildInGroupInSchool;
import com.ltst.schoolapp.parent.data.model.ParentChild;
import com.ltst.schoolapp.parent.data.model.ParentPost;
import com.ltst.schoolapp.parent.data.model.SchoolInfo;
import com.ltst.schoolapp.parent.layer.LayerSubscribeHelper;
import com.ltst.schoolapp.parent.ui.checkout.fragment.info.ParentProfile;
import com.ltst.schoolapp.parent.ui.family.add.request.RequestPresenter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

@ParentScope
public class DataService {

    private final ApiService apiService;
    private final DataBaseService dataBaseService;
    private final StringPreference fireBaseDeviceToken;
    private final StringPreference serverToken;
    private final BooleanPreference isFirstStart;
    private final LayerClient layerClient;
    private final LayerSubscribeHelper layerSubscribeHelper;

    @Inject
    public DataService(ApiService apiService,
                       DataBaseService dataBaseService,
                       @FirebaseDeviceToken StringPreference fireBaseDeviceToken,
                       @ServerToken StringPreference serverToken,
                       @IsFirstStart BooleanPreference isFirstStart,
                       LayerClient layerClient) {
        this.apiService = apiService;
        this.dataBaseService = dataBaseService;
        this.fireBaseDeviceToken = fireBaseDeviceToken;
        this.serverToken = serverToken;
        this.isFirstStart = isFirstStart;
        this.layerClient = layerClient;
        fireBaseDeviceToken.set(FirebaseInstanceId.getInstance().getToken());
        this.layerSubscribeHelper = new LayerSubscribeHelper(this.layerClient, this.apiService);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   ENTER  ////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Profile> registration(String email, String password, String code) {
        return apiService.registration(new ProfileRequest(email, password, fireBaseDeviceToken.get(), code))
                .doOnNext(profileResponse -> serverToken.set(profileResponse.getServerToken()))
                .flatMap(profileResponse -> layerConnect()
                        .flatMap(aBoolean -> Observable.just(profileResponse)
                                .map(ProfileScheme::fromResponce)
                                .flatMap(dataBaseService::createProfile)
                                .map(Profile::fromScheme)));
    }

    public Observable<Result<Void>> sendCodeAgain(String email) {
        return apiService.sendCodeAgain(email);
    }

    public Observable<Profile> login(String email, String password) {
        return apiService.login(new LoginRequest(email, password, fireBaseDeviceToken.get()))
                .doOnNext(response -> serverToken.set(response.getServerToken()))
                .flatMap(dataBaseService::updateProfile)
                .flatMap(profile -> layerConnect()
                        .flatMap(aBoolean -> Observable.just(profile)));
    }


    public Observable<String> getServerToken() {
        return Observable.just(serverToken.get());
    }

    public Observable<Void> resetPassword(String email) {
        return apiService.resetPassword(new ResetPasswordRequest(email));
    }

    public Observable<Profile> recoveryPassword(RecoveryPasswordRequest request) {

        request.setDevice(new LoginRequest.Device(fireBaseDeviceToken.get()));
        return apiService.recoveryPassword(request)
                .doOnNext(profileResponse -> serverToken.set(profileResponse.getServerToken()))
                .flatMap(dataBaseService::updateProfile);
    }

    private static final String LOGOUT_DEVICE_TYPE = "device_type";
    private static final String LOGOUT_DEVICE_TOKEN = "device_token";
    private static final String LOGOUT_ANDROID = "android";

    public Observable<Boolean> logout() {
        Map<String, String> options = new HashMap<>();
        options.put(LOGOUT_DEVICE_TYPE, LOGOUT_ANDROID);
        options.put(LOGOUT_DEVICE_TOKEN, fireBaseDeviceToken.get());
        return apiService.logout(options)
                .doOnNext(aVoid -> {
                    serverToken.delete();
                    isFirstStart.delete();
                })
                .flatMap(aVoid -> Observable.just(dataBaseService.dropData()))
                .flatMap(aBoolean -> layerDisconnect());

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   PROFILE ///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<ParentProfile> getProfile() {
        return Observable.concat(getProfileFromDataBase(), getProfileFromServer());
    }

    public Observable<ParentProfile> getProfileFromDataBase() {
        return dataBaseService.getProfile()
                .map(profile -> new ParentProfile(profile, null));
    }

    private Observable<ParentProfile> getProfileFromServer() {
        return Observable.zip(profileFromServer(), childrenFromServer(), ParentProfile::new);
    }

    private Observable<Profile> profileFromServer() {
        return apiService.getProfile()
                .flatMap(dataBaseService::updateProfile);

    }

    public Observable<Void> updateFireBaseToken(String newFireBaseToken) {
        return apiService.updateFireBaseToken(new UpdateFirebaseTokenRequest(newFireBaseToken));
    }

    public Observable<Profile> updateProfile(final ParentProfile profile, File avatar) {
        MultipartBody.Part avatarPart = null;
        Map<String, RequestBody> stringRequestBodyMap = getProfileBodyMap(profile);
        if (avatar != null && avatar.length() > 0) {
            RequestBody avatarFile = RequestBody.create(MediaType.parse("image/*"), avatar.getAbsoluteFile());
            avatarPart = MultipartBody.Part.createFormData("avatar", avatar.getName(), avatarFile);
        }

        return apiService.updateProfile(stringRequestBodyMap, avatarPart)
                .flatMap(new Func1<ProfileResponse, Observable<Profile>>() {
                    @Override
                    public Observable<Profile> call(ProfileResponse profileResponse) {
                        return dataBaseService.updateProfile(profileResponse);
                    }
                });
    }

    private Map<String, RequestBody> getProfileBodyMap(ParentProfile parentProfile) {
        Profile profile = parentProfile.getProfile();
        RequestBody name = createPartFromString(profile.getFirstName());
        RequestBody lastName = createPartFromString(profile.getLastName());
        RequestBody primaryNumber = createPartFromString(profile.getPhone());
        RequestBody secondaryNumber = null;
        if (profile.getAdditionalPhone() != null) {
            secondaryNumber = createPartFromString(profile.getAdditionalPhone());
        }
        Map<String, RequestBody> map = new HashMap<>();
        map.put("first_name", name);
        map.put("last_name", lastName);
        map.put("phone", primaryNumber);
        if (secondaryNumber != null) {
            map.put("additional_phone", secondaryNumber);
        }
        return map;
    }

    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   CHILDREN /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Observable<List<ParentChild>> childrenFromServer() {
        return apiService.getChildren()
                .flatMap(Observable::from)
                .flatMap(childResponse -> Observable.just(ParentChild.fromResponse(childResponse)))
                .toList();
    }

    public Observable<ParentChild> updateChild(Child child, long schoolId, File file) {
        long childServerId = child.getServerId();
        Map<String, RequestBody> childBodyMap = getChildBodyMap(schoolId, child);
        MultipartBody.Part avatarPart = null;
        if (file != null) {
            RequestBody avatarFile = RequestBody.create(MediaType.parse("image/*"), file.getAbsoluteFile());
            avatarPart = MultipartBody.Part.createFormData("avatar", file.getName(), avatarFile);
        }
        return apiService.updateChild(childServerId, childBodyMap, avatarPart)
                .map(ParentChild::fromResponse);
    }

    @NonNull
    private Map<String, RequestBody> getChildBodyMap(long schoolId, Child child) {
        Map<String, RequestBody> parts = new HashMap<>();

        RequestBody schoolIdBody = createPartFromString(String.valueOf(schoolId));
        parts.put("school_id", schoolIdBody);

        RequestBody name = createPartFromString(child.getFirstName());
        parts.put("first_name", name);

        RequestBody lastName = createPartFromString(child.getLastName());
        parts.put("last_name", lastName);

        RequestBody gender = createPartFromString(child.getGender());
        parts.put("gender", gender);

        if (!StringUtils.isBlank(child.getBirthDay())) {
            RequestBody birthday = createPartFromString(child.getBirthDay());
            parts.put("date_of_birth", birthday);

        }
        if (child.getBloodGroup() != null) {
            RequestBody bloodGroup = createPartFromString(child.getBloodGroup());
            parts.put("blood_group", bloodGroup);
        }

        if (child.getGenotype() != null) {
            RequestBody genotype = createPartFromString(child.getGenotype());
            parts.put("genotype", genotype);
        }

        if (child.getAllergies() != null) {
            RequestBody allergies = createPartFromString(child.getAllergies());
            parts.put("allergies", allergies);
        }

        if (child.getAdditional() != null) {
            RequestBody additional = createPartFromString(child.getAdditional());
            parts.put("information", additional);
        }
        return parts;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   POSTS /////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<List<Post>> getPosts(String date, Integer perPage, Long after,
                                           Integer before, FeedType feedType, long groupId,
                                           long childId) {
        Map<String, String> map = new GetPostsOptions(date, perPage, after, before, feedType,
                groupId, childId);
        return apiService.getPosts(map)
                .flatMap(Observable::from)
                .map(ParentPost::fromRestPost)
                .toList();

    }

    public Observable<List<Post>> getPostsForQuery(String date, Integer perPage, Long after,
                                                   Integer before, FeedType feedType, String query,
                                                   long groupId, long childId) {
        Map<String, String> map = new GetPostsOptions(date, perPage, after, before, feedType, query,
                groupId, childId);
        return apiService.getPostsForQuery(map)
                .flatMap(Observable::from)
                .map(ParentPost::fromRestPost)
                .toList();
    }

    public Observable<List<String>> generateShortUrls(Collection<String> resourceTokens) {
        return apiService.getShortUrls(new GenerateShortUrlsRequest(resourceTokens))
                .flatMap(Observable::from)
                .map(shortUrlResponse -> shortUrlResponse.shortUrl)
                .toList()
                .flatMap(Observable::just);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   CHILDREN IN GROUPS /////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<List<ChildInGroup>> getCachedChildrenInGroups() {
        return dataBaseService.getCachedChildrenInGroups();
    }

    public Observable<List<ChildInGroup>> updateChildrenInGroups() {
        return apiService.getChildrenInGroups()
                .map(ChildInGroup::fromResponse)
                .doOnNext(dataBaseService::updateChildrenInGroups);

    }

    public Observable<ChildInGroup> setSelectedChildInGroup(long childId, long groupId) {
        return dataBaseService.setSelectedChildInGroup(childId, groupId);
    }

    public Observable<List<ChildInGroupInSchool>> fromParentChildren(List<ParentChild> parentChildren,
                                                                     String parentEmail) {
        if (parentChildren == null) {
            ArrayList<ChildInGroupInSchool> childInGroupInSchools = new ArrayList<>();
            return Observable.just(childInGroupInSchools);
        }
        List<ChildInGroupInSchool> result = new ArrayList<>();
        for (ParentChild parentChild : parentChildren) {
            Child child = parentChild.getChild();

            List<Member> family = child.getFamily();
            List<Member> invites = child.getInvites();
            List<Member> allMembers = new ArrayList<>(family.size() + invites.size());
            allMembers.addAll(family);
            allMembers.addAll(invites);
            for (Member member : allMembers) {
                if (member.getEmail().equals(parentEmail) && member.getAccessLevel().equals(Member.FULL_ACCESS)) {
                    List<Group> groupsOfChild = child.getGroups();
                    if (groupsOfChild != null) {
                        for (Group group : groupsOfChild) {
                            ChildInGroupInSchool object = new ChildInGroupInSchool();
                            object.setChild(child);
                            object.setSchoolId(parentChild.getSchoolId());
                            object.setSchoolTitle(parentChild.getSchoolTitle());
                            object.setGroupId(group.getId());
                            object.setGroupTitle(group.getTitle());
                            object.setAvatarUrl(group.getAvatarUrl());
                            object.setSelected(false);
                            result.add(object);

                        }
                    }
                }
            }


        }
        return Observable.just(result);
//                                    childInGroupInSchool.setEventTitle(childInGroup.getEventTitle());
//                                    return childInGroupInSchool;
//                                })).toList();
    }

    public Observable<ChildInGroup> getChildInGroupByGroupId(long groupId) {
        return dataBaseService.getChildInGroupByGroupId(groupId);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   CHECKS /////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static final String GET_STATES_START_DATE = "beginning_of_range";
    private static final String GET_STATES_END_DATE = "end_of_range";

    public Observable<List<ChildCheck>> getStates(String startDate, String endDate) {
        Map<String, String> options = new HashMap<>();
        options.put(GET_STATES_START_DATE, startDate);
        options.put(GET_STATES_END_DATE, endDate);
        return apiService.getStates(options)
                .flatMap(Observable::from)
                .map(ChildCheck::fromResponse)
                .toList();
    }

    public Observable<String> generateCode(long childId, long groupId, long schoolId, String status, String firstName,
                                           String lastName, String time) {
        GenerateCodeRequest request = new GenerateCodeRequest(groupId, schoolId, status, firstName, lastName, time);
        return apiService.generateCheckoutCode(childId, request)
                .map(GenerateCodeResponse::getCode);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   EVENTS ////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<List<Event>> getEvents(Calendar from, Calendar to) {

        String dateFrom = dateFromCalendar(from);
        String dateTo = dateFromCalendar(to);
        return apiService.getEvents(dateFrom, dateTo)
                .flatMap(Observable::from)
                .flatMap(response -> {
                    Event event = Event.fromResponse(response);
                    return Observable.just(event);
                })
                .toList();
    }

    public Observable<List<CalendarView.PointDate>> getPoints(Calendar from, Calendar to) {

        String dateFrom = dateFromCalendar(from);
        String dateTo = dateFromCalendar(to);
        return apiService.getEventsDates(dateFrom, dateTo)
                .flatMap(datesResponse -> Observable.just(datesResponse.dates))
                .flatMap(points -> {
                    List<CalendarView.PointDate> result =
                            new ArrayList<CalendarView.PointDate>(points.size());
                    for (String point : points) {
                        Calendar pointCalendar = Calendar.getInstance();
                        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            pointCalendar.setTime(serverFormat.parse(point));
                            CalendarView.PointDate pointDate = new CalendarView.PointDate();
                            pointDate.year = pointCalendar.get(Calendar.YEAR);
                            pointDate.month = pointCalendar.get(Calendar.MONTH);
                            pointDate.day = pointCalendar.get(Calendar.DAY_OF_MONTH);
                            result.add(pointDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return Observable.just(result);
                });

    }

    private static final String SERVER_DATE_FORMAT = "%d-%d-%d";

    private String dateFromCalendar(Calendar calendar) {

        int year = calendar.get(Calendar.YEAR);
        int month = getRubyMonth(calendar);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String result = String.format(SERVER_DATE_FORMAT, year, month, day);
        return result;
    }

    private int getRubyMonth(Calendar calendar) {

        int month = calendar.get(Calendar.MONTH) + 1; //RUBY ON RAILS format of month
        if (month == 13) {
            month = 1;
        }
        return month;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   INVITES ////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Member> findByEmail(String email) {
        return apiService.findByEmail(email)
                .map(Member::fromRestMember);
    }

    public Observable<Member> inviteMember(long childId, long schoolId, final Member member,
                                           @RequestPresenter.ScreenStatus int screenStatus) {
        Map<String, RequestBody> inviteMemberMap = createInviteMemberMap(childId, schoolId, member, screenStatus);
        MultipartBody.Part memberAvatarPartSync = getMemberAvatarPart(member);
        return apiService.inviteMember(inviteMemberMap, memberAvatarPartSync)
                .map(Member::fromPosition);
    }

    @NonNull
    private Map<String, RequestBody> createInviteMemberMap(long childId, long schoolId, Member member, int screenStatus) {
        Map<String, RequestBody> parts = new HashMap<>();
        RequestBody position = createPartFromString(member.getPosition());
        parts.put("position", position);
        RequestBody child = createPartFromString(String.valueOf(childId));
        parts.put("child_id", child);
        RequestBody school = createPartFromString(String.valueOf(schoolId));
        parts.put("school_id", school);
        RequestBody email = createPartFromString(member.getEmail());
        parts.put("email", email);
        if (screenStatus == RequestPresenter.NEW_MEMBER) {
            RequestBody firstName = createPartFromString(member.getFirstName());
            parts.put("first_name", firstName);
            RequestBody lastName = createPartFromString(member.getLastName());
            parts.put("last_name", lastName);
            RequestBody phone = createPartFromString(member.getPhone());
            parts.put("phone", phone);
            String secondPhone = member.getSecondPhone();
            if (secondPhone != null) {
                RequestBody secondPhoneBody = createPartFromString(secondPhone);
                parts.put("additional_phone", secondPhoneBody);
            }
        }
        return parts;
    }

    private MultipartBody.Part getMemberAvatarPart(Member member) {
        MultipartBody.Part avatarPart = null;
        String avatarUrl = member.getAvatarUrl();
        if (avatarUrl != null) {
            File avatarFile = new File(avatarUrl);
            if (avatarFile.exists()) {
                RequestBody avatarBody =
                        RequestBody.create(MediaType.parse("image/*"), avatarFile.getAbsoluteFile());
                avatarPart =
                        MultipartBody.Part.createFormData("avatar", avatarFile.getName(), avatarBody);
            }

        }
        return avatarPart;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   REPORTS ////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<List<Post>> getCheckoutReport(long reportId) {
        return apiService.getCheckoutReport(reportId)
                .flatMap(Observable::from)
                .map(ParentPost::fromRestPost)
                .toList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   LAYER /////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Boolean> layerConnect() {
        layerClient.registerAuthenticationListener(layerSubscribeHelper);
        layerClient.registerConnectionListener(layerSubscribeHelper);
        return Observable.create(layerSubscribeHelper);
    }

    private Observable<Boolean> layerDisconnect() {
        layerClient.unregisterAuthenticationListener(layerSubscribeHelper);
        layerClient.unregisterConnectionListener(layerSubscribeHelper);
        Observable.OnSubscribe<Boolean> subscribe = subscriber -> {
            layerClient.registerAuthenticationListener(new LayerAuthenticationListener() {
                @Override
                public void onAuthenticated(LayerClient layerClient1, String s) {

                }

                @Override
                public void onDeauthenticated(LayerClient layerClient1) {
                    layerClient.unregisterAuthenticationListener(this);
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                }

                @Override
                public void onAuthenticationChallenge(LayerClient layerClient1, String s) {

                }

                @Override
                public void onAuthenticationError(LayerClient layerClient1, LayerException e) {

                }
            });
            layerClient.deauthenticate(LayerClient.DeauthenticationAction.CLEAR_LOCAL_DATA);
        };
        return Observable.create(subscribe);
    }

    public Observable<List<DialogItem>> getDialogItems() {
        return apiService.getTeachers()
                .flatMap(Observable::from)
                .map(teacherResponse -> DialogItem.fromTeacherResponse(teacherResponse, false))
                .toList();
    }

    public Observable<Boolean> onceOlnydeauthenticate() {

        class DeauthincationSubscriber implements Observable.OnSubscribe<Boolean>, LayerAuthenticationListener {

            private Subscriber subscriber;
            private LayerClient layerClient;

            public DeauthincationSubscriber(LayerClient layerClient) {
                this.layerClient = layerClient;
            }

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                this.subscriber = subscriber;
                this.layerClient.registerAuthenticationListener(this);
                this.layerClient.deauthenticate(LayerClient.DeauthenticationAction.KEEP_LOCAL_DATA);
            }


            @Override
            public void onAuthenticated(LayerClient layerClient, String s) {

            }

            @Override
            public void onDeauthenticated(LayerClient layerClient) {
                this.subscriber.onNext(true);
                this.layerClient.unregisterAuthenticationListener(this);
            }

            @Override
            public void onAuthenticationChallenge(LayerClient layerClient, String s) {

            }

            @Override
            public void onAuthenticationError(LayerClient layerClient, LayerException e) {

            }


        }
        layerClient.deauthenticate();
        return Observable.create(new DeauthincationSubscriber(layerClient));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   SCHOOL INFO ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<SchoolInfo> getSchoolInfo() {
        return apiService.getSchoolInfo()
                .map(SchoolInfo::fromResponse);
    }
}
