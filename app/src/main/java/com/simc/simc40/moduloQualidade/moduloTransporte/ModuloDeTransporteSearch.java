package com.simc.simc40.moduloQualidade.moduloTransporte;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.simc.simc40.R;
import com.simc.simc40.classes.Peca;
import com.simc.simc40.classes.Romaneio;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.configuracaoLeitor.ClearCallback;
import com.simc.simc40.configuracaoLeitor.ReadCallback;
import com.simc.simc40.configuracaoLeitor.ServiceRfidReader;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.dialogs.SuccessDialog;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.errorHandling.ReaderException;
import com.simc.simc40.errorHandling.ReaderExceptionErrorList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiRomaneios;
import com.simc.simc40.firebaseApiGET.ApiRomaneiosCallback;
import com.simc.simc40.sharedPreferences.LocalStorage;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModuloDeTransporteSearch extends AppCompatActivity implements SharedPrefsExceptionErrorList, ReaderExceptionErrorList, FirebaseDatabaseExceptionErrorList {

    Usuario usuario;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    SuccessDialog successDialog;
    ServiceRfidReader serviceRfidReader;
    LinearLayout tagHeader, tagItem;
    Button read, clear;
    TextView goBack;
    String database;
    DuploClique duploClique = new DuploClique();
    TreeMap<String, Peca> pecasMap;
    ApiRomaneios apiRomaneios;
    boolean mBounded;
    ReadCallback readCallback;
    ClearCallback clearCallback;
    HashMap<String, Peca> pecasRomaneio;
    HashMap<String, String> uidRomaneios = new HashMap<>();
    TreeMap<String, Romaneio> mapRomaneios;


//    ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            System.out.println("Service Disconnected");
//            mBounded = false;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            System.out.println("Service is connected");
//            mBounded = true;
//            ServiceRfidReader.LocalBinder mLocalBinder = (ServiceRfidReader.LocalBinder)service;
//            serviceRfidReader = mLocalBinder.getService();
//            serviceRfidReader.configureErrorHandling(loadingDialog, errorDialog);
//            serviceRfidReader.configureReaderLayout(ModuloDeTransporteSearch.this, tagItem, readCallback, clearCallback);
//        }
//    };
//
//    ServiceConnection serviceReconnection = new ServiceConnection() {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            System.out.println("Service Disconnected");
//            mBounded = false;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            System.out.println("Service is connected");
//            mBounded = true;
//            ServiceRfidReader.LocalBinder mLocalBinder = (ServiceRfidReader.LocalBinder)service;
//            serviceRfidReader = mLocalBinder.getService();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mtransporte_search);

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalAlertaDeErro.getBotao().setOnClickListener(null);
        modalAlertaDeErro.getBotao().setOnClickListener(view -> finish());
        successDialog = new SuccessDialog(this);
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);

        goBack = findViewById(R.id.goBack);

        //ReaderConnectorItems
        read = findViewById(R.id.read);
        clear = findViewById(R.id.clear);
        tagItem = findViewById(R.id.tagItem);
        tagHeader = findViewById(R.id.tagItemHeader);

        View headerXML = LayoutInflater.from(this).inflate(R.layout.custom_tag_layout_header, tagHeader ,false);
        tagHeader.addView(headerXML);

        try{
            usuario = LocalStorage.getUsuario(this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        read.setOnClickListener(view -> {
            try{
                serviceRfidReader.read();
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        });

        readCallback = tag -> {
            try{
                if(!(pecasMap != null && pecasMap.size() > 0 && pecasMap.get(tag) != null)) throw new ReaderException(EXCEPTION_UNREGISTERED_TAG);
                Peca peca = pecasMap.get(tag);
                serviceRfidReader.addTagToLayout(peca);
                if(pecasRomaneio == null || pecasRomaneio.size() == 0) throw new FirebaseDatabaseException(EXCEPTION_NULL_DATABASE_ROMANEIOS);
                if(pecasRomaneio.get(tag) == null) throw new ReaderException(EXCEPTION_TAG_WITH_NO_ROMANEIO);
                Romaneio romaneio = mapRomaneios.get(uidRomaneios.get(tag));
                successDialog.showSuccess(
                        "Peça cadastrada em Romaneio!",
                        "A peça" + tag + "\n" +
                                "Se encontra cadastrada no romaneio - Carga " + romaneio.getNumCarga() + "\n" +
                                "Com destino à " + romaneio.getObra().getNome_obra() + "\n" +
                                "Com status " + romaneio.getStatus() + "\n" +
                                "Com previsão em " + romaneio.getDataPrevisao()
                );
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        };

        clear.setOnClickListener(view -> serviceRfidReader.clear());

        clearCallback = () -> serviceRfidReader.clear();

//        Intent sIntent = new Intent(this, ServiceRfidReader.class);
//        bindService(sIntent, serviceConnection, 0);

        modalDeCarregamento.mostrarModalDeCarregamento(3 + ApiRomaneios.ticks);

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            onBackPressed();
        });

        modalDeCarregamento.avancarPasso(); // 1

        try{
            @SuppressLint("SetTextI18n") ApiRomaneiosCallback apiCallback = response -> {
                modalDeCarregamento.avancarPasso(); // 2
                if(this.isFinishing() || this.isDestroyed()) return;
                pecasMap = apiRomaneios.getPecas();
                mapRomaneios = response;
                pecasRomaneio = (HashMap<String, Peca>) mapRomaneios.values().stream().flatMap(Romaneio -> Romaneio.getPecas().stream()).collect(Collectors.toMap(Peca::getTag, Function.identity()));
                for(Romaneio romaneio: mapRomaneios.values()) for(Peca peca : romaneio.getPecas()) uidRomaneios.put(peca.getTag(), romaneio.getUid());

                modalDeCarregamento.passoFinal(); // 3
                modalDeCarregamento.fecharModal();
            };
            apiRomaneios = new ApiRomaneios(this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiCallback);
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(duploClique.detectado()){
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
        successDialog.endSuccessDialog();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
        successDialog.endSuccessDialog();
    }
//    @Override
//    protected void onStart() {
//        Intent sIntent = new Intent(this, ServiceRfidReader.class);
//        bindService(sIntent, serviceReconnection, 0);
//        super.onStart();
//    }
}