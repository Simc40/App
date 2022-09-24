package com.android.simc40.moduloGerenciamento;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.simc40.R;
import com.android.simc40.classes.Obra;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.selecaoListas.SelecaoListaObras;

public class GerenciamentoObras extends AppCompatActivity implements LayoutExceptionErrorList {

    CardView visualizarPDF, reportarInformacaoIncorreta, goBackEnd;
    Obra obra;
    TextView goBack, textViewNomeObra, textViewResponsavel, textViewTipoConstrucao, textViewCep, textViewCidade, textViewBairro, textViewEndereco, textViewUf, textViewPecasPlanejadas, textViewPrevisaoInicio, textViewPrevisaoFim, textViewCreation, textViewCreatedBy, textViewLastModifiedOn, textViewLastModifiedBy;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    DoubleClick doubleClick = new DoubleClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_obras);

        goBack = findViewById(R.id.goBack);
        goBackEnd = findViewById(R.id.goBackEnd);

        View.OnClickListener goBackListener = view ->  {
            if(doubleClick.detected()) return;
            finish();
        };

        goBack.setOnClickListener(goBackListener);
        goBackEnd.setOnClickListener(goBackListener);

        errorDialog = new ErrorDialog(GerenciamentoObras.this);
        loadingDialog = new LoadingDialog(GerenciamentoObras.this, errorDialog);

        textViewNomeObra = findViewById(R.id.nomeObra);
        textViewResponsavel = findViewById(R.id.responsavel);
        textViewTipoConstrucao = findViewById(R.id.tipoConstrucao);
        textViewCep = findViewById(R.id.cep);
        textViewCidade = findViewById(R.id.cidade);
        textViewBairro = findViewById(R.id.bairro);
        textViewEndereco = findViewById(R.id.endereco);
        textViewUf = findViewById(R.id.uf);
        textViewPecasPlanejadas = findViewById(R.id.quantidadePecas);
        textViewPrevisaoInicio = findViewById(R.id.previsaoInicio);
        textViewPrevisaoFim = findViewById(R.id.previsaoFim);
        textViewCreation = findViewById(R.id.creation);
        textViewCreatedBy = findViewById(R.id.createdBy);
        textViewLastModifiedOn = findViewById(R.id.lastModifiedOn);
        textViewLastModifiedBy = findViewById(R.id.lastModifiedBy);

        Intent intent = new Intent(GerenciamentoObras.this, SelecaoListaObras.class);
        selectObraFromList.launch(intent);
    }

    ActivityResultLauncher<Intent> selectObraFromList = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
            Intent data = result.getData();
            if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
            obra = (Obra) data.getSerializableExtra("result");
            fill_fields(obra);
            System.out.println(obra);
            } catch (LayoutException e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        }else{
            finish();
        }
    });

    void fill_fields(Obra obra) throws LayoutException {
        try{
            if(textViewNomeObra != null && obra.getNomeObra() != null) textViewNomeObra.setText(obra.getNomeObra());
            if(textViewResponsavel != null && obra.getResponsavel() != null) textViewResponsavel.setText(obra.getResponsavel());
            if(textViewTipoConstrucao != null && obra.getTipoConstrucao() != null) textViewTipoConstrucao.setText(obra.getTipoConstrucao());
            if(textViewCep != null && obra.getCep() != null) textViewCep.setText(obra.getCep());
            if(textViewCidade != null && obra.getCidade() != null) textViewCidade.setText(obra.getCidade());
            if(textViewBairro != null && obra.getBairro() != null) textViewBairro.setText(obra.getBairro());
            if(textViewEndereco != null && obra.getEndereco() != null) textViewEndereco.setText(obra.getEndereco());
            if(textViewUf != null && obra.getUf() != null) textViewUf.setText(obra.getUf());
            if(textViewPecasPlanejadas != null && obra.getPecasPlanejadas() != null) textViewPecasPlanejadas.setText(obra.getPecasPlanejadas());
            if(textViewPrevisaoInicio != null && obra.getPrevisaoInicio() != null) textViewPrevisaoInicio.setText(obra.getPrevisaoInicio());
            if(textViewPrevisaoFim != null && obra.getPrevisaoFim() != null) textViewPrevisaoFim.setText(obra.getPrevisaoFim());
            if(textViewCreation != null && obra.getCreation() != null) textViewCreation.setText(obra.getCreation());
            if(textViewCreatedBy != null && obra.getCreatedBy() != null) textViewCreatedBy.setText(obra.getCreatedBy());
            if(textViewLastModifiedOn != null && obra.getLastModifiedOn() != null) textViewLastModifiedOn.setText(obra.getLastModifiedOn());
            if(textViewLastModifiedBy != null && obra.getLastModifiedBy() != null) textViewLastModifiedBy.setText(obra.getLastModifiedBy());
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }
    }
}