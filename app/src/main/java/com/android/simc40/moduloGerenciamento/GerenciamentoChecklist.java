package com.android.simc40.moduloGerenciamento;

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

import com.android.simc40.R;
import com.android.simc40.checklist.Etapas;
import com.android.simc40.classes.Checklist;
import com.android.simc40.classes.User;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.firebaseApiGET.ApiChecklist;
import com.android.simc40.firebaseApiGET.ApiChecklistCallback;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.selecaoListas.SelecaoListaEtapas;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;

public class GerenciamentoChecklist extends AppCompatActivity implements LayoutExceptionErrorList, SharedPrefsExceptionErrorList, Etapas {

    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    String database;
    DoubleClick doubleClick = new DoubleClick();
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    User user;
    TextView title, checklistTitle, creation, createdBy;
    ConstraintLayout creationLayout, createdByLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_checklist);

        errorDialog = new ErrorDialog(GerenciamentoChecklist.this);
        errorDialog.getButton().setOnClickListener(null);
        errorDialog.getButton().setOnClickListener(view -> finish());
        loadingDialog = new LoadingDialog(GerenciamentoChecklist.this, errorDialog);

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
            if(doubleClick.detected()) return;
            onBackPressed();
        });

        try{
            user = sharedPrefsDatabase.getUser(GerenciamentoChecklist.this, MODE_PRIVATE, loadingDialog, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = user.getCliente().getDatabase();

            Intent intent = new Intent(GerenciamentoChecklist.this, SelecaoListaEtapas.class);
            selectEtapaFromList.launch(intent);
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
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
                loadingDialog.showLoadingDialog(3);
                getChecklistEtapa(etapa);
            } catch (LayoutException e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        }else{
            finish();
        }
    });

    private void getChecklistEtapa(String etapa){
        String titleText = "Checklist\n" + prettyEtapas.get(etapa);
        title.setText(titleText);
        checklistTitle.setText(prettyEtapas.get(etapa));
        loadingDialog.tick(); // 1
        ApiChecklistCallback apiCallback = response -> {
            loadingDialog.tick(); // 2
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
                loadingDialog.tick(); // 3
                loadingDialog.endLoadingDialog();
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        };
        new ApiChecklist(GerenciamentoChecklist.this, database, this.getClass().getSimpleName(), loadingDialog, errorDialog, apiCallback, null);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(doubleClick.detected()){
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
    }
}