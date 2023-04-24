package com.simc.simc40.moduloGerenciamento;

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
import com.simc.simc40.classes.Obra;
import com.simc.simc40.classes.PecaGalpao;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiObras;
import com.simc.simc40.firebaseApiGET.ApiObrasCallback;
import com.simc.simc40.sharedPreferences.LocalStorage;

import java.util.TreeMap;

public class GerenciamentoGalpoesControle extends AppCompatActivity implements SharedPrefsExceptionErrorList, FirebaseDatabaseExceptionErrorList {

    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack, nomeGalpao;
    String database;
    DuploClique duploClique = new DuploClique();
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    Galpao galpao;
    Usuario usuario;
    Intent intent;
    TreeMap<String, Obra> obrasMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_galpoes_controle);


        intent = getIntent();
        galpao = (Galpao) intent.getSerializableExtra("dependency");

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalAlertaDeErro.getBotao().setOnClickListener(null);
        modalAlertaDeErro.getBotao().setOnClickListener(view -> finish());
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(3 + ApiObras.ticks);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        nomeGalpao = findViewById(R.id.nomeGalpao);
        nomeGalpao.setText(galpao.getNome());

        try{
            usuario = LocalStorage.getUsuario(this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item_header, header, false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Entrada";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Destino";
        item2.setText(Text2);
        TextView item3 = headerXML.findViewById(R.id.item3);
        String Text3 = "Saída";
        item3.setText(Text3);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            onBackPressed();
        });

        modalDeCarregamento.avancarPasso(); // 1

        ApiObrasCallback apiObrasCallback = response -> {
            obrasMap = response;
            generateViews();
        };

        new ApiObras(this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiObrasCallback);
    }

    private void generateViews(){
        try {
            modalDeCarregamento.avancarPasso(); // 2
            for(PecaGalpao peca : galpao.getControle().values()) {
                View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item, listaLayout, false);
                TextView objItem1 = obj.findViewById(R.id.item1);
                TextView objItem2 = obj.findViewById(R.id.item2);
                TextView objItem3 = obj.findViewById(R.id.item3);
                ImageView objItem4 = obj.findViewById(R.id.item4);
                objItem1.setText(peca.getShortEntrada());
                objItem2.setText((obrasMap != null && obrasMap.get(peca.getUidObra()) != null) ? obrasMap.get(peca.getUidObra()).getNome_obra() : "");
                objItem3.setText(peca.getShortSaida());
                objItem4.setVisibility(View.INVISIBLE);
                listaLayout.addView(obj);
            }
            modalDeCarregamento.passoFinal(); // 3
            modalDeCarregamento.fecharModal();
        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (duploClique.detectado()) {
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