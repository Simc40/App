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
import com.simc.simc40.checklist.Etapas;
import com.simc.simc40.classes.Elemento;
import com.simc.simc40.classes.Galpao;
import com.simc.simc40.classes.Obra;
import com.simc.simc40.classes.Peca;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.errorHandling.QualidadeExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebasePecaPaths;

import java.util.HashMap;
import java.util.TreeMap;

public class ApiPecasUid implements DefaultErrorMessage, FirebaseDatabaseExceptionErrorList, QualidadeExceptionErrorList, FirebasePecaPaths, Etapas {

    public static final int ticks = ApiElementos.ticks + ApiGalpoes.ticksWithUsersMap + 3;
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    ApiPecasCallback apiPecasCallback;
    Obra obra;
    TreeMap<String, Obra> obrasMap;
    TreeMap<String, Elemento> elementosMap;
    TreeMap<String, Galpao> galpoesMap;
    TreeMap<String, Peca> pecaTreeMap = new TreeMap<>();
    ApiElementos apiElementos;
    HashMap<String, Boolean> apiResponse = new HashMap<>();

    public ApiPecasUid(Activity activity, String database, String contextException, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, ApiPecasCallback apiPecasCallback, Obra obra){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.modalDeCarregamento = modalDeCarregamento;
        this.modalAlertaDeErro = modalAlertaDeErro;
        this.apiPecasCallback = apiPecasCallback;
        this.obra = obra;

        ApiElementosCallback apiElementosCallback = response -> {
            elementosMap = response;
            obrasMap = apiElementos.getObras();
            checkResponse("apiPeca");
        };

        apiElementos = new ApiElementos(activity, database, contextException, modalDeCarregamento, modalAlertaDeErro, apiElementosCallback, false);

        ApiGalpoesCallback apiGalpoesCallback = response -> {
            galpoesMap = response;
            System.out.println(galpoesMap);
            checkResponse("apiGalpao");
        };

        new ApiGalpoes(activity, database, contextException, modalDeCarregamento, modalAlertaDeErro, apiGalpoesCallback, null, false);
    }

    private void checkResponse(String api){
        apiResponse.put(api, true);
        if(apiResponse.size() == 2) getPeca();
    }

    private void getPeca(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebasePecaPathsFirstKey);
            if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) {
                            if(ActivityStatus.activityIsRunning(activity)) apiPecasCallback.onCallback(pecaTreeMap);
                            return;
                        }
                        int indexCarga = etapasDePeca.indexOf(cargaKey);
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            String uidObra = dataSnapshot1.getKey();
                            Obra obraPeca = obrasMap.get(uidObra);
                            if(obra != null && !obra.getUid().equals(obraPeca.getUid())) continue;
                            for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){
                                String elementoUid = dataSnapshot2.getKey();
                                Elemento elemento = elementosMap.get(elementoUid);
                                for(DataSnapshot datasnapshot3: dataSnapshot2.getChildren()){
                                    Peca peca = datasnapshot3.getValue(Peca.class);
                                    peca.setObraObject(obraPeca);
                                    peca.setElementoObject(elemento);
                                    if(etapasDePeca.contains(peca.getEtapa_atual()) && etapasDePeca.indexOf(peca.getEtapa_atual()) >= indexCarga){
                                        String uidGalpao = "";
                                        uidGalpao = datasnapshot3.child(firebasePecaPathsSecondKey).child(liberacaoKey).child(firebasePecaPathsGalpao).getValue(String.class);
                                        peca.setGalpaoObject((galpoesMap != null && galpoesMap.get(uidGalpao) != null) ? galpoesMap.get(uidGalpao) : null);
                                    }
                                    pecaTreeMap.put(peca.getUid(), peca);
                                }
                            }
                        }
                        if(modalDeCarregamento != null) modalDeCarregamento.avancarPasso(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiPecasCallback.onCallback(pecaTreeMap);
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

    public TreeMap<String, Obra> getObras(){
        return obrasMap;
    }

    public TreeMap<String, Elemento> getElementos(){
        return elementosMap;
    }
}