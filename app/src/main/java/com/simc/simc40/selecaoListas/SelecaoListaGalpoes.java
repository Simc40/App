package com.simc.simc40.selecaoListas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.classes.Galpao;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiGalpoes;
import com.simc.simc40.firebaseApiGET.ApiGalpoesCallback;
import com.simc.simc40.firebasePaths.FirebaseObraPaths;
import com.simc.simc40.firebasePaths.FirebaseUserPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.sharedPreferences.LocalStorage;

public class SelecaoListaGalpoes extends AppCompatActivity implements DefaultErrorMessage, SharedPrefsExceptionErrorList, FirebaseObraPaths, FirebaseUserPaths {

    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    String database;
    DuploClique duploClique = new DuploClique();
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    Usuario usuario;
    Intent intent;
    Boolean control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_galpoes);

        intent = getIntent();
        control = intent.getBooleanExtra("control", false);

        modalAlertaDeErro = new ModalAlertaDeErro(SelecaoListaGalpoes.this);
        modalAlertaDeErro.getBotao().setOnClickListener(null);
        modalAlertaDeErro.getBotao().setOnClickListener(view -> finish());
        modalDeCarregamento = new ModalDeCarregamento(SelecaoListaGalpoes.this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(3 + ApiGalpoes.ticks);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        try{
            usuario = LocalStorage.getUsuario(SelecaoListaGalpoes.this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
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
            if(duploClique.detectado()) return;
            onBackPressed();
        });

        modalDeCarregamento.avancarPasso(); // 1

        try{
            ApiGalpoesCallback apiCallback = response -> {
                modalDeCarregamento.avancarPasso(); // 2
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
                    if(galpao.getPrettyStatus().equals(Galpao.disponivel)){
                        objItem3.setImageResource(R.drawable.checked);
                    }else{
                        objItem3.setImageResource(R.drawable.uncheck);
                    }
                    objItem3.setTag(galpao);
                    objItem3.setOnClickListener(view -> {
                        if(duploClique.detectado()) return;
                        Intent intent = new Intent();
                        Galpao selectedGalpao = (Galpao) objItem3.getTag();
                        intent.putExtra("result", selectedGalpao);
                        setResult (SelecaoListaGalpoes.RESULT_OK, intent);
                        finish();
                    });
                    listaLayout.addView(obj);
                }
                modalDeCarregamento.passoFinal(); // 3
                modalDeCarregamento.fecharModal();
            };
            new ApiGalpoes(SelecaoListaGalpoes.this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiCallback, null, control);
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