package com.simc.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.simc.simc40.activityStatus.ActivityStatus;
import com.simc.simc40.classes.Obra;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseObraPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.TreeMap;

public class ApiObras implements DefaultErrorMessage, FirebaseObraPaths, FirebaseDatabaseExceptionErrorList {

    public final static int ticks = ApiNameUsers.ticks + ApiPDFObra.ticks + 3;
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    ApiObrasCallback apiObrasCallback;
    HashMap<String, String> usersMap;
    HashMap<String, String> pdfMap;
    TreeMap<String, Obra> obrasMap = new TreeMap<>();
    boolean responseUsers = false, responsePDF = false;

    public ApiObras(Activity activity, String database, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ApiObrasCallback apiObrasCallback){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.modalDeCarregamento = modalDeCarregamento;
        this.modalAlertaDeErro = modalAlertaDeErro;
        this.apiObrasCallback = apiObrasCallback;

        ApiNameUsersCallback apiNameUsersCallback = response -> {
            usersMap = response;
            responseUsers = true;
            checkResponse();
        };

        new ApiNameUsers(activity, contextException, modalDeCarregamento, modalAlertaDeErro, apiNameUsersCallback);

        ApiPDFObraCallback apiPDFObraCallback = response -> {
            pdfMap = response;
            responsePDF = true;
            checkResponse();
        };

        new ApiPDFObra(activity, database, contextException, modalDeCarregamento, modalAlertaDeErro, apiPDFObraCallback);
    }

    private void checkResponse(){
        if(responseUsers && responsePDF) getObras();
    }

    private void getObras(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseObraPathFirstKey);
            if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) {
                            modalAlertaDeErro.getBotao().setOnClickListener(view -> activity.finish());
                            throw new FirebaseDatabaseException(EXCEPTION_NULL_DATABASE_OBRAS);
                        }
                        for(DataSnapshot datasnapshot1: dataSnapshot.getChildren()){
                            Obra obra = datasnapshot1.getValue(Obra.class);
                            obra.setPdfUrl(pdfMap.get(datasnapshot1.getKey()));
                            obra.setUsers(usersMap);
                            obrasMap.put(obra.getUid(), obra);
                        }
                        if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiObrasCallback.onCallback(obrasMap);
                    }catch (Exception e){
                        if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
                        else ErrorHandling.handleError(contextException, e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Exception e = new Exception(databaseError.getMessage());
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