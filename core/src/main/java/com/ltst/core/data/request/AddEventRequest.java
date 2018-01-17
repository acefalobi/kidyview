package com.ltst.core.data.request;

import com.ltst.core.data.model.Asset;
import com.squareup.moshi.Json;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Danil on 06.10.2016.
 */

public class AddEventRequest {
    private String date;
    private String time;
    private String content;
    @Json(name = "image_tokens")
    private List<String> imageTokens;
    @Json(name = "document_tokens")
    private List<String> documentTokens;

    private AddEventRequest() {
    }

    private AddEventRequest(String date, String time, String content) {
        this.date = date;
        this.time = time;
        this.content = content;
    }

    private AddEventRequest setImageTokens(List<String> imageTokens) {
        this.imageTokens = imageTokens;
        return this;
    }

    private AddEventRequest setDocumentTokens(List<String> documentTokens) {
        this.documentTokens = documentTokens;
        return this;
    }

    public static AddEventRequest createRequest(Calendar calendar, String content) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'");
        dateFormat.setTimeZone(timeZone);
        String date = dateFormat.format(new Date(calendar.getTimeInMillis()));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS'Z'");
        timeFormat.setTimeZone(timeZone);
        String time = timeFormat.format(new Date(calendar.getTimeInMillis()));
        return new AddEventRequest(date, time, content);
    }

    public AddEventRequest withImages(List<Asset> assets) {
        setImageTokens(Asset.toTokenList(assets));
        return this;
    }

    public AddEventRequest withDocs(List<Asset> assets) {
        setDocumentTokens(Asset.toTokenList(assets));
        return this;
    }
}
