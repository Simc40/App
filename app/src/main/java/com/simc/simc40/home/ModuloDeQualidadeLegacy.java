package com.simc.simc40.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simc.simc40.R;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.moduloQualidade.ModuloDeMontagem;
import com.simc.simc40.moduloQualidade.ModuloDeProducaoLegacy;
import com.simc.simc40.moduloQualidade.ModuloDeTransporte;
import com.simc.simc40.moduloQualidade.relatorioErros.RelatorioDeErros;

public class ModuloDeQualidadeLegacy extends AppCompatActivity {

    DuploClique duploClique = new DuploClique();
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
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeQualidadeLegacy.this, ModuloDeProducaoLegacy.class));
        });
        card2.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeQualidadeLegacy.this, ModuloDeTransporte.class));
        });
        card3.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeQualidadeLegacy.this, ModuloDeMontagem.class));
        });
        card4.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeQualidadeLegacy.this, RelatorioDeErros.class));
        });

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            onBackPressed();
        });


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(duploClique.detectado()){
            return;
        }
        finish();
    }
}