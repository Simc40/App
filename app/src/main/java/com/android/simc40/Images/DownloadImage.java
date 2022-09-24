package com.android.simc40.Images;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.simc40.errorHandling.ErrorHandling;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

public class DownloadImage{


    public static void fromUrlLayoutGone(ImageView imageView, String path){
        try{
            if(path == null || path.equals("")) {
                imageView.setVisibility(View.GONE);
                return;
            }
            Picasso.get()
                    .load(path)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {}
                        @Override
                        public void onError(Exception e) {
                            imageView.setVisibility(View.GONE);
                            ErrorHandling.printStackTrace(DownloadImage.class.getSimpleName(), e.getStackTrace());
                        }
                    });
        }catch (Exception e){
            ErrorHandling.printStackTrace(DownloadImage.class.getSimpleName(), e.getStackTrace());
        }
    }

    public static void fromUrl(ImageView imageView, String path){
        try{
            if(path == null || path.equals("")) {
                return;
            }
            Picasso.get()
                    .load(path)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {}
                        @Override
                        public void onError(Exception e) {
                            ErrorHandling.printStackTrace(DownloadImage.class.getSimpleName(), e.getStackTrace());
                        }
                    });
        }catch (Exception e){
            ErrorHandling.printStackTrace(DownloadImage.class.getSimpleName(), e.getStackTrace());
        }
    }

    public static void fromPath(ImageView imageView, String path){
        try{
            if(path == null || path.equals("")) {
                return;
            }
            File file = new File(path);
            Picasso.get()
                    .load(file)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {}
                        @Override
                        public void onError(Exception e) {
                            ErrorHandling.printStackTrace(DownloadImage.class.getSimpleName(), e.getStackTrace());
                        }
                    });
        }catch (Exception e){
            ErrorHandling.printStackTrace(DownloadImage.class.getSimpleName(), e.getStackTrace());
        }
    }

    public static void fromBitmap(ImageView imageView, String path){
        try{
            if(path == null || path.equals("")) {
                return;
            }
//            Picasso.get()
//                    .load(file)
//                    .into(imageView, new Callback() {
//                        @Override
//                        public void onSuccess() {}
//                        @Override
//                        public void onError(Exception e) {
//                            ErrorHandling.printStackTrace(contextException, e.getStackTrace());
//                        }
//                    });
        }catch (Exception e){
            ErrorHandling.printStackTrace(DownloadImage.class.getSimpleName(), e.getStackTrace());
        }
    }

}