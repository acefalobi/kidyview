package com.ltst.schoolapp.parent.data.response.schoolinfo;


import com.ltst.core.data.response.ChildResponse;
import com.squareup.moshi.Json;

import java.util.List;

public class ChildForInfoResponse extends ChildResponse {

    @Json(name = "school_ids")
    public List<Long> schoolIds;
}
