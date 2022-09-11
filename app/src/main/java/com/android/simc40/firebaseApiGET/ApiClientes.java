package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.classes.Cliente;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseClientePaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ApiClientes implements DefaultErrorMessage, FirebaseClientePaths, FirebaseDatabaseExceptionErrorList {
    Activity activity;
    DatabaseReference reference;
    HashMap<String, Cliente> clientesMap = new HashMap<>();

    public ApiClientes(Activity activity, String contextException, LoadingPage loadingPage, ErrorDialog errorDialog, ApiClientesCallback apiClientesCallback){
        this.activity = activity;
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance().getReference().child(firebaseClientePathFirstKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException(EXCEPTION_EMPTY_CLIENT_DATABASE);
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            if(dataSnapshot1.getKey() == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
                            Cliente cliente = new Cliente(
                                dataSnapshot1.getKey(),
                                dataSnapshot1.child(firebaseClientePathNomeClientKey).getValue(String.class),
                                dataSnapshot1.child(firebaseClientePathUfKey).getValue(String.class),
                                dataSnapshot1.child(firebaseClientePathDatabaseKey).getValue(String.class),
                                dataSnapshot1.child(firebaseClientePathStorageKey).getValue(String.class),
                                dataSnapshot1.child(firebaseClientePathStatusKey).getValue(String.class)
                            );
                            clientesMap.put(cliente.getUid(), cliente);
                        }
                        if(activityIsRunning()) apiClientesCallback.onCallback(clientesMap);
                    }catch (Exception e){
                        if(activityIsRunning()) ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
                        else ErrorHandling.handleError(contextException, e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Exception e = databaseError.toException();
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
