package com.android.simc40.moduloQualidade.modulo_transporte;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.simc40.R;
import com.android.simc40.doubleClick.DoubleClick;

public class ModuloDeTransporte extends AppCompatActivity {

    DoubleClick doubleClick = new DoubleClick();
    TextView goBack;
    CardView card1, card2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mtransporte_modulo_de_transporte);

        goBack = findViewById(R.id.goBack);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);

        card1.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeTransporte.this, Carga.class));
        });
        card2.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(ModuloDeTransporte.this, Descarga.class));
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