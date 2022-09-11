package com.android.simc40.Images;

import android.Manifest;

public interface PermissionList {
    String galleryPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
    String cameraPermission = Manifest.permission.CAMERA;
    String readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
    String writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    String[] permissionList = new String[]{cameraPermission, galleryPermission, readStoragePermission, writeStoragePermission};
}
