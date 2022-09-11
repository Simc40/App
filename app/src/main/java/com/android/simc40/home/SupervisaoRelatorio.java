package com.android.simc40.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.simc40.R;
import com.android.simc40.configuracaoLeitor.bluetooth_UHF_RFID_reader_1128.ReadWrite1128;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.supervisao_relatorio.pesquisar.FiltrarTag;
import com.android.simc40.supervisao_relatorio.pesquisar.PesquisarTag;

public class SupervisaoRelatorio extends AppCompatActivity {

    DoubleClick doubleClick = new DoubleClick();
    TextView goBack;
    CardView card1, card2, card3, card4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_supervisao_relatorio);

        goBack = findViewById(R.id.goBack);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);

        card1.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            AlertDialog.Builder builder = new AlertDialog.Builder(SupervisaoRelatorio.this);
            builder.setCancelable(true);
            builder.setTitle("Selecionar Modo");
            builder.setPositiveButton("Ler Tag", (dialog, which) -> {
                startActivity(new Intent(SupervisaoRelatorio.this, PesquisarTag.class));
            });
            builder.setNegativeButton("Filtrar Tag", (dialog, whichButton) -> {
                startActivity(new Intent(SupervisaoRelatorio.this, FiltrarTag.class));
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        card2.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(SupervisaoRelatorio.this, ReadWrite1128.class));
        });
        card3.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(SupervisaoRelatorio.this, ModuloDeGerenciamento.class));
        });
        card4.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(SupervisaoRelatorio.this, ModuloDeLogistica.class));
        });

        goBack.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            onBackPressed();
        });

    }
}