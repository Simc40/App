package com.simc.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.simc.simc40.activityStatus.ActivityStatus;
import com.simc.simc40.classes.Forma;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseFormaPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ApiFormas implements DefaultErrorMessage, FirebaseFormaPaths, FirebaseDatabaseExceptionErrorList {

    public final static int ticks = 3;
    Activity activity;
    DatabaseReference reference;
    HashMap<String, Forma> formaMap = new HashMap<>();

    public ApiFormas(Activity activity, String database, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ApiFormasCallback apiFormasCallback){
        this.activity = activity;
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseFormaPathFirstKey);
            if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) {
                            modalAlertaDeErro.getBotao().setOnClickListener(view -> activity.finish());
                            throw new FirebaseDatabaseException(EXCEPTION_NULL_DATABASE_FORMAS);
                        }
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
                        if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiFormasCallback.onCallback(formaMap);
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
