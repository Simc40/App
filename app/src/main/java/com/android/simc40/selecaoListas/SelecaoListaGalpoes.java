package com.android.simc40.selecaoListas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.simc40.R;
import com.android.simc40.classes.Galpao;
import com.android.simc40.classes.User;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.firebaseApiGET.ApiGalpoes;
import com.android.simc40.firebaseApiGET.ApiGalpoesCallback;
import com.android.simc40.firebasePaths.FirebaseObraPaths;
import com.android.simc40.firebasePaths.FirebaseUserPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;

public class SelecaoListaGalpoes extends AppCompatActivity implements DefaultErrorMessage, SharedPrefsExceptionErrorList, FirebaseObraPaths, FirebaseUserPaths {

    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    String database;
    DoubleClick doubleClick = new DoubleClick();
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    String contextException = "SelecaoListaObras";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_galpoes);

        errorDialog = new ErrorDialog(SelecaoListaGalpoes.this);
        errorDialog.getButton().setOnClickListener(null);
        errorDialog.getButton().setOnClickListener(view -> finish());
        loadingPage = new LoadingPage(SelecaoListaGalpoes.this, errorDialog);
        loadingPage.showLoadingPage();

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        try{
            user = sharedPrefsDatabase.getUser(SelecaoListaGalpoes.this, MODE_PRIVATE, loadingPage, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = user.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Galpão";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Disponibilidade";
        item2.setText(Text2);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            onBackPressed();
        });


        try{
            ApiGalpoesCallback apiCallback = response -> {
                if(this.isFinishing() || this.isDestroyed()) return;
                for(Galpao galpao: response.values()) {
                    String nome_galpao = galpao.getNome();
                    String disponivel = galpao.getStatus();
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item, listaLayout ,false);
                    TextView objItem1 = obj.findViewById(R.id.item1);
                    TextView objItem2 = obj.findViewById(R.id.item2);
                    ImageView objItem3 = obj.findViewById(R.id.item3);
                    objItem1.setText(nome_galpao);
                    objItem2.setText(disponivel);
                    if(disponivel.equals("ativo")) {
                        disponivel = "Disponível";
                        objItem3.setImageResource(R.drawable.checked);
                    }
                    else if(disponivel.equals("inativo")) {
                        disponivel = "Indisponível";
                        objItem3.setImageResource(R.drawable.uncheck);
                    }
                    else{
                        continue;
                    }
                    objItem3.setTag(galpao);
                    objItem3.setOnClickListener(view -> {
                        if(doubleClick.detected()) return;
                        Intent intent = new Intent();
                        Galpao selectedGalpao = (Galpao) objItem3.getTag();
                        intent.putExtra("result", selectedGalpao);
                        setResult (SelecaoListaGalpoes.RESULT_OK, intent);
                        finish();
                    });
                    listaLayout.addView(obj);
                }
                loadingPage.endLoadingPage();
            };
            new ApiGalpoes(SelecaoListaGalpoes.this, database, contextException, loadingPage, errorDialog, apiCallback, null);
        }catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }
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
        loadingPage.endLoadingPage();
        errorDialog.endErrorDialog();
    }
}