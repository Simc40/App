package com.simc.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.simc.simc40.accessLevel.AccessLevel;
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
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.TreeMap;

public class ApiUsers implements DefaultErrorMessage, FirebaseUserPaths, FirebaseDatabaseExceptionErrorList, AccessLevel {

    public final static int ticks = ApiClientes.ticks + 3;
    DatabaseReference reference;
    Activity activity;
    String contextException;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    ApiUsersCallback apiUsersCallback;
    HashMap<String, Cliente> clientesMap;
    TreeMap<String, Usuario> usersMap = new TreeMap<>();
    String accessLevel, uidCliente;

    public ApiUsers(Activity activity, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ApiUsersCallback apiUsersCallback, String uidCliente, String accessLevel) throws Exception {
        if(accessLevel == null || uidCliente == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
        if(accessLevel.equals(accessLevelUser)) {
            modalAlertaDeErro.getBotao().setOnClickListener(view -> activity.onBackPressed());
            throw new FirebaseAuthException("ERROR_ACCESS_DENIED", defaultErrorMessage);
        }
        this.activity = activity;
        this.contextException = contextException;
        this.modalDeCarregamento = modalDeCarregamento;
        this.modalAlertaDeErro = modalAlertaDeErro;
        this.apiUsersCallback = apiUsersCallback;
        this.uidCliente = uidCliente;
        this.accessLevel = accessLevel;

        ApiClientesCallback apiClientesCallback = response -> {
            clientesMap = response;
            getUsers();
        };

        new ApiClientes(activity, contextException, modalDeCarregamento, modalAlertaDeErro, apiClientesCallback);
    }

    private void getUsers(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance().getReference().child(firebaseUserPathFirstKey);
            if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            String userAcesso = dataSnapshot1.child(firebaseUserPathAccessLevelKey).getValue(String.class);
                            String userCliente = dataSnapshot1.child(firebaseUserPathClienteKey).getValue(String.class);
                            if(userAcesso == null || userCliente == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
                            if(!userCliente.equals(uidCliente)) continue;
                            if(accessLevel.equals(accessLevelResponsable) && userAcesso.equals(accessLevelAdmin)) continue;
                            Usuario usuario = new Usuario(
                                dataSnapshot1.getKey(),
                                dataSnapshot1.child(firebaseUserPathNomeKey).getValue(String.class),
                                dataSnapshot1.child(firebaseUserPathEmailKey).getValue(String.class),
                                clientesMap.get(dataSnapshot1.child(firebaseUserPathClienteKey).getValue(String.class)),
                                dataSnapshot1.child(firebaseUserPathAccessLevelKey).getValue(String.class),
                                dataSnapshot1.child(firebaseUserPathThirdKey).child(firebaseUserPathPermissionKey).getValue(String.class),
                                dataSnapshot1.child(firebaseUserPathThirdKey).child(firebaseUserPathReportsPermissionKey).getValue(String.class),
                                dataSnapshot1.child(firebaseUserPathMatriculaKey).getValue(String.class),
                                dataSnapshot1.child(firebaseUserPathTelefoneKey).getValue(String.class),
                                dataSnapshot1.child(firebaseUserPathImgUrlKey).getValue(String.class)
                            );
                            usersMap.put(accessLevelMap.get(usuario.getAccessLevel()) + usuario.getNome(), usuario);
                        }
                        if(usersMap.size() == 0) {
                            modalAlertaDeErro.getBotao().setOnClickListener(view -> activity.finish());
                            throw new FirebaseDatabaseException(EXCEPTION_NULL_DATABASE_USERS);
                        }
                        if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiUsersCallback.onCallback(usersMap);
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
