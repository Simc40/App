package com.simc.simc40.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simc.simc40.R;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.moduloGerenciamento.GerenciamentoAcessos;
import com.simc.simc40.moduloGerenciamento.GerenciamentoChecklist;
import com.simc.simc40.moduloGerenciamento.GerenciamentoElementos;
import com.simc.simc40.moduloGerenciamento.GerenciamentoGalpoes;
import com.simc.simc40.moduloGerenciamento.GerenciamentoObras;
import com.simc.simc40.moduloGerenciamento.GerenciamentoTransportadoras;

public class ModuloDeGerenciamento extends AppCompatActivity {

    DuploClique duploClique = new DuploClique();
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
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, GerenciamentoObras.class));
        });
        card2.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, GerenciamentoElementos.class));
        });
        card3.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, GerenciamentoTransportadoras.class));
        });
        card4.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, GerenciamentoAcessos.class));
        });
        card5.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, GerenciamentoChecklist.class));
        });
        card6.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(ModuloDeGerenciamento.this, GerenciamentoGalpoes.class));
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