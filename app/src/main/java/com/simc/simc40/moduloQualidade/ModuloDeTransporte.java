package com.simc.simc40.moduloQualidade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.moduloQualidade.moduloTransporte.ModuloDeTransportePecas;
import com.simc.simc40.moduloQualidade.moduloTransporte.ModuloDeTransporteRomaneio;
import com.simc.simc40.moduloQualidade.moduloTransporte.ModuloDeTransporteSearch;

public class ModuloDeTransporte extends AppCompatActivity {

    DuploClique duploClique = new DuploClique();
    TextView goBack;
    CardView card1, card2, card3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mtransporte);


        goBack = findViewById(R.id.goBack);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);

        card1.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            startActivity(new Intent(this, ModuloDeTransporteRomaneio.class));
        });
        card2.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            startActivity(new Intent(this, ModuloDeTransportePecas.class));
        });

        card3.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            startActivity(new Intent(this, ModuloDeTransporteSearch.class));
        });

        goBack.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (duploClique.detectado()) {
            return;
        }
        finish();
    }
}