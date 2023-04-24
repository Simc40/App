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
import com.simc.simc40.classes.Transportadora;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiTransportadoras;
import com.simc.simc40.firebaseApiGET.ApiTransportadorasCallback;
import com.simc.simc40.sharedPreferences.LocalStorage;

public class SelecaoListaTransportadoras extends AppCompatActivity implements SharedPrefsExceptionErrorList, FirebaseDatabaseExceptionErrorList {

    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    String database;
    DuploClique duploClique = new DuploClique();
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_transportadoras);

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalAlertaDeErro.getBotao().setOnClickListener(null);
        modalAlertaDeErro.getBotao().setOnClickListener(view -> finish());
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(4 + ApiTransportadoras.ticks);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        try{
            usuario = LocalStorage.getUsuario(this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        modalDeCarregamento.avancarPasso(); // 1

        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Transportadoras";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Status";
        item2.setText(Text2);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            onBackPressed();
        });

        try{
            modalDeCarregamento.avancarPasso(); // 2
            ApiTransportadorasCallback apiCallback = response -> {
                System.out.println(response);
                modalDeCarregamento.avancarPasso(); // 3
                if(this.isFinishing() || this.isDestroyed()) return;
                for(Transportadora transportadora: response.values()) {
                        String nome = transportadora.getNome();
                        String status = transportadora.getStatus();
//                        if(!status.equals("ativo")) continue;
                        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item, listaLayout, false);
                        TextView objItem1 = obj.findViewById(R.id.item1);
                        TextView objItem2 = obj.findViewById(R.id.item2);
                        ImageView objItem3 = obj.findViewById(R.id.item3);
                        objItem1.setText(nome);
                        objItem2.setText(status);
                        objItem3.setImageResource(R.drawable.forward);
                        objItem3.setTag(transportadora);
                        objItem3.setOnClickListener(view -> {
                            try {
                                Transportadora selectedTransportadora = (Transportadora) objItem3.getTag();
                                if(selectedTransportadora.getMotoristas().isEmpty() && selectedTransportadora.getVeiculos().isEmpty()) throw new FirebaseDatabaseException(EXCEPTION_TRANSPORTADORA_NULL_VEICULOS_AND_MOTORISTAS);
                                Intent intent = new Intent();
                                intent.putExtra("result",selectedTransportadora);
                                setResult (SelecaoListaTransportadoras.RESULT_OK, intent);
                                finish();
                            } catch (Exception e) {
                                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                            }
                        });
                        listaLayout.addView(obj);
                }
                modalDeCarregamento.passoFinal(); // 4
                modalDeCarregamento.fecharModal();
            };
            new ApiTransportadoras(this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiCallback, null, true);
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
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