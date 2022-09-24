package com.android.simc40.firebaseApiGET;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.simc40.activityStatus.ActivityStatus;
import com.android.simc40.classes.Elemento;
import com.android.simc40.classes.Forma;
import com.android.simc40.classes.Obra;
import com.android.simc40.classes.TipoDePeca;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseElementoPaths;
import com.android.simc40.dialogs.LoadingDialog;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApiElementos implements DefaultErrorMessage, FirebaseElementoPaths, FirebaseDatabaseExceptionErrorList {

    public final static int ticks = ApiFormas.ticks + ApiTiposDePeca.ticks + ApiObras.ticks + 3;
    DatabaseReference reference;
    Activity activity;
    String contextException, database;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    ApiElementosCallback apiElementosCallback;
    TreeMap<String, Elemento> elementosMap = new TreeMap<>();
    TreeMap<String, Obra> obrasMap = new TreeMap<>();
    HashMap<String, Forma> formasMap = new HashMap<>();
    HashMap<String, TipoDePeca> tiposDePecaMap = new HashMap<>();
    HashMap<String, String> usersMap;
    HashMap<String, HashMap<String, String>> PDFmap = new HashMap<>();
    boolean responseForma = false, responseTiposdePeca = false, responseObra = false, responsePDF = false;
    ApiObras apiObras;
    boolean ordered;

    public ApiElementos(Activity activity, String database, String contextException, LoadingDialog loadingDialog, ErrorDialog errorDialog, ApiElementosCallback apiElementosCallback, boolean ordered){
        this.activity = activity;
        this.database = database;
        this.contextException = contextException;
        this.loadingDialog = loadingDialog;
        this.errorDialog = errorDialog;
        this.apiElementosCallback = apiElementosCallback;
        this.ordered = ordered;

        ApiFormasCallback apiFormasCallback = response -> {
            formasMap = response;
            responseForma = true;
            checkResponse();
        };

        ApiTiposDePecaCallback apiTiposDePecaCallback = response -> {
            tiposDePecaMap = response;
            responseTiposdePeca = true;
            checkResponse();
        };

        ApiObrasCallback apiObrasCallback = response -> {
            try{
                obrasMap = response;
                usersMap = apiObras.usersMap;
                responseObra = true;
                checkResponse();
            }catch (Exception e){
                if(ActivityStatus.activityIsRunning(activity)) ErrorHandling.handleError(contextException, e, loadingDialog, errorDialog);
                else ErrorHandling.handleError(contextException, e);
            }
        };

        ApiPDFElementoCallback apiPDFElementoCallback = response -> {
            PDFmap = response;
            responsePDF = true;
            checkResponse();
        };

        new ApiPDFElemento(activity, database, contextException, loadingDialog, errorDialog, apiPDFElementoCallback);
        new ApiFormas(activity, database, contextException, loadingDialog, errorDialog, apiFormasCallback);
        new ApiTiposDePeca(activity, database, contextException, loadingDialog, errorDialog, apiTiposDePecaCallback);
        apiObras = new ApiObras(activity, database, contextException, loadingDialog, errorDialog, apiObrasCallback);
    }

    private void checkResponse(){
        if(responseForma && responseTiposdePeca && responseObra && responsePDF) getElementos();
    }

    private void getElementos(){
        try{
            if(!ErrorHandling.deviceIsConnected(activity)) throw new FirebaseNetworkException(defaultErrorMessage);
            reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseElementoPathFirstKey);
            if(loadingDialog != null) loadingDialog.tick(); // 1
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(loadingDialog != null) loadingDialog.tick(); // 2
                    try{
                        if(dataSnapshot.getValue() == null) {
                            errorDialog.getButton().setOnClickListener(view -> activity.finish());
                            throw new FirebaseDatabaseException(EXCEPTION_NULL_DATABASE_ELEMENTOS);
                        }
                        for(DataSnapshot datasnapshot1: dataSnapshot.getChildren()){
                            String uidObra = datasnapshot1.getKey();
                            for(DataSnapshot dataSnapshot2 : datasnapshot1.getChildren()){
                                String uidElemento = dataSnapshot2.getKey();
                                Elemento elemento = new Elemento(
                                    (obrasMap != null) ? obrasMap.get(uidObra) : null,
                                    (obrasMap != null) ? formasMap.get(dataSnapshot2.child(firebaseElementoPathFormasKey).getValue(String.class)) : null,
                                    (obrasMap != null) ? tiposDePecaMap.get(dataSnapshot2.child(firebaseElementoPathTipoPecaKey).getValue(String.class)) : null,
                                    uidElemento,
                                    dataSnapshot2.child(firebaseElementoPathNomeKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathBKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathHKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathCKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathPecasPlanejadasKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathPecasCadastradasKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathFckDesfKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathFckIcKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathVolumeKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathPesoKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathPesoAcoKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathStatusKey).getValue(String.class),
                                    dataSnapshot2.child(firebaseElementoPathCreationKey).getValue(String.class),
                                    usersMap.get(dataSnapshot2.child(firebaseElementoPathCreatedByKey).getValue(String.class)),
                                    dataSnapshot2.child(firebaseElementoPathLastModifiedOnKey).getValue(String.class),
                                    usersMap.get(dataSnapshot2.child(firebaseElementoPathLastModifiedByKey).getValue(String.class)),
                                    (PDFmap != null && PDFmap.get(uidObra) != null && PDFmap.get(uidObra).get(uidElemento) != null) ? PDFmap.get(uidObra).get(uidElemento) : null
                                );
                                if(ordered) elementosMap.put(toUpperCase(elemento.getObra().getNomeObra()) + toUpperCase(elemento.getNome()) + toUpperCase(elemento.getUid()), elemento);
                                else elementosMap.put(elemento.getUid(), elemento);
                            }
                        }
                        if(loadingDialog != null) loadingDialog.tick(); // 3
                        if(ActivityStatus.activityIsRunning(activity)) apiElementosCallback.onCallback(elementosMap);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String toUpperCase(String string){
        return Stream.of(string.trim().split("\\s"))
                .filter(word -> word.length() > 0)
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    public TreeMap<String, Obra> getObras(){
        return obrasMap;
    }
}
