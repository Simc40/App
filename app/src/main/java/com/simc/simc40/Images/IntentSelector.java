package com.simc.simc40.Images;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IntentSelector implements PermissionList{

    public boolean galleryPermissionDenied;
    public boolean cameraPermissionDenied;
    Activity activity;

    public IntentSelector(Activity activity){
        this.activity = activity;
    }

    public Object[] getIntent() {
        Intent galleryintent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryintent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File outputFile = getOutputMediaFile();
        Uri outputUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".fileprovider", outputFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);

        chooser.putExtra(Intent.EXTRA_INTENT, galleryintent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Selecionar Imagem");

        Intent[] intentArray = { cameraIntent };
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        return new Object[] {chooser, outputUri, outputFile};
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

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

//        File mediaStorageDir = new File(activity.getExternalFilesDir(null).getAbsolutePath());
        File mediaStorageDir = new File(activity.getExternalFilesDir(null).getAbsolutePath());
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        System.out.println("mediaStorageDir: " + mediaStorageDir.getAbsolutePath());

        System.out.println("mediaStorageDir.exists(): " + mediaStorageDir.exists());
        // Create a media file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);

        if (mediaFile == null) System.out.println("mediaStorageDir is null");
        else System.out.println("mediaStorageDir is valid: " + mediaFile.getAbsolutePath());

        return mediaFile;
    }
}
