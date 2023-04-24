package com.simc.simc40.moduloGerenciamento;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.classes.Obra;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.selecaoListas.SelecaoListaObras;

public class GerenciamentoObras extends AppCompatActivity implements LayoutExceptionErrorList {

    CardView visualizarPDF, reportarInformacaoIncorreta, goBackEnd;
    Obra obra;
    TextView goBack, textViewNomeObra, textViewResponsavel, textViewTipoConstrucao, textViewCep, textViewCidade, textViewBairro, textViewEndereco, textViewUf, textViewPecasPlanejadas, textViewPrevisaoInicio, textViewPrevisaoFim, textViewCreation, textViewCreatedBy, textViewLastModifiedOn, textViewLastModifiedBy;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    DuploClique duploClique = new DuploClique();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_obras);

        goBack = findViewById(R.id.goBack);
        goBackEnd = findViewById(R.id.goBackEnd);

        View.OnClickListener goBackListener = view ->  {
            if(duploClique.detectado()) return;
            finish();
        };

        goBack.setOnClickListener(goBackListener);
        goBackEnd.setOnClickListener(goBackListener);

        modalAlertaDeErro = new ModalAlertaDeErro(GerenciamentoObras.this);
        modalDeCarregamento = new ModalDeCarregamento(GerenciamentoObras.this, modalAlertaDeErro);

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
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        }else{
            finish();
        }
    });

    @SuppressLint("SetTextI18n")
    void fill_fields(Obra obra) throws LayoutException {
        try{
            if(textViewNomeObra != null && obra.getNome_obra() != null) textViewNomeObra.setText(obra.getNome_obra());
            if(textViewResponsavel != null && obra.getResponsavel() != null) textViewResponsavel.setText(obra.getResponsavel());
            if(textViewTipoConstrucao != null && obra.getTipo_construcao() != null) textViewTipoConstrucao.setText(obra.getTipo_construcao());
            if(textViewCep != null && obra.getCep() != null) textViewCep.setText(obra.getCep());
            if(textViewCidade != null && obra.getCidade() != null) textViewCidade.setText(obra.getCidade());
            if(textViewBairro != null && obra.getBairro() != null) textViewBairro.setText(obra.getBairro());
            if(textViewEndereco != null && obra.getEndereco() != null) textViewEndereco.setText(obra.getEndereco());
            if(textViewUf != null && obra.getUf() != null) textViewUf.setText(obra.getUf());
            if(textViewPecasPlanejadas != null && obra.getQuantidade_pecas() != null) textViewPecasPlanejadas.setText(obra.getQuantidade_pecas().toString());
            if(textViewPrevisaoInicio != null && obra.getPrevisao_inicio() != null) textViewPrevisaoInicio.setText(obra.getPrevisao_inicio());
            if(textViewPrevisaoFim != null && obra.getPrevisao_entrega() != null) textViewPrevisaoFim.setText(obra.getPrevisao_entrega());
            if(textViewCreation != null && obra.getCreation() != null) textViewCreation.setText(obra.getCreation());
            if(textViewCreatedBy != null && obra.getCreatedBy() != null) textViewCreatedBy.setText(obra.getCreatedBy());
            if(textViewLastModifiedOn != null && obra.getLastModifiedOn() != null) textViewLastModifiedOn.setText(obra.getLastModifiedOn());
            if(textViewLastModifiedBy != null && obra.getLastModifiedBy() != null) textViewLastModifiedBy.setText(obra.getLastModifiedBy());
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }
}