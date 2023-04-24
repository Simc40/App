package com.simc.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.simc.simc40.activityStatus.ActivityStatus;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseClientePaths;
import com.simc.simc40.firebasePaths.FirebaseUserPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ApiNameUsers implements DefaultErrorMessage, FirebaseUserPaths, FirebaseClientePaths, FirebaseDatabaseExceptionErrorList {

    public final static int ticks = 3;
    Activity activity;
    DatabaseReference reference;
    HashMap<String, String> usersMap = new HashMap<>();

    public ApiNameUsers(Activity activity, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ApiNameUsersCallback apiNameUsersCallback){
        this.activity = activity;
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            modalDeCarregamento.avancarPasso(); // 1
            reference = FirebaseDatabase.getInstance().getReference().child(firebaseUserPathFirstKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        modalDeCarregamento.avancarPasso(); // 2
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            String userUid = dataSnapshot1.getKey();
                            String userName = dataSnapshot1.child(firebaseUserPathNomeKey).getValue(String.class);
                            usersMap.put(userUid, userName);
                        }
                        modalDeCarregamento.avancarPasso(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiNameUsersCallback.onCallback(usersMap);
                    }catch (Exception e){
                        if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
                        else ErrorHandling.handleError(contextException, e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Exception e = databaseError.toException();
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