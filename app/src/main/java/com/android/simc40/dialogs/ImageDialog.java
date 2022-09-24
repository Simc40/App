package com.android.simc40.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.android.simc40.Images.DownloadImage;
import com.android.simc40.R;
import com.android.simc40.touchListener.TouchListener;

public class ImageDialog {
    AlertDialog dialog;
    ImageView image;

    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    public ImageDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_image, null);
        this.image = v.findViewById(R.id.image);
        image.setOnTouchListener(TouchListener.getTouch());


        builder.setView(v);

        dialog = builder.create();
        dialog.getWindow().getDecorView().setBackgroundResource(R.drawable.loading_page_background);
    }

    public void showImageDialog(){
        dialog.show();
    }

    public void showImageDialog(String path){
        DownloadImage.fromPath(image, path);
        dialog.show();
    }

    public ImageView getImageView(){
        return image;
    }

    public void endImageDialog(){
        dialog.dismiss();
    }
}
