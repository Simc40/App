package com.android.simc40.moduloQualidade.moduloProducao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.simc40.PDFs.DisplayLoadedPDF;
import com.android.simc40.R;
import com.android.simc40.checklist.Etapas;
import com.android.simc40.classes.Checklist;
import com.android.simc40.classes.Elemento;
import com.android.simc40.classes.Obra;
import com.android.simc40.classes.Pdf;
import com.android.simc40.classes.Peca;
import com.android.simc40.classes.User;
import com.android.simc40.configuracaoLeitor.ListaLeitores;
import com.android.simc40.configuracaoLeitor.readerConnectorQrCode;
import com.android.simc40.configuracaoLeitor.readerConnectorUHF1128;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.errorDialog.ErrorDialog;
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
import com.android.simc40.firebaseApiGET.ApiPDFElemento;
import com.android.simc40.firebaseApiGET.ApiPDFElementoCallback;
import com.android.simc40.firebaseApiGET.ApiPeca;
import com.android.simc40.firebaseApiGET.ApiPecaCallback;
import com.android.simc40.home.Home;
import com.android.simc40.loadingPage.LoadingPage;
import com.android.simc40.moduloGerenciamento.GerenciamentoChecklist;
import com.android.simc40.moduloGerenciamento.GerenciamentoElementos;
import com.android.simc40.moduloQualidade.relatorio_erros.RelatarErro;
import com.android.simc40.selecaoListas.SelecaoListaElementos;
import com.android.simc40.selecaoListas.SelecaoListaObras;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.android.simc40.successDialog.SuccessDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cadastro extends AppCompatActivity implements SharedPrefsExceptionErrorList, LayoutExceptionErrorList, Etapas, QualidadeExceptionErrorList, ListaLeitores {

    User user;
    Peca peca;
    Checklist checklist;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    SuccessDialog successDialog;
    readerConnectorQrCode readerConnectorQrCode;

    TextView goBack, textViewObra, textViewElemento;
    DatabaseReference reference;
    //String database, userUid, obraUid, modeloUid, etapa = "cadastro";
    static final String etapa = cadastroKey;
    static final String contextException = cadastroKey;
    //int numMax, numPecas;
    HashMap<String, Object> firebaseObj;
    Button read, clear;
    CardView projetoLocacao, projetoPeca, informacoesProjeto, reportarErro,  submitForm;
    DoubleClick doubleClick = new DoubleClick();
    boolean processing = false;
    LinearLayout tagHeader, tagItem;
    readerConnectorUHF1128 readerConnectorUHF1128;

    String database, selectedReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mproducao_cadastro);

        errorDialog = new ErrorDialog(Cadastro.this);
        loadingPage = new LoadingPage(Cadastro.this, errorDialog);
        successDialog = new SuccessDialog(Cadastro.this);

        try {
            peca = new Peca(cadastroKey);
        } catch (Exception e) {
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }

        textViewObra = findViewById(R.id.obra);
        textViewElemento = findViewById(R.id.elemento);
        goBack = findViewById(R.id.goBack);
        projetoLocacao = findViewById(R.id.projetoLocacao);
        projetoPeca = findViewById(R.id.projetoPeca);
        informacoesProjeto = findViewById(R.id.informacoesProjeto);
        reportarErro = findViewById(R.id.reportarErro);
        submitForm = findViewById(R.id.submitForm);

        //ReaderConnectorItems
        read = findViewById(R.id.read);
        clear = findViewById(R.id.clear);
        tagItem = findViewById(R.id.tagItem);
        tagHeader = findViewById(R.id.tagItemHeader);

        try{
            user = sharedPrefsDatabase.getUser(Cadastro.this, MODE_PRIVATE, loadingPage, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = user.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }

        goBack.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            onBackPressed();
        });

        Intent intent = new Intent(Cadastro.this, SelecaoListaObras.class);
        selectObraFromList.launch(intent);

        textViewObra.setOnClickListener(view -> {
            Intent oIntent = getIntent();
            finish();
            startActivity(oIntent);
        });

        textViewElemento.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            try{
                Intent mIntent = new Intent(Cadastro.this, SelecaoListaElementos.class);
                mIntent.putExtra("dependency", peca.getObra().getUid());
                selectElementoFromList.launch(mIntent);
            } catch (Exception e){
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }
        });

        try{
            selectedReader = sharedPrefsDatabase.getReader(Cadastro.this, MODE_PRIVATE, loadingPage, errorDialog);
            if(selectedReader.equals(UHF_RFID_Reader_1128)){
                readerConnectorUHF1128 = new readerConnectorUHF1128(Cadastro.this, tagItem, read, clear);
                View headerXML = LayoutInflater.from(Cadastro.this).inflate(R.layout.custom_tag_layout_header, tagHeader ,false);
                tagHeader.addView(headerXML);
            }
            else if(selectedReader.equals(QR_CODE)){
                readerConnectorQrCode = new readerConnectorQrCode(Cadastro.this, tagItem, read, clear);
                View headerXML = LayoutInflater.from(Cadastro.this).inflate(R.layout.custom_tag_layout_header, tagHeader ,false);
                tagHeader.addView(headerXML);
            }
        }catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }


        projetoLocacao.setOnClickListener(view -> {
            try{
                if(peca.getObra().getPdfUrl() != null && !peca.getObra().getPdfUrl().equals("")){
                    Intent plIntent = new Intent(Cadastro.this, DisplayLoadedPDF.class);
                    plIntent.putExtra("dependency", new Pdf("PDF Locação\n" + peca.getObra().getNomeObra(), peca.getObra().getPdfUrl()));
                    startActivity(plIntent);
                }else{
                    throw new QualidadeException(EXCEPTION_NO_REGISTERED_PDF_IN_OBRA);
                }
            } catch (Exception e){
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }
        });

        projetoPeca.setOnClickListener(view -> {
            try{
                if(peca.getElemento() == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                if(peca.getElemento().getPdfUrl() != null && !peca.getElemento().getPdfUrl().equals("")){
                    Intent plIntent = new Intent(Cadastro.this, DisplayLoadedPDF.class);
                    plIntent.putExtra("dependency", new Pdf("PDF de Peça\n" + peca.getElemento().getNome(), peca.getElemento().getPdfUrl()));
                    startActivity(plIntent);
                }else{
                    throw new QualidadeException(EXCEPTION_NO_REGISTERED_PDF_IN_ELEMENTO);
                }
            } catch (Exception e){
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }
        });



        informacoesProjeto.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            try{
                if(peca.getElemento() == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                Intent ipIntent = new Intent(Cadastro.this, GerenciamentoElementos.class);
                ipIntent.putExtra("dependency", peca.getElemento());
                startActivity(ipIntent);
            } catch (Exception e){
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }
        });

//        reportarErro.setOnClickListener(view -> {
//            if(doubleClick.detected()) return;
//            try{
//                if(peca.getElemento() == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
//                AlertDialog.Builder builder = new AlertDialog.Builder(Cadastro.this);
//                builder.setCancelable(true);
//                builder.setTitle("Reportar Inconformidade");
//                builder.setMessage("A inconformidade é do tipo:");
//                builder.setPositiveButton("Planejamento", (dialog, which) -> {
//                    Intent epIntent = new Intent(Cadastro.this, RelatarErro.class);
//                    epIntent.putExtra("dependency", peca);
//                    epIntent.putExtra("etapa", "planejamento");
//                    relatarErroDePlanejamento.launch(epIntent);
//                });
//                builder.setNegativeButton("Cadastro", (dialog, whichButton) -> {
//                    if(moreThanOneOrNoneTag()) return;
//                    Intent epIntent = new Intent(Cadastro.this, RelatarErro.class);
//                    epIntent.putExtra("dependency", peca);
//                    epIntent.putExtra("etapa", "cadastro");
//                    relatarErroDePlanejamento.launch(epIntent);
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            } catch (Exception e){
//                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
//            }
//        });


        submitForm.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            loadingPage.setTimeLimitMillisseconds(25000);
            if(loadingPage.isVisible) return;
            loadingPage.showLoadingPage();
            try{
                if(selectedReader.equals(UHF_RFID_Reader_1128) && readerConnectorUHF1128.getTagMap().size() > 1) throw new QualidadeException(EXCEPTION_MULTIPLE_SELECTED_TAGS);
                else if(selectedReader.equals(QR_CODE) && readerConnectorQrCode.getTagMap().size() > 1) throw new QualidadeException(EXCEPTION_MULTIPLE_SELECTED_TAGS);
                if(peca.getElemento() == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                firebaseObj = new HashMap<>();
                @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                firebaseObj.put("elemento", peca.getElemento().getUid());
                firebaseObj.put("etapa_atual", armacaoKey);
                firebaseObj.put("lastModifiedBy", user.getUid());
                firebaseObj.put("lastModifiedOn", data);
                firebaseObj.put("nome_peca", peca.getElemento().getNome() + (Integer.parseInt(peca.getElemento().getPecasCadastradas()) + 1));
                firebaseObj.put("num", String.valueOf(Integer.parseInt(peca.getElemento().getPecasCadastradas()) + 1));
                System.out.println("Finished FirebaseObject");
                if (inputFieldsAreNotValid(firebaseObj)) return;
                System.out.println("Finished inputFieldsAreNotValid");
                int pecasEmAtividades = (selectedReader.equals(UHF_RFID_Reader_1128)) ? readerConnectorUHF1128.countRegisteredTags().size() : readerConnectorQrCode.countRegisteredTags().size();
                int numMax = Integer.parseInt(peca.getElemento().getPecasPlanejadas());
                int numPecas = Integer.parseInt(peca.getElemento().getPecasCadastradas());
                if (pecasEmAtividades + Integer.parseInt(peca.getElemento().getPecasCadastradas()) > Integer.parseInt(peca.getElemento().getPecasPlanejadas())) {
                    if ((numMax - numPecas) == 0) {
                        Map<String, String> response = new HashMap<>();
                        response.put("errorCode", "Planejamento Já foi atendido.");
                        response.put("message", "O Elemento " + peca.getElemento().getNome() + " Não tem mais peças para registro no planejamento");
                        loadingPage.endLoadingPage();
                        errorDialog.showError(response);
                    } else if ((numMax - numPecas) == 1) {
                        Map<String, String> response = new HashMap<>();
                        response.put("errorCode", "Planejamento Já foi atendido.");
                        response.put("message", "O Elemento " + peca.getElemento().getNome() +  " só possui mais 1 peça para registro no planejamento");
                        loadingPage.endLoadingPage();
                        errorDialog.showError(response);
                    } else {
                        Map<String, String> response = new HashMap<>();
                        response.put("errorCode", "Planejamento Já foi atendido.");
                        response.put("message", "O Elemento " + peca.getElemento().getNome() +  " só possui mais \" + (numMax - numPecas) + \" peças para registro no planejamento\"");
                        loadingPage.endLoadingPage();
                        errorDialog.showError(response);
                    }
                    return;
                }
                System.out.println("First Etapa, going to Firebase");
                reference = FirebaseDatabase.getInstance(database).getReference();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            List<String> listaTags = (selectedReader.equals(UHF_RFID_Reader_1128)) ? readerConnectorUHF1128.countRegisteredTags() : readerConnectorQrCode.countRegisteredTags();
                            for (String tag : listaTags) {
                                if (snapshot.child("pecas").child(peca.getObra().getUid()).child(peca.getElemento().getUid()).child(tag).exists()) {
                                    Map<String, String> response = new HashMap<>();
                                    response.put("errorCode", "A peça já existe!.");
                                    response.put("message", "A tag :" + tag + " Já foi registrada");
                                    loadingPage.endLoadingPage();
                                    errorDialog.showError(response);
                                    return;
                                }
                            }
                            System.out.println("Non duplicates OK");
                            int i = numPecas;
                            for (String tag : listaTags) {
                                Map<String, Object> pecaObj = firebaseObj;
                                String num = String.valueOf(i);
                                i++;
                                pecaObj.put("num", num);
                                pecaObj.put("tag", tag);
                                pecaObj.put("nome_peca", peca.getElemento().getNome() + "-" + num);
                                Map<String, Object> etapa_atual = new HashMap<>();
                                etapa_atual.put("creation", data);
                                etapa_atual.put("createdBy", user.getUid());
                                etapa_atual.put("checklist", checklist.getUid());
                                //pecaObj.put("etapas/" + etapa, etapa_atual);
                                reference.child("pecas").child(peca.getObra().getUid()).child(peca.getElemento().getUid()).child(tag).setValue(pecaObj).addOnSuccessListener(unused -> Log.i(contextException, "SubmitForm step 1")).addOnFailureListener(e -> ErrorHandling.handleError(contextException, e, loadingPage, errorDialog));
                                reference.child("pecas").child(peca.getObra().getUid()).child(peca.getElemento().getUid()).child(tag).child("etapas").child(etapa).setValue(etapa_atual).addOnSuccessListener(unused -> Log.i(contextException, "SubmitForm step 2")).addOnFailureListener(e -> ErrorHandling.handleError(contextException, e, loadingPage, errorDialog));
                            }
                            reference.child("elementos").child(peca.getObra().getUid()).child(peca.getElemento().getUid()).child("numPecas").setValue(String.valueOf(i)).addOnSuccessListener(unused -> {
                                Log.i(contextException, "SubmitForm finished successfully");
                                successDialog.showSuccess("Peças cadastradas com Sucesso!", "Aperte em Ok para Prosseguir!");
                                successDialog.getButton().setOnClickListener(view1 -> {
                                    successDialog.endSuccessDialog();
                                    finish();
                                });
                            }).addOnFailureListener(e -> ErrorHandling.handleError(contextException, e, loadingPage, errorDialog));
                        }catch (Exception e){
                            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ErrorHandling.handleError(contextException, error.toException(), loadingPage, errorDialog);
                    reference.removeEventListener(this);
                    }
                });
            }catch (Exception e){
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }
        });

        ApiChecklistCallback apiCallback = response -> {
            if(this.isFinishing() || this.isDestroyed()) return;
            try{
                checklist = response.get(etapa);
                if(checklist == null) throw new FirebaseDatabaseException(EXCEPTION_NULL_UID);
                loadingPage.endLoadingPage();
            }catch (Exception e){
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }
        };
        new ApiChecklist(Cadastro.this, database, contextException, loadingPage, errorDialog, apiCallback, null);

    }

    ActivityResultLauncher<Intent> selectObraFromList = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                Obra obra = (Obra) data.getSerializableExtra("result");
                peca.setObra(obra);
                textViewObra.setText(peca.getObra().getNomeObra());
            } catch (LayoutException e) {
                errorDialog.getButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        errorDialog.endErrorDialog();
                        onBackPressed();
                    }
                });
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }
        }else{
            finish();
        }
    });

    ActivityResultLauncher<Intent> selectElementoFromList = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try{
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                Elemento elemento = (Elemento) data.getSerializableExtra("result");
                peca.setElemento(elemento);
                textViewElemento.setText(peca.getElemento().getNome());
                System.out.println(Integer.parseInt(peca.getElemento().getPecasCadastradas()));
            }catch (Exception e){
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }

//            modeloUid = data.getStringExtra("result1");
//            textViewElemento.setText(data.getStringExtra("result2"));
//            String max = data.getStringExtra("result3");
//            String num = data.getStringExtra("result4");
//            numMax = Integer.parseInt(max);
//            numPecas = Integer.parseInt(num);
        }
    });

    private boolean moreThanOneOrNoneTag() {
        if(readerConnectorUHF1128.countRegisteredTags().size() == 0){
            Toast.makeText(Cadastro.this, "Selecione uma tag para continuar", Toast.LENGTH_LONG).show();
            return true;
        }
        else if(readerConnectorUHF1128.countRegisteredTags().size() > 1){
            Toast.makeText(Cadastro.this, "Selecione apenas uma tag para continuar", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean inputFieldsAreNotValid(Map<String, Object> firebaseObj){

        for (String key: firebaseObj.keySet()) {
            if(firebaseObj.get(key) == null){
                Map<String, String> response = new HashMap<>();
                response.put("errorCode", "Valor preenchido inválido");
                response.put("message", "Preencha o campo: \" + key!");
                loadingPage.endLoadingPage();
                errorDialog.showError(response);
                return true;
            }else if(firebaseObj.get(key) == null){
                Map<String, String> response = new HashMap<>();
                response.put("errorCode", "Valor preenchido inválido");
                response.put("message", "Preencha o campo: \" + key");
                loadingPage.endLoadingPage();
                errorDialog.showError(response);
                return true;
            }
        }
        if(readerConnectorUHF1128 != null && readerConnectorUHF1128.getTagMap().isEmpty()){
            Map<String, String> response = new HashMap<>();
            response.put("errorCode", "Valor preenchido inválido");
            response.put("message", "Não há leitura de Tags");
            loadingPage.endLoadingPage();
            errorDialog.showError(response);
            return true;
        }else if(readerConnectorQrCode != null && readerConnectorQrCode.getTagMap().isEmpty()){
            Map<String, String> response = new HashMap<>();
            response.put("errorCode", "Valor preenchido inválido");
            response.put("message", "Não há leitura de Tags");
            loadingPage.endLoadingPage();
            errorDialog.showError(response);
            return true;
        }
        return false;
    }

    ActivityResultLauncher<Intent> relatarErroDePlanejamento = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            finish();
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            readerConnectorQrCode.addTagToLayout(result.getContents());
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingPage.endLoadingPage();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingPage.endLoadingPage();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
    }
}