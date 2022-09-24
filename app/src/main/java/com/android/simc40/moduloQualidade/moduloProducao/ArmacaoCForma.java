package com.android.simc40.moduloQualidade.moduloProducao;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.simc40.PDFs.DisplayLoadedPDF;
import com.android.simc40.R;
import com.android.simc40.checklist.Etapas;
import com.android.simc40.classes.Checklist;
import com.android.simc40.classes.Elemento;
import com.android.simc40.classes.Erro;
import com.android.simc40.classes.Obra;
import com.android.simc40.classes.Pdf;
import com.android.simc40.classes.Peca;
import com.android.simc40.classes.User;
import com.android.simc40.configuracaoLeitor.ListaLeitores;
import com.android.simc40.configuracaoLeitor.ServiceRfidReader;
import com.android.simc40.configuracaoLeitor.readerConnectorUHF1128;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.dialogs.QuestionDialog;
import com.android.simc40.dialogs.SuccessDialog;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.errorHandling.QualidadeException;
import com.android.simc40.errorHandling.QualidadeExceptionErrorList;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.firebaseApiGET.ApiChecklist;
import com.android.simc40.firebaseApiGET.ApiChecklistCallback;
import com.android.simc40.firebaseApiGET.ApiErros;
import com.android.simc40.firebaseApiGET.ApiErrosCallback;
import com.android.simc40.firebaseApiGET.ApiPecas;
import com.android.simc40.firebaseApiGET.ApiPecasCallback;
import com.android.simc40.firebasePaths.FirebasePecaPaths;
import com.android.simc40.moduloGerenciamento.GerenciamentoElementos;
import com.android.simc40.moduloQualidade.relatorioErros.RelatarErro;
import com.android.simc40.moduloQualidade.relatorioErros.SolucionarErro;
import com.android.simc40.selecaoListas.SelecaoListaObras;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ArmacaoCForma extends AppCompatActivity implements SharedPrefsExceptionErrorList, LayoutExceptionErrorList, Etapas, QualidadeExceptionErrorList, ListaLeitores, FirebasePecaPaths {

    Obra obra;
    User user;
    Peca peca;
    boolean responseApiPecas = false, responseApiChecklist = false, responseApiErros = false;
    Checklist checklist;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    SuccessDialog successDialog;
    QuestionDialog questionDialog;
    TreeMap<String, Peca> pecaTreeMap = new TreeMap<>();
    TreeMap <String, Erro> errosMap;
    TreeMap <String, Elemento> elementosMap;
    TextView goBack, textViewObra, textViewElemento;
    DatabaseReference reference;
    static final String etapa = armacaoFormaKey;
    HashMap<String, Object> firebaseObj;
    Button read, clear;
    CardView projetoLocacao, projetoPeca, informacoesProjeto, reportarErro,  submitForm;
    DoubleClick doubleClick = new DoubleClick();
    LinearLayout tagHeader, tagItem, checklistHeader, checkList, errorListHeader, errorList, errosLayout;
    String database;
    ServiceRfidReader serviceRfidReader;
    boolean mBounded;
    CheckBox pecaLocked;
    ArrayList<Boolean> postResponse;
    ApiPecas apiPecas;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("Service Disconnected");
            mBounded = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("Service is connected");
            mBounded = true;
            ServiceRfidReader.LocalBinder mLocalBinder = (ServiceRfidReader.LocalBinder)service;
            serviceRfidReader = mLocalBinder.getService();
            serviceRfidReader.configureErrorHandling(loadingDialog, errorDialog);
            serviceRfidReader.configureReaderLayout(ArmacaoCForma.this, tagItem, read, clear, etapa, peca);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mproducao_armacao_cforma);

        errorDialog = new ErrorDialog(this);
        loadingDialog = new LoadingDialog(this, errorDialog);
        successDialog = new SuccessDialog(this);
        questionDialog = new QuestionDialog(this);
        loadingDialog.showLoadingDialog(4 + ApiPecas.ticks + ApiErros.ticks + ApiChecklist.ticks);

        try {
            peca = new Peca(etapa);
        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }

        textViewObra = findViewById(R.id.obra);
        textViewElemento = findViewById(R.id.elemento);
        goBack = findViewById(R.id.goBack);
        projetoLocacao = findViewById(R.id.projetoLocacao);
        projetoPeca = findViewById(R.id.projetoPeca);
        informacoesProjeto = findViewById(R.id.informacoesProjeto);
        checklistHeader = findViewById(R.id.checklistHeader);
        errosLayout = findViewById(R.id.errosLayout);
        checkList = findViewById(R.id.checkList);
        errorListHeader = findViewById(R.id.errosListHeader);
        errorList = findViewById(R.id.errosList);
        pecaLocked = findViewById(R.id.pecaLocked);
        pecaLocked.setEnabled(false);
        reportarErro = findViewById(R.id.reportarErro);
        submitForm = findViewById(R.id.submitForm);

        //ReaderConnectorItems
        read = findViewById(R.id.read);
        clear = findViewById(R.id.clear);
        tagItem = findViewById(R.id.tagItem);
        tagHeader = findViewById(R.id.tagItemHeader);

        try{
            user = sharedPrefsDatabase.getUser(this, MODE_PRIVATE, loadingDialog, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = user.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }

        goBack.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            onBackPressed();
        });

        Intent intent = new Intent(this, SelecaoListaObras.class);
        selectObraFromList.launch(intent);

        Intent sIntent = new Intent(this, ServiceRfidReader.class);
        bindService(sIntent, serviceConnection, 0);

        textViewObra.setOnClickListener(view -> reestartActivity());

        View headerXML = LayoutInflater.from(this).inflate(R.layout.custom_tag_layout_header, tagHeader ,false);
        tagHeader.addView(headerXML);

        projetoLocacao.setOnClickListener(view -> {
            try{
                if(peca.getObra().getPdfUrl() != null && !peca.getObra().getPdfUrl().equals("")){
                    Intent plIntent = new Intent(this, DisplayLoadedPDF.class);
                    plIntent.putExtra("dependency", new Pdf("PDF Locação\n" + peca.getObra().getNomeObra(), peca.getObra().getPdfUrl()));
                    startActivity(plIntent);
                }else{
                    throw new QualidadeException(EXCEPTION_NO_REGISTERED_PDF_IN_OBRA);
                }
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        });

        projetoPeca.setOnClickListener(view -> {
            try{
                if(peca.getElemento() == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                if(peca.getElemento().getPdfUrl() != null && !peca.getElemento().getPdfUrl().equals("")){
                    Intent plIntent = new Intent(this, DisplayLoadedPDF.class);
                    plIntent.putExtra("dependency", new Pdf("PDF de Peça\n" + peca.getElemento().getNome(), peca.getElemento().getPdfUrl()));
                    restartOnResult.launch(plIntent);
                }else{
                    throw new QualidadeException(EXCEPTION_NO_REGISTERED_PDF_IN_ELEMENTO);
                }
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        });

        informacoesProjeto.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            try{
                if(peca.getElemento() == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                Intent ipIntent = new Intent(this, GerenciamentoElementos.class);
                ipIntent.putExtra("dependency", peca.getElemento());
                startActivity(ipIntent);
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        });

        reportarErro.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            try{
                if(peca.getElemento() == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                if(serviceRfidReader.getTagMap().size() == 0) throw new QualidadeException(EXCEPTION_NO_SELECTED_TAG_ON_SUBMIT);
                Intent reIntent = new Intent(this, RelatarErro.class);
                reIntent.putExtra("dependency", new Object[]{new HashMap<String, Peca>(){{put(peca.getTag(), peca);}}, checklist});
                restartOnResult.launch(reIntent);
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        });

        submitForm.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            if(loadingDialog.isVisible) return;
            loadingDialog.showLoadingDialog(4);
            try{
                if(checklist.getCheckedList().contains(false)) throw new QualidadeException(EXCEPTION_CHECKLIST_NOT_FILLED);
                if(serviceRfidReader.getTagMap().size() == 0) throw new QualidadeException(EXCEPTION_NO_SELECTED_TAG_ON_SUBMIT);
                if(peca.getElemento() == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);

                firebaseObj = new HashMap<>();
                @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                firebaseObj.put(firebasePecaPathsEtapaAtual, etapasDePeca.get(etapasDePeca.indexOf(etapa) + 1));
                firebaseObj.put(firebasePecaPathsLastModifiedBy, user.getUid());
                firebaseObj.put(firebasePecaPathsLastModifiedOn, data);
                Map<String, Object> etapa_atual = new HashMap<>();
                etapa_atual.put(firebasePecaPathsCreation, data);
                etapa_atual.put(firebasePecaPathsCreatedBy, user.getUid());
                etapa_atual.put(firebasePecaPathsChecklist, checklist.getUid());
                if (inputFieldsAreNotValid(firebaseObj)) return;
                loadingDialog.tick(); // 2.1
                postResponse = new ArrayList<>();
                reference = FirebaseDatabase.getInstance(database).getReference();
                reference.child(firebasePecaPathsFirstKey).child(peca.getObra().getUid()).child(peca.getElemento().getUid()).child(peca.getTag()).updateChildren(firebaseObj)
                        .addOnSuccessListener(unused -> {
                            loadingDialog.tick(); // 2.2
                            checkPostResponse();
                        })
                        .addOnFailureListener(e -> ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog));
                reference.child(firebasePecaPathsFirstKey).child(peca.getObra().getUid()).child(peca.getElemento().getUid()).child(peca.getTag()).child(firebasePecaPathsSecondKey).child(etapa).setValue(etapa_atual)
                        .addOnSuccessListener(unused -> {
                            loadingDialog.tick(); // 2.3
                            checkPostResponse();
                        })
                        .addOnFailureListener(e -> ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog));
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        });

        loadingDialog.tick(); // 1

        ApiChecklistCallback apiCallback = response -> {
            if(this.isFinishing() || this.isDestroyed()) return;
            try{
                checklist = response.get(etapa);
                if(checklist == null) throw new FirebaseDatabaseException(EXCEPTION_NULL_UID);
                responseApiChecklist = true;
                checkApiResponse();
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        };
        new ApiChecklist(this, database, this.getClass().getSimpleName(), loadingDialog, errorDialog, apiCallback, null);

        ApiErrosCallback apiErrosCallback = response -> {
            if(this.isFinishing() || this.isDestroyed()) return;
            try{
                errosMap = response;
                responseApiErros = true;
                checkApiResponse();
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        };
        new ApiErros(this, database, this.getClass().getSimpleName(), loadingDialog, errorDialog, apiErrosCallback, true);
    }

    void checkPostResponse(){
        postResponse.add(true);
        if(postResponse.size() == 2){
            loadingDialog.finalTick(); // 2.4
            loadingDialog.endLoadingDialog();
            successDialog.showSuccess("Operação realizada com Sucesso!", "Aperte em Ok para Prosseguir!");
            successDialog.getButton1().setOnClickListener(view1 -> {
                successDialog.endSuccessDialog();
                finish();
            });
        }
    }

    void callApiPecas(){
        ApiPecasCallback apiPecasCallback = response -> {
            pecaTreeMap = response;
            responseApiPecas = true;
            serviceRfidReader.setPecasMap(pecaTreeMap);
            elementosMap = apiPecas.getElementos();
            checkApiResponse();
            generateChecklist();
        };

        apiPecas = new ApiPecas(this, database, this.getClass().getSimpleName(), loadingDialog, errorDialog, apiPecasCallback, obra);

        tagItem.setOnClickListener(view -> {
            if(peca.getTag() == null){
                textViewElemento.setText("");
                hideErrorList();
                return;
            }
            textViewElemento.setText(peca.getElemento().getNome());
            generateErrorlist();

        });
        tagItem.setClickable(false);
    }

    private void checkApiResponse(){
        loadingDialog.tick();
        if(responseApiPecas && responseApiChecklist && responseApiErros) loadingDialog.endLoadingDialog();
    }

    ActivityResultLauncher<Intent> selectObraFromList = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        Intent data = result.getData();
                        if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                        obra = (Obra) data.getSerializableExtra("result");
                        peca.setObra(obra);
                        textViewObra.setText(peca.getObra().getNomeObra());
                        loadingDialog.continueLoadingDialog();
                        callApiPecas();
                    } catch (LayoutException e) {
                        errorDialog.getButton().setOnClickListener(view -> {
                            errorDialog.endErrorDialog();
                            onBackPressed();
                        });
                        ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
                    }
                }else{
                    finish();
                }
            });

    ActivityResultLauncher<Intent> restartOnResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    reestartActivity();
                }
            });

    public void reestartActivity(){
        this.recreate();
    }

    private boolean inputFieldsAreNotValid(Map<String, Object> firebaseObj){

        for (String key: firebaseObj.keySet()) {
            if(firebaseObj.get(key) == null){
                Map<String, String> response = new HashMap<>();
                response.put("errorCode", "Valor preenchido inválido");
                response.put("message", "Preencha o campo: \" + key!");
                loadingDialog.endLoadingDialog();
                errorDialog.showError(response);
                return true;
            }else if(firebaseObj.get(key) == null){
                Map<String, String> response = new HashMap<>();
                response.put("errorCode", "Valor preenchido inválido");
                response.put("message", "Preencha o campo: \" + key");
                loadingDialog.endLoadingDialog();
                errorDialog.showError(response);
                return true;
            }
        }
        return false;
    }

    private void generateChecklist(){
        checklist.createCheckedList();
        View checklistHeaderXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_header, checklistHeader, false);
        TextView item1 = checklistHeaderXML.findViewById(R.id.item1);
        String Text1 = "Checklist - " +  prettyEtapas.get(etapa);
        item1.setText(Text1);
        checklistHeader.addView(checklistHeaderXML);

        int ordem = 0;
        for (String checklistItem : checklist.getItems().values()) {
            ordem++;
            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_ordered, checkList, false);
            TextView objItem1 = obj.findViewById(R.id.item1);
            TextView objItem2 = obj.findViewById(R.id.item2);
            ImageView objItem3 = obj.findViewById(R.id.item3);
            objItem1.setText(String.valueOf(ordem));
            objItem2.setText(checklistItem);
            objItem3.setImageResource(R.drawable.empty_box);
            objItem3.setTag(checklistItem);
            objItem3.setOnClickListener(view -> {
                String item = (String) objItem3.getTag();
                if(checklist.markCheckedListItem(item)){
                    objItem3.setImageResource(R.drawable.checked);
                }else{
                    objItem3.setImageResource(R.drawable.empty_box);
                }
            });
            checkList.addView(obj);
        }
    }

    private void hideErrorList(){
        errosLayout.setVisibility(View.GONE);
    }

    private void generateErrorlist(){
        errosLayout.setVisibility(View.VISIBLE);
        pecaLocked.setChecked(false);

        if(errorListHeader.getChildCount() == 0){
            View errorListHeaderXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_header, errorListHeader, false);
            TextView item1 = errorListHeaderXML.findViewById(R.id.item1);
            String Text1 = "Inconformidades Em Aberto";
            item1.setText(Text1);
            errorListHeader.addView(errorListHeaderXML);
        }
        errorList.removeAllViews();
        if(errosMap.isEmpty()){
            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_ordered, errorList, false);
            TextView objItem1 = obj.findViewById(R.id.item1);
            String text = "Nenhum Erro detectado";
            objItem1.setText(text);
            errorList.addView(obj);
            return;
        }

        TreeMap<String, Erro> filteredErrosMap = new TreeMap<>();

        for(Erro erro : errosMap.values()){
            if(erro.getEtapaDetectada().equals(planejamentoKey) && erro.getUidElemento().equals(peca.getElemento().getUid())) filteredErrosMap.put(erro.getUid(), erro);
            if(!serviceRfidReader.getTagMap().isEmpty() && peca.getTag() != null) if(!erro.getEtapaDetectada().equals(planejamentoKey) && serviceRfidReader.getTagMap().containsKey(erro.getTag())) filteredErrosMap.put(erro.getUid(), erro);
        }

        if(filteredErrosMap.isEmpty()){
            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item, errorList, false);
            TextView objItem1 = obj.findViewById(R.id.item1);
            String text = "Nenhum Erro detectado";
            objItem1.setText(text);
            errorList.addView(obj);
            return;
        }

        for (Erro erro : filteredErrosMap.values()) {
            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item, errorList, false);
            TextView objItem1 = obj.findViewById(R.id.item1);
            TextView objItem2 = obj.findViewById(R.id.item2);
            TextView objItem3 = obj.findViewById(R.id.item3);
            ImageView objItem4 = obj.findViewById(R.id.item4);
            objItem1.setText(erro.getCreation().substring(0, 11));
            objItem2.setText(erro.getItem());
            objItem3.setText((erro.getEtapaDetectada().equals(planejamentoKey) ? peca.getElemento().getNome() : erro.getTag()));
            objItem4.setImageResource(R.drawable.forward);
            objItem4.setTag(erro);
            objItem4.setOnClickListener(view -> {
                Intent intent = new Intent(this, SolucionarErro.class);
                Erro tagErro = (Erro) objItem4.getTag();
                if(tagErro == null || elementosMap == null) return;
                Elemento elemento = elementosMap.get(erro.getUidElemento());
                if(elemento == null || pecaTreeMap == null) return;
                if(!tagErro.getEtapaDetectada().equals(planejamentoKey) && pecaTreeMap.get(erro.getTag()) == null) return;
                intent.putExtra("dependency", new Object[]{tagErro, (tagErro.getEtapaDetectada().equals(planejamentoKey)) ? elemento : pecaTreeMap.get(erro.getTag())});
                restartOnResult.launch(intent);
            });
            if(erro.getStatusLocked().equals(String.valueOf(true))) pecaLocked.setChecked(true);
            errorList.addView(obj);
        }
    }

    @Override
    protected void onDestroy() {
        serviceRfidReader.clearData();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
        questionDialog.endQuestionDialog();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
        questionDialog.endQuestionDialog();
    }
}