package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.checklist.Etapas;
import com.android.simc40.classes.Checklist;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseChecklistPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ApiChecklist implements DefaultErrorMessage, FirebaseChecklistPaths, FirebaseDatabaseExceptionErrorList, Etapas {

    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    ApiChecklistCallback apiChecklistCallback;
    HashMap<String, String> usersMap;
    Hashtable<String, Checklist> checklistMap = new Hashtable<>();

    public ApiChecklist(Activity activity, String database, String contextException, LoadingPage loadingPage, ErrorDialog errorDialog, ApiChecklistCallback apiChecklistCallback, HashMap<String, String> usersMap){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingPage = loadingPage;
        this.errorDialog = errorDialog;
        this.apiChecklistCallback = apiChecklistCallback;

        if(usersMap == null) {
            ApiNameUsersCallback apiNameUsersCallback = response -> {
                this.usersMap = response;
                getChecklist();
            };
            new ApiNameUsers(activity, contextException, loadingPage, errorDialog, apiNameUsersCallback);
        }else{
            this.usersMap = usersMap;
            getChecklist();
        }
    }

    private void getChecklist(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseChecklistPathFirstKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                        Hashtable<String, String> uidCheckListAtual = new Hashtable<>();
                        for (String etapa : firebaseCheckLists) uidCheckListAtual.put(etapa, dataSnapshot.child(etapa).getValue(String.class));
                        for (String etapa: uidCheckListAtual.keySet()){
                            String uidEtapa = uidCheckListAtual.get(etapa);
                            if(uidEtapa == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
                            Map<String, String> checklist = (Map) dataSnapshot.child(firebaseChecklistPathSecondKey).child(etapa).child(uidEtapa).getValue();
                            if(checklist == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
                            String creation = checklist.get(firebaseChecklistPathCreationKey);
                            String createdBy = usersMap.get(checklist.get(firebaseChecklistPathCreatedByKey));
                            checklist.remove(firebaseChecklistPathCreationKey);
                            checklist.remove(firebaseChecklistPathCreatedByKey);
                            Checklist item = new Checklist(uidEtapa, etapa, checklist, creation, createdBy);
                            checklistMap.put(etapa, item);
                        }
                        if(activityIsRunning()) apiChecklistCallback.onCallback(checklistMap);
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