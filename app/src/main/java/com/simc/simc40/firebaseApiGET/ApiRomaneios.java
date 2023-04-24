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
import com.simc.simc40.classes.Obra;
import com.simc.simc40.classes.Peca;
import com.simc.simc40.classes.Romaneio;
import com.simc.simc40.classes.Transportadora;
import com.simc.simc40.classes.Veiculo;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseRomaneioPaths;

import java.util.HashMap;
import java.util.TreeMap;

public class ApiRomaneios implements DefaultErrorMessage, FirebaseRomaneioPaths, FirebaseDatabaseExceptionErrorList {
    public static final int ticks = ApiPecas.ticks + ApiTransportadoras.ticks + 3;
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    ApiRomaneiosCallback apiRomaneiosCallback;
    ApiPecas apiPecas;
    TreeMap<String, Obra> obrasMap;
    TreeMap<String, Peca> pecasMap;
    TreeMap<String, Transportadora> transportadorasMap;
    TreeMap<String, Romaneio> romaneiosMap = new TreeMap<>();
    HashMap<String, Boolean> apiResponse = new HashMap<>();

    public ApiRomaneios(Activity activity, String database, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ApiRomaneiosCallback apiRomaneiosCallback){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.modalDeCarregamento = modalDeCarregamento;
        this.modalAlertaDeErro = modalAlertaDeErro;
        this.apiRomaneiosCallback = apiRomaneiosCallback;

        ApiPecasCallback apiPecasCallback = response -> {
            try{
                pecasMap = response;
                obrasMap = apiPecas.getObras();
                checkResponse("pecas");
            }catch (Exception e){
                if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
                else ErrorHandling.handleError(contextException, e);
            }
        };

        ApiTransportadorasCallback apiTransportadorasCallback = response -> {
            try{
                transportadorasMap = response;
                checkResponse("transportadoras");
            }catch (Exception e){
                if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, modalDeCarregamento, modalAlertaDeErro);
                else ErrorHandling.handleError(contextException, e);
            }
        };

        apiPecas = new ApiPecas(activity, database, contextException, modalDeCarregamento, modalAlertaDeErro, apiPecasCallback, null);
        new ApiTransportadoras(activity, database, contextException, modalDeCarregamento, modalAlertaDeErro, apiTransportadorasCallback, null, false);
    }

    private void checkResponse(String api){
        apiResponse.put(api, true);
        if(apiResponse.size() == 2) getRomaneios();
    }

    private void getRomaneios(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseRomaneioPathFirstKey);
            if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) {
                            if(ActivityStatus.activityIsRunning(activity)) apiRomaneiosCallback.onCallback(romaneiosMap);
                            throw new FirebaseDatabaseException(EXCEPTION_NULL_DATABASE_ELEMENTOS);
                        }
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            if(dataSnapshot1.getKey().equals(firebaseRomaneioPathTotalCargas)) continue;
                            String uidRomaneio = dataSnapshot1.getKey();
                            TreeMap<String, Peca> pecasRomaneio = new TreeMap<>();
                            for(DataSnapshot dataSnapshot2 : dataSnapshot1.child(firebaseRomaneioPathPecasKey).getChildren()) if(pecasMap != null && pecasMap.get(dataSnapshot2.getKey()) != null) pecasRomaneio.put(dataSnapshot2.getKey(), pecasMap.get(dataSnapshot2.getKey()));
                            String uidTransportadora = dataSnapshot1.child(firebaseRomaneioPathTransportadora).getValue(String.class);
                            String uidMotorista = dataSnapshot1.child(firebaseRomaneioPathMotorista).getValue(String.class);
                            String uidVeiculo = dataSnapshot1.child(firebaseRomaneioPathVeiculo).getValue(String.class);
                            Transportadora transportadora;
                            Motorista motorista;
                            Veiculo veiculo;
                            if(transportadorasMap != null && transportadorasMap.get(uidTransportadora) != null){
                                transportadora = transportadorasMap.get(uidTransportadora);
                                motorista = (transportadora.getMotoristas() != null && transportadora.getMotoristas().get(uidMotorista) != null) ? transportadora.getMotoristas().get(uidMotorista) : null;
                                veiculo = (transportadora.getVeiculos() != null && transportadora.getVeiculos().get(uidVeiculo) != null) ? transportadora.getVeiculos().get(uidVeiculo) : null;
                            }else{
                                transportadora = null;
                                motorista = null;
                                veiculo = null;
                            }
                            Romaneio romaneio = new Romaneio(
                                uidRomaneio,
                                dataSnapshot1.child(firebaseRomaneioPathNumCarga).getValue(String.class),
                                dataSnapshot1.child(firebaseRomaneioPathPesoCarregamento).getValue(String.class),
                                dataSnapshot1.child(firebaseRomaneioPathVolumeCarregamento).getValue(String.class),
                                dataSnapshot1.child(firebaseRomaneioPathDataPrevisao).getValue(String.class),
                                dataSnapshot1.child(firebaseRomaneioPathDataCarga).getValue(String.class),
                                dataSnapshot1.child(firebaseRomaneioPathDataTransporte).getValue(String.class),
                                dataSnapshot1.child(firebaseRomaneioPathDataDescarga).getValue(String.class),
                                motorista,
                                (obrasMap != null && obrasMap.get(dataSnapshot1.child(firebaseRomaneioPathObra).getValue(String.class)) != null) ? obrasMap.get(dataSnapshot1.child(firebaseRomaneioPathObra).getValue(String.class)) : null,
                                pecasRomaneio,
                                transportadora,
                                veiculo,
                                dataSnapshot1.child(firebaseRomaneioPathCreationKey).getValue(String.class),
                                dataSnapshot1.child(firebaseRomaneioPathCreatedByKey).getValue(String.class),
                                dataSnapshot1.child(firebaseRomaneioPathLastModifiedOnKey).getValue(String.class),
                                dataSnapshot1.child(firebaseRomaneioPathLastModifiedByKey).getValue(String.class)
                            );
                            romaneiosMap.put(uidRomaneio, romaneio);
                        }
                        if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiRomaneiosCallback.onCallback(romaneiosMap);
                    }catch (Exception e){
                        e.printStackTrace();
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

    public TreeMap<String, Peca> getPecas() {return (pecasMap != null) ? pecasMap : null;}
}
