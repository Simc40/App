package com.simc.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.simc.simc40.activityStatus.ActivityStatus;
import com.simc.simc40.classes.Galpao;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseGalpaoPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.TreeMap;

public class ApiGalpoes implements DefaultErrorMessage, FirebaseGalpaoPaths, FirebaseDatabaseExceptionErrorList {

    public final static int ticks = ApiNameUsers.ticks + 3;
    public final static int ticksWithUsersMap = 3;
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    ApiGalpoesCallback apiGalpoesCallback;
    HashMap<String, String> usersMap;
    TreeMap<String, Galpao> galpoesMap = new TreeMap<>();
    Boolean control;

    public ApiGalpoes(Activity activity, String database, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ApiGalpoesCallback apiGalpoesCallback, HashMap<String, String> usersMap, Boolean control){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.modalDeCarregamento = modalDeCarregamento;
        this.modalAlertaDeErro = modalAlertaDeErro;
        this.apiGalpoesCallback = apiGalpoesCallback;
        this.control = control;

        if(usersMap == null) {
            ApiNameUsersCallback apiNameUsersCallback = response -> {
                this.usersMap = response;
                getGalpoes();
            };
            new ApiNameUsers(activity, contextException, modalDeCarregamento, modalAlertaDeErro, apiNameUsersCallback);
        }else{
            this.usersMap = usersMap;
            getGalpoes();
        }
    }

    private void getGalpoes(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseGalpaoPathFirstKey);
            if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) {
                            modalAlertaDeErro.getBotao().setOnClickListener(view -> activity.finish());
                            throw new FirebaseDatabaseException(EXCEPTION_NULL_DATABASE_GALPOES);
                        }
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            Galpao galpao = new Galpao(
                                dataSnapshot1.getKey(),
                                dataSnapshot1.child(firebaseGalpaoPathNome).getValue(String.class),
                                dataSnapshot1.child(firebaseGalpaoPathStatus).getValue(String.class),
                                dataSnapshot1.child(firebaseGalpaoPathCreation).getValue(String.class),
                                (usersMap != null) ? usersMap.get(dataSnapshot1.child(firebaseGalpaoPathCreatedBy).getValue(String.class)) : "",
                                dataSnapshot1.child(firebaseGalpaoPathLastModifiedOn).getValue(String.class),
                                (usersMap != null) ? usersMap.get(dataSnapshot1.child(firebaseGalpaoPathLastModifiedBy).getValue(String.class)) : ""
                            );
                            if(control && dataSnapshot1.child(firebaseGalpaoPathControl).exists()) for(DataSnapshot dataSnapshot2 : dataSnapshot1.child(firebaseGalpaoPathControl).getChildren()){
                                galpao.insertPecaGalpao(
                                    dataSnapshot2.getKey(),
                                    dataSnapshot2.child(firebaseGalpaoPathControlObra).getValue(String.class),
                                    dataSnapshot2.child(firebaseGalpaoPathControlEntrada).getValue(String.class),
                                    dataSnapshot2.child(firebaseGalpaoPathControlSaida).getValue(String.class)
                                );
                            }
                            if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 3
                            galpoesMap.put(galpao.getUid(), galpao);
                        }
                        if(ActivityStatus.activityIsRunning(activity)) apiGalpoesCallback.onCallback(galpoesMap);
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
