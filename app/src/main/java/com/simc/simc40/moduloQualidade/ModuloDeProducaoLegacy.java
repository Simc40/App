package com.simc.simc40.moduloQualidade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.moduloQualidade.moduloProducao.ArmacaoLegacy;
import com.simc.simc40.moduloQualidade.moduloProducao.ArmacaoCFormaLegacy;
import com.simc.simc40.moduloQualidade.moduloProducao.CadastroLegacy;
import com.simc.simc40.moduloQualidade.moduloProducao.ConcretagemLegacy;
import com.simc.simc40.moduloQualidade.moduloProducao.FormaLegacy;
import com.simc.simc40.moduloQualidade.moduloProducao.LiberacaoLegacy;

public class ModuloDeProducaoLegacy extends AppCompatActivity {

    DuploClique duploClique = new DuploClique();
    CardView card1, card2, card3, card4, card5, card6;
    TextView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mproducao_modulo_de_producao);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);
        card6 = findViewById(R.id.card6);
        goBack = findViewById(R.id.goBack);

        card1.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeProducaoLegacy.this, CadastroLegacy.class));
        });
        card2.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeProducaoLegacy.this, ArmacaoLegacy.class));
        });
        card3.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeProducaoLegacy.this, FormaLegacy.class));
        });
        card4.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeProducaoLegacy.this, ArmacaoCFormaLegacy.class));
        });
        card5.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeProducaoLegacy.this, ConcretagemLegacy.class));
        });
        card6.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeProducaoLegacy.this, LiberacaoLegacy.class));
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