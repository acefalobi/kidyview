package com.layer.atlas.util;


import com.layer.sdk.messaging.Identity;

import java.util.Comparator;

public class IdentityDisplayNameComparator implements Comparator<Identity> {

    @Override
    public int compare(Identity lhs, Identity rhs) {
        return LayerUtils.getDisplayName(lhs).compareTo(LayerUtils.getDisplayName(rhs));
    }
}
