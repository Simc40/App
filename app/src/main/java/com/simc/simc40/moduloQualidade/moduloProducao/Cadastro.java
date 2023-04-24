package com.simc.simc40.moduloQualidade.moduloProducao;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simc.simc40.MainActivity;
import com.simc.simc40.PDFs.DisplayLoadedPDF;
import com.simc.simc40.R;
import com.simc.simc40.checklist.Etapas;
import com.simc.simc40.classes.Checklist;
import com.simc.simc40.classes.Elemento;
import com.simc.simc40.classes.Erro;
import com.simc.simc40.classes.Obra;
import com.simc.simc40.classes.Pdf;
import com.simc.simc40.classes.Peca;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.configuracaoLeitor.ClearCallback;
import com.simc.simc40.configuracaoLeitor.ListaLeitores;
import com.simc.simc40.configuracaoLeitor.ReadCallback;
import com.simc.simc40.configuracaoLeitor.ServiceRfidReader;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.dialogs.QuestionDialog;
import com.simc.simc40.dialogs.SuccessDialog;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.errorHandling.QualidadeException;
import com.simc.simc40.errorHandling.QualidadeExceptionErrorList;
import com.simc.simc40.errorHandling.ReaderException;
import com.simc.simc40.errorHandling.ReaderExceptionErrorList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiChecklist;
import com.simc.simc40.firebaseApiGET.ApiChecklistCallback;
import com.simc.simc40.firebaseApiGET.ApiErros;
import com.simc.simc40.firebaseApiGET.ApiErrosCallback;
import com.simc.simc40.firebaseApiGET.ApiPecas;
import com.simc.simc40.firebaseApiGET.ApiPecasCallback;
import com.simc.simc40.firebasePaths.FirebaseElementoPaths;
import com.simc.simc40.firebasePaths.FirebasePecaPaths;
import com.simc.simc40.moduloGerenciamento.GerenciamentoElementos;
import com.simc.simc40.moduloQualidade.relatorioErros.RelatarErro;
import com.simc.simc40.selecaoListas.SelecaoListaObras;
import com.simc.simc40.selecaoListas.SelecaoListaPecasCadastro;
import com.simc.simc40.sharedPreferences.LocalStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Cadastro extends Fragment implements SharedPrefsExceptionErrorList, LayoutExceptionErrorList, Etapas, QualidadeExceptionErrorList, ListaLeitores, FirebasePecaPaths, FirebaseElementoPaths, ReaderExceptionErrorList {

    Usuario usuario;
    boolean responseApiPecas = false, responseApiChecklist = false, responseApiErros = false;
    Checklist checklistPlanejamento;
    Checklist checklist;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    SuccessDialog successDialog;
    QuestionDialog questionDialog;
    TreeMap<String, Peca> pecaTreeMap = new TreeMap<>();
    TreeMap <String, Elemento> elementosMap;
    TreeMap <String, Erro> errosMap;
    TextView goBack, textViewObra, textViewElemento;
    DatabaseReference reference;
    static final String etapa = cadastroKey;
    HashMap<String, Object> firebaseObj;
    Button read, clear;
    CardView projetoLocacao, projetoPeca, informacoesProjeto, reportarErro,  submitForm;
    DuploClique duploClique = new DuploClique();
    LinearLayout tagHeader, tagItem, checklistHeader, checkList, errorListHeader, errorList, errosLayout;
    String database;
    boolean mBounded;
    CheckBox pecaLocked;
    ArrayList<Boolean> postResponse;
    ApiPecas apiPecas;
    ReadCallback readCallback;
    ClearCallback clearCallback;

    ServiceRfidReader serviceRfidReader;

    public Cadastro() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setViews(){


        goBack = requireView().findViewById(R.id.goBack);
        textViewObra = requireView().findViewById(R.id.obra);
        textViewElemento = requireView().findViewById(R.id.elemento);
        goBack = requireView().findViewById(R.id.goBack);
        projetoLocacao = requireView().findViewById(R.id.projetoLocacao);
        projetoPeca = requireView().findViewById(R.id.projetoPeca);
        informacoesProjeto = requireView().findViewById(R.id.informacoesProjeto);
        checklistHeader = requireView().findViewById(R.id.checklistHeader);
        errosLayout = requireView().findViewById(R.id.errosLayout);
        checkList = requireView().findViewById(R.id.checkList);
        errorListHeader = requireView().findViewById(R.id.errosListHeader);
        errorList = requireView().findViewById(R.id.errosList);
        pecaLocked = requireView().findViewById(R.id.pecaLocked);
        pecaLocked.setEnabled(false);
        reportarErro = requireView().findViewById(R.id.reportarErro);
        submitForm = requireView().findViewById(R.id.submitForm);

        //ReaderConnectorItems
        read = requireView().findViewById(R.id.read);
        clear = requireView().findViewById(R.id.clear);
        tagItem = requireView().findViewById(R.id.tagItem);
        tagHeader = requireView().findViewById(R.id.tagItemHeader);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mproducao_cadastro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews();

        goBack.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            getActivity().onBackPressed();
        });

        modalAlertaDeErro = new ModalAlertaDeErro(this.requireActivity());
        modalAlertaDeErro.getBotao().setOnClickListener(view12 -> {
            requireActivity().onBackPressed();
            modalAlertaDeErro.fecharModal();
        });

        modalDeCarregamento = new ModalDeCarregamento(this.requireActivity(), modalAlertaDeErro);
        successDialog = new SuccessDialog(this.requireActivity());
        questionDialog = new QuestionDialog(this.requireActivity());
        modalDeCarregamento.mostrarModalDeCarregamento(6 + ApiPecas.ticks + ApiErros.ticks + ApiChecklist.ticks);

        try{
            usuario = LocalStorage.getUsuario(this.requireActivity(), MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e){
            closePopUpOnHandleError(e);
        }

        modalDeCarregamento.avancarPasso(); // 1

        Intent intent = new Intent(this.requireActivity(), SelecaoListaObras.class);
        selectObraFromList.launch(intent);

        read.setOnClickListener(view1 -> {
            try{
                Elemento elemento = (Elemento) textViewElemento.getTag();
                if(elemento == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                serviceRfidReader.read();
            }catch (Exception e){
                closePopUpOnHandleError(e);
            }
        });

        readCallback = tag -> {
            try {
                Peca peca = (Peca) textViewElemento.getTag();
                if(serviceRfidReader.getReader().equals(QR_CODE)) throw new ReaderException(EXCEPTION_WRONG_READING_TYPE);
                peca.setTag(tag);
                textViewElemento.setTag(peca);
                serviceRfidReader.addTagToLayout(peca);
//                generateErrorlist();
            } catch (Exception e){
                closePopUpOnHandleError(e);
            }
        };

        clear.setOnClickListener(view1 -> {
            serviceRfidReader.clear();
//            generateErrorlist();
        });

        clearCallback = this::generateErrorlist;

        textViewObra.setOnClickListener(view1 -> {
            questionDialog.getButton2().setOnClickListener(view2 -> reestartActivity());
            questionDialog.showQuestion("Selecionar nova Obra?", "A página será recarregada e as informações já preenchidas serão perdidas.", "Não", "Sim");
        });

        textViewElemento.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            try{
                Obra obra = (Obra) textViewObra.getTag();
                Intent mIntent = new Intent(this.requireActivity(), SelecaoListaPecasCadastro.class);
                mIntent.putExtra("dependency", obra.getUid());
                selectPecaFromList.launch(mIntent);
            } catch (Exception e){
                closeFragmentOnHandleError(e);
            }
        });

        View headerXML = LayoutInflater.from(this.requireActivity()).inflate(R.layout.custom_tag_layout_header, tagHeader ,false);
        tagHeader.addView(headerXML);

        modalDeCarregamento.avancarPasso(); // 2

        projetoLocacao.setOnClickListener(view1 -> {
            try{
                Obra obra = (Obra) textViewObra.getTag();
                if(obra.getPdfUrl() != null && !obra.getPdfUrl().equals("")){
                    Intent plIntent = new Intent(this.requireActivity(), DisplayLoadedPDF.class);
                    plIntent.putExtra("dependency", new Pdf("PDF Locação\n" + obra.getNome_obra(), obra.getPdfUrl()));
                    startActivity(plIntent);
                }else{
                    throw new QualidadeException(EXCEPTION_NO_REGISTERED_PDF_IN_OBRA);
                }
            } catch (Exception e){
                closePopUpOnHandleError(e);
            }
        });

        projetoPeca.setOnClickListener(view1 -> {
            try{
                Elemento elemento = (Elemento) textViewElemento.getTag();
                if(elemento == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                if(elemento.getPdfUrl() != null && !elemento.getPdfUrl().equals("")){
                    Intent plIntent = new Intent(this.requireActivity(), DisplayLoadedPDF.class);
                    plIntent.putExtra("dependency", new Pdf("PDF de Peça\n" + elemento.getNome(), elemento.getPdfUrl()));
                    startActivity(plIntent);
                }else{
                    throw new QualidadeException(EXCEPTION_NO_REGISTERED_PDF_IN_ELEMENTO);
                }
            } catch (Exception e){
                closePopUpOnHandleError(e);
            }
        });

        informacoesProjeto.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            try{
                Elemento elemento = (Elemento) textViewElemento.getTag();
                if(elemento == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                Intent ipIntent = new Intent(this.requireActivity(), GerenciamentoElementos.class);
                ipIntent.putExtra("dependency", elemento);
                restartOnResult.launch(ipIntent);
            } catch (Exception e){
                closePopUpOnHandleError(e);
            }
        });

        reportarErro.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            try{
                Elemento elemento = (Elemento) textViewElemento.getTag();
                Peca peca = serviceRfidReader.getPeca();
                if(elemento == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                if(peca == null) throw new QualidadeException(EXCEPTION_NO_SELECTED_TAG_ON_SUBMIT);
                questionDialog.getButton1().setText(prettyShortEtapas.get(planejamentoKey));
                questionDialog.getButton1().setOnClickListener(view12 -> {
                    Intent reIntent = new Intent(this.requireActivity(), RelatarErro.class);
                    reIntent.putExtra("dependency", new Object[]{new HashMap<String, Peca>(){{put(peca.getTag(), peca);}}, checklistPlanejamento});
                    restartOnResult.launch(reIntent);
                });
                questionDialog.getButton2().setText(prettyShortEtapas.get(etapa));
                questionDialog.getButton2().setOnClickListener(view12 -> {
                    Intent reIntent = new Intent(this.requireActivity(), RelatarErro.class);
                    reIntent.putExtra("dependency", new Object[]{new HashMap<String, Peca>(){{put(peca.getTag(), peca);}}, checklist});
                    restartOnResult.launch(reIntent);
                });
                questionDialog.showQuestion("Reportar qual tipo de inconformidade?");
            } catch (Exception e){
                closePopUpOnHandleError(e);
            }
        });

        submitForm.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            if(modalDeCarregamento.estaVisivel) return;
            modalDeCarregamento.mostrarModalDeCarregamento(4);
            try{
                Obra obra = (Obra) textViewObra.getTag();
                Peca peca = (Peca) textViewElemento.getTag();
                if(checklist.getCheckedList().contains(false)) throw new QualidadeException(EXCEPTION_CHECKLIST_NOT_FILLED);
                if(peca == null) throw new QualidadeException(EXCEPTION_NO_SELECTED_TAG_ON_SUBMIT);
                if(pecaTreeMap.containsKey(peca.getTag())) throw new QualidadeException(EXCEPTION_TAG_ALREADY_REGISTERED);
                if(pecaLocked.isChecked()) throw new QualidadeException(EXCEPTION_PRODUCAO_INTERRUPTED);
                modalDeCarregamento.avancarPasso(); // 2.1
                postResponse = new ArrayList<>();
                reference = FirebaseDatabase.getInstance(database).getReference();
                reference.child("pecas").child(obra.getUid()).child(peca.getElementoObject().getUid()).child(peca.getUid()).child("tag").setValue(peca.getTag())
                .addOnSuccessListener(unused -> {
                    modalDeCarregamento.passoFinal(); // 2.5
                    modalDeCarregamento.fecharModal();
                    successDialog.showSuccess("Peças cadastradas com Sucesso!", "Aperte em Ok para Prosseguir!");
                    successDialog.getButton1().setOnClickListener(view2 -> {
                        successDialog.endSuccessDialog();
                        requireActivity().onBackPressed();
                    });
                }).addOnFailureListener(this::closeFragmentOnHandleError);

            }catch (Exception e){
                closeFragmentOnHandleError(e);
            }
        });

        modalDeCarregamento.avancarPasso(); // 3

        ApiErrosCallback apiErrosCallback = response -> {
            if(this.requireActivity().isFinishing() || this.requireActivity().isDestroyed()) return;
            try{
                errosMap = response;
                responseApiErros = true;
                checkApiResponse();
            }catch (Exception e){
                closeFragmentOnHandleError(e);
            }
        };
        new ApiErros(this.requireActivity(), database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiErrosCallback, true);
        serviceRfidReader = new ServiceRfidReader(getActivity(), tagItem, readCallback, clearCallback, modalDeCarregamento, modalAlertaDeErro);
    }

    ActivityResultLauncher<Intent> restartOnResult = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            reestartActivity();
        }
    });

    ActivityResultLauncher<Intent> selectObraFromList = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                Obra obra = (Obra) data.getSerializableExtra("result");
                textViewObra.setText(obra.getNome_obra());
                textViewObra.setTag(obra);
                modalDeCarregamento.continueLoadingDialog();
                callApiPecas();
            } catch (LayoutException e) {
                closeFragmentOnHandleError(e);
            }
        }else{
            requireActivity().onBackPressed();
        }
    });

    ActivityResultLauncher<Intent> selectPecaFromList = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try{
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                Peca peca = (Peca) data.getSerializableExtra("result");
                textViewElemento.setText(peca.getNome_peca());
                textViewElemento.setTag(peca);
                serviceRfidReader.clear();
            }catch (Exception e){
                closeFragmentOnHandleError(e);
            }
        }
    });

    private boolean inputFieldsAreNotValid(Map<String, Object> firebaseObj){

        for (String key: firebaseObj.keySet()) {
            if(firebaseObj.get(key) == null){
                Map<String, String> response = new HashMap<>();
                response.put("errorCode", "Valor preenchido inválido");
                response.put("message", "Preencha o campo: \" + key!");
                modalDeCarregamento.fecharModal();
                modalAlertaDeErro.showError(response);
                return true;
            }else if(firebaseObj.get(key) == null){
                Map<String, String> response = new HashMap<>();
                response.put("errorCode", "Valor preenchido inválido");
                response.put("message", "Preencha o campo: \" + key");
                modalDeCarregamento.fecharModal();
                modalAlertaDeErro.showError(response);
                return true;
            }
        }
        return false;
    }

    public void reestartActivity(){
        ((MainActivity) getActivity()).replaceFragments(Cadastro.class);
    }

    //Call Apis for data

    void callApiPecas(){
        ApiPecasCallback apiPecasCallback = response -> {
            pecaTreeMap = response;
            responseApiPecas = true;
            elementosMap = apiPecas.getElementos();
            checkApiResponse();
            generateChecklist();
        };
        Obra obra = (Obra) textViewObra.getTag();
        apiPecas = new ApiPecas(this.requireActivity(), database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiPecasCallback, obra);

        ApiChecklistCallback apiCallback = response -> {
            if(this.requireActivity().isFinishing() || this.requireActivity().isDestroyed()) return;
            try{
                checklist = response.get(etapa);
                if(checklist == null) throw new FirebaseDatabaseException(EXCEPTION_NULL_UID);
                responseApiChecklist = true;
                checkApiResponse();
            }catch (Exception e){
                closeFragmentOnHandleError(e);
            }
        };

        new ApiChecklist(obra.getUid(), this.requireActivity(), database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiCallback, null);

    }

    private void checkApiResponse(){
        modalDeCarregamento.avancarPasso(); // 4, 5, 6
        if(responseApiPecas && responseApiChecklist && responseApiErros) {
            modalAlertaDeErro.getBotao().setOnClickListener(view12 -> modalAlertaDeErro.fecharModal());
            modalDeCarregamento.fecharModal();
        }
    }

    //Generate Checklist Layout

    private void generateChecklist(){
        checklist.createCheckedList();
        View checklistHeaderXML = LayoutInflater.from(this.getContext()).inflate(R.layout.custom_3_list_item_header, checklistHeader, false);
        TextView item1 = checklistHeaderXML.findViewById(R.id.item1);
        String Text1 = "Checklist - " +  prettyEtapas.get(etapa);
        item1.setText(Text1);
        checklistHeader.addView(checklistHeaderXML);

        int ordem = 0;
        for (String checklistItem : checklist.getItems().values()) {
            ordem++;
            View obj = LayoutInflater.from(this.getContext()).inflate(R.layout.custom_3_list_item_ordered, checkList, false);
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

    private void generateErrorlist(){
        errosLayout.setVisibility(View.VISIBLE);
        pecaLocked.setChecked(false);

        Elemento elemento = (Elemento) textViewElemento.getTag();
        Peca peca = serviceRfidReader.getPeca();

        if(errorListHeader.getChildCount() == 0){
            View errorListHeaderXML = LayoutInflater.from(this.getContext()).inflate(R.layout.custom_3_list_item_header, errorListHeader, false);
            TextView item1 = errorListHeaderXML.findViewById(R.id.item1);
            String Text1 = "Inconformidades Em Aberto";
            item1.setText(Text1);
            errorListHeader.addView(errorListHeaderXML);
        }

        errorList.removeAllViews();
        if(errosMap.isEmpty()){
            View obj = LayoutInflater.from(this.getContext()).inflate(R.layout.custom_3_list_item_ordered, errorList, false);
            TextView objItem1 = obj.findViewById(R.id.item1);
            String text = "Nenhum Erro detectado";
            objItem1.setText(text);
            errorList.addView(obj);
            return;
        }

        TreeMap<String, Erro> filteredErrosMap = new TreeMap<>();

        for(Erro erro : errosMap.values()){
//            if(erro.getEtapa_detectada().equals(planejamentoKey) && erro.getUidElemento().equals(elemento.getUid())) filteredErrosMap.put(erro.getUid(), erro);
            if(peca != null) if(!erro.getEtapa_detectada().equals(planejamentoKey) && peca.getUid().equals(erro.getPeca())) filteredErrosMap.put(erro.getUid(), erro);
        }

        if(filteredErrosMap.isEmpty()){
            View obj = LayoutInflater.from(this.getContext()).inflate(R.layout.custom_3_list_item, errorList, false);
            TextView objItem1 = obj.findViewById(R.id.item1);
            String text = "Nenhum Erro detectado";
            objItem1.setText(text);
            errorList.addView(obj);
            return;
        }

//        for (Erro erro : filteredErrosMap.values()) {
//            View obj = LayoutInflater.from(this.getContext()).inflate(R.layout.custom_4_list_item, errorList, false);
//            TextView objItem1 = obj.findViewById(R.id.item1);
//            TextView objItem2 = obj.findViewById(R.id.item2);
//            TextView objItem3 = obj.findViewById(R.id.item3);
//            ImageView objItem4 = obj.findViewById(R.id.item4);
//            objItem1.setText(erro.getCreation().substring(0, 11));
//            objItem2.setText(erro.getItem());
//            objItem3.setText((erro.getEtapa_detectada().equals(planejamentoKey) ? elemento.getNome() : erro.getTag()));
//            objItem4.setImageResource(R.drawable.forward);
//            objItem4.setTag(erro);
//            objItem4.setOnClickListener(view -> {
//                Intent intent = new Intent(this.requireActivity(), SolucionarErro.class);
//                Erro tagErro = (Erro) objItem4.getTag();
//                if(tagErro == null || elementosMap == null) return;
//                Elemento elementoErro = elementosMap.get(erro.getUidElemento());
//                if(elementoErro == null || pecaTreeMap == null) return;
//                if(!tagErro.getEtapa_detectada().equals(planejamentoKey) && pecaTreeMap.get(erro.getTag()) == null) return;
//                intent.putExtra("dependency", new Object[]{tagErro, (tagErro.getEtapa_detectada().equals(planejamentoKey)) ? elementoErro : pecaTreeMap.get(erro.getTag())});
////                restartOnResult.launch(intent);
//            });
//            if(erro.getStatusLocked().equals(String.valueOf(true))) pecaLocked.setChecked(true);
//            errorList.addView(obj);
//        }
    }

    private void closeFragmentOnHandleError(Exception e){
        modalAlertaDeErro.getBotao().setOnClickListener(view -> {
            modalAlertaDeErro.fecharModal();
            requireActivity().onBackPressed();
        });
        ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
    }

    private void closePopUpOnHandleError(Exception e){
        modalAlertaDeErro.getBotao().setOnClickListener(view -> modalAlertaDeErro.fecharModal());
        ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
    }

    @Override
    public void onDestroy() {
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
        successDialog.endSuccessDialog();
        questionDialog.endQuestionDialog();
        serviceRfidReader.unregisterReceiver();
        super.onDestroy();
    }
}