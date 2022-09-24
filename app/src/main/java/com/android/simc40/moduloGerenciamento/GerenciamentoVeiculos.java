package com.android.simc40.moduloGerenciamento;

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

import com.android.simc40.R;
import com.android.simc40.classes.Veiculo;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.selecaoListas.SelecaoListaVeiculos;


public class GerenciamentoVeiculos extends AppCompatActivity implements LayoutExceptionErrorList {

    CardView reportarInformacaoIncorreta, goBackEnd;
    TextView goBack, textViewTransportadora, textViewCapacidadeCarga, textViewMarca, textViewModelo, textViewNumeroEixos, textViewPeso, textViewPlaca, textViewCreation, textViewCreatedBy, textViewLastModifiedOn, textViewLastModifiedBy;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    DoubleClick doubleClick = new DoubleClick();
    Veiculo veiculo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_veiculos);

        goBack = findViewById(R.id.goBack);
        goBackEnd = findViewById(R.id.goBackEnd);

        View.OnClickListener goBackListener = view ->  {
            if(doubleClick.detected()) return;
            finish();
        };

        goBack.setOnClickListener(goBackListener);
        goBackEnd.setOnClickListener(goBackListener);

        errorDialog = new ErrorDialog(this);
        loadingDialog = new LoadingDialog(this, errorDialog);

        textViewTransportadora = findViewById(R.id.transportadora);
        textViewCapacidadeCarga = findViewById(R.id.capacidadeCarga);
        textViewMarca = findViewById(R.id.marca);
        textViewModelo = findViewById(R.id.modelo);
        textViewNumeroEixos = findViewById(R.id.numeroEixos);
        textViewPeso = findViewById(R.id.peso);
        textViewPlaca = findViewById(R.id.placa);
        textViewCreation = findViewById(R.id.creation);
        textViewCreatedBy = findViewById(R.id.createdBy);
        textViewLastModifiedOn = findViewById(R.id.lastModifiedOn);
        textViewLastModifiedBy = findViewById(R.id.lastModifiedBy);

        Intent intent = new Intent(this, SelecaoListaVeiculos.class);
        selectVeiculoFromList.launch(intent);
    }

    ActivityResultLauncher<Intent> selectVeiculoFromList = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        Intent data = result.getData();
                        if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                        veiculo = (Veiculo) data.getSerializableExtra("result");
                        fill_fields(veiculo);
                        System.out.println(veiculo);
                    } catch (LayoutException e) {
                        ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
                    }
                }else{
                    finish();
                }
            });

    @SuppressLint("SetTextI18n")
    void fill_fields(Veiculo veiculo) throws LayoutException {
        try{
            if(textViewCapacidadeCarga != null && veiculo.getCapacidadeCarga() != null) textViewCapacidadeCarga.setText(veiculo.getCapacidadeCarga() + "Kg");
            if(textViewTransportadora != null && veiculo.getTransportadora() != null) textViewTransportadora.setText(veiculo.getTransportadora().getNome());
            if(textViewMarca != null && veiculo.getMarca() != null) textViewMarca.setText(veiculo.getMarca());
            if(textViewModelo != null && veiculo.getModelo() != null) textViewModelo.setText(veiculo.getModelo());
            if(textViewNumeroEixos != null && veiculo.getNumeroEixos() != null) textViewNumeroEixos.setText(veiculo.getNumeroEixos());
            if(textViewPeso != null && veiculo.getPeso() != null) textViewPeso.setText(veiculo.getPeso() + "Kg");
            if(textViewPlaca != null && veiculo.getPlaca() != null) textViewPlaca.setText(veiculo.getPlaca());
            if(textViewCreation != null && veiculo.getCreation() != null) textViewCreation.setText(veiculo.getCreation());
            if(textViewCreatedBy != null && veiculo.getCreatedBy() != null) textViewCreatedBy.setText(veiculo.getCreatedBy());
            if(textViewLastModifiedOn != null && veiculo.getLastModifiedOn() != null) textViewLastModifiedOn.setText(veiculo.getLastModifiedOn());
            if(textViewLastModifiedBy != null && veiculo.getLastModifiedBy() != null) textViewLastModifiedBy.setText(veiculo.getLastModifiedBy());
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }
    }
}