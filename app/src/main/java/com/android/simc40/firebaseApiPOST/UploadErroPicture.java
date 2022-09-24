package com.android.simc40.firebaseApiPOST;

import android.net.Uri;

import com.android.simc40.classes.Image;
import com.android.simc40.classes.User;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.firebasePaths.FirebaseErrosPaths;
import com.android.simc40.firebasePaths.FirebaseStoragePaths;
import com.android.simc40.firebasePaths.FirebaseUserPaths;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.dialogs.SuccessDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UploadErroPicture implements FirebaseStoragePaths, FirebaseErrosPaths {

    public static final int solucaoTicks = 3;

    public static void uploadErroPictureOnFirebaseStorage(String uuid, User user, Image image, String contextException, LoadingDialog loadingDialog, ErrorDialog errorDialog, ImageUploadSuccessCallback imageUploadSuccessCallback){
        try{
            StorageReference storageRef = FirebaseStorage.getInstance(firebasStorageGSLink + user.getCliente().getStorage()).getReference().child(firebaseErrosPathsFirstKey).child(uuid).child(image.getImageName());
            storageRef.putFile(Uri.fromFile(image.getImageFile()))
            .addOnSuccessListener (taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                String uploadUrl = task.getResult().toString();
                imageUploadSuccessCallback.onCallback(uploadUrl);
            }))
            .addOnFailureListener (e -> {
                ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
                imageUploadSuccessCallback.onCallback("");
            });
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
        }
    }

    public static void uploadSolucaoPictureOnFirebaseStorage(String uuid, User user, Image image, String contextException, LoadingDialog loadingDialog, ErrorDialog errorDialog, ImageUploadSuccessCallback imageUploadSuccessCallback){
        try{
            StorageReference storageRef = FirebaseStorage.getInstance(firebasStorageGSLink + user.getCliente().getStorage()).getReference().child(firebaseErrosPathsFirstKey).child(uuid).child("solved"+image.getImageName());
            storageRef.putFile(Uri.fromFile(image.getImageFile()))
                    .addOnSuccessListener (taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                        String uploadUrl = task.getResult().toString();
                        imageUploadSuccessCallback.onCallback(uploadUrl);
                    }))
                    .addOnFailureListener (e -> {
                        ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
                        imageUploadSuccessCallback.onCallback("");
                    });
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
        }
    }
}
