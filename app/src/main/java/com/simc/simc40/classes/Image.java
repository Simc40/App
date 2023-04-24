package com.simc.simc40.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.ImageException;
import com.simc.simc40.errorHandling.ImageExceptionErrorList;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Image implements Serializable, ImageExceptionErrorList {
    public static String gallery = "Gallery";
    public static String camera = "Camera";
    transient Activity activity;
    transient Intent data;
    transient Bitmap bitmap;
    File imageFile;
    String imageName;
    String src;
    String path;
    String extension;
    transient Uri uri;

    public Image(Activity activity, File imageFile){
        this.imageFile = imageFile;
        this.src = camera;
        this.path = imageFile.getPath();
        this.extension = path.substring(path.lastIndexOf("."));
        this.imageName = "image" + extension;
    }

    public Image(Activity activity, Intent data, String contextException, ModalAlertaDeErro modalAlertaDeErro) throws Exception {
        this.activity = activity;
        this.data = data;
        this.uri = data.getData();

//        if(uri == null){
//            //Imagem comes from camera
//            try {
//                this.bitmap = (Bitmap) data.getExtras().get("data");
//                File pictureFile = getOutputMediaFile();
//                if(pictureFile == null) throw new PermissionException();
//                FileOutputStream fos = new FileOutputStream(pictureFile);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                fos.close();
//                MediaStore.Images.Media.insertImage(activity.getContentResolver(), pictureFile.getAbsolutePath(), pictureFile.getName(), null);
//                MediaScannerConnection.scanFile(activity, new String[]{pictureFile.getPath()}, null, null);
//                this.imageFile = pictureFile;
//                this.src = camera;
//                this.path = pictureFile.getPath();
//                this.extension = path.substring(path.lastIndexOf("."));
//                this.imageName = "image" + extension;
//            } catch (Exception e) {
//                ErrorHandling.handleError(contextException, e, errorDialog);
//            }
//            return;
//        }
        //Image comes from Gallery
        try{
            this.src = gallery;
            this.path = getRealPathFromURI(uri);
            this.extension = path.substring(path.lastIndexOf("."));
            this.imageName = "image" + extension;
            this.imageFile = new File(path);
            if (imageFile == null) throw new ImageException();
            if (!extension.equals (".jpg") && !extension.equals (".png") && !extension.equals (".jpeg")) throw new ImageException(EXCEPTION_INVALID_FORMAT);
        }catch (Exception e){
            ErrorHandling.handleError(contextException, e, modalAlertaDeErro);
        }

    }

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//                + "/Android/data/"
//                + activity.getApplicationContext().getPackageName()
//                + "/Files");
        File mediaStorageDir = new File(activity.getExternalFilesDir(null).getAbsolutePath(), "/SIMC40");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    @SuppressLint("ObsoleteSdkInt")
    public String getRealPathFromURI(Uri contentURI) {
        if(Build.VERSION.SDK_INT <=23) {
            String result;
            Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
            return result;
        }else{
            String yourRealPath = null;
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(contentURI, filePathColumn, null, null, null);
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                yourRealPath = cursor.getString(columnIndex);
            }
            cursor.close();
            return yourRealPath;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Image{" + "\n" + "src='" + src + '\'' + "\n" + ", path='" + path + '\'' + "\n" + ", extension='" + extension + '\'' + "\n" + '}';
    }

    public File getImageFile() {return imageFile;}
    public String getImageName() {return imageName;}
    public String getSrc() {return src;}
    public String getPath() {return path;}
}
