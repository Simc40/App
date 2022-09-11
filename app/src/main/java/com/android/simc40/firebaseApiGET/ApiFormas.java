package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.classes.Forma;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseFormaPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ApiFormas implements DefaultErrorMessage, FirebaseFormaPaths, FirebaseDatabaseExceptionErrorList {

    Activity activity;
    DatabaseReference reference;
    HashMap<String, Forma> formaMap = new HashMap<>();

    public ApiFormas(Activity activity, String database, String contextException, LoadingPage loadingPage, ErrorDialog errorDialog, ApiFormasCallback apiFormasCallback){
        this.activity = activity;
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseFormaPathFirstKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Forma forma = new Forma(
                                dataSnapshot1.getKey(),
                                dataSnapshot1.child(firebaseFormaPathBKey).getValue(String.class),
                                dataSnapshot1.child(firebaseFormaPathHKey).getValue(String.class),
                                dataSnapshot1.child(firebaseFormaPathNomeKey).getValue(String.class),
                                dataSnapshot1.child(firebaseFormaPathStatusKey).getValue(String.class),
                                dataSnapshot1.child(firebaseFormaPathCreationKey).getValue(String.class),
                                dataSnapshot1.child(firebaseFormaPathCreatedByKey).getValue(String.class),
                                dataSnapshot1.child(firebaseFormaPathLastModifiedOnKey).getValue(String.class),
                                dataSnapshot1.child(firebaseFormaPathLastModifiedByKey).getValue(String.class)
                            );

                            formaMap.put(forma.getUid(), forma);
                        }
                        if(activityIsRunning()) apiFormasCallback.onCallback(formaMap);
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
