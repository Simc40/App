package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.classes.Elemento;
import com.android.simc40.classes.Obra;
import com.android.simc40.classes.Peca;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.errorHandling.QualidadeException;
import com.android.simc40.errorHandling.QualidadeExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseObraPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.android.simc40.selecaoListas.SelecaoListaElementos;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.TreeMap;

public class ApiPeca implements DefaultErrorMessage, FirebaseDatabaseExceptionErrorList, QualidadeExceptionErrorList {
    DatabaseReference reference;
    Activity activity;
    String contextException, database, tag;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    ApiPecaCallback apiPecaCallback;
    Peca peca;
    Obra obra;
    Elemento elemento;
    TreeMap<String, Elemento> ElementoMap;
    boolean exists = false;

    public ApiPeca(Activity activity, String database, String contextException, LoadingPage loadingPage, ErrorDialog errorDialog, ApiPecaCallback apiPecaCallback, Obra obra, String tag){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingPage = loadingPage;
        this.errorDialog = errorDialog;
        this.apiPecaCallback = apiPecaCallback;
        this.obra = obra;
        this.tag = tag;

        ApiElementosCallback apiElementosCallback = response -> {
            ElementoMap = response;
            getPeca();
        };

        new ApiElementos(activity, database, contextException, loadingPage, errorDialog, apiElementosCallback, false);
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
                        if(activityIsRunning()) apiPecaCallback.onCallback(peca);
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