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
import com.android.simc40.classes.Transportadora;
import com.android.simc40.classes.User;
import com.android.simc40.classes.Veiculo;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.firebaseApiGET.ApiVeiculos;
import com.android.simc40.firebaseApiGET.ApiVeiculosCallback;
import com.android.simc40.loadingPage.LoadingPage;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;

public class SelecaoListaVeiculos extends AppCompatActivity implements SharedPrefsExceptionErrorList {

    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    String database;
    DoubleClick doubleClick = new DoubleClick();
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    String contextException = "SelecaoListaVeiculos";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_veiculos);

        errorDialog = new ErrorDialog(SelecaoListaVeiculos.this);
        errorDialog.getButton().setOnClickListener(null);
        errorDialog.getButton().setOnClickListener(view -> finish());
        loadingPage = new LoadingPage(SelecaoListaVeiculos.this, errorDialog);
        loadingPage.showLoadingPage();

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        try{
            user = sharedPrefsDatabase.getUser(SelecaoListaVeiculos.this, MODE_PRIVATE, loadingPage, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = user.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }

        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_5_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Transportadoras";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Modelo";
        item2.setText(Text2);
        TextView item3 = headerXML.findViewById(R.id.item3);
        String Text3 = "Placa";
        item3.setText(Text3);
        TextView item4 = headerXML.findViewById(R.id.item4);
        String Text4 = "Marca";
        item4.setText(Text4);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            onBackPressed();
        });

        try{
            ApiVeiculosCallback apiCallback = response -> {
                if(this.isFinishing() || this.isDestroyed()) return;
                for(Veiculo veiculo: response.values()) {

                        Transportadora transportadora = veiculo.getTransportadora();
                        String nomeTransportadora = transportadora.getNome();
                        String statusTransportadora = transportadora.getStatus();
                        String statusVeiculo = veiculo.getStatus();
                        if(!statusTransportadora.equals("ativo")) continue;
                        if(!statusVeiculo.equals("ativo")) continue;
                        String placa = veiculo.getPlaca();
                        String marca = veiculo.getMarca();
                        String modelo = veiculo.getModelo();
                        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_5_list_item, listaLayout, false);
                        TextView objItem1 = obj.findViewById(R.id.item1);
                        TextView objItem2 = obj.findViewById(R.id.item2);
                        TextView objItem3 = obj.findViewById(R.id.item3);
                        TextView objItem4 = obj.findViewById(R.id.item4);
                        ImageView objItem5 = obj.findViewById(R.id.item5);
                        objItem1.setText(nomeTransportadora);
                        objItem2.setText(modelo);
                        objItem3.setText(placa);
                        objItem4.setText(marca);
                        objItem5.setImageResource(R.drawable.forward);
                        objItem5.setTag(veiculo);
                        objItem5.setOnClickListener(view -> {
                            Intent intent = new Intent();
                            Veiculo selectedVeiculo = (Veiculo) objItem5.getTag();
                            intent.putExtra("result",selectedVeiculo);
                            setResult (SelecaoListaVeiculos.RESULT_OK, intent);
                            finish();
                        });
                        listaLayout.addView(obj);

                }
                loadingPage.endLoadingPage();
            };
            new ApiVeiculos(SelecaoListaVeiculos.this, database, contextException, loadingPage, errorDialog, apiCallback, null);
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