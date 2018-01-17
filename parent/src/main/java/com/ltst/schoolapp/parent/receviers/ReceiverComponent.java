package com.ltst.schoolapp.parent.receviers;


import dagger.Subcomponent;

@Subcomponent
public interface ReceiverComponent {
    void inject(LayerPushReceiver receiver);
}
