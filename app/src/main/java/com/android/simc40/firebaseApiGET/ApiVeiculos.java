package com.android.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.android.simc40.classes.Transportadora;
import com.android.simc40.classes.Veiculo;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseTransportadoraPaths;
import com.android.simc40.firebasePaths.FirebaseVeiculoPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.TreeMap;

public class ApiVeiculos implements DefaultErrorMessage, FirebaseVeiculoPaths, FirebaseTransportadoraPaths, FirebaseDatabaseExceptionErrorList {
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    ApiVeiculosCallback apiVeiculosCallback;
    HashMap<String, String> usersMap;
    TreeMap<String, Veiculo> veiculosMap = new TreeMap<>();

    public ApiVeiculos(Activity activity, String database, String contextException, LoadingPage loadingPage, ErrorDialog errorDialog, ApiVeiculosCallback apiVeiculosCallback, HashMap<String, String> usersMap){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingPage = loadingPage;
        this.errorDialog = errorDialog;
        this.apiVeiculosCallback = apiVeiculosCallback;

        if(usersMap == null) {
            ApiNameUsersCallback apiNameUsersCallback = response -> {
                this.usersMap = response;
                getVeiculos();
            };

            new ApiNameUsers(activity, contextException, loadingPage, errorDialog, apiNameUsersCallback);
        }else{
            this.usersMap = usersMap;
            getVeiculos();
        }

    }

    private void getVeiculos(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseVeiculoPathFirstKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            System.out.println(dataSnapshot1);
                            for(DataSnapshot datasnapshot2: dataSnapshot1.child(firebaseVeiculoPathSecondKey).getChildren()){
                                System.out.println(datasnapshot2);
                                Veiculo veiculo = new Veiculo(
                                    datasnapshot2.getKey(),
                                    new Transportadora(dataSnapshot1.getKey(), dataSnapshot1.child(firebaseTransportadoraPathNomeKey).getValue(String.class), dataSnapshot1.child(firebaseTransportadoraPathStatusKey).getValue(String.class)),
                                    datasnapshot2.child(firebaseVeiculoPathCapacidadeCargaKey).getValue(String.class),
                                    datasnapshot2.child(firebaseVeiculoPathMarcaKey).getValue(String.class),
                                    datasnapshot2.child(firebaseVeiculoPathModeloKey).getValue(String.class),
                                    datasnapshot2.child(firebaseVeiculoPathNumeroEixosKey).getValue(String.class),
                                    datasnapshot2.child(firebaseVeiculoPathPesoKey).getValue(String.class),
                                    datasnapshot2.child(firebaseVeiculoPathPlacaKey).getValue(String.class),
                                    datasnapshot2.child(firebaseVeiculoPathStatusKey).getValue(String.class),
                                    datasnapshot2.child(firebaseVeiculoPathCreationKey).getValue(String.class),
                                    (usersMap != null) ? usersMap.get(datasnapshot2.child(firebaseVeiculoPathCreatedByKey).getValue(String.class)) : "",
                                    datasnapshot2.child(firebaseVeiculoPathLastModifiedOnKey).getValue(String.class),
                                    (usersMap != null) ? usersMap.get(datasnapshot2.child(firebaseVeiculoPathLastModifiedByKey).getValue(String.class)) : ""
                                );
                                System.out.println(veiculo);
                                veiculosMap.put(veiculo.getTransportadora().getNome() + veiculo.getMarca() + veiculo.getModelo() + veiculo.getUid(), veiculo);
                            }
                        }

                        if(activityIsRunning()) apiVeiculosCallback.onCallback(veiculosMap);
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
