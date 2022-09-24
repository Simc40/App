package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.activityStatus.ActivityStatus;
import com.android.simc40.checklist.Etapas;
import com.android.simc40.classes.Checklist;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseChecklistPaths;
import com.android.simc40.dialogs.LoadingDialog;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ApiChecklistHistory implements DefaultErrorMessage, FirebaseChecklistPaths, FirebaseDatabaseExceptionErrorList, Etapas {

    public final static int ticks = ApiNameUsers.ticks + 3;
    public final static int ticksWithApiNameUsers = 3;
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    ApiChecklistCallback apiChecklistCallback;
    HashMap<String, String> usersMap;
    Hashtable<String, Checklist> checklistMap = new Hashtable<>();

    public ApiChecklistHistory(Activity activity, String database, String contextException, LoadingDialog loadingDialog, ErrorDialog errorDialog, ApiChecklistCallback apiChecklistCallback, HashMap<String, String> usersMap){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingDialog = loadingDialog;
        this.errorDialog = errorDialog;
        this.apiChecklistCallback = apiChecklistCallback;

        if(usersMap == null) {
            ApiNameUsersCallback apiNameUsersCallback = response -> {
                this.usersMap = response;
                getChecklist();
            };
            new ApiNameUsers(activity, contextException, loadingDialog, errorDialog, apiNameUsersCallback);
        }else{
            this.usersMap = usersMap;
            getChecklist();
        }
    }

    private void getChecklist(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseChecklistPathFirstKey).child(firebaseChecklistPathSecondKey);
            if(loadingDialog != null) loadingDialog.tick(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(loadingDialog != null) loadingDialog.tick(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            String etapa = dataSnapshot1.getKey();
                            for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                String uidChecklist = dataSnapshot2.getKey();
                                Map<String, String> checklist = (Map) dataSnapshot2.getValue();
                                if(checklist == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
                                String creation = checklist.get(firebaseChecklistPathCreationKey);
                                String createdBy = usersMap.get(checklist.get(firebaseChecklistPathCreatedByKey));
                                checklist.remove(firebaseChecklistPathCreationKey);
                                checklist.remove(firebaseChecklistPathCreatedByKey);
                                Checklist item = new Checklist(uidChecklist, etapa, checklist, creation, createdBy);
                                checklistMap.put(uidChecklist, item);
                            }
                        }
                        if(loadingDialog != null) loadingDialog.tick(); // 3;
                        if(ActivityStatus.activityIsRunning(activity)) apiChecklistCallback.onCallback(checklistMap);
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
