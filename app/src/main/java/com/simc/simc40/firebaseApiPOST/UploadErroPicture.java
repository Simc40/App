package com.simc.simc40.firebaseApiPOST;

import android.net.Uri;

import com.simc.simc40.classes.Image;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.firebasePaths.FirebaseErrosPaths;
import com.simc.simc40.firebasePaths.FirebaseStoragePaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UploadErroPicture implements FirebaseStoragePaths, FirebaseErrosPaths {

    public static final int solucaoTicks = 3;

    public static void uploadErroPictureOnFirebaseStorage(String uuid, Usuario usuario, Image image, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ImageUploadSuccessCallback imageUploadSuccessCallback){
        try{
            StorageReference storageRef = FirebaseStorage.getInstance(firebasStorageGSLink + usuario.getCliente().getStorage()).getReference().child(firebaseErrosPathsFirstKey).child(uuid).child(image.getImageName());
            storageRef.putFile(Uri.fromFile(image.getImageFile()))
            .addOnSuccessListener (taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                String uploadUrl = task.getResult().toString();
                imageUploadSuccessCallback.onCallback(uploadUrl);
            }))
            .addOnFailureListener (e -> {
                ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
                imageUploadSuccessCallback.onCallback("");
            });
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    public static void uploadSolucaoPictureOnFirebaseStorage(String uuid, Usuario usuario, Image image, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ImageUploadSuccessCallback imageUploadSuccessCallback){
        try{
            StorageReference storageRef = FirebaseStorage.getInstance(firebasStorageGSLink + usuario.getCliente().getStorage()).getReference().child(firebaseErrosPathsFirstKey).child(uuid).child("solved"+image.getImageName());
            storageRef.putFile(Uri.fromFile(image.getImageFile()))
                    .addOnSuccessListener (taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                        String uploadUrl = task.getResult().toString();
                        imageUploadSuccessCallback.onCallback(uploadUrl);
                    }))
                    .addOnFailureListener (e -> {
                        ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
                        imageUploadSuccessCallback.onCallback("");
                    });
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
        }
    }
}
