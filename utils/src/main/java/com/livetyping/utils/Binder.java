package com.livetyping.utils;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.WeakHashMap;

public class Binder<AttachedObject> {
    private WeakReference<AttachedObject> attachedObjectRef;
    private WeakHashMap<AttachedObject, Object> weakHashMap = new WeakHashMap<>();

    public final void attach(AttachedObject object) {
        final WeakReference<AttachedObject> weakReference = new WeakReference<>(object);
        weakHashMap.put(object, new Object());
        attachedObjectRef = weakReference;
    }

    public void detach(AttachedObject object) {
        if (weakHashMap.remove(object) == null) {
            return;
        }

        Iterator<AttachedObject> it = weakHashMap.keySet().iterator();
        if (it.hasNext()) {
            attachedObjectRef = new WeakReference<>(it.next());
        } else {
            attachedObjectRef = null;
        }
    }

    protected AttachedObject getAttachedObject() {
        if (attachedObjectRef == null)
            return null;
        return attachedObjectRef.get();
    }
}
