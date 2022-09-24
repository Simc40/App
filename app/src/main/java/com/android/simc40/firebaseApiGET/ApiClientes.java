package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.activityStatus.ActivityStatus;
import com.android.simc40.classes.Cliente;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseClientePaths;
import com.android.simc40.dialogs.LoadingDialog;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ApiClientes implements DefaultErrorMessage, FirebaseClientePaths, FirebaseDatabaseExceptionErrorList {

    public final static int ticks = 3;
    Activity activity;
    DatabaseReference reference;
    HashMap<String, Cliente> clientesMap = new HashMap<>();

    public ApiClientes(Activity activity, String contextException, LoadingDialog loadingDialog, ErrorDialog errorDialog, ApiClientesCallback apiClientesCallback){
        this.activity = activity;
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance().getReference().child(firebaseClientePathFirstKey);
            if(loadingDialog != null) loadingDialog.tick(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(loadingDialog != null) loadingDialog.tick(); // 2
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
                        if(loadingDialog != null) loadingDialog.tick(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiClientesCallback.onCallback(clientesMap);
                    }catch (Exception e){
                        if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
                        else ErrorHandling.handleError(contextException, e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Exception e = databaseError.toException();
                    if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
                    else ErrorHandling.handleError(contextException, e);
                }
            });
        }catch (Exception e){
            if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
            else ErrorHandling.handleError(contextException, e);
        }
    }

}
