package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.classes.Galpao;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseGalpaoPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.TreeMap;

public class ApiGalpoes implements DefaultErrorMessage, FirebaseGalpaoPaths, FirebaseDatabaseExceptionErrorList {
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    ApiGalpoesCallback apiGalpoesCallback;
    HashMap<String, String> usersMap;
    TreeMap<String, Galpao> galpoesMap = new TreeMap<>();

    public ApiGalpoes(Activity activity, String database, String contextException, LoadingPage loadingPage, ErrorDialog errorDialog, ApiGalpoesCallback apiGalpoesCallback, HashMap<String, String> usersMap){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingPage = loadingPage;
        this.errorDialog = errorDialog;
        this.apiGalpoesCallback = apiGalpoesCallback;

        if(usersMap == null) {
            ApiNameUsersCallback apiNameUsersCallback = response -> {
                this.usersMap = response;
                getGalpoes();
            };
            new ApiNameUsers(activity, contextException, loadingPage, errorDialog, apiNameUsersCallback);
        }else{
            this.usersMap = usersMap;
            getGalpoes();
        }

    }

    private void getGalpoes(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseGalpaoPathFirstKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                        for(DataSnapshot datasnapshot1: dataSnapshot.getChildren()){
                            Galpao galpao = new Galpao(
                                datasnapshot1.getKey(),
                                datasnapshot1.child(firebaseGalpaoPathNomeKey).getValue(String.class),
                                datasnapshot1.child(firebaseGalpaoPathStatusKey).getValue(String.class),
                                datasnapshot1.child(firebaseGalpaoPathCreationKey).getValue(String.class),
                                (usersMap != null) ? usersMap.get(datasnapshot1.child(firebaseGalpaoPathCreatedByKey).getValue(String.class)) : "",
                                datasnapshot1.child(firebaseGalpaoPathLastModifiedOnKey).getValue(String.class),
                                (usersMap != null) ? usersMap.get(datasnapshot1.child(firebaseGalpaoPathLastModifiedByKey).getValue(String.class)) : ""
                            );
                            galpoesMap.put(galpao.getNome() + galpao.getUid(), galpao);
                        }
                        if(activityIsRunning()) apiGalpoesCallback.onCallback(galpoesMap);
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
