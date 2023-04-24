package com.simc.simc40.selecaoListas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.simc.simc40.R;
import com.simc.simc40.classes.Cliente;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiClientes;
import com.simc.simc40.firebaseApiGET.ApiClientesCallback;
import com.simc.simc40.firebasePaths.FirebaseClientePaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.google.firebase.auth.FirebaseAuth;

public class SelecaoListaClientes extends AppCompatActivity implements DefaultErrorMessage, FirebaseClientePaths, FirebaseDatabaseExceptionErrorList {

    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    DuploClique duploClique = new DuploClique();
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_clientes);

        modalAlertaDeErro = new ModalAlertaDeErro(SelecaoListaClientes.this);
        modalDeCarregamento = new ModalDeCarregamento(SelecaoListaClientes.this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(5 + ApiClientes.ticks);
        modalDeCarregamento.avancarPasso(); // 1

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "UF";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Cliente";
        item2.setText(Text2);
        TextView item3 = headerXML.findViewById(R.id.item3);
        String Text3 = "Status";
        item3.setText(Text3);
        header.addView(headerXML);

        modalDeCarregamento.avancarPasso(); // 2

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            onBackPressed();
        });

        try{
            modalDeCarregamento.avancarPasso(); // 3
            ApiClientesCallback apiClientesCallback = response -> {
                modalDeCarregamento.avancarPasso(); // 4
                for(Cliente cliente: response.values()) {
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item, listaLayout, false);
                    TextView objItem1 = obj.findViewById(R.id.item1);
                    TextView objItem2 = obj.findViewById(R.id.item2);
                    TextView objItem3 = obj.findViewById(R.id.item3);
                    ImageView objItem4 = obj.findViewById(R.id.item4);
                    objItem1.setText(cliente.getUf());
                    objItem2.setText(cliente.getNome());
                    objItem3.setText(cliente.getStatus());
                    objItem4.setImageResource(R.drawable.forward);
                    objItem4.setTag(cliente);
                    objItem4.setOnClickListener(view -> {
                        if (duploClique.detectado()) return;
                        try{
                            if(cliente.getStatus().equals("inativo")) throw new FirebaseDatabaseException(EXCEPTION_CLIENT_WITH_INACTIVE_STATUS);
                            Intent intent = new Intent();
                            Cliente selectedCliente = (Cliente) objItem4.getTag();
                            intent.putExtra("result", selectedCliente);
                            setResult(SelecaoListaClientes.RESULT_OK, intent);
                            finish();
                        }catch (Exception e){
                            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                        }
                    });
                    listaLayout.addView(obj);
                }
                modalDeCarregamento.passoFinal(); // 5
                modalDeCarregamento.fecharModal();
            };
            new ApiClientes(this, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiClientesCallback);
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalAlertaDeErro);
            FirebaseAuth.getInstance().signOut();
        }
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