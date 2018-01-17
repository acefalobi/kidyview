package com.ltst.core.firebase;


import com.google.firebase.messaging.RemoteMessage;
import com.layer.sdk.services.LayerFcmService;
import com.ltst.core.util.Foreground;

public abstract class BaseFcmService extends LayerFcmService {

    @Override public void onMessageReceived(RemoteMessage remoteMessage) {
        if (isLayerMessage(remoteMessage) && Foreground.get().isBackground()) {
            super.onMessageReceived(remoteMessage);
        } else {
            handleFireBaseMessage(remoteMessage);
        }

    }

    protected abstract void handleFireBaseMessage(RemoteMessage remoteMessage);
}
