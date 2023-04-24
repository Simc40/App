package com.simc.simc40.selecaoListas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.simc.simc40.R;
import com.simc.simc40.classes.Romaneio;
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

public class SelecaoListaRomaneio extends AppCompatActivity implements SharedPrefsExceptionErrorList {


    Usuario usuario;
    String database;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    TextView goBack;
    LinearLayout header, listaLayout;
    DuploClique duploClique = new DuploClique();
    Intent intent;
    Boolean filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_romaneio);

        intent = getIntent();
        filter = intent.getBooleanExtra("dependency", false);

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalAlertaDeErro.getBotao().setOnClickListener(null);
        modalAlertaDeErro.getBotao().setOnClickListener(view -> finish());
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(3 + ApiRomaneios.ticks);

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

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Romaneios";
        item1.setText(Text1);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            onBackPressed();
        });

        modalDeCarregamento.avancarPasso(); // 1

        try{
            @SuppressLint("SetTextI18n") ApiRomaneiosCallback apiCallback = response -> {
                modalDeCarregamento.avancarPasso(); // 2
                if(this.isFinishing() || this.isDestroyed()) return;
                for(Romaneio romaneio: response.values()) {
                    if(filter && romaneio.getStatus().equals(Romaneio.statusFinalizado)) continue;
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_romaneio_item, listaLayout ,false);
                    CardView statusCard = obj.findViewById(R.id.statusCard);
                    TextView status = obj.findViewById(R.id.status);
                    CardView status1 = obj.findViewById(R.id.status1);
                    CardView status2 = obj.findViewById(R.id.status2);
                    CardView status3 = obj.findViewById(R.id.status3);
                    TextView numCarga = obj.findViewById(R.id.numCarga);
                    TextView data = obj.findViewById(R.id.data);
                    TextView obra = obj.findViewById(R.id.obra);
                    TextView transportadora = obj.findViewById(R.id.transportadora);
                    TextView motorista = obj.findViewById(R.id.motorista);
                    TextView veiculo = obj.findViewById(R.id.veiculo);

                    status.setText(romaneio.getStatus());
                    switch (romaneio.getStatus()) {
                        case Romaneio.statusCarga:
                            status1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                            statusCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
                            break;
                        case Romaneio.statusTransporte:
                            status1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                            status2.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                            statusCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.soft_orange));
                            break;
                        case Romaneio.statusFinalizado:
                            status1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                            status2.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                            status3.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                            statusCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.soft_green));
                            break;
                    }
                    numCarga.setText("Carga " + romaneio.getNumCarga());
                    data.setText(romaneio.getDataPrevisao());
                    obra.setText((romaneio.getObra() != null) ? romaneio.getObra().getNome_obra() : "");
                    transportadora.setText((romaneio.getTransportadora() != null) ? romaneio.getTransportadora().getNome() : "");
                    motorista.setText((romaneio.getMotorista() != null) ? romaneio.getMotorista().getNome() : "");
                    veiculo.setText((romaneio.getVeiculo() != null) ? romaneio.getVeiculo().getModelo() + " - " + romaneio.getVeiculo().getPlaca() : "");
                    obj.setTag(romaneio);
                    obj.setOnClickListener(view -> {
                        if(duploClique.detectado()) return;
                        Intent intent = new Intent();
                        Romaneio selected = (Romaneio) obj.getTag();
                        intent.putExtra("result", selected);
                        setResult (SelecaoListaRomaneio.RESULT_OK, intent);
                        finish();
                    });
                    listaLayout.addView(obj);
                }
                modalDeCarregamento.passoFinal(); // 3
                modalDeCarregamento.fecharModal();
            };
            new ApiRomaneios(this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiCallback);
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