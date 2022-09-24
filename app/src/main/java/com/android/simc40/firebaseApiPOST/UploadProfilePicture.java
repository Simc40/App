package com.android.simc40.firebaseApiPOST;

import android.net.Uri;

import com.android.simc40.firebasePaths.FirebaseStoragePaths;
import com.android.simc40.classes.Image;
import com.android.simc40.classes.User;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.firebasePaths.FirebaseUserPaths;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.dialogs.SuccessDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UploadProfilePicture implements FirebaseStoragePaths, FirebaseUserPaths {

    public static void uploadProfilePictureOnFirebaseStorage(User user, Image image, String contextException, SuccessDialog successDialog, LoadingDialog loadingDialog, ErrorDialog errorDialog, ImageUploadSuccessCallback imageUploadSuccessCallback){
        try{
            loadingDialog.tick(); // 1
            StorageReference storageRef = FirebaseStorage.getInstance(firebasStorageGSLink + user.getCliente().getStorage()).getReference().child(firebaseStorageUserImagePath).child(user.getUid()).child(image.getImageName());
            storageRef.putFile(Uri.fromFile(image.getImageFile())).addOnSuccessListener (taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                String uploadUrl = task.getResult().toString();
                try{
                    loadingDialog.tick(); // 2
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebaseUserPathFirstKey).child(user.getUid()).child(firebaseUserPathImgUrlKey);
                    databaseReference.setValue(uploadUrl).addOnCompleteListener(task1 -> {
                        loadingDialog.finalTick(); // 3
                        loadingDialog.endLoadingDialog();
                        successDialog.getButton1().setOnClickListener(view -> {
                            successDialog.endSuccessDialog();
                            imageUploadSuccessCallback.onCallback("Success");
                        });
                        successDialog.showImageUploadSuccess();
                    }).addOnFailureListener(e -> ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog));
                }catch (Exception e){
                    ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
                }

            }))
            .addOnFailureListener (e -> ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog));
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
        }
    }
}
