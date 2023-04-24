package com.simc.simc40.selecaoListas;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.classes.Obra;
import com.simc.simc40.classes.Peca;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.QualidadeException;
import com.simc.simc40.errorHandling.QualidadeExceptionErrorList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiElementos;
import com.simc.simc40.firebaseApiGET.ApiPecas;
import com.simc.simc40.firebaseApiGET.ApiPecasCallback;
import com.simc.simc40.sharedPreferences.LocalStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SelecaoListaPecasCadastro extends AppCompatActivity implements SharedPrefsExceptionErrorList, QualidadeExceptionErrorList {

    Intent intent;
    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    DuploClique duploClique = new DuploClique();
    String database, obraUid;
    Usuario usuario;
    ModalAlertaDeErro modalAlertaDeErro;
    ModalDeCarregamento modalDeCarregamento;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_pecas_cadastro);
        intent = getIntent();
        obraUid = intent.getStringExtra("dependency");
        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        modalAlertaDeErro = new ModalAlertaDeErro(SelecaoListaPecasCadastro.this);
        modalDeCarregamento = new ModalDeCarregamento(SelecaoListaPecasCadastro.this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(3 + ApiElementos.ticks);

        try {
            usuario = LocalStorage.getUsuario(SelecaoListaPecasCadastro.this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        inflateHeader();

        goBack.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            onBackPressed();
        });

        modalDeCarregamento.avancarPasso(); // 1

        try {
            ApiPecasCallback apiPecasCallback = response -> {
                modalDeCarregamento.avancarPasso(); // 2
                List<Peca> sortedMap = response.values().stream().sorted(Comparator.comparing(Peca::getNome_peca)).collect(Collectors.toList());
                for (Peca peca : sortedMap) {
                    String nome = peca.getNome_peca();
                    String tipo = peca.getElementoObject().getTipoDePeca().getNome();
                    String qrCode = peca.getQrCode();
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item, listaLayout, false);
                    TextView item1 = obj.findViewById(R.id.item1);
                    TextView item2 = obj.findViewById(R.id.item2);
                    TextView item3 = obj.findViewById(R.id.item3);
                    ImageView item4 = obj.findViewById(R.id.item4);
                    item1.setText(nome);
                    item2.setText(qrCode);
                    item3.setText(tipo);
                    item4.setImageResource(R.drawable.forward);
                    item4.setTag(peca);
                    item4.setOnClickListener(view -> {
                        if (duploClique.detectado()) return;
                        try {
                            Intent intent = new Intent();
                            Peca selectedPeca = (Peca) item4.getTag();
                            intent.putExtra("result", selectedPeca);
                            setResult(SelecaoListaPecasCadastro.RESULT_OK, intent);
                            finish();
                        } catch (Exception e) {
                            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                        }
                    });
                    listaLayout.addView(obj);
                }
                modalDeCarregamento.passoFinal(); // 3
                modalDeCarregamento.fecharModal();
                if (obraUid != null && listaLayout.getChildCount() == 0) {
                    modalAlertaDeErro.getBotao().setOnClickListener(view -> {
                        modalAlertaDeErro.fecharModal();
                        finish();
                    });
                    throw new QualidadeException(EXCEPTION_NO_REGISTERED_PECAS);
                }
            };

            Obra obra = new Obra();
            obra.setUid(obraUid);
            new ApiPecas(SelecaoListaPecasCadastro.this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiPecasCallback, obra);
        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    private void inflateHeader() {
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_6_list_item_header, header, false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Nome";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "QrCode";
        item2.setText(Text2);
        TextView item3 = headerXML.findViewById(R.id.item3);
        String Text3 = "Tipo";
        item3.setText(Text3);
        header.addView(headerXML);
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