package com.ltst.schoolapp.teacher.data;

import com.ltst.core.data.request.AddEventRequest;
import com.ltst.core.data.request.AddPostRequest;
import com.ltst.core.data.request.CheckRequest;
import com.ltst.core.data.request.GenerateShortUrlsRequest;
import com.ltst.core.data.request.GroupUpdateRequest;
import com.ltst.core.data.request.LoginRequest;
import com.ltst.core.data.request.PasswordUpdateRequest;
import com.ltst.core.data.request.ProfileRequest;
import com.ltst.core.data.request.ProfileUpdateRequest;
import com.ltst.core.data.request.RecoveryPasswordRequest;
import com.ltst.core.data.request.ResetPasswordRequest;
import com.ltst.core.data.request.UpdateFirebaseTokenRequest;
import com.ltst.core.data.response.AssetResponse;
import com.ltst.core.data.response.ChildResponse;
import com.ltst.core.data.response.DatesResponse;
import com.ltst.core.data.response.EventResponse;
import com.ltst.core.data.response.GetLayerIdentityTokenResponse;
import com.ltst.core.data.response.GroupResponse;
import com.ltst.core.data.response.ProfileResponse;
import com.ltst.core.data.response.ShortUrlResponse;
import com.ltst.core.data.rest.model.RestChildActivity;
import com.ltst.core.data.rest.model.RestChildCheck;
import com.ltst.core.data.rest.model.RestFamilyMember;
import com.ltst.core.data.rest.model.RestPost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface ApiService {

    String TEACHER_PREFIX = "teacher/";
    String TEACHERS_PREFIX = "teachers/";
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   SESSION  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    String SESSION_PATH = TEACHER_PREFIX + "session";

    @POST(SESSION_PATH)
    Observable<ProfileResponse> login(@Body LoginRequest request);

    @DELETE(SESSION_PATH)
    Observable<Result<Void>> logout(@QueryMap Map<String, String> options);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////   REGISTRATION  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    String REGISTRATION_PATH = TEACHERS_PREFIX + "registration";
    String REGISTRATION_BY_INVITE_PATH = TEACHERS_PREFIX + "registration_by_invite";

    @POST(REGISTRATION_PATH)
    Observable<ProfileResponse> registration(@Body ProfileRequest request);

    @POST(REGISTRATION_BY_INVITE_PATH)
    Observable<ProfileResponse> registrationByInvite(@Body ProfileRequest request);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   PROFILE  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    String PROFILE_PATH = TEACHER_PREFIX + "profile";

    @GET(PROFILE_PATH)
    Observable<ProfileResponse> getProfile();

    @PATCH(PROFILE_PATH)
    Observable<ProfileResponse> updateProfile(@Body ProfileUpdateRequest request);

    @Multipart
    @PATCH(PROFILE_PATH)
    Observable<ProfileResponse> updateProfileAvatar(@Part MultipartBody.Part file);

    @POST("devices")
    Observable<Void> updateFireBaseToken(@Body UpdateFirebaseTokenRequest tokenRequest);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   CHILDREN  /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    String CHILDREN_PATH = TEACHER_PREFIX + "children";

    @Multipart
    @POST("teacher/children")
    Observable<ChildResponse> createChild(@PartMap Map<String, RequestBody> partMap,
                                          @Query("group_ids[]") List<Long> groupIds,
                                          @Part MultipartBody.Part avatar);

    @GET("teacher/groups/{group_id}/children")
    Observable<List<ChildResponse>> getChildren(@Path("group_id") long groupId);

    @GET("teacher/children/{id}")
    Observable<ChildResponse> getChild(@Path("id") long id);

    @Multipart
    @PUT("teacher/children/{child_id}")
    Observable<ChildResponse> updateChild(@Path("child_id") long childServerId,
                                          @PartMap Map<String, RequestBody> childProfile,
                                          @Query("group_ids[]") List<Long> group_ids,
                                          @Part MultipartBody.Part avatar);

    @DELETE(TEACHER_PREFIX + "groups/{group_id}/children/{child_id}")
    Observable<Result<Void>> deleteChild(@Path("group_id") long groupId,
                                         @Path("child_id") long childId);

    ////////////////////////////////   CHILDREN.CHECKIN  //////////////////////////////////////////

    @POST("teacher/groups/{group_id}/children/checkin")
    Observable<List<RestChildCheck>> checkIn(@Path("group_id") long groupId,
                                             @Body CheckRequest checkRequest);

    ////////////////////////////////   CHILDREN.CHECKOUT   ////////////////////////////////////////

    @POST("teacher/groups/{group_id}/children/checkout")
    Observable<List<RestChildCheck>> checkOut(@Path("group_id") long groupId,
                                              @Body CheckRequest checkRequest);

    //////////////////////////////////////   STATES   /////////////////////////////////////////////
    String STATES_PATH = CHILDREN_PATH + "/states";

    @GET(TEACHER_PREFIX + "groups/{group_id}/children/states")
    Observable<List<RestChildCheck>> getStates(@Path("group_id") long groupId,
                                               @QueryMap Map<String, String> options);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////   GROUP   //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    String GROUP_PATH = TEACHER_PREFIX + "groups";

    @GET(GROUP_PATH)
    Observable<List<GroupResponse>> getGroups();

    @PATCH("teacher/groups/{group_id}")
    Observable<GroupResponse> updateGroup(@Path("group_id") long groupId, @Body GroupUpdateRequest request);

    @Multipart
    @PATCH("teacher/groups/{group_id}")
    Observable<GroupResponse> updateGroupAvatar(@Path("group_id") long groupId, @Part MultipartBody.Part file);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////   PASSWORD  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    String UPDATE_PASSWORD = TEACHERS_PREFIX + "update_password";
    String RESET_PASSWORD = TEACHERS_PREFIX + "reset_password";
    String RECOVERY_PASSWORD = TEACHERS_PREFIX + "recovery_password";

    @POST(UPDATE_PASSWORD)
    Observable<ProfileResponse> updatePassword(@Body PasswordUpdateRequest request);

    @POST(RESET_PASSWORD)
    Observable<Result<Void>> resetPassword(@Body ResetPasswordRequest body);

    @POST(RECOVERY_PASSWORD)
    Observable<ProfileResponse> recoveryPassword(@Body RecoveryPasswordRequest recoveryPasswordRequest);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////   POST  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    String POST_PATH = TEACHER_PREFIX + "posts";

    @GET(TEACHER_PREFIX + "groups/{group_id}/posts")
    Observable<List<RestPost>> getPosts(@Path("group_id") long groupId,
                                        @QueryMap Map<String, String> options);

    @POST("teacher/groups/{group_id}/posts")
    Observable<RestPost> addPost(@Path("group_id") long groupId, @Body AddPostRequest addPostRequest);

    @GET(TEACHER_PREFIX + "groups/{group_id}/posts/search")
    Observable<List<RestPost>> getPostsForQuery(@Path("group_id") long groupId,
                                                @QueryMap Map<String, String> options);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////  ACTIVITY ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    String ACTIVITIES_PATH = TEACHER_PREFIX + "activities";

    @GET(ACTIVITIES_PATH)
    Observable<List<RestChildActivity>> getChildActivities();

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////   ASSETS  ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    String ASSETS_PATH = "assets";

    @Multipart
    @POST(ASSETS_PATH)
    Observable<AssetResponse> addAsset(@Query("type") String type, @Part MultipartBody.Part file);

    @POST(ASSETS_PATH + "/generate_short_url")
    Observable<List<ShortUrlResponse>> getShortUrls(@Body GenerateShortUrlsRequest request);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////   FAMILY MEMBER     ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////


    String FAMILY_MEMBER_INVITES_PATH = TEACHER_PREFIX + "family_member_invites";

    @POST(FAMILY_MEMBER_INVITES_PATH)
    @Multipart
    Observable<ChildResponse.Position> inviteMember(@PartMap Map<String, RequestBody> partMap,
                                                    @Part MultipartBody.Part avatar);

    @POST("teacher/family_members/{id}/give_full_access")
    Observable<Result<Void>> giveFullAccess(@Path("id") long memberId);

    @POST("teacher/family_members/{id}/revoke_full_access")
    Observable<Result<Void>> revokeFullAccess(@Path("id") long memberId);

    @GET("teacher/family_members/find_by_email")
    Observable<RestFamilyMember> findByEmail(@Query("email") String email);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////   EVENTS    ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    String EVENTS_PATH = TEACHER_PREFIX + "events";

    @GET(TEACHER_PREFIX + "groups/{group_id}/events")
    Observable<List<EventResponse>> getEvents(@Path("group_id") long groupId,
                                              @Query("beginning_of_range") String from,
                                              @Query("end_of_range") String to);

    @POST("teacher/groups/{group_id}/events")
    Observable<EventResponse> addEvent(@Path("group_id") long groupId,
                                       @Body AddEventRequest request);

    @GET(TEACHER_PREFIX + "groups/{group_id}/events" + "/dates")
    Observable<DatesResponse> getEventsDates(@Path("group_id") long groupId,
                                             @Query("beginning_of_range") String from,
                                             @Query("end_of_range") String to);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////   LAYER    ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @POST("conversations/identity_token")
    Observable<GetLayerIdentityTokenResponse> getIdentityToken(@Query("nonce") String nonce);

}
