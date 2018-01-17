package com.ltst.core.data.model;


import com.ltst.core.data.response.EventResponse;

import java.util.ArrayList;
import java.util.List;

public class Event {
    private Long id;
    private String date;
    private String time;
    private String content;
    private String kind;
    private List<Image> images;
    private List<Document> documents;
    private String eventTitle;
    private String eventAvatarUrl;

    public Event(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public String getKind() {
        return kind;
    }

    public List<Image> getImages() {
        return images;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventAvatarUrl() {
        return eventAvatarUrl;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventAvatarUrl(String eventAvatarUrl) {
        this.eventAvatarUrl = eventAvatarUrl;
    }

    public static class Image {
        String resourceToken;
        String url;

        public Image(String resourceToken, String url) {
            this.resourceToken = resourceToken;
            this.url = url;
        }

        public String getResourceToken() {
            return resourceToken;
        }

        public String getUrl() {
            return url;
        }

        public static Image fromResponse(EventResponse.ImageResponse imageResponse) {
            return new Image(imageResponse.getResourceToken(), imageResponse.getUrl());
        }
    }

    public static class Document {
        String resourceToken;
        String url;

        public Document(String resourceToken, String url) {
            this.resourceToken = resourceToken;
            this.url = url;
        }

        public String getResourceToken() {
            return resourceToken;
        }

        public String getUrl() {
            return url;
        }

        public static Document fromResponse(EventResponse.DocumentResponse documentResponse) {
            return new Document(documentResponse.getResourceToken(), documentResponse.getUrl());
        }
    }

    public static Event fromResponse(EventResponse response) {
        Event event = new Event(response.getId());
        event.setDate(response.getDate());
        event.setTime(response.getTime());
        event.setContent(response.getContent());
        event.setKind(response.getKind());
        event.setEventTitle(response.getEventTitle());
        event.setEventAvatarUrl(response.getEventAvatarUrl());

        List<EventResponse.ImageResponse> responseImages = response.getImages();
        if (responseImages != null && !responseImages.isEmpty()) {
            List<Image> images = new ArrayList<>(responseImages.size());
            for (EventResponse.ImageResponse imageResponse : responseImages) {
                images.add(Image.fromResponse(imageResponse));
            }
            event.setImages(images);
        }
        List<EventResponse.DocumentResponse> responseDocuments = response.getDocuments();
        if (responseDocuments != null && !responseDocuments.isEmpty()) {
            List<Document> documents = new ArrayList<>(responseDocuments.size());
            for (EventResponse.DocumentResponse documentResponse : responseDocuments) {
                documents.add(Document.fromResponse(documentResponse));
            }
            event.setDocuments(documents);
        }
        return event;
    }
}
