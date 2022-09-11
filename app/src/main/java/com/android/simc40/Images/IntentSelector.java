package com.android.simc40.Images;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

public class IntentSelector implements PermissionList{

    public boolean galleryPermissionDenied;
    public boolean cameraPermissionDenied;
    Activity activity;

    public IntentSelector(Activity activity){
        this.activity = activity;
    }

    public static Intent getIntent() {
        Intent galleryintent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryintent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);

        chooser.putExtra(Intent.EXTRA_INTENT, galleryintent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Selecionar Imagem");

        Intent[] intentArray = { cameraIntent };
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        return chooser;
    }

    public void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //If not permitted return false
            galleryPermissionDenied = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED;
            cameraPermissionDenied = activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED;
        }else{
            //system OS is less then API 23.
            galleryPermissionDenied = cameraPermissionDenied = false;
        }
    }
}
