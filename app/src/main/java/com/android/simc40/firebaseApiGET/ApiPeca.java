package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.activityStatus.ActivityStatus;
import com.android.simc40.classes.Elemento;
import com.android.simc40.classes.Obra;
import com.android.simc40.classes.Peca;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.errorHandling.QualidadeException;
import com.android.simc40.errorHandling.QualidadeExceptionErrorList;
import com.android.simc40.dialogs.LoadingDialog;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.TreeMap;

public class ApiPeca implements DefaultErrorMessage, FirebaseDatabaseExceptionErrorList, QualidadeExceptionErrorList {
    DatabaseReference reference;
    Activity activity;
    String contextException, database, tag;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    ApiPecaCallback apiPecaCallback;
    Peca peca;
    Obra obra;
    Elemento elemento;
    TreeMap<String, Elemento> ElementoMap;
    boolean exists = false;

    public ApiPeca(Activity activity, String database, String contextException, LoadingDialog loadingDialog, ErrorDialog errorDialog, ApiPecaCallback apiPecaCallback, Obra obra, String tag){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingDialog = loadingDialog;
        this.errorDialog = errorDialog;
        this.apiPecaCallback = apiPecaCallback;
        this.obra = obra;
        this.tag = tag;

        ApiElementosCallback apiElementosCallback = response -> {
            ElementoMap = response;
            getPeca();
        };

        new ApiElementos(activity, database, contextException, loadingDialog, errorDialog, apiElementosCallback, false);
    }

    private void getPeca(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child("pecas").child(obra.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getValue() == null) throw new QualidadeException(EXCEPTION_TAG_NOT_FOUND);
                        for(DataSnapshot datasnapshot1: dataSnapshot.getChildren()){
                            String elementoUid = datasnapshot1.getKey();
                            if(datasnapshot1.child(tag).exists()) {
                                if(ElementoMap != null) elemento = ElementoMap.get(elementoUid);
                                exists = true;
                                break;
                            }
                        }
                        if(!exists) throw new QualidadeException(EXCEPTION_TAG_NOT_FOUND);
                        else{
                            String etapaAtual = dataSnapshot.child(elemento.getUid()).child(tag).child("etapa_atual").getValue(String.class);
                            String nome_peca =dataSnapshot.child(elemento.getUid()).child(tag).child("nome_peca").getValue(String.class);
                            peca = new Peca(tag, etapaAtual, obra, elemento, nome_peca);
                        }
                        if(ActivityStatus.activityIsRunning(activity)) apiPecaCallback.onCallback(peca);
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