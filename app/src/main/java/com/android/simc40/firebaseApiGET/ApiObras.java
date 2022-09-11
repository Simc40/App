package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.classes.Obra;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseObraPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;

public class ApiObras implements DefaultErrorMessage, FirebaseObraPaths, FirebaseDatabaseExceptionErrorList {

    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    ApiObrasCallback apiObrasCallback;
    HashMap<String, String> usersMap;
    HashMap<String, String> pdfMap;
    TreeMap<String, Obra> obrasMap = new TreeMap<>();
    boolean responseUsers = false, responsePDF = false;

    public ApiObras(Activity activity, String database, String contextException, LoadingPage loadingPage, ErrorDialog errorDialog, ApiObrasCallback apiObrasCallback){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingPage = loadingPage;
        this.errorDialog = errorDialog;
        this.apiObrasCallback = apiObrasCallback;

        ApiNameUsersCallback apiNameUsersCallback = response -> {
            usersMap = response;
            responseUsers = true;
            checkResponse();
        };

        new ApiNameUsers(activity, contextException, loadingPage, errorDialog, apiNameUsersCallback);

        ApiPDFObraCallback apiPDFObraCallback = response -> {
            pdfMap = response;
            responsePDF = true;
            checkResponse();
        };

        new ApiPDFObra(activity, database, contextException, loadingPage, errorDialog, apiPDFObraCallback);
    }

    private void checkResponse(){
        if(responseUsers && responsePDF) getObras();
    }

    private void getObras(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseObraPathFirstKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                        for(DataSnapshot datasnapshot1: dataSnapshot.getChildren()){
                            Obra obra = new Obra(
                                datasnapshot1.getKey(),
                                datasnapshot1.child(firebaseObraPathNomeKey).getValue(String.class),
                                (usersMap != null) ? usersMap.get(datasnapshot1.child(firebaseObraPathResponsavelKey).getValue(String.class)) : "",
                                datasnapshot1.child(firebaseObraPathTipoConstrucaoKey).getValue(String.class),
                                datasnapshot1.child(firebaseObraPathCEPKey).getValue(String.class),
                                datasnapshot1.child(firebaseObraPathCidadeKey).getValue(String.class),
                                datasnapshot1.child(firebaseObraPathBairroKey).getValue(String.class),
                                datasnapshot1.child(firebaseObraPathEnderecoKey).getValue(String.class),
                                datasnapshot1.child(firebaseObraPathUfKey).getValue(String.class),
                                datasnapshot1.child(firebaseObraPathQuantidadePecasKey).getValue(String.class),
                                datasnapshot1.child(firebaseObraPathPrevisaoInicioKey).getValue(String.class),
                                datasnapshot1.child(firebaseObraPathPrevisaoFimKey).getValue(String.class),
                                datasnapshot1.child(firebaseObraPathStatusKey).getValue(String.class),
                                datasnapshot1.child(firebaseObraPathCreationKey).getValue(String.class),
                                (usersMap != null) ? usersMap.get(datasnapshot1.child(firebaseObraPathCreatedByKey).getValue(String.class)) : "",
                                datasnapshot1.child(firebaseObraPathLastModifiedOnKey).getValue(String.class),
                                (usersMap != null) ? usersMap.get(datasnapshot1.child(firebaseObraPathLastModifiedByKey).getValue(String.class)) : "",
                                (pdfMap.get(datasnapshot1.getKey()))
                            );
                            obrasMap.put(obra.getUid(), obra);
                        }
                        if(activityIsRunning()) apiObrasCallback.onCallback(obrasMap);
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