package com.android.simc40.moduloQualidade.moduloProducao;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.simc40.R;
import com.android.simc40.doubleClick.DoubleClick;

public class ModuloDeProducao extends AppCompatActivity {

    DoubleClick doubleClick = new DoubleClick();
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
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeProducao.this, Cadastro.class));
        });
        card2.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
//            startActivity(new Intent(ModuloDeProducao.this, Armacao.class));
        });
        card3.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
//            startActivity(new Intent(ModuloDeProducao.this, Forma.class));
        });
        card4.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
//            startActivity(new Intent(ModuloDeProducao.this, ArmacaoCForma.class));
        });
        card5.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
//            startActivity(new Intent(ModuloDeProducao.this, Concretagem.class));
        });
        card6.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
//            startActivity(new Intent(ModuloDeProducao.this, Liberacao.class));
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