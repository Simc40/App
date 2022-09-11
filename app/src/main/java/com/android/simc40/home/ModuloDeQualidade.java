package com.android.simc40.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.simc40.R;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.moduloQualidade.ModuloDeMontagem;
import com.android.simc40.moduloQualidade.moduloProducao.ModuloDeProducao;
import com.android.simc40.moduloQualidade.modulo_transporte.ModuloDeTransporte;
import com.android.simc40.moduloQualidade.relatorio_erros.RelatorioDeErros;

public class ModuloDeQualidade extends AppCompatActivity {

    DoubleClick doubleClick = new DoubleClick();
    CardView card1, card2, card3, card4;
    TextView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_modulo_de_qualidade);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        goBack = findViewById(R.id.goBack);

        card1.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeQualidade.this, ModuloDeProducao.class));
        });
        card2.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeQualidade.this, ModuloDeTransporte.class));
        });
        card3.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeQualidade.this, ModuloDeMontagem.class));
        });
        card4.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeQualidade.this, RelatorioDeErros.class));
        });

        goBack.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            onBackPressed();
        });


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(doubleClick.detected()){
            return;
        }
        finish();
    }
}