package com.ltst.schoolapp.teacher.data;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.google.firebase.iid.FirebaseInstanceId;
import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerAuthenticationListener;
import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.preferences.StringPreference;
import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.DataBaseService;
import com.ltst.core.data.model.Asset;
import com.ltst.core.data.model.AssetType;
import com.ltst.core.data.model.Child;
import com.ltst.core.data.model.ChildActivity;
import com.ltst.core.data.model.ChildCheck;
import com.ltst.core.data.model.Event;
import com.ltst.core.data.model.Group;
import com.ltst.core.data.model.Member;
import com.ltst.core.data.model.Post;
import com.ltst.core.data.model.Profile;
import com.ltst.core.data.preferences.qualifiers.FirebaseDeviceToken;
import com.ltst.core.data.preferences.qualifiers.IsAdmin;
import com.ltst.core.data.preferences.qualifiers.ServerToken;
import com.ltst.core.data.realm.model.ChildScheme;
import com.ltst.core.data.realm.model.ProfileScheme;
import com.ltst.core.data.request.AddEventRequest;
import com.ltst.core.data.request.AddPostRequest;
import com.ltst.core.data.request.CheckRequest;
import com.ltst.core.data.request.GenerateShortUrlsRequest;
import com.ltst.core.data.request.GetPostsOptions;
import com.ltst.core.data.request.GroupUpdateRequest;
import com.ltst.core.data.request.LoginRequest;
import com.ltst.core.data.request.PasswordUpdateRequest;
import com.ltst.core.data.request.ProfileRequest;
import com.ltst.core.data.request.ProfileUpdateRequest;
import com.ltst.core.data.request.RecoveryPasswordRequest;
import com.ltst.core.data.request.ResetPasswordRequest;
import com.ltst.core.data.request.UpdateFirebaseTokenRequest;
import com.ltst.core.data.response.ChildResponse;
import com.ltst.core.data.response.GroupResponse;
import com.ltst.core.data.response.ProfileResponse;
import com.ltst.core.data.uimodel.FeedType;
import com.ltst.core.data.uimodel.SelectPersonModel;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.net.exceptions.NotFoundException;
import com.ltst.core.ui.calendar.CalendarView;
import com.ltst.core.util.TokenExceptionHandler;
import com.ltst.schoolapp.TeacherScope;
import com.ltst.schoolapp.teacher.layer.LayerSubscribeHelper;
import com.ltst.schoolapp.teacher.ui.child.addmember.AddMemberScope;
import com.ltst.schoolapp.teacher.ui.child.family.status.ChangeStatusMemberWrapper;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static okhttp3.MultipartBody.Part.createFormData;

@TeacherScope
public class DataService {

    private final DataBaseService dataBaseService;
    private final ApiService apiService;
    private final StringPreference deviceToken;
    private final StringPreference serverToken;
    private final BooleanPreference isAdmin;
    private final TokenExceptionHandler tokenExceptionHandler;
    private final LayerClient layerClient;
    private final LayerSubscribeHelper layerHelper;

    private static final MediaType IMAGE_MEDIA_TYPE = MediaType.parse("image/*");

    @Inject
    public DataService(DataBaseService dataBaseService,
                       LayerClient layerClient,
                       ApiService apiService,
                       @FirebaseDeviceToken StringPreference deviceToken,
                       @ServerToken StringPreference serverToken,
                       @IsAdmin BooleanPreference isAdmin, TokenExceptionHandler tokenExceptionHandler
    ) {

        this.dataBaseService = dataBaseService;
        this.apiService = apiService;
        this.layerClient = layerClient;
        this.deviceToken = deviceToken;
        this.serverToken = serverToken;
        this.isAdmin = isAdmin;
        this.tokenExceptionHandler = tokenExceptionHandler;
        this.layerHelper = new LayerSubscribeHelper(this.layerClient, this.apiService);
    }

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applySchedulers() {

        return (Observable.Transformer<T, T>) schedulersTransformer;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   SESSION  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Profile> login(String email, String password) {

        return apiService.login(new LoginRequest(email, password, deviceToken.get()))
                .doOnNext(profileResponse -> {
                    serverToken.set(profileResponse.getServerToken());
                    isAdmin.set(profileResponse.isAdmin());
                })
                .flatMap(dataBaseService::updateProfile)
                .flatMap(profile -> updateGroups())
                .flatMap(new Func1<Group, Observable<List<Child>>>() {
                    @Override
                    public Observable<List<Child>> call(Group group) {
                        if (group.getId() == 0) { //fake group
                            return Observable.just(Collections.emptyList());
                        }
                        return getChildren();
                    }
                })
                .flatMap(children -> dataBaseService.getProfile())
                .doOnNext(profile -> tokenExceptionHandler.setEnabled(true));

    }

    private static final String LOGOUT_DEVICE_TYPE = "device_type";
    private static final String LOGOUT_DEVICE_TOKEN = "device_token";
    private static final String LOGOUT_ANDROID = "android";

    public Observable<Boolean> logout() {

        Map<String, String> options = new HashMap<>();
        options.put(LOGOUT_DEVICE_TYPE, LOGOUT_ANDROID);
        options.put(LOGOUT_DEVICE_TOKEN, deviceToken.get());
        return apiService.logout(options)
                .map(Result::isError)
                .map(dataBaseService::dropData)
                .map(serverToken::deleteIfNeeded)
                .doOnNext(aBoolean -> tokenExceptionHandler.setEnabled(false))
                .doOnNext(aBoolean -> {
                    layerDisconnect();
//                    FirebaseInstanceId.getInstance().deleteInstanceId();
                });
    }


    public Observable<String> getServerToken() {

        return Observable.just(serverToken.get());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   PROFILE  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Profile> getProfile() {

        return Observable.concat(getProfileFromDb(), getProfileFromApi());
    }

    public Observable<Profile> getProfileFromApi() {

        return apiService.getProfile()
                .flatMap(dataBaseService::updateProfile)
                .onErrorResumeNext(dataBaseService.getProfile());
    }

    public Observable<Profile> getProfileFromDb() {

        return dataBaseService.getProfile();
    }

    public Observable<Profile> registration(String email, String password) {

        return apiService.registration(new ProfileRequest(email, password, deviceToken.get(), null))
                .doOnNext(profileResponse -> {
                    serverToken.set(profileResponse.getServerToken());
                    isAdmin.set(profileResponse.isAdmin());
                })
                .flatMap(profileResponse -> DataService.this.layerConnect()
                        .filter(aBoolean -> aBoolean)
                        .flatMap(aBoolean -> Observable.just(profileResponse)
                                .map(ProfileScheme::fromResponce)
                                .flatMap(dataBaseService::createProfile)
                                .map(Profile::fromScheme)));
    }

    public Observable<Profile> registrationByInvite(String email, String password, String code) {

        ProfileRequest request = new ProfileRequest(email, password, deviceToken.get(), code);
        return apiService.registrationByInvite(request)
                .doOnNext(profileResponse -> {
                    serverToken.set(profileResponse.getServerToken());
                    isAdmin.set(profileResponse.isAdmin());
                })
                .flatMap(profileResponse -> layerConnect()
                        .filter(aBoolean -> aBoolean)
                        .flatMap(aBoolean -> Observable.just(profileResponse)
                                .map(ProfileScheme::fromResponce)
                                .flatMap(dataBaseService::createProfile)
                                .flatMap(profileScheme -> updateGroups())
                                .flatMap(group -> getProfileFromDb())));

    }

    public Observable<Profile> updateProfile(final Profile profile, final String file) {

        return apiService.updateProfile(ProfileUpdateRequest.fromProfile(profile))
                .flatMap(profileResponse -> updateProfileAvatar(profileResponse, file))
                .flatMap(dataBaseService::updateProfile)
                .flatMap(profile1 -> updateGroups())
                .flatMap(group -> getProfileFromDb());
    }

    private Observable<ProfileResponse> updateProfileAvatar(ProfileResponse response,
                                                            final String filePath) {

        if (filePath == null) return Observable.just(response);
        return Observable.just(new File(filePath))
                .flatMap(this::getAvatarPartFromFile)
                .flatMap(apiService::updateProfileAvatar);
    }

    private Observable<MultipartBody.Part> getAvatarPartFromFile(File file) {

        return Observable.just(RequestBody.create(IMAGE_MEDIA_TYPE, file.getAbsoluteFile()))
                .map(requestBody -> createFormData("avatar", file.getName(), requestBody));
    }

    public Observable<Void> updateFireBaseProfile(String newToken) {
        return apiService.updateFireBaseToken(new UpdateFirebaseTokenRequest(newToken));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////   PASSWORD  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Profile> updatePassword(PasswordUpdateRequest request) {

        return apiService.updatePassword(request)
                .doOnNext(response -> serverToken.set(response.getServerToken()))
                .flatMap(dataBaseService::updateProfile);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////   GROUP   //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Group> getSelectedGroup() {
        return dataBaseService.getSelectedGroup();
    }

    public Observable<Group> getCachedGroupById(long groupId) {
        return dataBaseService.getCachedGroupById(groupId);
    }

    public Observable<Group> updateGroups() {
        return apiService.getGroups()
                .map(new Func1<List<GroupResponse>, List<Group>>() {
                    @Override
                    public List<Group> call(List<GroupResponse> responses) {
                        return Group.fromGroupsResposne(responses);
                    }
                })
                .flatMap(new Func1<List<Group>, Observable<Group>>() {
                    @Override
                    public Observable<Group> call(List<Group> groups) {
                        if (groups.size() < 1) {
                            return Observable.just(Group.fakeGroup());
                        }
                        return dataBaseService.updateGroups(groups);
                    }
                });
    }

    public Observable<List<Group>> getGroups() {
        return apiService.getGroups()
                .map(Group::fromGroupsResposne)
                .flatMap(dataBaseService::groupUpdate)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends List<Group>>>() {
                    @Override
                    public Observable<? extends List<Group>> call(Throwable throwable) {
                        if (throwable instanceof NetErrorException) {
                            return dataBaseService.getCachedGroups();
                        }
                        return Observable.error(throwable);
                    }
                });
    }

    public Observable<List<Group>> getCachedGroups() {
        return dataBaseService.getCachedGroups();
    }

    public Observable<Group> updateGroup(long groupId, final String title, final String file) {
        return apiService.updateGroup(groupId, GroupUpdateRequest.fromTitle(title))
                .flatMap(new Func1<GroupResponse, Observable<? extends GroupResponse>>() {
                    @Override
                    public Observable<? extends GroupResponse> call(GroupResponse groupResponse) {
                        return updateGroupAvatar(groupId, groupResponse, file);
                    }
                })
                .map(new Func1<GroupResponse, Group>() {
                    @Override
                    public Group call(GroupResponse groupResponse) {
                        return Group.fromGroupResponse(groupResponse);
                    }
                })
                .flatMap(new Func1<Group, Observable<? extends Group>>() {
                    @Override
                    public Observable<? extends Group> call(Group group) {
                        return dataBaseService.updateGroup(group);
                    }
                });
    }

    private Observable<GroupResponse> updateGroupAvatar(long groupId, GroupResponse response,
                                                        final String filePath) {

        if (filePath == null) return Observable.just(response);
        return Observable.just(new File(filePath))
                .flatMap(this::getAvatarPartFromFile)
                .flatMap(part -> apiService.updateGroupAvatar(groupId, part));
    }

    public void changeSelectedGroup(long groupId) {
        dataBaseService.changeSelectedGroup(groupId);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////   POST  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<List<Post>> getPosts(String date, Integer perPage, Long after,
                                           Integer before, FeedType feedType) {

        Map<String, String> map = new GetPostsOptions(date, perPage, after, before, feedType, null, null);
        return getSelectedGroup()
                .flatMap(group -> apiService.getPosts(group.getId(), map)
                        .flatMap(Observable::from)
                        .map(restPost -> Post.fromRestPost(restPost, group))
                        .toList());
    }

    public Observable<List<Post>> getPostsForQuery(String date, Integer perPage, Long after,
                                                   Integer before, FeedType feedType, String query) {

        Map<String, String> map = new GetPostsOptions(date, perPage, after, before, feedType, query, null, null);
        return getSelectedGroup()
                .flatMap(group -> apiService.getPostsForQuery(group.getId(), map)
                        .flatMap(Observable::from)
                        .map(restPost -> Post.fromRestPost(restPost, group))
                        .toList());
    }

    public Observable<Post> addPost(long groupId, ChildActivity childActivity,
                                    List<SelectPersonModel> children,
                                    String content,
                                    List<String> filePaths) {

        return Observable.from(filePaths)
                .flatMap(filePath -> addAsset(AssetType.IMAGE, filePath))
                .toList()
                .map(assets -> AddPostRequest.create(childActivity, children, content, assets))
                .flatMap(addPostRequest -> apiService.addPost(groupId, addPostRequest))
                .map(Post::fromRestPost);
    }

    public Observable<List<String>> generateShortUrls(Collection<String> resourceTokens) {
        return apiService.getShortUrls(new GenerateShortUrlsRequest(resourceTokens))
                .flatMap(Observable::from)
                .map(shortUrlResponse -> shortUrlResponse.shortUrl)
                .toList()
                .flatMap(Observable::just);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////   SELECT CHILD  ///////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<List<SelectPersonModel>> getSelectChildData(long groupId, boolean withGroup) {

        return Observable.concat(
                getSelectChildDataFromDb(groupId, withGroup),
                getSelectChildDataFromApi(groupId, withGroup))
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof NetErrorException) {
                        return getSelectChildDataFromDb(groupId, withGroup);
                    }
                    return Observable.error(throwable);

                });
    }

    public Observable<List<SelectPersonModel>> getSelectChildDataFromDb(boolean withGroup) {

        return getSelectedGroup().flatMap(group -> dataBaseService.getChildren(group.getId()))
//                dataBaseService.getChildren(groupId)
                .map(SelectPersonModel::fromChildList)
                .flatMap(models -> {
                    if (withGroup) {
                        return getSelectChildDataGroup(models);
                    } else {
                        return Observable.just(models);
                    }
                });
    }

    public Observable<List<SelectPersonModel>> getSelectChildDataFromDb(long groupId, boolean withGroup) {

        return dataBaseService.getChildren(groupId)
//                dataBaseService.getChildren(groupId)
                .map(SelectPersonModel::fromChildList)
                .flatMap(models -> {
                    if (withGroup) {
                        return getSelectChildDataGroup(groupId, models);
                    } else {
                        return Observable.just(models);
                    }
                });
    }

    private Observable<List<SelectPersonModel>> getSelectChildDataFromApi(long groupId, boolean withGroup) {

        return dataBaseService.getCachedGroupById(groupId)
                .flatMap(new Func1<Group, Observable<List<Child>>>() {
                    @Override
                    public Observable<List<Child>> call(Group group) {
                        return getChildrenFromServer(group.getId());
                    }
                })
//                getChildrenFromServer()
                .map(SelectPersonModel::fromChildList)
                .flatMap(models -> {
                    if (withGroup) {
                        return getSelectChildDataGroup(groupId, models);
                    } else {
                        return Observable.just(models);
                    }
                });
    }

    private Observable<List<SelectPersonModel>> getSelectChildDataGroup(
            List<SelectPersonModel> models) {

        return getSelectedGroup()
                .map(SelectPersonModel::fromGroup)
                .doOnNext(group -> {
                    models.add(SelectPersonModel.GROUP_POSITION, group);
                })
                .map(group -> models);
    }

    private Observable<List<SelectPersonModel>> getSelectChildDataGroup(long groupId,
                                                                        List<SelectPersonModel> models) {

        return getCachedGroupById(groupId)
                .map(SelectPersonModel::fromGroup)
                .doOnNext(group -> {
                    models.add(SelectPersonModel.GROUP_POSITION, group);
                })
                .map(group -> models);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   ACTIVITY  /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<List<ChildActivity>> getChildActivities() {

        return apiService.getChildActivities()
                .flatMap(Observable::from)
                .map(ChildActivity::fromRest)
                .toList();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   CHILDREN  /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Child> createChild(final Child child, final File file) {

        final int id = DataBaseService.getFreeChildId();
        child.setId(id);
        if (file != null) {
            child.setAvatarUrl(file.getAbsolutePath());
        }
        return dataBaseService.insertChild(child)
                .flatMap(child1 -> {
                    MultipartBody.Part avatarPart = null;
                    if (file != null) {
                        RequestBody avatarFile = RequestBody.create(MediaType.parse("image/*"), file.getAbsoluteFile());
                        avatarPart = MultipartBody.Part.createFormData("avatar", file.getName(), avatarFile);
                    }
                    Map<String, RequestBody> parts = getChildBodyMap(child1);
                    return apiService.createChild(parts, child1.getGroupIds(), avatarPart);
                })
                .map(Child::fromResponse)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof NetErrorException) {
                        return Observable.just(child);
                    } else return Observable.error(throwable);
                })
                .doOnNext(child1 -> child1.setId(id))
                .flatMap(dataBaseService::updateWithInnerId);
    }

    @NonNull
    private Map<String, RequestBody> getChildBodyMap(Child child1) {

        RequestBody name = createPartFromString(child1.getFirstName());
        RequestBody lastName = createPartFromString(child1.getLastName());
        RequestBody gender = createPartFromString(child1.getGender());
        RequestBody birthday = createPartFromString(child1.getBirthDay());
        RequestBody bloodGroup = createPartFromString(child1.getBloodGroup());
        RequestBody genotype = createPartFromString(child1.getGenotype());
        RequestBody allergies = createPartFromString(child1.getAllergies());
        RequestBody additional = createPartFromString(child1.getAdditional());
        Map<String, RequestBody> parts = new HashMap<>();
        parts.put("first_name", name);
        parts.put("last_name", lastName);
        parts.put("gender", gender);
        parts.put("date_of_birth", birthday);
        parts.put("blood_group", bloodGroup);
        parts.put("genotype", genotype);
        parts.put("allergies", allergies);
        parts.put("information", additional);
        return parts;
    }

    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {

        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }

    public Observable<List<Child>> getChildren() {

        return dataBaseService.getNotSyncChildren()
                .flatMap(children -> {
                    if (children.size() == 0) {
                        return dataBaseService.getSelectedGroup().flatMap(new Func1<Group, Observable<List<Child>>>() {
                            @Override
                            public Observable<List<Child>> call(Group group) {
                                return getChildrenFromServer(group.getId());
                            }
                        });

                    } else return dataBaseService.getSelectedGroup()
                            .flatMap(new Func1<Group, Observable<List<Child>>>() {
                                @Override
                                public Observable<List<Child>> call(Group group) {
                                    return sincAllUnsincChildren(group.getId(), children);
                                }
                            });
                })
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof NetErrorException) {
                        return getSelectedGroup().flatMap(group -> dataBaseService.getChildren(group.getId()));
                    }
                    return Observable.error(throwable);
                });
    }


    private Observable<List<Child>> sincAllUnsincChildren(long groupId, List<Child> children) {

        return Observable.from(children)
                .flatMap(this::syncChildWithServer)
                .toList()
                .flatMap(children1 -> getChildrenFromServer(groupId));

    }

    public Observable<List<Child>> getChildrenFromServer(long groupId) {

        return apiService.getChildren(groupId)
                .flatMap(Observable::from)
                .map(Child::fromResponse)
                .toList()
                .flatMap(children -> {
                    dataBaseService.deleteUnsynchronizedChildren(groupId, children);
                    return Observable.from(children);
                })
                .flatMap(dataBaseService::updateWithServerId)
                .toList()
                .flatMap(children -> dataBaseService.getChildren(groupId));
    }

    public Observable<List<Child>> getChildrenFromDataBase(long groupId) {
        return dataBaseService.getChildren(groupId);
    }

    public Observable<List<Child>> getAllChildrenFromDataBase() {
        return dataBaseService.getAllChildren();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   CHILD  ///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Child> getChildById(int id) {

        return dataBaseService.getChildById(id)
                .flatMap(this::syncChildWithServer)
                .onErrorResumeNext(throwable -> {
                    return dataBaseService.getChildById(id);
                });
    }

    public Observable<Child> getChildByServerId(long serverId) {
        return apiService.getChild(serverId)
                .flatMap(childResponse -> dataBaseService.updateWithServerId(Child.fromResponse(childResponse)));
    }


    public Observable<List<Post>> getPostsOfChild(String date, Integer perPage, Long after,
                                                  Integer before, FeedType feedType) {
// TODO: 02.12.16 (alexeenkoff) temp feed list, must be post of one child
        Map<String, String> map = new GetPostsOptions(date, perPage, after, before, feedType, null, null);
        return getSelectedGroup()
                .flatMap(group -> apiService.getPosts(group.getId(), map)
                        .flatMap(Observable::from)
                        .map(restPost -> Post.fromRestPost(restPost, group))
                        .toList());
    }


    private Observable<Child> createChildOnServer(Child child) {

        return dataBaseService.getChildById(child.getId())
                .flatMap(child1 -> {
                    Map<String, RequestBody> stringRequestBodyMap
                            = getChildBodyMap(child1);
                    MultipartBody.Part avatarPart = null;
                    String avatarUrl = child1.getAvatarUrl();
                    if (!StringUtils.isBlank(avatarUrl)) {
                        try {
                            File avatarFile = new File(avatarUrl);
                            if (avatarFile.exists()) {
                                RequestBody avatarBody =
                                        RequestBody.create(MediaType.parse("image/*"), avatarFile.getAbsoluteFile());
                                avatarPart =
                                        MultipartBody.Part.createFormData("avatar", avatarFile.getName(), avatarBody);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return apiService.createChild(stringRequestBodyMap, child1.getGroupIds(), avatarPart)
                            .flatMap(childResponse -> {
                                Child child2 = Child.fromResponse(childResponse);
                                child2.setId(child.getId());
                                child2.setInviteMembers(child.getInvites());
                                return dataBaseService.updateWithInnerId(child2);
                            });
                }).flatMap(new Func1<Child, Observable<Child>>() {
                    @Override
                    public Observable<Child> call(Child child) {

                        return syncChildWithServer(child);
                    }
                });
    }

    public Observable<Child> editChild(Child child, File file) {

        long serverId = child.getServerId();
        Map<String, RequestBody> stringRequestBodyMap = getChildBodyMap(child);
        MultipartBody.Part avatarPart = null;
        if (file != null) {
            RequestBody avatarFile = RequestBody.create(MediaType.parse("image/*"), file.getAbsoluteFile());
            avatarPart = MultipartBody.Part.createFormData("avatar", file.getName(), avatarFile);
        }
        final MultipartBody.Part finalAvatarPart = avatarPart;

        List<Long> groupIds = child.getGroupIds();
        if (groupIds == null) {
            groupIds = new ArrayList<>();
        }
        return apiService.updateChild(serverId, stringRequestBodyMap, groupIds, finalAvatarPart)
                .map((childResponse) -> {
                    Child child1 = Child.fromResponse(childResponse);
                    child1.setId(child.getId());
                    return child1;
                })
                .flatMap(dataBaseService::updateWithInnerId);
    }

    public Observable<Child> syncChildWithServer(Child child) {

        return dataBaseService.getNotSynchronizedMembers(child.getId())
                .flatMap(Observable::from)
                .flatMap(new Func1<Member, Observable<Member>>() {
                    @Override
                    public Observable<Member> call(Member member) {
                        // FIXME: 04.02.17 Screen mode in data service?
                        return inviteMember(child, member, AddMemberScope.SCREEN_MODE_EXIST);
                    }
                }).toList()
                .flatMap(members -> apiService.getChild(child.getServerId()))
                .flatMap(new Func1<ChildResponse, Observable<Child>>() {
                    @Override
                    public Observable<Child> call(ChildResponse childResponse) {

                        return Observable.just(Child.fromResponse(childResponse));
                    }
                }).flatMap(new Func1<Child, Observable<Child>>() {
                    @Override
                    public Observable<Child> call(Child child) {

                        return dataBaseService.updateWithServerId(child);
                    }
                }).onErrorResumeNext(throwable -> {
                    if (throwable instanceof NotFoundException) {
                        return createChildOnServer(child);
                    } else return Observable.error(throwable);

                });

    }

    public Observable<List<Child>> deleteChild(long serverId) {

        return getSelectedGroup()
                .flatMap(group -> apiService.deleteChild(group.getId(), serverId))
                .flatMap(voidResult -> {
                    if (voidResult.error() != null) {
                        return Observable.error(new NetworkErrorException());
                    } else {
                        return getSelectedGroup().flatMap(group -> dataBaseService.deleteChild(group.getId(), serverId));
//                                dataBaseService.deleteChild(group.getId(), serverId);
                    }
                });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   RESET PASSWORD  ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Result<Void>> resetPassword(String email) {

        return apiService.resetPassword(new ResetPasswordRequest(email));
    }

    public Observable<Profile> recoveryPassword(RecoveryPasswordRequest request) {
        request.setDevice(new LoginRequest.Device(deviceToken.get()));
        return apiService.recoveryPassword(request)
                .doOnNext(profileResponse -> serverToken.set(profileResponse.getServerToken()))
                .flatMap(profileResponse -> updateGroups()
                        .flatMap(group -> getChildren())
                        .flatMap(children -> dataBaseService.updateProfile(profileResponse)))
                ;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////   FAMILY MEMBER     ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Member> findMemberByEmail(String email) {
        return apiService.findByEmail(email)
                .map(Member::fromRestMember);
    }

    public Observable<Member> inviteMember(final Child child, Member newMember,
                                           @AddMemberScope.AddMEmberScreenMode int screenMode) {

        return dataBaseService.addInviteMember(child.getId(), newMember, false)
                .flatMap(member -> {
                    Map<String, RequestBody> inviteMemberMap = createInviteMemberMap(child.getServerId(), member, screenMode);
                    MultipartBody.Part memberAvatarPartSync = getMemberAvatarPartSync(member);
                    return apiService.inviteMember(inviteMemberMap, memberAvatarPartSync);
                })
                .map(position -> {
                    Member member = Member.fromPosition(position);
                    member.setId(newMember.getId());
                    return member;
                })
                .flatMap(fromServer -> dataBaseService.updateInviteMember(fromServer, true))
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof NetErrorException) {
                        return dataBaseService.updateInviteMember(newMember, false);
                    } else if (throwable instanceof NotFoundException) {
                        return DataService.this.createChildOnServer(child)
                                .flatMap(unused -> Observable.just(newMember));
                    } else {
                        return Observable.error(throwable);
                    }
                });
    }

    private MultipartBody.Part getMemberAvatarPartSync(Member member) {
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

    @NonNull
    private Map<String, RequestBody> createInviteMemberMap(long childId, Member member,
                                                           @AddMemberScope.AddMEmberScreenMode int screenMode) {
        Map<String, RequestBody> parts = new HashMap<>();
        RequestBody child = createPartFromString(String.valueOf(childId));
        RequestBody position = createPartFromString(member.getPosition());
        RequestBody email = createPartFromString(member.getEmail());
        parts.put("child_id", child);
        parts.put("position", position);
        parts.put("email", email);
        if (screenMode == AddMemberScope.SCREEN_MODE_CREATE) {
            RequestBody firstName = createPartFromString(member.getFirstName());
            RequestBody lastName = createPartFromString(member.getLastName());
            RequestBody phone = createPartFromString(member.getPhone());
            parts.put("first_name", firstName);
            parts.put("last_name", lastName);
            parts.put("phone", phone);
            String secondPhone = member.getSecondPhone();
            if (secondPhone != null) {
                RequestBody secondPhoneBody = createPartFromString(secondPhone);
                parts.put("additional_phone", secondPhoneBody);
            }
        }

        return parts;
    }

    public int getFreeFamilyMemberId() {

        return dataBaseService.getFreeMemberId();
    }

    public Observable<List<Member>> getMembersForChildrenIds(List<Long> childrenIds, boolean isCheckin) {

        return dataBaseService.getMembersForChildrenIds(childrenIds, isCheckin);
    }

    private Observable<Result<Void>> giveFullAccess(long memberId) {
        return apiService.giveFullAccess(memberId);
    }

    private Observable<Result<Void>> revokeFullAccess(long memberId) {
        return apiService.revokeFullAccess(memberId);
    }

    public Observable<Result<Void>> changeMemberStatus(ChangeStatusMemberWrapper memberWrapper) {
        String accessLevel = memberWrapper.getMember().getAccessLevel();
        Observable<Result<Void>> resultObservable = null;
        switch (accessLevel) {
            case Member.LIMITED_ACCESS:
                resultObservable = revokeFullAccess(memberWrapper.getMember().getFamilyId());
                break;
            case Member.FULL_ACCESS:
                resultObservable = giveFullAccess(memberWrapper.getMember().getFamilyId());
                break;
            default:
                break;
        }
        return resultObservable;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////    ASSETS   /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Observable<Asset> addAsset(AssetType type, String filePath) {

        return Observable.just(new File(filePath))
                .flatMap(this::getPartFromFileForAssets)
                .flatMap(part -> apiService.addAsset(type.toString(), part))
                .map(Asset::fromResponse);
    }

    public Observable<Asset> addImageAsset(String filePath) {

        return addAsset(AssetType.IMAGE, filePath);
    }

    public Observable<Asset> addDocAsset(String filePath) {

        return addAsset(AssetType.DOC, filePath);
    }

    public Observable<List<Asset>> addImageAssetList(List<String> filePaths) {

        return Observable.from(filePaths)
                .flatMap(this::addImageAsset)
                .toList();
    }

    public Observable<List<Asset>> addDocAssetList(List<String> filePaths) {

        return Observable.from(filePaths)
                .flatMap(this::addDocAsset)
                .toList();
    }

    private Observable<MultipartBody.Part> getPartFromFileForAssets(File file) {

        return Observable.just(RequestBody.create(MediaType.parse("*/*"), file.getAbsoluteFile()))
                .map(requestBody -> createFormData("file", file.getName(), requestBody));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////    EVENTS   /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<List<Event>> getEvents(Calendar from, Calendar to) {

        String dateFrom = dateFromCalendar(from);
        String dateTo = dateFromCalendar(to);
        return getSelectedGroup()
                .flatMap(group -> apiService.getEvents(group.getId(), dateFrom, dateTo))
                .flatMap(eventResponses -> Observable.from(eventResponses))
                .flatMap(response -> {
                    Event event = Event.fromResponse(response);
                    return Observable.just(event);
                })
                .toList();
    }

    public Observable<Event> addEvent(long groupId, Calendar calendar, String content,
                                      List<String> images, List<String> docs) {

        return Observable.zip(prepareRequest(calendar, content, images, docs),
                getCachedGroupById(groupId), Pair::create)
                .flatMap(pair -> apiService.addEvent(pair.second.getId(), pair.first))
                .map(Event::fromResponse);
    }

    private Observable<AddEventRequest> prepareRequest(Calendar calendar, String content,
                                                       List<String> images, List<String> docs) {
        return Observable.just(AddEventRequest.createRequest(calendar, content))
                .flatMap(request -> addImageAssetList(images)
                        .map(request::withImages))
                .flatMap(request -> addDocAssetList(docs)
                        .map(request::withDocs));
    }

    public Observable<List<CalendarView.PointDate>> getPoints(Calendar from, Calendar to) {

        String dateFrom = dateFromCalendar(from);
        String dateTo = dateFromCalendar(to);
        return getSelectedGroup().flatMap(group -> apiService.getEventsDates(group.getId(), dateFrom, dateTo))
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

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   CHECKS  /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static final String GET_STATES_START_DATE = "beginning_of_range";
    private static final String GET_STATES_END_DATE = "end_of_range";

    public Observable<List<ChildCheck>> getStates(String startDate, String endDate) {
        Map<String, String> options = new HashMap<>();
        options.put(GET_STATES_START_DATE, startDate);
        options.put(GET_STATES_END_DATE, endDate);
        return getSelectedGroup()
                .flatMap(group -> apiService.getStates(group.getId(), options)
                        .flatMap(Observable::from)
                        .flatMap(restChildCheck -> dataBaseService.putChildCheck(group.getId(), restChildCheck))
                        .toList());
    }

    public Observable<List<ChildCheck>> checkIn(Long groupId, List<Long> childIds, Member member) {

        CheckRequest request = CheckRequest.checkIn(childIds, member);
        return apiService.checkIn(groupId, request)
//                apiService.checkIn(request)
                .flatMap(Observable::from)
                .flatMap(restChildCheck -> dataBaseService.putChildCheck(groupId, restChildCheck))
                .toList()
                .onErrorResumeNext(throwable -> dataBaseService.putChildCheck(request));
    }

    public Observable<List<ChildCheck>> checkOut(Long groupId, List<Long> childIds, Member member, String code) {

        CheckRequest request = CheckRequest.checkOut(childIds, member, code);
        return apiService.checkOut(groupId, request)
//                apiService.checkOut(request)
                .flatMap(Observable::from)
                .flatMap(restChildCheck -> dataBaseService.putChildCheck(groupId, restChildCheck))
                .toList()
                .onErrorResumeNext(throwable -> {
                    if (code != null) {
                        return Observable.error(throwable);
                    }
                    return dataBaseService.putChildCheck(request);
                });
    }

    public Observable<List<ChildCheck>> syncChecksIfNeeded() {
        final long[] groupId = new long[1];
        return getSelectedGroup().flatMap(group -> {
            groupId[0] = group.getId();
            return dataBaseService.getAllNotSynced();
        })
//                dataBaseService.getAllNotSynced()
                .flatMap(Observable::from)
                .flatMap(childCheck -> Observable.just(childCheck.getChild())
                        .flatMap(this::syncChildWithServer)
                        .flatMap(unused -> Observable.just(childCheck)))
                .flatMap(check -> Observable.just(check.getChildStates())
                        .flatMap(Observable::from)
                        .flatMap(state -> Observable.just(CheckRequest.fromCheck(check, state))
                                .flatMap(request -> {
                                    if (request.isCheckIn()) {
                                        return apiService.checkIn(groupId[0], request);
                                    } else {
                                        return apiService.checkOut(groupId[0], request);
                                    }
                                })
                                .doOnNext(unused -> dataBaseService.deleteState(state)))
                        .flatMap(restChildChecks -> dataBaseService.putChildCheck(groupId[0], restChildChecks))
                        .toList()
                        .doOnNext(unused -> dataBaseService.deleteCheck(check))
                        .flatMap(unused -> Observable.just(check)))
                .toList();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////LAYER///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<Boolean> layerConnect() {
        layerClient.registerAuthenticationListener(layerHelper);
        layerClient.registerConnectionListener(layerHelper);
        return Observable.create(layerHelper);
    }

    private void layerDisconnect() {
        layerClient.unregisterAuthenticationListener(layerHelper);
        layerClient.unregisterConnectionListener(layerHelper);
        layerClient.disconnect();
        layerClient.deauthenticate(LayerClient.DeauthenticationAction.CLEAR_LOCAL_DATA);
    }

    public Observable<Boolean> onceOnlyDeauthenticateFromLayer() {

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

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static Observable.Transformer<Object, Object> schedulersTransformer =
            listObservable -> listObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

    public void enableTokenExceptionHandler() {
        tokenExceptionHandler.setEnabled(true);
    }
}



