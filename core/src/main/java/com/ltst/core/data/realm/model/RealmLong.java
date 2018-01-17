package com.ltst.core.data.realm.model;


import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmLong extends RealmObject {
    private Long value;

    public RealmLong() {
    }

    public RealmLong(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public static RealmList<RealmLong> fromLongList(List<Long> aLongs){
        RealmList<RealmLong> result = new RealmList<>();
        for (Long aLong : aLongs){
            result.add(new RealmLong(aLong));
        }
        return result;
    }
}
