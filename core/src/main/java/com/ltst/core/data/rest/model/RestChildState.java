package com.ltst.core.data.rest.model;

import com.squareup.moshi.Json;

public class RestChildState {
    private Integer id;
    private String datetime;
    private String kind;
    private String responsible;
    @Json(name = "family_member")
    private RestFamilyMember familyMember;
    @Json(name = "first_name")
    private String firstName;
    @Json(name = "last_name")
    private String lastName;

    public RestChildState(Integer id, String datetime, String kind, String responsible,
                          RestFamilyMember familyMember, String firstName, String lastName) {
        this.id = id;
        this.datetime = datetime;
        this.kind = kind;
        this.responsible = responsible;
        this.familyMember = familyMember;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getId() {
        return id;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getKind() {
        return kind;
    }

    public String getResponsible() {
        return responsible;
    }

    public RestFamilyMember getFamilyMember() {
        return familyMember;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
