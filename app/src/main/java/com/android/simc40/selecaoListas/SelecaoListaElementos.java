package com.android.simc40.selecaoListas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.simc40.R;
import com.android.simc40.classes.Elemento;
import com.android.simc40.classes.User;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.QualidadeException;
import com.android.simc40.errorHandling.QualidadeExceptionErrorList;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.firebaseApiGET.ApiElementos;
import com.android.simc40.firebaseApiGET.ApiElementosCallback;
import com.android.simc40.loadingPage.LoadingPage;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;

public class SelecaoListaElementos extends AppCompatActivity implements SharedPrefsExceptionErrorList, QualidadeExceptionErrorList {

    Intent intent;
    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    DoubleClick doubleClick = new DoubleClick();
    String database, obraUid;
    User user;
    ErrorDialog errorDialog;
    LoadingPage loadingPage;
    String contextException = "SelecaoListaElementos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_elementos);

        intent = getIntent();
        obraUid = intent.getStringExtra("dependency");
        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        errorDialog = new ErrorDialog(SelecaoListaElementos.this);
        loadingPage = new LoadingPage(SelecaoListaElementos.this, errorDialog);
        loadingPage.showLoadingPage();

        try{
            user = sharedPrefsDatabase.getUser(SelecaoListaElementos.this, MODE_PRIVATE, loadingPage, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = user.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }

        inflateHeader();

        goBack.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            onBackPressed();
        });

        try{
            ApiElementosCallback apiElementosCallback = response -> {
                    for(Elemento elemento : response.values()){
                        String obra = elemento.getObra().getNomeObra();
                        String status = elemento.getStatus();
                        if(obraUid != null){
                            if(status.equals("inativo")) continue;
                            else if(!elemento.getObra().getUid().equals(obraUid)) continue;
                        }
                        String nome = elemento.getNome();
                        String tipo = elemento.getTipoDePeca().getNome();
                        String numMax = elemento.getPecasPlanejadas();
                        String numPecas = elemento.getPecasCadastradas();
                        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_6_list_item, listaLayout ,false);
                        TextView item1 = obj.findViewById(R.id.item1);
                        TextView item2 = obj.findViewById(R.id.item2);
                        TextView item3 = obj.findViewById(R.id.item3);
                        TextView item4 = obj.findViewById(R.id.item4);
                        TextView item5 = obj.findViewById(R.id.item5);
                        ImageView item6 = obj.findViewById(R.id.item6);
                        item1.setText(obra);
                        item2.setText(nome);
                        item3.setText(tipo);
                        item4.setText(numMax);
                        item5.setText(numPecas);
                        item6.setImageResource(R.drawable.forward);
                        item6.setTag(elemento);
                        item6.setOnClickListener(view -> {
                            if(doubleClick.detected()) return;
                            try {
                                Intent intent = new Intent();
                                Elemento selectedElemento = (Elemento) item6.getTag();
                                intent.putExtra("result", selectedElemento);
                                setResult (SelecaoListaElementos.RESULT_OK, intent);
                                finish();
                            }catch (Exception e){
                                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
                            }
                        });
                        listaLayout.addView(obj);
                    }
                loadingPage.endLoadingPage();
                if(obraUid != null && listaLayout.getChildCount() == 0) {
                    errorDialog.getButton().setOnClickListener(view -> {
                        errorDialog.endErrorDialog();
                        finish();
                    });
                    throw new QualidadeException(EXCEPTION_NO_REGISTERED_ELEMENTOS);
                }
            };
            new ApiElementos(SelecaoListaElementos.this, database, contextException, loadingPage, errorDialog, apiElementosCallback, true);
        }catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }
    }

    private void inflateHeader() {
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_6_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Obra";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Nome";
        item2.setText(Text2);
        TextView item3 = headerXML.findViewById(R.id.item3);
        String Text3 = "Tipo";
        item3.setText(Text3);
        TextView item4 = headerXML.findViewById(R.id.item4);
        String Text4 = "Peças\nPlan.";
        item4.setText(Text4);
        TextView item5 = headerXML.findViewById(R.id.item5);
        String Text5 = "Peças\nCad.";
        item5.setText(Text5);
        header.addView(headerXML);
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