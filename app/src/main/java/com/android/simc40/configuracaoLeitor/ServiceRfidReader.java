package com.android.simc40.configuracaoLeitor;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.simc40.R;
import com.android.simc40.checklist.Etapas;
import com.android.simc40.classes.Elemento;
import com.android.simc40.classes.Peca;
import com.android.simc40.configuracaoLeitor.qrCode.ScanActivity;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.errorHandling.QualidadeException;
import com.android.simc40.errorHandling.QualidadeExceptionErrorList;
import com.android.simc40.errorHandling.ReaderException;
import com.android.simc40.errorHandling.ReaderExceptionErrorList;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class  ServiceRfidReader extends Service implements Etapas, ListaLeitores, ReaderExceptionErrorList, QualidadeExceptionErrorList, FirebaseDatabaseExceptionErrorList {


    IBinder mBinder = new LocalBinder();
    String selectedReader;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;

    //Read in Activity
    String etapaAtual;
    TreeMap <String, Peca> pecaTreeMap;
    HashMap<String, Peca> tagMap = new HashMap<>();
    long lastClickTime = 0;
    LinearLayout tagItem;
    Activity activity;
    Button read;
    Button clear;
    Elemento elemento;
    Peca peca;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Service", "Service Binded!");
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public ServiceRfidReader getService() {
            return ServiceRfidReader.this;
        }
    }

    @Override
    public void onCreate() {
        Log.i("Service", "Service created!");
        selectedReader = sharedPrefsDatabase.getReader(this, MODE_PRIVATE, loadingDialog, errorDialog);
        if(selectedReader == null || selectedReader.equals("")) selectedReader = null;
    }

    @Override
    public void onDestroy() {
        Log.i("Service", "Service Destroyed");
    }

    public void configureErrorHandling(LoadingDialog loadingDialog, ErrorDialog errorDialog){
        this.loadingDialog = loadingDialog;
        this.errorDialog = errorDialog;
    }

    public String getReader(){
        return selectedReader;
    }

    public void setReader(String reader, LoadingDialog loadingDialog, ErrorDialog errorDialog){
        selectedReader = reader;
        sharedPrefsDatabase.SaveReaderOnSharedPreferences(this, reader, MODE_PRIVATE, loadingDialog, errorDialog);
    }

    public void setPecasMap(TreeMap<String, Peca> pecaTreeMap){
        this.pecaTreeMap = pecaTreeMap;
    }
    public void setElemento(Elemento elemento){
        this.elemento = elemento;
    }

    public void configureReaderLayout(Activity activity, LinearLayout tagItem, Button read, Button clear, String etapaAtual, Peca peca) {
        this.activity = activity;
        this.tagItem = tagItem;
        this.read = read;
        this.clear = clear;
        this.peca = peca;
        this.etapaAtual = etapaAtual;
        if (activity == null) return;

        read.setOnClickListener(view -> {
            if(etapaAtual.equals(cadastroKey) && elemento == null) try {
                throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
            } catch (QualidadeException e) {
                ErrorHandling.handleError(activity.getClass().getSimpleName(), e, loadingDialog, errorDialog);
                return;
            }
            read();
        });

        clear.setOnClickListener(view -> {
            clear();
            if(!etapaAtual.equals(cadastroKey)) peca.setTag(null);
            if(tagItem.hasOnClickListeners() && peca.getElemento() != null) tagItem.callOnClick();
        });
    }

    public void clear(){
        Log.d("Service", "Clear");
        tagItem.removeAllViews();
        tagMap.clear();
    }

    public void clearData(){
        Log.d("Service", "ClearData");
        clear();
        tagMap = new HashMap<>();
        lastClickTime = 0;
        etapaAtual = null;
        pecaTreeMap = null;
        tagItem = null;
        activity = null;
        read = null;
        clear = null;
        elemento = null;
        peca = null;
    }

    public void addTagToLayout(String tag){
        Log.d("Service", peca.toString());
        if(detectedDoubleAction()) return;
        if(tagMap.containsKey(tag)) return;
        View obj = LayoutInflater.from(activity).inflate(R.layout.custom_tag_layout, tagItem ,false);
        TextView item1 = obj.findViewById(R.id.item1);
        ImageView item2 = obj.findViewById(R.id.item2);
        item1.setText(tag);
        item2.setOnClickListener(view -> {
            tagItem.removeView(obj);
            tagMap.remove(tag);
            if(!etapaAtual.equals(cadastroKey)) peca.setTag(null);
            if(tagItem.hasOnClickListeners()) tagItem.callOnClick();
        });
        tagMap.put(tag, peca);
        tagItem.addView(obj);
        if(tagItem.hasOnClickListeners()) tagItem.callOnClick();
    }

    public List<String> countRegisteredTags(){
        if (activity == null){
            return null;
        }
        return new ArrayList<>(tagMap.keySet());
    }

    public HashMap<String, Peca> getTagMap() {
        return tagMap;
    }

    private boolean detectedDoubleAction(){
        if (SystemClock.elapsedRealtime() - this.lastClickTime < 1000){
            return true;
        }
        this.lastClickTime = SystemClock.elapsedRealtime();
        return false;
    }

    //Qr Code
    void read(){
        try{
            if(selectedReader == null) throw new ReaderException(EXCEPTION_READER_NOT_SELECTED);
            if(selectedReader.equals(QR_CODE)){
                Intent scannerIntent = new Intent(this, ScanActivity.class);
                scannerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(scannerIntent);
            }
        } catch (Exception e){
            ErrorHandling.handleError(activity.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }
    }

    public void giveQrCodeScanResult(String qrCode){
        try{
            if(!validTag(qrCode)) return;
            if(!etapaAtual.equals(cadastroKey)) {
                clear();
                peca.formatVariable(pecaTreeMap.get(qrCode));
                this.elemento = peca.getElemento();
            }else{
                if(peca.getTag() == null) peca.setTag(qrCode);
                if(peca.getNomePeca() == null) peca.setNomePeca(tagMap.size());
                peca = new Peca(qrCode, peca.getEtapaAtual(), peca.getObra(), peca.getElemento(), null);
                peca.setNomePeca(tagMap.size());
            }
            addTagToLayout(qrCode);
        }catch (Exception e){
            ErrorHandling.handleError(activity.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            return;
        }

    }

    private boolean validTag(String tag){
        try{
            if(tagMap.containsKey(tag)) throw new ReaderException(EXCEPTION_TAG_ALREADY_READED + tag);
            if(tagFormatInvalid(tag)) throw new ReaderException(EXCEPTION_TAG_FORMAT_INVALID + tag);
            if(!etapaAtual.equals(cadastroKey) && !tagRegistered(tag)) throw new ReaderException(EXCEPTION_UNREGISTERED_TAG + tag);
            if(etapaAtual.equals(cadastroKey)){
                if(tagRegistered(tag)) throw new ReaderException(EXCEPTION_WRONG_ETAPA + tag + "etapa" + pecaTreeMap.get(tag).getEtapaAtual());
                int pecasLidas = tagMap.size();
                int numMax = Integer.parseInt(elemento.getPecasPlanejadas());
                int numPecas = Integer.parseInt(elemento.getPecasCadastradas());
                if(numPecas == numMax) throw new QualidadeException(EXCEPTION_MAX_REGISTERED_PECAS_REACHED);
                if(1 + pecasLidas + numPecas > numMax) throw new QualidadeException(EXCEPTION_MAX_POSSIBLE_READ_PECAS_REACHED + elemento.getPecasPlanejadas());
            }
            if(tagRegistered(tag) && tagEtapaIncorreta(tag))throw new ReaderException(EXCEPTION_WRONG_ETAPA + tag + "etapa" + pecaTreeMap.get(tag).getEtapaAtual());
        }catch (Exception e){
            ErrorHandling.handleError(activity.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            return false;
        }
        return true;
    }

    private boolean tagFormatInvalid(String tag){
        return !tag.matches("[a-zA-Z0-9]*");
    }

    private boolean tagRegistered(String tag){
        return (pecaTreeMap.containsKey(tag));
    }

    private boolean tagEtapaIncorreta(String tag){
        return !pecaTreeMap.get(tag).getEtapaAtual().equals(etapaAtual);
    }
}