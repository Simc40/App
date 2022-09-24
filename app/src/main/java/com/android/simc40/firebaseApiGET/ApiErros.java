package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.activityStatus.ActivityStatus;
import com.android.simc40.classes.Checklist;
import com.android.simc40.classes.Erro;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseErrosPaths;
import com.android.simc40.dialogs.LoadingDialog;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;

public class ApiErros implements DefaultErrorMessage, FirebaseErrosPaths, FirebaseDatabaseExceptionErrorList {

    public final static int ticks = ApiNameUsers.ticks + ApiChecklistHistory.ticksWithApiNameUsers + 3;
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    ApiErrosCallback apiErrosCallback;
    HashMap<String, String> usersMap;
    Hashtable<String, Checklist> checklistMap;
    TreeMap<String, Erro> errosMap = new TreeMap<>();
    boolean onlyOpenErros;

    public ApiErros(Activity activity, String database, String contextException, LoadingDialog loadingDialog, ErrorDialog errorDialog, ApiErrosCallback apiErrosCallback, boolean onlyOpenErros){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingDialog = loadingDialog;
        this.errorDialog = errorDialog;
        this.apiErrosCallback = apiErrosCallback;
        this.onlyOpenErros = onlyOpenErros;

        ApiNameUsersCallback apiNameUsersCallback = response -> {
            this.usersMap = response;
            callApiChecklist();
        };
        new ApiNameUsers(activity, contextException, loadingDialog, errorDialog, apiNameUsersCallback);
    }

    private void callApiChecklist(){
        ApiChecklistCallback apiChecklistCallback = response -> {
            checklistMap = response;
            getErros();
        };
        new ApiChecklistHistory(activity, database, contextException, loadingDialog, errorDialog, apiChecklistCallback, usersMap);
    }

    private void getErros(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseErrosPathsFirstKey);
            if(loadingDialog != null) loadingDialog.tick(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(loadingDialog != null) loadingDialog.tick(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) {
                            apiErrosCallback.onCallback(errosMap);
                            return;
                        }
                        for(DataSnapshot datasnapshot1: dataSnapshot.getChildren()){
                            String uidObra = datasnapshot1.getKey();
                            for(DataSnapshot datasnapshot2: datasnapshot1.getChildren()) {
                                String uidElementoOrTag = datasnapshot2.getKey();
                                for(DataSnapshot datasnapshot3: datasnapshot2.getChildren()) {
                                    String uidErro = datasnapshot3.getKey();
                                    Checklist checklist = checklistMap.get(datasnapshot3.child(firebaseErrosPathsChecklist).getValue(String.class));
                                    if(checklist == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
                                    Erro erro = new Erro(
                                    datasnapshot3.child(firebaseErrosPathsItem).getValue(String.class),
                                    uidObra,
                                    uidElementoOrTag,
                                    checklist,
                                    datasnapshot3.child(firebaseErrosPathsComentarios).getValue(String.class),
                                    datasnapshot3.child(firebaseErrosPathsComentariosSolucao).getValue(String.class),
                                    (usersMap.get(datasnapshot3.child(firebaseErrosPathsCreatedBy).getValue(String.class)) != null) ? usersMap.get(datasnapshot3.child(firebaseErrosPathsCreatedBy).getValue(String.class)) : null,
                                    datasnapshot3.child(firebaseErrosPathsCreation).getValue(String.class),
                                    datasnapshot3.child(firebaseErrosPathsEtapaDetectada).getValue(String.class),
                                    (datasnapshot3.child(firebaseErrosPathsImgUrlDefeito).exists()) ? datasnapshot3.child(firebaseErrosPathsImgUrlDefeito).getValue(String.class) : null,
                                    (datasnapshot3.child(firebaseErrosPathsImgUrlSolucao).exists()) ? datasnapshot3.child(firebaseErrosPathsImgUrlSolucao).getValue(String.class) : null,
                                    datasnapshot3.child(firebaseErrosPathsLastModifiedOn).getValue(String.class),
                                    (usersMap.get(datasnapshot3.child(firebaseErrosPathsLastModifiedBy).getValue(String.class)) != null) ? usersMap.get(datasnapshot3.child(firebaseErrosPathsLastModifiedBy).getValue(String.class)) : null,
                                    datasnapshot3.child(firebaseErrosPathsStatus).getValue(String.class),
                                    datasnapshot3.child(firebaseErrosPathsStatusLocked).getValue(String.class),
                                    uidErro
                                    );
                                    if(!onlyOpenErros) errosMap.put(erro.getCreation() + erro.getUid(), erro);
                                    else if(erro.getStatus().equals("aberto")) errosMap.put(erro.getCreation() + erro.getUid(), erro);
                                }
                            }
                        }
                        if(loadingDialog != null) loadingDialog.tick(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiErrosCallback.onCallback(errosMap);
                    }catch (Exception e){
                        if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
                        else ErrorHandling.handleError(contextException, e);
                        if(ActivityStatus.activityIsRunning(activity)) {
                            try {
                                apiErrosCallback.onCallback(errosMap);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
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
