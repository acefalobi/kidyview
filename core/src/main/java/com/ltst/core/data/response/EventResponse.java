
package com.ltst.core.data.response;

import com.squareup.moshi.Json;

import java.util.List;

public class EventResponse {

    @Json(name = "content")
    private String mContent;

    @Json(name = "date")
    private String mDate;

    @Json(name = "documents")
    private List<DocumentResponse> mDocumentResponses;

//    @Json(name = "group_id")
//    private Long mGroupId;

    @Json(name = "id")
    private Long mId;

    @Json(name = "images")
    private List<ImageResponse> mImageResponses;

    @Json(name = "kind")
    private String mKind;

    @Json(name = "time")
    private String mTime;

    @Json(name = "event_title")
    private String eventTitle;

    @Json(name = "event_avatar_url")
    private String eventAvatarUrl;

    private EventResponse() {
        //no instance
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public List<DocumentResponse> getDocuments() {
        return mDocumentResponses;
    }

    public void setDocuments(List<DocumentResponse> documentResponses) {
        mDocumentResponses = documentResponses;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventAvatarUrl() {
        return eventAvatarUrl;
    }



//    public Long getGroupId() {
//        return mGroupId;
//    }
//
//    public void setGroupId(Long group_id) {
//        mGroupId = group_id;
//    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public List<ImageResponse> getImages() {
        return mImageResponses;
    }

    public void setImages(List<ImageResponse> imageResponses) {
        mImageResponses = imageResponses;
    }

    public String getKind() {
        return mKind;
    }

    public void setKind(String kind) {
        mKind = kind;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public static class DocumentResponse {

        @Json(name = "resource_token")
        private String mResourceToken;
        @Json(name = "url")
        private String mUrl;

        public String getResourceToken() {
            return mResourceToken;
        }

        public void setResourceToken(String resource_token) {
            mResourceToken = resource_token;
        }

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String url) {
            mUrl = url;
        }

    }

    public static class ImageResponse {
        @Json(name = "resource_token")
        private String mResourceToken;
        @Json(name = "url")
        private String mUrl;

        public String getResourceToken() {
            return mResourceToken;
        }

        public void setResourceToken(String resource_token) {
            mResourceToken = resource_token;
        }

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String url) {
            mUrl = url;
        }
    }
}
