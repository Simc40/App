package com.simc.simc40.moduloQualidade;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.configuracaoLeitor.readerConnectorUHF1128;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.selecaoListas.SelecaoListaObras;

public class ModuloDeMontagem extends AppCompatActivity {

    TextView textViewObra, textViewModelo;
    String obraUid, obra;
    Button read, clear;
    LinearLayout tagHeader, tagItem;
    com.simc.simc40.configuracaoLeitor.readerConnectorUHF1128 readerConnectorUHF1128;
    DuploClique duploClique = new DuploClique();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqualidade_modulo_de_montagem);

        textViewObra = findViewById(R.id.obra);
        textViewModelo = findViewById(R.id.modelo);
        //ReaderConnectorItems
        read = findViewById(R.id.read);
        clear = findViewById(R.id.clear);
        tagItem = findViewById(R.id.tagItem);
        tagHeader = findViewById(R.id.tagItemHeader);

        Intent intent = new Intent(ModuloDeMontagem.this, SelecaoListaObras.class);
        selectObraFromList.launch(intent);

        readerConnectorUHF1128 = new readerConnectorUHF1128(ModuloDeMontagem.this, tagItem, read, clear);
        View headerXML = LayoutInflater.from(ModuloDeMontagem.this).inflate(R.layout.custom_tag_layout_header, tagHeader ,false);
        tagHeader.addView(headerXML);

    }

    ActivityResultLauncher<Intent> selectObraFromList = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    obraUid = data.getStringExtra("result1");
                    System.out.println(obraUid);
                    textViewObra.setText(data.getStringExtra("result2"));
                }else{
                    finish();
                }
            });

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(duploClique.detectado()){
            return;
        }
        finish();
    }
}