package com.ltst.core.permission;

public interface PermissionsHandler {

    int CAMERA_REQUEST = 4567;
    int WRITE_STORAGE_REQUEST = 7564;

    void requestPermission(int requestCode, String permission, Callback callback);

    interface Callback {

        void resultOk();

    }
}
