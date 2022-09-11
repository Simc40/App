package com.android.simc40.moduloGerenciamento;

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

import com.android.simc40.Images.DownloadImage;
import com.android.simc40.R;
import com.android.simc40.classes.Elemento;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.loadingPage.LoadingPage;
import com.android.simc40.selecaoListas.SelecaoListaElementos;

public class GerenciamentoElementos extends AppCompatActivity implements LayoutExceptionErrorList {

    CardView visualizarPDF, reportarInformacaoIncorreta, goBackEnd;
    TextView goBack;
    TextView textViewObra, textViewForma, textViewTipoDePeca, textViewNome, textViewB, textViewH, textViewC, textViewPecasPlanejadas, textViewPecasCadastradas, textViewFckDesf, textViewFckIc, textViewVolume, textViewPeso, textViewPesoAco, textViewStatus, textViewCreation, textViewCreatedBy, textViewLastModifiedOn, textViewLastModifiedBy;
    ImageView imageViewImagemTipo;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    String contextException = "Gerenc.Elementos";
    DoubleClick doubleClick = new DoubleClick();
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

        View.OnClickListener goBackListener = view ->  {
            if(doubleClick.detected()) return;
            finish();
        };

        goBack.setOnClickListener(goBackListener);
        goBackEnd.setOnClickListener(goBackListener);

        errorDialog = new ErrorDialog(GerenciamentoElementos.this);
        loadingPage = new LoadingPage(GerenciamentoElementos.this, errorDialog);

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
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }
        }else{
            finish();
        }
    });

    @SuppressLint("SetTextI18n")
    void fill_fields(Elemento elemento) {
        try{
            DownloadImage.fromUrlLayoutGone(imageViewImagemTipo, elemento.getTipoDePeca().getImgUrl());
            if(textViewObra != null && elemento.getObra() != null) textViewObra.setText(elemento.getObra().getNomeObra());
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
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }
    }
}