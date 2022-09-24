package com.android.simc40.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.simc40.R;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.moduloGerenciamento.GerenciamentoAcessos;
import com.android.simc40.moduloGerenciamento.GerenciamentoChecklist;
import com.android.simc40.moduloGerenciamento.GerenciamentoElementos;
import com.android.simc40.moduloGerenciamento.GerenciamentoObras;
import com.android.simc40.moduloGerenciamento.GerenciamentoVeiculos;
import com.android.simc40.selecaoListas.SelecaoListaGalpoes;

public class ModuloDeGerenciamento extends AppCompatActivity {

    DoubleClick doubleClick = new DoubleClick();
    CardView card1, card2, card3, card4, card5, card6;
    TextView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_modulo_de_gerenciamento);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);
        card6 = findViewById(R.id.card6);
        goBack = findViewById(R.id.goBack);

        card1.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, GerenciamentoObras.class));
        });
        card2.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, GerenciamentoElementos.class));
        });
        card3.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, GerenciamentoVeiculos.class));
        });
        card4.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, GerenciamentoAcessos.class));
        });
        card5.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, GerenciamentoChecklist.class));
        });
        card6.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, SelecaoListaGalpoes.class));
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