package com.ltst.core.layer;


import android.content.Context;

import com.layer.sdk.LayerClient;
import com.ltst.core.CoreScope;
import com.ltst.core.data.GetUserByLayerIdentityService;

import dagger.Module;
import dagger.Provides;

@Module
public class LayerModule {
    public static final String LAYER_ID_LINK = "layer:///apps/production/8046ebf4-e3cd-11e6-bac5-166800001110";
    public static final String CHATS_SCREEN_NAME = "Application.ChatsScreen"; // must be use for chats screens

    public static final String LAYER_IDENTITIES_KEY = "LayerChats.LayerIdenities";
    public static final String LAYER_SCREEN_TITLE_KEY = "ConversationScreen.ScreenTitle";

    @Provides
    @CoreScope
    LayerClient provideLayerClient(Context context) {
        LayerClient layerClient = LayerClient.newInstance(context, LAYER_ID_LINK,
                new LayerClient.Options()
                        .broadcastPushInForeground(true)
                        .useFirebaseCloudMessaging(true));
        return layerClient;
    }

    @Provides
    @CoreScope LayerNotificationsHelper provideLayerNotificationHelper(Context context, LayerClient layerClient,
                                                                       GetUserByLayerIdentityService layerService) {
        return new LayerNotificationsHelper(context, layerClient, layerService);
    }
}
