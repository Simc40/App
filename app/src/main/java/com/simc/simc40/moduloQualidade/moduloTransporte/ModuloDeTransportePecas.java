package com.simc.simc40.moduloQualidade.moduloTransporte;

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
import com.simc.simc40.checklist.Etapas;
import com.simc.simc40.classes.Peca;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiRomaneios;
import com.simc.simc40.firebaseApiGET.ApiRomaneiosCallback;
import com.simc.simc40.sharedPreferences.LocalStorage;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ModuloDeTransportePecas extends AppCompatActivity implements Etapas, SharedPrefsExceptionErrorList {

    Usuario usuario;
    String database;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    TextView goBack;
    LinearLayout header, listaLayout;
    DuploClique duploClique = new DuploClique();
    Intent intent;
    TreeMap<String, Peca> pecasMap;
    ApiRomaneios apiRomaneios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mtransporte_pecas);

        intent = getIntent();

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalAlertaDeErro.getBotao().setOnClickListener(null);
        modalAlertaDeErro.getBotao().setOnClickListener(view -> finish());
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(3 + ApiRomaneios.ticks);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        try {
            usuario = LocalStorage.getUsuario(this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item_header, header, false);
        TextView headerItem1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Destino";
        headerItem1.setText(Text1);
        TextView headerItem2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Etapa";
        headerItem2.setText(Text2);
        TextView headerItem3 = headerXML.findViewById(R.id.item3);
        String Text3 = "Nome";
        headerItem3.setText(Text3);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            onBackPressed();
        });

        modalDeCarregamento.avancarPasso(); // 1

        try {
            @SuppressLint("SetTextI18n") ApiRomaneiosCallback apiCallback = response -> {
                modalDeCarregamento.avancarPasso(); // 2
                if (this.isFinishing() || this.isDestroyed()) return;
                pecasMap = apiRomaneios.getPecas();
                List<Peca> pecasRomaneio = response.values().stream().flatMap(Romaneio -> Romaneio.getPecas().stream()).collect(Collectors.toList());
                pecasMap.values().removeAll(pecasRomaneio);
                TreeMap<String, Peca> newMap = new TreeMap<>();
                for (Map.Entry<String, Peca> entry : pecasMap.entrySet()) newMap.put(entry.getValue().getObraObject() + String.valueOf(etapasDePeca.indexOf(entry.getValue().getEtapa_atual())) + entry.getValue().getElementoObject().getNome(), entry.getValue());
                for (Peca peca : newMap.values()) {
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item, listaLayout ,false);
                    TextView item1 = obj.findViewById(R.id.item1);
                    TextView item2 = obj.findViewById(R.id.item2);
                    TextView item3 = obj.findViewById(R.id.item3);
                    ImageView item4 = obj.findViewById(R.id.item4);
                    item1.setText(peca.getObraObject().getNome_obra());
                    item2.setText(prettyShortEtapas.get(peca.getEtapa_atual()));
                    item3.setText(peca.getNome_peca());
                    item4.setVisibility(View.GONE);
                    item1.setMaxLines(1);
                    item2.setMaxLines(1);
                    item3.setMaxLines(1);
                    listaLayout.addView(obj);
                }
                modalDeCarregamento.passoFinal(); // 3
                modalDeCarregamento.fecharModal();
            };
            apiRomaneios = new ApiRomaneios(this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiCallback);
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