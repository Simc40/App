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
import com.simc.simc40.classes.Obra;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiObras;
import com.simc.simc40.firebaseApiGET.ApiObrasCallback;
import com.simc.simc40.firebasePaths.FirebaseObraPaths;
import com.simc.simc40.firebasePaths.FirebaseUserPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.sharedPreferences.LocalStorage;

import java.util.TreeMap;

public class SelecaoListaObras extends AppCompatActivity implements DefaultErrorMessage, SharedPrefsExceptionErrorList, FirebaseObraPaths, FirebaseUserPaths {

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
        setContentView(R.layout.selecao_lista_obras);

        modalAlertaDeErro = new ModalAlertaDeErro(SelecaoListaObras.this);
        modalAlertaDeErro.getBotao().setOnClickListener(null);
        modalAlertaDeErro.getBotao().setOnClickListener(view -> finish());
        modalDeCarregamento = new ModalDeCarregamento(SelecaoListaObras.this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(3);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        try{
            usuario = LocalStorage.getUsuario(SelecaoListaObras.this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Obra";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Responsável";
        item2.setText(Text2);
        TextView item3 = headerXML.findViewById(R.id.item3);
        String Text3 = "Tipo";
        item3.setText(Text3);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            onBackPressed();
        });

        modalDeCarregamento.avancarPasso(); // 1

        try{
            ApiObrasCallback apiCallback = response -> {
                modalDeCarregamento.avancarPasso(); // 2;
                if(this.isFinishing() || this.isDestroyed()) return;
                TreeMap <String, Obra> sorted = new TreeMap<String, Obra>(){{
                    for(Obra obra: response.values()) put(toFirstLetterUpperCase(obra.getNome_obra()) + obra.getUid(), obra);
                }};
                for(Obra obra: sorted.values()) {
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item, listaLayout ,false);
                    TextView objItem1 = obj.findViewById(R.id.item1);
                    TextView objItem2 = obj.findViewById(R.id.item2);
                    TextView objItem3 = obj.findViewById(R.id.item3);
                    ImageView objItem4 = obj.findViewById(R.id.item4);
                    objItem1.setText(toFirstLetterUpperCase(obra.getNome_obra()));
                    objItem2.setText(obra.getResponsavel());
                    objItem3.setText(obra.getTipo_construcao());
                    objItem4.setImageResource(R.drawable.forward);
                    objItem4.setTag(obra);
                    objItem4.setOnClickListener(view -> {
                        if(duploClique.detectado()) return;
                        Intent intent = new Intent();
                        Obra selectedObra = (Obra) objItem4.getTag();
                        intent.putExtra("result", selectedObra);
                        setResult (SelecaoListaObras.RESULT_OK, intent);
                        finish();
                    });
                    listaLayout.addView(obj);
                }
                modalDeCarregamento.passoFinal(); // 3
                modalDeCarregamento.fecharModal();
            };
            new ApiObras(SelecaoListaObras.this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiCallback);
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

    }

    private String toFirstLetterUpperCase(String string){
        return (string == null || string.length() < 1) ? string : string.substring(0,1).toUpperCase() + string.substring(1);
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