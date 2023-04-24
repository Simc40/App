package com.simc.simc40.firebaseApiGET;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simc.simc40.activityStatus.ActivityStatus;
import com.simc.simc40.classes.Motorista;
import com.simc.simc40.classes.Transportadora;
import com.simc.simc40.classes.Veiculo;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseTransportadoraPaths;

import java.util.HashMap;
import java.util.TreeMap;

public class ApiTransportadoras implements DefaultErrorMessage, FirebaseTransportadoraPaths, FirebaseDatabaseExceptionErrorList {
    public final static int ticks = ApiNameUsers.ticks + 3;
    public final static int ticksWithUsersMap = 3;
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    ApiTransportadorasCallback apiTransportadorasCallback;
    HashMap<String, String> usersMap;
    TreeMap<String, Transportadora> transportadorasMap = new TreeMap<>();
    Boolean ordered;

    public ApiTransportadoras(Activity activity, String database, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ApiTransportadorasCallback apiTransportadorasCallback, HashMap<String, String> usersMap, Boolean ordered){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.modalDeCarregamento = modalDeCarregamento;
        this.modalAlertaDeErro = modalAlertaDeErro;
        this.apiTransportadorasCallback = apiTransportadorasCallback;
        this.ordered = ordered;

        if(usersMap == null) {
            ApiNameUsersCallback apiNameUsersCallback = response -> {
                this.usersMap = response;
                getTransportadoras();
            };
            new ApiNameUsers(activity, contextException, modalDeCarregamento, modalAlertaDeErro, apiNameUsersCallback);
        }else{
            this.usersMap = usersMap;
            getTransportadoras();
        }
    }

    private void getTransportadoras(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseTransportadoraPathFirstKey);
            if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) {
                            modalAlertaDeErro.getBotao().setOnClickListener(view -> activity.finish());
                            throw new FirebaseDatabaseException(EXCEPTION_NULL_DATABASE_TRANSPORTADORAS);
                        }
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            TreeMap<String, Motorista> motoristas = new TreeMap<>();
                            TreeMap<String, Veiculo> veiculos = new TreeMap<>();
                            if(dataSnapshot1.child(firebaseTransportadoraPathMotoristasKey).exists()) for(DataSnapshot dataSnapshot2: dataSnapshot1.child(firebaseTransportadoraPathMotoristasKey).getChildren()) {
                                String uidMotorista = dataSnapshot2.getKey();
                                Motorista motorista = new Motorista(
                                    uidMotorista,
                                    dataSnapshot2.child(firebaseMotoristaPathCnh).getValue(String.class),
                                    dataSnapshot2.child(firebaseMotoristaPathEmail).getValue(String.class),
                                    dataSnapshot2.child(firebaseMotoristaPathTransportadora).getValue(String.class),
                                    dataSnapshot2.child(firebaseMotoristaPathImgUrl).getValue(String.class),
                                    dataSnapshot2.child(firebaseMotoristaPathNomeMotorista).getValue(String.class),
                                    dataSnapshot2.child(firebaseMotoristaPathStatus).getValue(String.class),
                                    dataSnapshot2.child(firebaseMotoristaPathTelefone).getValue(String.class),
                                    dataSnapshot2.child(firebaseMotoristaCreation).getValue(String.class),
                                    dataSnapshot2.child(firebaseMotoristaCreatedBy).getValue(String.class),
                                    dataSnapshot2.child(firebaseMotoristaLastModifiedOn).getValue(String.class),
                                    dataSnapshot2.child(firebaseMotoristaLastModifiedBy).getValue(String.class)
                                );
                                motoristas.put(uidMotorista, motorista);
                            }
                            if(dataSnapshot1.child(firebaseTransportadoraPathVeiculosKey).exists()) for(DataSnapshot dataSnapshot2: dataSnapshot1.child(firebaseTransportadoraPathVeiculosKey).getChildren()) {
                                String uidVeiculo = dataSnapshot2.getKey();
                                Veiculo veiculo = new Veiculo(
                                        uidVeiculo,
                                        dataSnapshot2.child(firebaseVeiculoPathCapacidadeCarga).getValue(String.class),
                                        dataSnapshot2.child(firebaseVeiculoPathTransportadora).getValue(String.class),
                                        dataSnapshot2.child(firebaseVeiculoPathMarca).getValue(String.class),
                                        dataSnapshot2.child(firebaseVeiculoPathModelo).getValue(String.class),
                                        dataSnapshot2.child(firebaseVeiculoPathNumeroEixos).getValue(String.class),
                                        dataSnapshot2.child(firebaseVeiculoPathPeso).getValue(String.class),
                                        dataSnapshot2.child(firebaseVeiculoPathPlaca).getValue(String.class),
                                        dataSnapshot2.child(firebaseVeiculoPathStatus).getValue(String.class),
                                        dataSnapshot2.child(firebaseVeiculoPathCreation).getValue(String.class),
                                        dataSnapshot2.child(firebaseVeiculoPathCreatedBy).getValue(String.class),
                                        dataSnapshot2.child(firebaseVeiculoPathLastModifiedOn).getValue(String.class),
                                        dataSnapshot2.child(firebaseVeiculoPathLastModifiedBy).getValue(String.class)
                                );
                                veiculos.put(uidVeiculo, veiculo);
                            }
                            Transportadora transportadora = new Transportadora(
                                    dataSnapshot1.getKey(),
                                    dataSnapshot1.child(firebaseTransportadoraPathBairro).getValue(String.class),
                                    dataSnapshot1.child(firebaseTransportadoraPathCep).getValue(String.class),
                                    dataSnapshot1.child(firebaseTransportadoraPathCidade).getValue(String.class),
                                    dataSnapshot1.child(firebaseTransportadoraPathCnpj).getValue(String.class),
                                    dataSnapshot1.child(firebaseTransportadoraPathemail).getValue(String.class),
                                    dataSnapshot1.child(firebaseTransportadoraPathEndereco).getValue(String.class),
                                    dataSnapshot1.child(firebaseTransportadoraPathNome).getValue(String.class),
                                    dataSnapshot1.child(firebaseTransportadoraPathStatus).getValue(String.class),
                                    dataSnapshot1.child(firebaseTransportadoraPathTelefone).getValue(String.class),
                                    dataSnapshot1.child(firebaseTransportadoraPathUf).getValue(String.class),
                                    dataSnapshot1.child(firebaseTransportadoraCreation).getValue(String.class),
                                    (usersMap != null && usersMap.get(dataSnapshot1.child(firebaseTransportadoraCreatedBy).getValue(String.class)) != null) ? usersMap.get(dataSnapshot1.child(firebaseTransportadoraCreatedBy).getValue(String.class)) : "",
                                    dataSnapshot1.child(firebaseTransportadoraLastModifiedOn).getValue(String.class),
                                    (usersMap != null && usersMap.get(dataSnapshot1.child(firebaseTransportadoraLastModifiedBy).getValue(String.class)) != null) ? usersMap.get(dataSnapshot1.child(firebaseTransportadoraLastModifiedBy).getValue(String.class)) : "",
                                    motoristas,
                                    veiculos
                            );
                            if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 3
                            if(ordered) transportadorasMap.put(transportadora.getNome() + transportadora.getUid(), transportadora);
                            else transportadorasMap.put(transportadora.getUid(), transportadora);
                        }
                        if(ActivityStatus.activityIsRunning(activity)) apiTransportadorasCallback.onCallback(transportadorasMap);
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
