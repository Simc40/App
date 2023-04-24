package com.simc.simc40.firebaseApiPOST;

import android.net.Uri;

import com.simc.simc40.firebasePaths.FirebaseStoragePaths;
import com.simc.simc40.classes.Image;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.firebasePaths.FirebaseUserPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.dialogs.SuccessDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UploadProfilePicture implements FirebaseStoragePaths, FirebaseUserPaths {

    public static void uploadProfilePictureOnFirebaseStorage(Usuario usuario, Image image, String contextException, SuccessDialog successDialog, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ImageUploadSuccessCallback imageUploadSuccessCallback){
        try{
            modalDeCarregamento.avancarPasso(); // 1
            StorageReference storageRef = FirebaseStorage.getInstance(firebasStorageGSLink + usuario.getCliente().getStorage()).getReference().child(firebaseStorageUserImagePath).child(usuario.getUid()).child(image.getImageName());
            storageRef.putFile(Uri.fromFile(image.getImageFile())).addOnSuccessListener (taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                String uploadUrl = task.getResult().toString();
                try{
                    modalDeCarregamento.avancarPasso(); // 2
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebaseUserPathFirstKey).child(usuario.getUid()).child(firebaseUserPathImgUrlKey);
                    databaseReference.setValue(uploadUrl).addOnCompleteListener(task1 -> {
                        modalDeCarregamento.passoFinal(); // 3
                        modalDeCarregamento.fecharModal();
                        successDialog.getButton1().setOnClickListener(view -> {
                            successDialog.endSuccessDialog();
                            imageUploadSuccessCallback.onCallback("Success");
                        });
                        successDialog.showImageUploadSuccess();
                    }).addOnFailureListener(e -> ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro));
                }catch (Exception e){
                    ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
                }

            }))
            .addOnFailureListener (e -> ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro));
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
        }
    }
}
