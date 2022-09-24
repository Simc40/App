package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.activityStatus.ActivityStatus;
import com.android.simc40.classes.Galpao;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseGalpaoPaths;
import com.android.simc40.dialogs.LoadingDialog;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.TreeMap;

public class ApiGalpoes implements DefaultErrorMessage, FirebaseGalpaoPaths, FirebaseDatabaseExceptionErrorList {

    public final static int ticks = ApiNameUsers.ticks + 3;
    public final static int ticksWithUsersMap = 3;
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    ApiGalpoesCallback apiGalpoesCallback;
    HashMap<String, String> usersMap;
    TreeMap<String, Galpao> galpoesMap = new TreeMap<>();

    public ApiGalpoes(Activity activity, String database, String contextException, LoadingDialog loadingDialog, ErrorDialog errorDialog, ApiGalpoesCallback apiGalpoesCallback, HashMap<String, String> usersMap){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingDialog = loadingDialog;
        this.errorDialog = errorDialog;
        this.apiGalpoesCallback = apiGalpoesCallback;

        if(usersMap == null) {
            ApiNameUsersCallback apiNameUsersCallback = response -> {
                this.usersMap = response;
                getGalpoes();
            };
            new ApiNameUsers(activity, contextException, loadingDialog, errorDialog, apiNameUsersCallback);
        }else{
            this.usersMap = usersMap;
            getGalpoes();
        }
    }

    private void getGalpoes(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseGalpaoPathFirstKey);
            if(loadingDialog != null) loadingDialog.tick(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(loadingDialog != null) loadingDialog.tick(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) {
                            errorDialog.getButton().setOnClickListener(view -> activity.finish());
                            throw new FirebaseDatabaseException(EXCEPTION_NULL_DATABASE_GALPOES);
                        }
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
                            if(loadingDialog != null) loadingDialog.tick(); // 3
                            galpoesMap.put(galpao.getNome() + galpao.getUid(), galpao);
                        }
                        if(ActivityStatus.activityIsRunning(activity)) apiGalpoesCallback.onCallback(galpoesMap);
                    }catch (Exception e){
                        if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
                        else ErrorHandling.handleError(contextException, e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Exception e = new Exception(databaseError.getMessage());
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
