package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ApiSingleUser implements DefaultErrorMessage, FirebaseUserPaths, FirebaseDatabaseExceptionErrorList {

    DatabaseReference reference;
    Activity activity;
    String contextException;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    ApiSingleUserCallback apiSingleUserCallback;
    HashMap<String, Cliente> clientesMap;
    String uid;
    User user;


    public ApiSingleUser(Activity activity, String contextException, LoadingPage loadingPage, ErrorDialog errorDialog, ApiSingleUserCallback apiSingleUserCallback, String uid) throws FirebaseDatabaseException {
        if(uid == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
        this.uid = uid;
        this.activity = activity;
        this.contextException = contextException;
        this.loadingPage = loadingPage;
        this.errorDialog = errorDialog;
        this.apiSingleUserCallback = apiSingleUserCallback;

        ApiClientesCallback apiClientesCallback = response -> {
            clientesMap = response;
            getUser();
        };

        new ApiClientes(activity, contextException, loadingPage, errorDialog, apiClientesCallback);
    }

    private void getUser(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance().getReference().child(firebaseUserPathFirstKey).child(uid);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException(EXCEPTION_USER_NOT_FOUND);
                        user = new User(
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
                        if(activityIsRunning()) apiSingleUserCallback.onCallback(user);
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
