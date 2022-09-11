package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.classes.TipoDePeca;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseTipoDePecaPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ApiTiposDePeca implements DefaultErrorMessage, FirebaseTipoDePecaPaths, FirebaseDatabaseExceptionErrorList {
    Activity activity;
    DatabaseReference reference;
    HashMap<String, TipoDePeca> tipoDePecaMap = new HashMap<>();

    public ApiTiposDePeca(Activity activity, String database, String contextException, LoadingPage loadingPage, ErrorDialog errorDialog, ApiTiposDePecaCallback apiTiposDePecaCallback){
        this.activity = activity;
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseTipoDePecaPathFirstKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            TipoDePeca tipoDePeca = new TipoDePeca(
                                dataSnapshot1.getKey(),
                                dataSnapshot1.child(firebaseTipoDePecaPathNomeKey).getValue(String.class),
                                dataSnapshot1.child(firebaseTipoDePecaPathImgUrlKey).getValue(String.class),
                                dataSnapshot1.child(firebaseTipoDePecaPathStatusKey).getValue(String.class),
                                dataSnapshot1.child(firebaseTipoDePecaPathCreationKey).getValue(String.class),
                                dataSnapshot1.child(firebaseTipoDePecaPathCreatedByKey).getValue(String.class),
                                dataSnapshot1.child(firebaseTipoDePecaPathLastModifiedOnKey).getValue(String.class),
                                dataSnapshot1.child(firebaseTipoDePecaPathLastModifiedByKey).getValue(String.class)
                            );
                            tipoDePecaMap.put(tipoDePeca.getUid(), tipoDePeca);
                        }
                        if(activityIsRunning()) apiTiposDePecaCallback.onCallback(tipoDePecaMap);
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

    @Override
    public String toString() {
        return "ApiTiposDePeca{" + "activity=" + activity + ", reference=" + reference + ", tipoDePecaMap=" + tipoDePecaMap + '}';
    }
}
