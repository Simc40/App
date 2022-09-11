package com.android.simc40.Images;

import android.net.Uri;

import com.android.simc40.classes.Image;
import com.android.simc40.classes.User;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.firebasePaths.FirebaseUserPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.android.simc40.successDialog.SuccessDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UploadPicture implements FirebaseStoragePaths, FirebaseUserPaths {

    public static void uploadProfilePictureOnFirebaseStorage(User user, Image image, String contextException, SuccessDialog successDialog, LoadingPage loadingPage, ErrorDialog errorDialog, ImageUploadSuccessCallback imageUploadSuccessCallback){
        try{
            StorageReference storageRef = FirebaseStorage.getInstance(firebasStorageGSLink + user.getCliente().getStorage()).getReference().child(firebaseStorageUserImagePath).child(user.getUid()).child(image.getImageName());
            storageRef.putFile(Uri.fromFile(image.getImageFile())).addOnSuccessListener (taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                String uploadUrl = task.getResult().toString();
                try{
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebaseUserPathFirstKey).child(user.getUid()).child(firebaseUserPathImgUrlKey);
                    databaseReference.setValue(uploadUrl).addOnCompleteListener(task1 -> {
                        loadingPage.endLoadingPage();
                        successDialog.getButton().setOnClickListener(view -> {
                            successDialog.endSuccessDialog();
                            imageUploadSuccessCallback.onCallback("Success");
                        });
                        successDialog.showImageUploadSuccess();
                    }).addOnFailureListener(e -> ErrorHandling.handleError(contextException, e, loadingPage, errorDialog));
                }catch (Exception e){
                    ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
                }

            }))
            .addOnFailureListener (e -> ErrorHandling.handleError(contextException, e, loadingPage, errorDialog));
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }
    }
}
