package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.accessLevel.AccessLevel;
import com.android.simc40.classes.Cliente;
import com.android.simc40.classes.User;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseUserPaths;
import com.android.simc40.loadingPage.LoadingPage;
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

    DatabaseReference reference;
    Activity activity;
    String contextException;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    ApiUsersCallback apiUsersCallback;
    HashMap<String, Cliente> clientesMap;
    TreeMap<String, User> usersMap = new TreeMap<>();
    String accessLevel, uidCliente;

    public ApiUsers(Activity activity, String contextException, LoadingPage loadingPage, ErrorDialog errorDialog, ApiUsersCallback apiUsersCallback, String uidCliente, String accessLevel) throws Exception {
        if(accessLevel == null || uidCliente == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
        if(accessLevel.equals(accessLevelUser)) {
            errorDialog.getButton().setOnClickListener(view -> activity.onBackPressed());
            throw new FirebaseAuthException("ERROR_ACCESS_DENIED", defaultErrorMessage);
        }
        this.activity = activity;
        this.contextException = contextException;
        this.loadingPage = loadingPage;
        this.errorDialog = errorDialog;
        this.apiUsersCallback = apiUsersCallback;
        this.uidCliente = uidCliente;
        this.accessLevel = accessLevel;

        ApiClientesCallback apiClientesCallback = response -> {
            clientesMap = response;
            getUsers();
        };

        new ApiClientes(activity, contextException, loadingPage, errorDialog, apiClientesCallback);
    }

    private void getUsers(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance().getReference().child(firebaseUserPathFirstKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            String userAcesso = dataSnapshot1.child(firebaseUserPathAccessLevelKey).getValue(String.class);
                            String userCliente = dataSnapshot1.child(firebaseUserPathClienteKey).getValue(String.class);
                            if(userAcesso == null || userCliente == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
                            if(!userCliente.equals(uidCliente)) continue;
                            if(accessLevel.equals(accessLevelResponsable) && userAcesso.equals(accessLevelAdmin)) continue;
                            User user = new User(
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
                            System.out.println(user);
                            usersMap.put(accessLevelMap.get(user.getAccessLevel()) + user.getNome(), user);
                        }
                        if(activityIsRunning()) apiUsersCallback.onCallback(usersMap);
                    }catch (Exception e){
                        if(activityIsRunning()) ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
                        else ErrorHandling.handleError(contextException, e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Exception e = new Exception(databaseError.getMessage());
                    if(activityIsRunning()) ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
                    else ErrorHandling.handleError(contextException, e);
                }
            });
        }catch (Exception e){
            if(activityIsRunning()) ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            else ErrorHandling.handleError(contextException, e);
        }
    }

    private boolean activityIsRunning(){
        return !(activity.isFinishing() || activity.isDestroyed());
    }
}
