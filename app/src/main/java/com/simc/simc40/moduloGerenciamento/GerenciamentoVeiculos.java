package com.simc.simc40.moduloGerenciamento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.classes.Transportadora;
import com.simc.simc40.classes.Veiculo;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.dialogs.ModalDeCarregamento;


public class GerenciamentoVeiculos extends AppCompatActivity implements LayoutExceptionErrorList {

    CardView reportarInformacaoIncorreta, goBackEnd;
    TextView goBack, textViewTransportadora, textViewCapacidadeCarga, textViewMarca, textViewModelo, textViewNumeroEixos, textViewPeso, textViewPlaca, textViewCreation, textViewCreatedBy, textViewLastModifiedOn, textViewLastModifiedBy;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    DuploClique duploClique = new DuploClique();
    Transportadora transportadora;
    Veiculo veiculo;
    Intent intent;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_veiculos);

        intent = getIntent();
        Object[] dependency = (Object[]) intent.getSerializableExtra("dependency");
        transportadora = (Transportadora) dependency[0];
        veiculo = (Veiculo) dependency[1];

        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            finish();
        });

        goBackEnd = findViewById(R.id.goBackEnd);
        goBackEnd.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            finish();
        });

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);

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

        fill_fields(transportadora, veiculo);
    }

    @SuppressLint("SetTextI18n")
    void fill_fields(Transportadora transportadora, Veiculo veiculo) {
        try{
            textViewTransportadora.setText(transportadora.getNome());
            textViewPlaca.setText(veiculo.getPlaca());

            textViewMarca.setText(veiculo.getMarca());
            textViewModelo.setText(veiculo.getModelo());
            textViewNumeroEixos.setText(veiculo.getNumeroEixos());
            textViewCreation.setText(veiculo.getCreation());
            textViewCreatedBy.setText(veiculo.getCreatedBy());
            textViewLastModifiedOn.setText(veiculo.getLastModifiedOn());
            textViewLastModifiedBy.setText(veiculo.getLastModifiedBy());
            textViewCapacidadeCarga.setText(veiculo.getCapacidadeCarga() + "Kg");
            textViewPeso.setText(veiculo.getPeso() + "Kg");
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }
}