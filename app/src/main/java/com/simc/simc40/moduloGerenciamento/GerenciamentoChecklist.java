package com.simc.simc40.moduloGerenciamento;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.simc.simc40.R;
import com.simc.simc40.checklist.Etapas;
import com.simc.simc40.classes.Checklist;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiChecklistCallback;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.selecaoListas.SelecaoListaEtapas;
import com.simc.simc40.sharedPreferences.LocalStorage;

public class GerenciamentoChecklist extends AppCompatActivity implements LayoutExceptionErrorList, SharedPrefsExceptionErrorList, Etapas {

    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    String database;
    DuploClique duploClique = new DuploClique();
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    Usuario usuario;
    TextView title, checklistTitle, creation, createdBy;
    ConstraintLayout creationLayout, createdByLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_checklist);

        modalAlertaDeErro = new ModalAlertaDeErro(GerenciamentoChecklist.this);
        modalAlertaDeErro.getBotao().setOnClickListener(null);
        modalAlertaDeErro.getBotao().setOnClickListener(view -> finish());
        modalDeCarregamento = new ModalDeCarregamento(GerenciamentoChecklist.this, modalAlertaDeErro);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);
        title = findViewById(R.id.title);
        creation = findViewById(R.id.creation);
        createdBy = findViewById(R.id.createdBy);
        creationLayout = findViewById(R.id.creationLayout);
        createdByLayout = findViewById(R.id.createdByLayout);

        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_header, header, false);
        checklistTitle = headerXML.findViewById(R.id.item1);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            onBackPressed();
        });

        try{
            usuario = LocalStorage.getUsuario(GerenciamentoChecklist.this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();

            Intent intent = new Intent(GerenciamentoChecklist.this, SelecaoListaEtapas.class);
            selectEtapaFromList.launch(intent);
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    ActivityResultLauncher<Intent> selectEtapaFromList = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                String etapa = data.getStringExtra("result");
                modalDeCarregamento.mostrarModalDeCarregamento(3);
                getChecklistEtapa(etapa);
            } catch (LayoutException e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        }else{
            finish();
        }
    });

    private void getChecklistEtapa(String etapa){
        String titleText = "Checklist\n" + prettyEtapas.get(etapa);
        title.setText(titleText);
        checklistTitle.setText(prettyEtapas.get(etapa));
        modalDeCarregamento.avancarPasso(); // 1
        ApiChecklistCallback apiCallback = response -> {
            modalDeCarregamento.avancarPasso(); // 2
            if(this.isFinishing() || this.isDestroyed()) return;
            try{
                Checklist checklist = response.get(etapa);
                if(checklist == null) throw new FirebaseDatabaseException(EXCEPTION_NULL_UID);
                if(checklist.getCreation().equals("")) creationLayout.setVisibility(View.GONE);
                if(checklist.getCreatedBy().equals("")) createdByLayout.setVisibility(View.GONE);
                creation.setText(checklist.getCreation());
                createdBy.setText(checklist.getCreatedBy());
                for(String item: checklist.getItems().values()) {
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item, listaLayout, false);
                    TextView objItem1 = obj.findViewById(R.id.item1);
                    objItem1.setText(item);
                    ImageView item2 = obj.findViewById(R.id.item2);
                    item2.setImageResource(R.drawable.checked);
                    listaLayout.addView(obj);
                }
                modalDeCarregamento.avancarPasso(); // 3
                modalDeCarregamento.fecharModal();
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        };
//        new ApiChecklist(GerenciamentoChecklist.this, database, this.getClass().getSimpleName(), loadingDialog, errorDialog, apiCallback, null);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(duploClique.detectado()){
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
    }
}