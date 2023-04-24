package com.simc.simc40.configuracaoLeitor;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.checklist.Etapas;
import com.simc.simc40.classes.Peca;
import com.simc.simc40.configuracaoLeitor.qrCode.ScanActivity;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.errorHandling.QualidadeExceptionErrorList;
import com.simc.simc40.errorHandling.ReaderException;
import com.simc.simc40.errorHandling.ReaderExceptionErrorList;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.sharedPreferences.LocalStorage;

import java.util.Objects;
import java.util.TreeMap;

public class  ServiceRfidReader implements Etapas, ListaLeitores, ReaderExceptionErrorList, QualidadeExceptionErrorList, FirebaseDatabaseExceptionErrorList {

    String selectedReader;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;

    long lastClickTime = 0;
    LinearLayout tagItem;
    Activity activity;
    Peca peca;
    ReadCallback readCallback;
    ClearCallback clearCallback;
    BroadcastReceiver mMessageReceiver;

    public ServiceRfidReader(Activity activity, LinearLayout tagItem, ReadCallback readCallback, ClearCallback clearCallback, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro) {
        Log.i("Service", "Service created!");
        this.activity = activity;
        this.tagItem = tagItem;
        this.lastClickTime = 0;
        this.readCallback = readCallback;
        this.clearCallback = clearCallback;
        this.peca = null;
        this.modalAlertaDeErro = modalAlertaDeErro;
        this.modalDeCarregamento = modalDeCarregamento;

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String result = intent.getStringExtra("scanResult");
                if(result==null || result.length()<3){
                    return;
                }else{
                    scanResult(result);
                }
                Log.d("mMessageReceiver", "Got message: " + result);
            }
        };

        activity.registerReceiver(mMessageReceiver, new IntentFilter("rfid_server"));
    }

    public void configureErrorHandling(ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro){
        this.modalDeCarregamento = modalDeCarregamento;
        this.modalAlertaDeErro = modalAlertaDeErro;
    }

    public String getReader(){
        return LocalStorage.getLeitor(activity, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
    }



    public void setReader(String reader, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro){
        selectedReader = reader;
        LocalStorage.salvarLeitor(activity, reader, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
    }

    public void clear(){
        Log.d("Service", "Clear");
        if(tagItem != null) tagItem.removeAllViews();
        peca = null;
    }

    public void addTagToLayout(Peca peca){
        if(detectedDoubleAction()) return;
        if(this.peca != null && this.peca.getTag().equals(peca.getTag())) return;
        tagItem.removeAllViews();
        View obj = LayoutInflater.from(activity).inflate(R.layout.custom_tag_layout, tagItem ,false);
        TextView item1 = obj.findViewById(R.id.item1);
        ImageView item2 = obj.findViewById(R.id.item2);
        String pecaText = (Objects.equals(getReader(), QR_CODE)) ? peca.getQrCode() : peca.getTag();
        item1.setText(pecaText);
        item2.setOnClickListener(view -> {
            tagItem.removeView(obj);
            this.peca = null;
            clearCallback.onCallback();
        });
        this.peca = peca;
        tagItem.setTag(peca);
        tagItem.addView(obj);
    }

    public Peca getPeca() {
        return peca;
    }

    private boolean detectedDoubleAction(){
        if (SystemClock.elapsedRealtime() - this.lastClickTime < 1000){
            return true;
        }
        this.lastClickTime = SystemClock.elapsedRealtime();
        return false;
    }

    //Qr Code
    public void read(){
        try{
            if(getReader() == null || Objects.equals(getReader(), "")) throw new ReaderException(EXCEPTION_READER_NOT_SELECTED);
            if(getReader().equals(QR_CODE)){
                Intent scannerIntent = new Intent(activity, ScanActivity.class);
                scannerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(scannerIntent);
            }
        } catch (Exception e){
            ErrorHandling.handleError(modalDeCarregamento, modalAlertaDeErro,"Leitor não Selecionado", "O leitor não foi escolhido. Aacesse a Configuração de Leitor.");
        }
    }

    public void scanResult(String tag){
        try{
            if(!validTag(tag)) return;
            readCallback.onCallback(tag);
        }catch (Exception e){
            ErrorHandling.handleError(activity.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

    }

    private boolean validTag(String tag){
        try{
            if(this.peca != null && this.peca.getTag().equals(tag)) throw new ReaderException(EXCEPTION_TAG_ALREADY_READED + tag);
            if(tagFormatInvalid(tag)) throw new ReaderException(EXCEPTION_TAG_FORMAT_INVALID + tag);
        }catch (Exception e){
            ErrorHandling.handleError(activity.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            return false;
        }
        return true;
    }

    private boolean tagFormatInvalid(String tag){
        return !tag.matches("[a-zA-Z0-9-]*");
    }

    public static boolean tagRegistered(String tag, TreeMap<String, Peca> pecaTreeMap){
        return (pecaTreeMap.containsKey(tag));
    }

    public static boolean tagEtapaIncorreta(String tag, String etapaAtual, TreeMap<String, Peca> pecaTreeMap){
        return !pecaTreeMap.get(tag).getEtapa_atual().equals(etapaAtual);
    }

    public void unregisterReceiver() {
        activity.unregisterReceiver(mMessageReceiver);
    }


}