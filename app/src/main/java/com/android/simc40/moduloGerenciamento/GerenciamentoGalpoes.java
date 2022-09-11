package com.android.simc40.moduloGerenciamento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.simc40.R;
import com.android.simc40.alerts.errorDialog;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GerenciamentoGalpoes extends AppCompatActivity {

    LinearLayout header;
    LinearLayout listaLayout;
    DatabaseReference reference;
    String database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_galpoes);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        database = sharedPrefsDatabase.getDatabase(GerenciamentoGalpoes.this, MODE_PRIVATE);
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Galpão";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Disponibilidade";
        item2.setText(Text2);
        header.addView(headerXML);

        reference = FirebaseDatabase.getInstance(database).getReference().child("galpoes");
        reference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String galpao = dataSnapshot1.child("nome_galpao").getValue(String.class);
                    String disponivel = dataSnapshot1.child("ativo").getValue(String.class);
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item, listaLayout ,false);
                    TextView item1 = obj.findViewById(R.id.item1);
                    TextView item2 = obj.findViewById(R.id.item2);
                    ImageView item3 = obj.findViewById(R.id.item3);
                    item1.setText(galpao);
                    item2.setText(disponivel);
                    if (disponivel != null) {
                        if(disponivel.equals("Disponível")){
                            item3.setImageResource(R.drawable.checked);
                        }else{
                            item3.setImageResource(R.drawable.uncheck);
                        }
                    }
                    System.out.println(dataSnapshot1.getKey());
                    listaLayout.addView(obj);
                }

                reference.removeEventListener (this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                errorDialog.showError(GerenciamentoGalpoes.this, "Erro! "+databaseError);
                reference.removeEventListener (this);
            }
        });
    }
}