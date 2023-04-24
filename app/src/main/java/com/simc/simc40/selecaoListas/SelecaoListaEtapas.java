package com.simc.simc40.selecaoListas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.checklist.Etapas;
import com.simc.simc40.doubleClick.DuploClique;

public class SelecaoListaEtapas extends AppCompatActivity implements Etapas {

    Intent intent;
    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    DuploClique duploClique = new DuploClique();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_etapas);

        intent = getIntent();

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_header, header, false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Etapa";
        item1.setText(Text1);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            onBackPressed();
        });

        for (String etapa : firebaseCheckLists){
            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item, listaLayout, false);
            TextView objItem1 = obj.findViewById(R.id.item1);
            objItem1.setText(prettyEtapas.get(etapa));
            ImageView item2 = obj.findViewById(R.id.item2);
            item2.setImageResource(R.drawable.forward);
            item2.setTag(etapa);
            item2.setOnClickListener(view -> {
                if (duploClique.detectado()) return;
                Intent intent = new Intent();
                String etapaSelecionada = (String) item2.getTag();
                intent.putExtra("result", etapaSelecionada);
                setResult(SelecaoListaEtapas.RESULT_OK, intent);
                finish();
            });
            listaLayout.addView(obj);
        }
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