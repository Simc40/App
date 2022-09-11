package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebasePdfPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ApiPDFElemento implements DefaultErrorMessage, FirebasePdfPaths, FirebaseDatabaseExceptionErrorList {

    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    ApiPDFElementoCallback apiPDFElementoCallback;
    HashMap<String, HashMap<String,String>> PDFmap = new HashMap<>();

    public ApiPDFElemento(Activity activity, String database, String contextException, LoadingPage loadingPage, ErrorDialog errorDialog, ApiPDFElementoCallback apiPDFElementoCallback){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingPage = loadingPage;
        this.errorDialog = errorDialog;
        this.apiPDFElementoCallback = apiPDFElementoCallback;

        getPDFElementos();
    }

    private void getPDFElementos(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebasePdfElementoPathFirstKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getValue() == null) {
                            if(activityIsRunning()) apiPDFElementoCallback.onCallback(PDFmap);
                            return;
                        }
                        for(DataSnapshot datasnapshot1: dataSnapshot.getChildren()){
                            String obraUid = datasnapshot1.getKey();
                            HashMap<String, String> mapPDFElementos = new HashMap<>();
                            for(DataSnapshot datasnapshot2: datasnapshot1.getChildren()) {
                                String elementoUid = datasnapshot2.getKey();
                                String activePDFUid = datasnapshot2.child(firebasePdfPathSecondKey).getValue(String.class);
                                String pdfUrl = datasnapshot2.child(activePDFUid).child(firebasePdfPathPdfUrlKey).getValue(String.class);
                                mapPDFElementos.put(elementoUid, pdfUrl);
                            }
                            PDFmap.put(obraUid, mapPDFElementos);
                        }
                        if(activityIsRunning()) apiPDFElementoCallback.onCallback(PDFmap);
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