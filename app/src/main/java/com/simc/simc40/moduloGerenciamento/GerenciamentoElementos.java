package com.simc.simc40.moduloGerenciamento;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simc.simc40.Images.DownloadImage;
import com.simc.simc40.PDFs.DisplayLoadedPDF;
import com.simc.simc40.R;
import com.simc.simc40.checklist.Etapas;
import com.simc.simc40.classes.Checklist;
import com.simc.simc40.classes.Elemento;
import com.simc.simc40.classes.Pdf;
import com.simc.simc40.classes.Peca;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.errorHandling.QualidadeException;
import com.simc.simc40.errorHandling.QualidadeExceptionErrorList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiChecklist;
import com.simc.simc40.firebaseApiGET.ApiChecklistCallback;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.moduloQualidade.relatorioErros.RelatarErro;
import com.simc.simc40.selecaoListas.SelecaoListaElementos;
import com.simc.simc40.sharedPreferences.LocalStorage;

import java.util.HashMap;

public class GerenciamentoElementos extends AppCompatActivity implements LayoutExceptionErrorList, QualidadeExceptionErrorList, Etapas, SharedPrefsExceptionErrorList {

    CardView visualizarPDF, reportarErro, goBackEnd;
    TextView goBack;
    TextView textViewObra, textViewForma, textViewTipoDePeca, textViewNome, textViewB, textViewH, textViewC, textViewPecasPlanejadas, textViewPecasCadastradas, textViewFckDesf, textViewFckIc, textViewVolume, textViewPeso, textViewPesoAco, textViewStatus, textViewCreation, textViewCreatedBy, textViewLastModifiedOn, textViewLastModifiedBy;
    ImageView imageViewImagemTipo;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    String database;
    Usuario usuario;
    DuploClique duploClique = new DuploClique();
    Checklist checklistPlanejamento;
    Elemento elemento;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_elementos);

        intent = getIntent();
        elemento = (Elemento) intent.getSerializableExtra("dependency");

        goBack = findViewById(R.id.goBack);
        goBackEnd = findViewById(R.id.goBackEnd);
        visualizarPDF = findViewById(R.id.PDF);
        reportarErro = findViewById(R.id.reportarErro);

        View.OnClickListener goBackListener = view ->  {
            if(duploClique.detectado()) return;
            finish();
        };

        goBack.setOnClickListener(goBackListener);
        goBackEnd.setOnClickListener(goBackListener);

        modalAlertaDeErro = new ModalAlertaDeErro(GerenciamentoElementos.this);
        modalDeCarregamento = new ModalDeCarregamento(GerenciamentoElementos.this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(3 + ApiChecklist.ticks);

        imageViewImagemTipo = findViewById(R.id.imagemTipo);
        textViewObra = findViewById(R.id.nomeObra);
        textViewForma = findViewById(R.id.forma);
        textViewTipoDePeca = findViewById(R.id.tipoElemento);
        textViewNome = findViewById(R.id.nomeElemento);
        textViewB = findViewById(R.id.b);
        textViewH = findViewById(R.id.h);
        textViewC = findViewById(R.id.c);
        textViewPecasPlanejadas = findViewById(R.id.pecasPlanejadas);
        textViewPecasCadastradas = findViewById(R.id.pecasCadastradas);
        textViewFckDesf = findViewById(R.id.fckDesf);
        textViewFckIc = findViewById(R.id.fckIc);
        textViewVolume = findViewById(R.id.volume);
        textViewPeso = findViewById(R.id.peso);
        textViewPesoAco = findViewById(R.id.taxaaco);
        textViewCreation = findViewById(R.id.creation);
        textViewCreatedBy = findViewById(R.id.createdBy);
        textViewLastModifiedOn = findViewById(R.id.lastModifiedOn);
        textViewLastModifiedBy = findViewById(R.id.lastModifiedBy);

        if (elemento == null) {
            Intent intent = new Intent(GerenciamentoElementos.this, SelecaoListaElementos.class);
            selectElementoFromList.launch(intent);
        }else{
            fill_fields(elemento);
        }

        try{
            usuario = LocalStorage.getUsuario(this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        modalDeCarregamento.avancarPasso(); // 1
        ApiChecklistCallback apiCallback = response -> {
            modalDeCarregamento.avancarPasso(); // 2
            if(this.isFinishing() || this.isDestroyed()) return;
            try{
                checklistPlanejamento = response.get(planejamentoKey);
                if(checklistPlanejamento == null) throw new FirebaseDatabaseException(EXCEPTION_NULL_UID);
                modalDeCarregamento.passoFinal(); // 3
                modalDeCarregamento.fecharModal();
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        };
        new ApiChecklist(elemento.getObra().getUid(), this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiCallback, null);

        visualizarPDF.setOnClickListener(view -> {
            try{
                if(elemento == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                if(elemento.getPdfUrl() != null && !elemento.getPdfUrl().equals("")){
                    Intent plIntent = new Intent(this, DisplayLoadedPDF.class);
                    plIntent.putExtra("dependency", new Pdf("PDF de Peça\n" + elemento.getNome(), elemento.getPdfUrl()));
                    startActivity(plIntent);
                }else{
                    throw new QualidadeException(EXCEPTION_NO_REGISTERED_PDF_IN_ELEMENTO);
                }
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        });

        reportarErro.setOnClickListener(view -> {
            Intent reIntent = new Intent(this, RelatarErro.class);
            Peca peca = new Peca("", "","", planejamentoKey, elemento.getObra(), elemento, "");
            reIntent.putExtra("dependency", new Object[]{new HashMap<String, Peca>(){{put(peca.getTag(), peca);}}, checklistPlanejamento});
            finishOnResult.launch(reIntent);
        });
    }

    ActivityResultLauncher<Intent> selectElementoFromList = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                elemento = (Elemento) data.getSerializableExtra("result");
                System.out.println(elemento);
                fill_fields(elemento);
            } catch (Exception e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        }else{
            finish();
        }
    });

    ActivityResultLauncher<Intent> finishOnResult = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = new Intent();
            setResult (GerenciamentoElementos.RESULT_OK, intent);
            finish();
        }
    });

    @SuppressLint("SetTextI18n")
    void fill_fields(Elemento elemento) {
        try{
            DownloadImage.fromUrlLayoutGone(imageViewImagemTipo, elemento.getTipoDePeca().getImgUrl());
            if(textViewObra != null && elemento.getObra() != null) textViewObra.setText(elemento.getObra().getNome_obra());
            if(textViewForma != null && elemento.getForma() != null) textViewForma.setText(elemento.getForma().getNome());
            if(textViewTipoDePeca != null && elemento.getTipoDePeca() != null) textViewTipoDePeca.setText(elemento.getTipoDePeca().getNome());
            if(textViewNome != null && elemento.getNome() != null) textViewNome.setText(elemento.getNome());
            if(textViewB != null && elemento.getB() != null) textViewB.setText(elemento.getB() + "cm");
            if(textViewH != null && elemento.getH() != null) textViewH.setText(elemento.getH() + "cm");
            if(textViewC != null && elemento.getC() != null) textViewC.setText(elemento.getC() + "m");
            if(textViewPecasPlanejadas != null && elemento.getPecasPlanejadas() != null) textViewPecasPlanejadas.setText(elemento.getPecasPlanejadas() + "pçs");
            if(textViewPecasCadastradas != null && elemento.getPecasCadastradas() != null) textViewPecasCadastradas.setText(elemento.getPecasCadastradas() + "pçs");
            if(textViewFckDesf != null && elemento.getFckDesf() != null) textViewFckDesf.setText(elemento.getFckDesf() + "MPa");
            if(textViewFckIc != null && elemento.getFckIc() != null) textViewFckIc.setText(elemento.getFckIc() + "MPa");
            if(textViewVolume != null && elemento.getVolume() != null) textViewVolume.setText(elemento.getVolume() + "m³");
            if(textViewPeso != null && elemento.getPeso() != null) textViewPeso.setText(elemento.getPeso() + "Kg");
            if(textViewPesoAco != null && elemento.getPesoAco() != null) textViewPesoAco.setText(elemento.getPesoAco() + "Kg");
            if(textViewCreation != null && elemento.getCreation() != null) textViewCreation.setText(elemento.getCreation());
            if(textViewCreatedBy != null && elemento.getCreatedBy() != null) textViewCreatedBy.setText(elemento.getCreatedBy());
            if(textViewLastModifiedOn != null && elemento.getLastModifiedOn() != null) textViewLastModifiedOn.setText(elemento.getLastModifiedOn());
            if(textViewLastModifiedBy != null && elemento.getLastModifiedBy() != null) textViewLastModifiedBy.setText(elemento.getLastModifiedBy());
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }
}