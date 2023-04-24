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
import com.simc.simc40.classes.Elemento;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.QualidadeException;
import com.simc.simc40.errorHandling.QualidadeExceptionErrorList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiElementos;
import com.simc.simc40.firebaseApiGET.ApiElementosCallback;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.sharedPreferences.LocalStorage;

public class SelecaoListaElementos extends AppCompatActivity implements SharedPrefsExceptionErrorList, QualidadeExceptionErrorList {

    Intent intent;
    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    DuploClique duploClique = new DuploClique();
    String database, obraUid;
    Usuario usuario;
    ModalAlertaDeErro modalAlertaDeErro;
    ModalDeCarregamento modalDeCarregamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_elementos);

        intent = getIntent();
        obraUid = intent.getStringExtra("dependency");
        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        modalAlertaDeErro = new ModalAlertaDeErro(SelecaoListaElementos.this);
        modalDeCarregamento = new ModalDeCarregamento(SelecaoListaElementos.this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(3 + ApiElementos.ticks);

        try{
            usuario = LocalStorage.getUsuario(SelecaoListaElementos.this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        inflateHeader();

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            onBackPressed();
        });

        modalDeCarregamento.avancarPasso(); // 1

        try{
            ApiElementosCallback apiElementosCallback = response -> {
                modalDeCarregamento.avancarPasso(); // 2
                for(Elemento elemento : response.values()){
                    String obra = elemento.getObra().getNome_obra();
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
                        if(duploClique.detectado()) return;
                        try {
                            Intent intent = new Intent();
                            Elemento selectedElemento = (Elemento) item6.getTag();
                            intent.putExtra("result", selectedElemento);
                            setResult (SelecaoListaElementos.RESULT_OK, intent);
                            finish();
                        }catch (Exception e){
                            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                        }
                    });
                    listaLayout.addView(obj);
                }
                modalDeCarregamento.passoFinal(); // 3
                modalDeCarregamento.fecharModal();
                if(obraUid != null && listaLayout.getChildCount() == 0) {
                    modalAlertaDeErro.getBotao().setOnClickListener(view -> {
                        modalAlertaDeErro.fecharModal();
                        finish();
                    });
                    throw new QualidadeException(EXCEPTION_NO_REGISTERED_ELEMENTOS);
                }
            };
            new ApiElementos(SelecaoListaElementos.this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiElementosCallback, true);
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    private void inflateHeader() {
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Obra";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Nome";
        item2.setText(Text2);
        TextView item3 = headerXML.findViewById(R.id.item3);
        String Text3 = "Tipo";
        item3.setText(Text3);
        header.addView(headerXML);
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