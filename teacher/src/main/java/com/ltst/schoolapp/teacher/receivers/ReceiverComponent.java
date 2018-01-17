package com.ltst.schoolapp.teacher.receivers;


import dagger.Module;
import dagger.Subcomponent;

@Subcomponent(modules = ReceiverComponent.ReceiverModule.class)
public interface ReceiverComponent {
    void inject(LayerPushReceiver receiver);

    @Module
    class ReceiverModule {

    }
}



