package com.simc.simc40.selecaoListas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.ReaderException;
import com.simc.simc40.errorHandling.ReaderExceptionErrorList;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.configuracaoLeitor.ListaLeitores;

public class SelecaoListaLeitores extends AppCompatActivity implements ListaLeitores, ReaderExceptionErrorList {

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
        setContentView(R.layout.selecao_lista_leitores);

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_image_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Nome do Leitor";
        item1.setText(Text1);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            onBackPressed();
        });

        for(String leitor: listaLeitores.keySet()) {
            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_image_item, listaLayout ,false);
            TextView objItem1 = obj.findViewById(R.id.item1);
            ImageView objItem2 = obj.findViewById(R.id.item2);
            objItem1.setText(leitor);
            objItem2.setTag(leitor);
            objItem2.setImageResource(listaLeitores.get(leitor));
            objItem2.setOnClickListener(view -> {
                if(duploClique.detectado()) return;
                try{
                    Intent intent = new Intent();
                    String selectedReader = (String) objItem2.getTag();
                    if(selectedReader == null || (!selectedReader.equals(UHF_RFID_Reader_1128) && !selectedReader.equals(QR_CODE))) throw new ReaderException(EXCEPTION_READER_NOT_CONFIGURED);
                    if(selectedReader.equals(UHF_RFID_Reader_1128) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) throw new ReaderException(EXCEPTION_API_VERSION_NOT_CONFIGURED);
                    intent.putExtra("result", selectedReader);
                    setResult (SelecaoListaLeitores.RESULT_OK, intent);
                    finish();
                }catch (Exception e){
                    ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                }
            });
            listaLayout.addView(obj);
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