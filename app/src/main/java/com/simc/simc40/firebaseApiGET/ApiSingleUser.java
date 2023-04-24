package com.simc.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.simc.simc40.activityStatus.ActivityStatus;
import com.simc.simc40.classes.Cliente;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseUserPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ApiSingleUser implements DefaultErrorMessage, FirebaseUserPaths, FirebaseDatabaseExceptionErrorList {

    public static final int ticks = ApiClientes.ticks + 3;
    DatabaseReference reference;
    Activity activity;
    String contextException;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    ApiSingleUserCallback apiSingleUserCallback;
    HashMap<String, Cliente> clientesMap;
    String uid;
    Usuario usuario;


    public ApiSingleUser(Activity activity, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ApiSingleUserCallback apiSingleUserCallback, String uid) throws FirebaseDatabaseException {
        if(uid == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
        this.uid = uid;
        this.activity = activity;
        this.contextException = contextException;
        this.modalDeCarregamento = modalDeCarregamento;
        this.modalAlertaDeErro = modalAlertaDeErro;
        this.apiSingleUserCallback = apiSingleUserCallback;

        ApiClientesCallback apiClientesCallback = response -> {
            clientesMap = response;
            getUser();
        };

        new ApiClientes(activity, contextException, modalDeCarregamento, modalAlertaDeErro, apiClientesCallback);
    }

    private void getUser(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance().getReference().child(firebaseUserPathFirstKey).child(uid);
            if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException(EXCEPTION_USER_NOT_FOUND);
                        usuario = new Usuario(
                                dataSnapshot.getKey(),
                                dataSnapshot.child(firebaseUserPathNomeKey).getValue(String.class),
                                dataSnapshot.child(firebaseUserPathEmailKey).getValue(String.class),
                                clientesMap.get(dataSnapshot.child(firebaseUserPathClienteKey).getValue(String.class)),
                                dataSnapshot.child(firebaseUserPathAccessLevelKey).getValue(String.class),
                                dataSnapshot.child(firebaseUserPathThirdKey).child(firebaseUserPathPermissionKey).getValue(String.class),
                                dataSnapshot.child(firebaseUserPathThirdKey).child(firebaseUserPathReportsPermissionKey).getValue(String.class),
                                dataSnapshot.child(firebaseUserPathMatriculaKey).getValue(String.class),
                                dataSnapshot.child(firebaseUserPathTelefoneKey).getValue(String.class),
                                dataSnapshot.child(firebaseUserPathImgUrlKey).getValue(String.class)
                        );
                        if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiSingleUserCallback.onCallback(usuario);
                    }catch (Exception e){
                        if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
                        else ErrorHandling.handleError(contextException, e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Exception e = new Exception(databaseError.getMessage());
                    if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
                    else ErrorHandling.handleError(contextException, e);
                }
            });
        }catch (Exception e){
            if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
            else ErrorHandling.handleError(contextException, e);
        }
    }
}
