package com.simc.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.simc.simc40.activityStatus.ActivityStatus;
import com.simc.simc40.classes.TipoDePeca;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseTipoDePecaPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ApiTiposDePeca implements DefaultErrorMessage, FirebaseTipoDePecaPaths, FirebaseDatabaseExceptionErrorList {
    public final static int ticks = 3;
    Activity activity;
    DatabaseReference reference;
    HashMap<String, TipoDePeca> tipoDePecaMap = new HashMap<>();

    public ApiTiposDePeca(Activity activity, String database, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ApiTiposDePecaCallback apiTiposDePecaCallback){
        this.activity = activity;
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseTipoDePecaPathFirstKey);
            if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 2
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
                        if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiTiposDePecaCallback.onCallback(tipoDePecaMap);
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

    @Override
    public String toString() {
        return "ApiTiposDePeca{" + "activity=" + activity + ", reference=" + reference + ", tipoDePecaMap=" + tipoDePecaMap + '}';
    }
}
