package com.ltst.schoolapp.parent.layer;


import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerAuthenticationListener;
import com.layer.sdk.listeners.LayerConnectionListener;
import com.ltst.schoolapp.parent.data.ApiService;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LayerSubscribeHelper implements LayerConnectionListener.Weak.BackgroundThread,
        LayerAuthenticationListener.Weak.BackgroundThread,
        Observable.OnSubscribe<Boolean> {

    private LayerClient layerClient;
    private Subscriber subscriber;
    private final ApiService apiService;

    public LayerSubscribeHelper(LayerClient layerClient, ApiService apiService) {
        this.apiService = apiService;
        this.layerClient = layerClient;
    }

    public void connect() {
        if (!layerClient.isAuthenticated()) {
            layerClient.authenticate();
        } else if (!layerClient.isConnected()) {
            layerClient.connect();
        } else subscriber.onNext(true);

    }

    @Override public void onConnectionConnected(LayerClient layerClient) {
        if (!subscriber.isUnsubscribed()) {
            if (layerClient.isAuthenticated()) {
                subscriber.onNext(true);
            } else {
                layerClient.authenticate();
            }
        }
        subscriber.unsubscribe();
    }

    @Override public void onConnectionDisconnected(LayerClient layerClient) {
        layerClient.connect();
    }

    @Override public void onConnectionError(LayerClient layerClient, LayerException e) {
        connect();
    }

    @Override public void call(Subscriber<? super Boolean> subscriber) {
        this.subscriber = subscriber;
        connect();
    }

    @Override public void onAuthenticated(LayerClient layerClient, String s) {
        if (!layerClient.isConnected()) {
            layerClient.connect();
        } else subscriber.onNext(true);

    }

    @Override public void onDeauthenticated(LayerClient layerClient) {
        layerClient.authenticate();
    }

    @Override public void onAuthenticationChallenge(LayerClient layerClient, String s) {
        respondToChallenge(layerClient, s);
    }

    @Override public void onAuthenticationError(LayerClient layerClient, LayerException e) {
        layerClient.authenticate();
    }


    private void respondToChallenge(LayerClient layerClient, String nonce) {
        apiService.getIdentityToken(nonce)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(identityTokenResponse -> {
                    layerClient.answerAuthenticationChallenge(identityTokenResponse.identityToken);
                }, Throwable::printStackTrace);
    }

}
