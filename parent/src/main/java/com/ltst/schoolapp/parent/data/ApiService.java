package com.ltst.schoolapp.parent.data;

import com.ltst.core.data.request.GenerateCodeRequest;
import com.ltst.core.data.request.GenerateShortUrlsRequest;
import com.ltst.core.data.request.LoginRequest;
import com.ltst.core.data.request.ProfileRequest;
import com.ltst.core.data.request.RecoveryPasswordRequest;
import com.ltst.core.data.request.ResetPasswordRequest;
import com.ltst.core.data.request.UpdateFirebaseTokenRequest;
import com.ltst.core.data.response.ChildInGroupResponse;
import com.ltst.core.data.response.ChildResponse;
import com.ltst.core.data.response.DatesResponse;
import com.ltst.core.data.response.EventResponse;
import com.ltst.core.data.response.GenerateCodeResponse;
import com.ltst.core.data.response.GetLayerIdentityTokenResponse;
import com.ltst.core.data.response.ParentChildResponse;
import com.ltst.core.data.response.ProfileResponse;
import com.ltst.core.data.response.ShortUrlResponse;
import com.ltst.core.data.response.TeacherResponse;
import com.ltst.core.data.rest.model.RestChildCheck;
import com.ltst.core.data.rest.model.RestFamilyMember;
import com.ltst.core.util.validator.ValidateType;
import com.ltst.schoolapp.parent.data.response.RestPost;
import com.ltst.schoolapp.parent.data.response.schoolinfo.SchoolInfoResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface ApiService {

    String MEMBER_PREFIX = "family_member/";
    String MEMBERS_PREFIX = "family_members/";

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   SESSION   /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @POST(MEMBERS_PREFIX + "registration_by_invite")
    Observable<ProfileResponse> registration(@Body ProfileRequest profileRequest);

    @POST(MEMBER_PREFIX + "session")
    Observable<ProfileResponse> login(@Body LoginRequest request);

    @POST(MEMBERS_PREFIX + "reset_password")
    Observable<Void> resetPassword(@Body ResetPasswordRequest request);

    @POST(MEMBERS_PREFIX + "recovery_password")
    Observable<ProfileResponse> recoveryPassword(@Body RecoveryPasswordRequest recoveryPasswordRequest);

    @DELETE(MEMBER_PREFIX + "session")
    Observable<Void> logout(@QueryMap Map<String, String> options);

    @POST(MEMBERS_PREFIX + "resend_code")
    Observable<Result<Void>> sendCodeAgain(@Query("email") String email);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   PROFILE  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @GET(MEMBER_PREFIX + "profile")
    Observable<ProfileResponse> getProfile();

    @Multipart
    @PUT(MEMBER_PREFIX + "profile")
    Observable<ProfileResponse> updateProfile(@PartMap Map<String, RequestBody> partMap,
                                              @Part MultipartBody.Part avatar);

    @POST("devices")
    Observable<Void> updateFireBaseToken(@Body UpdateFirebaseTokenRequest request);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   CHILDREN  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @GET(MEMBER_PREFIX + "children")
    Observable<List<ParentChildResponse>> getChildren();

    @Multipart
    @PUT(MEMBER_PREFIX + "children" + "/{child_id}")
    Observable<ParentChildResponse> updateChild(@Path("child_id") long childServerId,
                                                @PartMap Map<String, RequestBody> partMap,
                                                @Part MultipartBody.Part avatar);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   CHECKS  //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @GET(MEMBER_PREFIX + "children/states")
    Observable<List<RestChildCheck>> getStates(@QueryMap Map<String, String> options);

    @POST("family_member/children/{child_id}/checkout")
    Observable<GenerateCodeResponse> generateCheckoutCode(@Path("child_id") long childId,
                                                          @Body GenerateCodeRequest request);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////   FEED     //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @GET(MEMBER_PREFIX + "posts")
    Observable<List<RestPost>> getPosts(@QueryMap Map<String, String> options);

    @GET(MEMBER_PREFIX + "posts/search")
    Observable<List<RestPost>> getPostsForQuery(@QueryMap Map<String, String> options);

    @POST("assets/generate_short_url")
    Observable<List<ShortUrlResponse>> getShortUrls(@Body GenerateShortUrlsRequest request);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  CHILDREN IN GROUPS     /////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @GET("family_member/posts/groups_of_children")
    Observable<List<ChildInGroupResponse>> getChildrenInGroups();

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  INVITE     //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @GET("family_member/family_members/find_by_email")
    Observable<RestFamilyMember> findByEmail(@Query("email") String email);

    @POST(MEMBER_PREFIX + "invites")
    @Multipart
    Observable<ChildResponse.Position> inviteMember(@PartMap Map<String, RequestBody> partMap,
                                                    @Part MultipartBody.Part avatar);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  EVENTS     //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @GET("family_member/events/dates")
    Observable<DatesResponse> getEventsDates(@Query("beginning_of_range") String from,
                                             @Query("end_of_range") String to);

    @GET("family_member/events")
    Observable<List<EventResponse>> getEvents(@Query("beginning_of_range") String from,
                                              @Query("end_of_range") String to);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  REPORT     /////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @GET("family_member/checkout_reports/{id}")
    Observable<List<RestPost>> getCheckoutReport(@Path("id") long reportId);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////   LAYER    ////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @POST("conversations/identity_token")
    Observable<GetLayerIdentityTokenResponse> getIdentityToken(@Query("nonce") String nonce);

    @GET("family_member/teachers")
    Observable<List<TeacherResponse>> getTeachers();

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////   SCHOOL INFO    ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @GET("family_member/school_info")
    Observable<SchoolInfoResponse> getSchoolInfo();
}
