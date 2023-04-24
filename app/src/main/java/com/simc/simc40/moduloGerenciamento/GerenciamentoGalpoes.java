package com.simc.simc40.moduloGerenciamento;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.classes.Galpao;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.selecaoListas.SelecaoListaGalpoes;

public class GerenciamentoGalpoes extends AppCompatActivity implements LayoutExceptionErrorList, FirebaseDatabaseExceptionErrorList {

    CardView visualizarControle, reportarInformacaoIncorreta, goBackEnd;
    Galpao galpao;
    TextView goBack, textViewNome, textViewStatus, textViewCreation, textViewCreatedBy, textViewLastModifiedOn, textViewLastModifiedBy;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    DuploClique duploClique = new DuploClique();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_galpoes);

        goBack = findViewById(R.id.goBack);
        goBackEnd = findViewById(R.id.goBackEnd);

        View.OnClickListener goBackListener = view -> {
            if (duploClique.detectado()) return;
            finish();
        };

        goBack.setOnClickListener(goBackListener);
        goBackEnd.setOnClickListener(goBackListener);

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);

        textViewNome = findViewById(R.id.nome);
        textViewStatus = findViewById(R.id.status);
        textViewCreation = findViewById(R.id.creation);
        textViewCreatedBy = findViewById(R.id.createdBy);
        textViewLastModifiedOn = findViewById(R.id.lastModifiedOn);
        textViewLastModifiedBy = findViewById(R.id.lastModifiedBy);
        visualizarControle = findViewById(R.id.controle);

        visualizarControle.setOnClickListener(view -> {
            try {
                if(galpao.getControle().isEmpty()) throw new FirebaseDatabaseException(EXCEPTION_NULL_CONTROLE_GALPAO);
                Intent intent = new Intent(this, GerenciamentoGalpoesControle.class);
                intent.putExtra("dependency", galpao);
                startActivity(intent);
            } catch (FirebaseDatabaseException e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        });

        Intent intent = new Intent(this, SelecaoListaGalpoes.class);
        intent.putExtra("control", true);
        selectGalpaoFromList.launch(intent);
    }

    ActivityResultLauncher<Intent> selectGalpaoFromList = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        Intent data = result.getData();
                        if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                        galpao = (Galpao) data.getSerializableExtra("result");
                        fill_fields(galpao);
                    } catch (LayoutException e) {
                        ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                    }
                } else {
                    finish();
                }
            });

    void fill_fields(Galpao galpao) throws LayoutException {
        try {
            textViewNome.setText(galpao.getNome());
            textViewStatus.setText(galpao.getPrettyStatus());
            textViewCreation.setText(galpao.getCreation());
            textViewCreatedBy.setText(galpao.getCreatedBy());
            textViewLastModifiedOn.setText(galpao.getLastModifiedOn());
            textViewLastModifiedBy.setText(galpao.getLastModifiedBy());
        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }
}