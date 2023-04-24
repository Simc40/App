package com.simc.simc40.moduloGerenciamento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.classes.Motorista;
import com.simc.simc40.classes.Transportadora;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;

public class GerenciamentoMotoristas extends AppCompatActivity {

    CardView reportarInformacaoIncorreta, goBackEnd;
    TextView goBack, textViewTransportadora, textViewNome, textViewCnh, textViewEmail, textViewTelefone, textViewCreation, textViewCreatedBy, textViewLastModifiedOn, textViewLastModifiedBy;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    DuploClique duploClique = new DuploClique();
    Transportadora transportadora;
    Motorista motorista;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_motoristas);

        intent = getIntent();
        Object[] dependency = (Object[]) intent.getSerializableExtra("dependency");
        transportadora = (Transportadora) dependency[0];
        motorista = (Motorista) dependency[1];

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
        textViewNome = findViewById(R.id.nome);
        textViewCnh = findViewById(R.id.cnh);
        textViewEmail = findViewById(R.id.email);
        textViewTelefone = findViewById(R.id.telefone);
        textViewCreation = findViewById(R.id.creation);
        textViewCreatedBy = findViewById(R.id.createdBy);
        textViewLastModifiedOn = findViewById(R.id.lastModifiedOn);
        textViewLastModifiedBy = findViewById(R.id.lastModifiedBy);

        fill_fields(transportadora, motorista);
    }

    @SuppressLint("SetTextI18n")
    void fill_fields(Transportadora transportadora, Motorista motorista) {
        try{
            textViewTransportadora.setText(transportadora.getNome());
            textViewNome.setText(motorista.getNome());
            textViewCnh.setText(motorista.getCnh());
            textViewEmail.setText(motorista.getEmail());
            textViewTelefone.setText(motorista.getTelefone());
            textViewCreation.setText(motorista.getCreation());
            textViewCreatedBy.setText(motorista.getCreatedBy());
            textViewLastModifiedOn.setText(motorista.getLastModifiedOn());
            textViewLastModifiedBy.setText(motorista.getLastModifiedBy());
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }
}