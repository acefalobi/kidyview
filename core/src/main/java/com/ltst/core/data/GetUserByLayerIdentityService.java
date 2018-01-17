package com.ltst.core.data;


import com.ltst.core.data.response.LayerProfileResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface GetUserByLayerIdentityService {

    @GET("layer_profile/{layer_id}")
    Observable<LayerProfileResponse> getLayerProfile(@Path("layer_id") String layerId);
}
