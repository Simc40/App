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
import com.android.simc40.errorHandling.QualidadeExceptionErrorList;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.firebasePaths.FirebasePecaPaths;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.TreeMap;

public class ApiPecas implements DefaultErrorMessage, FirebaseDatabaseExceptionErrorList, QualidadeExceptionErrorList, FirebasePecaPaths {

    public static final int ticks = ApiElementos.ticks + 3;
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    ApiPecasCallback apiPecasCallback;
    Obra obra;
    TreeMap<String, Obra> obrasMap;
    TreeMap<String, Elemento> elementosMap;
    TreeMap<String, Peca> pecaTreeMap = new TreeMap<>();
    ApiElementos apiElementos;

    public ApiPecas(Activity activity, String database, String contextException, LoadingDialog loadingDialog, ErrorDialog errorDialog, ApiPecasCallback apiPecasCallback, Obra obra){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingDialog = loadingDialog;
        this.errorDialog = errorDialog;
        this.apiPecasCallback = apiPecasCallback;
        this.obra = obra;

        ApiElementosCallback apiElementosCallback = response -> {
            elementosMap = response;
            obrasMap = apiElementos.getObras();
            getPeca();
        };

        apiElementos = new ApiElementos(activity, database, contextException, loadingDialog, errorDialog, apiElementosCallback, false);
    }

    private void getPeca(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebasePecaPathsFirstKey);
            if(loadingDialog != null) loadingDialog.tick(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(loadingDialog != null) loadingDialog.tick(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) {
                            if(ActivityStatus.activityIsRunning(activity)) apiPecasCallback.onCallback(pecaTreeMap);
                            return;
                        }

                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            String uidObra = dataSnapshot1.getKey();
                            Obra newObra = obrasMap.get(uidObra);
                            if(obra != null && !obra.getUid().equals(newObra.getUid())) continue;
                            for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){
                                String elementoUid = dataSnapshot2.getKey();
                                Elemento elemento = elementosMap.get(elementoUid);
                                for(DataSnapshot datasnapshot3: dataSnapshot2.getChildren()){
                                    String tag = datasnapshot3.getKey();
                                    String etapaAtual = datasnapshot3.child(firebasePecaPathsEtapaAtual).getValue(String.class);
                                    String nomePeca = datasnapshot3.child(firebasePecaPathsNomePeca).getValue(String.class);
                                    Peca peca = new Peca(tag, etapaAtual, newObra, elemento, nomePeca);
                                    pecaTreeMap.put(tag, peca);
                                }
                            }
                        }
                        if(loadingDialog != null) loadingDialog.tick(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiPecasCallback.onCallback(pecaTreeMap);
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

    public TreeMap<String, Obra> getObras(){
        return obrasMap;
    }

    public TreeMap<String, Elemento> getElementos(){
        return elementosMap;
    }
}