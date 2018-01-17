package com.ltst.core.data.realm;

import com.ltst.core.data.realm.model.ChildCheckScheme;
import com.ltst.core.data.realm.model.ChildInGroupScheme;
import com.ltst.core.data.realm.model.ChildScheme;
import com.ltst.core.data.realm.model.ChildStateScheme;
import com.ltst.core.data.realm.model.GroupScheme;
import com.ltst.core.data.realm.model.MemberScheme;
import com.ltst.core.data.realm.model.ProfileScheme;
import com.ltst.core.data.realm.model.RealmLong;
import com.ltst.core.data.realm.model.SchoolScheme;

import io.realm.annotations.RealmModule;

@RealmModule(classes = {SchoolScheme.class, ProfileScheme.class, ChildScheme.class,
        MemberScheme.class, ChildCheckScheme.class, ChildStateScheme.class, GroupScheme.class,
        RealmLong.class, ChildInGroupScheme.class})
public class AppRealmModule {
}
