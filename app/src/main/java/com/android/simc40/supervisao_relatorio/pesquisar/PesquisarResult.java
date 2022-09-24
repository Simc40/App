package com.android.simc40.supervisao_relatorio.pesquisar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.simc40.R;
import com.android.simc40.alerts.errorDialog;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PesquisarResult extends AppCompatActivity {

//    HashMap<String, String> usersMap = new HashMap<>();
//    HashMap<String, String> etapasNameMap = new HashMap<>();
//    HashMap<String, String> unidades = new HashMap<>();
//    HashMap<String, String> modeloObject = new HashMap<>();
//    HashMap<String, String> pecaObject = new HashMap<>();
//    Set<String> firebaseUsers = new HashSet<>();
//    HashMap<String, HashMap<String, String>> etapasMap = new HashMap<>();
//    List<String> orderEtapas = new ArrayList<>();
//    Intent intent;
//    String tag, obraUid, database, modeloUid;
//    LinearLayout header, header2;
//    LinearLayout listaLayout, listaLayout2;
//    TextView title2, goBack;
//    DoubleClick doubleClick = new DoubleClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supervisao_relatorio_pesquisar_result);

//        intent = getIntent();
//        modeloUid = intent.getStringExtra("modelo");
//        tag = intent.getStringExtra("tag");
//        obraUid = intent.getStringExtra("obra");
//
//        header = findViewById(R.id.header);
//        header2 = findViewById(R.id.header2);
//        listaLayout = findViewById(R.id.listaLayout);
//        listaLayout2 = findViewById(R.id.listaLayout2);
//        goBack = findViewById(R.id.goBack);
//        title2 = findViewById(R.id.title2);
//        title2.setVisibility(View.GONE);
//
//        fillEtapasCustomNamesMap();
//
//        database = sharedPrefsDatabase.getDatabase(PesquisarResult.this, MODE_PRIVATE);
//
//        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_start_header, header ,false);
//        TextView item1 = headerXML.findViewById(R.id.item1);
//        String Text1 = "Propriedade";
//        item1.setText(Text1);
//        TextView item2 = headerXML.findViewById(R.id.item2);
//        String Text2 = "Valor";
//        item2.setText(Text2);
//        header.addView(headerXML);
//
//        View header2XML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_start_header, header ,false);
//        item1 = header2XML.findViewById(R.id.item1);
//        item1.setText(Text1);
//        item2 = header2XML.findViewById(R.id.item2);
//        item2.setText(Text2);
//        header2.addView(header2XML);
//
//        goBack.setOnClickListener(view -> {
//            if(doubleClick.detected()) return;
//            onBackPressed();
//        });
//
//        getModeloFromDatabase();
//        //getUserDatabase();
    }

//    private void getModeloFromDatabase(){
//        DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference();
//        reference.addListenerForSingleValueEvent (new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot dataSnapshot1: dataSnapshot.child("modelos").child(obraUid).child(modeloUid).child("unidades").getChildren()){
//                    String unidade = dataSnapshot1.getKey();
//                    String value = dataSnapshot1.getValue(String.class);
//                    unidades.put(unidade, value);
//                }
//                for(DataSnapshot dataSnapshot1: dataSnapshot.child("modelos").child(obraUid).child(modeloUid).getChildren()){
//                    if(!Objects.equals(dataSnapshot1.getKey(), "unidades") && !Objects.equals(dataSnapshot1.getKey(), "history")){
//                        String property = dataSnapshot1.getKey();
//                        String value = dataSnapshot1.getValue(String.class);
//                        modeloObject.put(property, value);
//                    }
//                }
//                DataSnapshot snapshot2 = dataSnapshot.child("formas").child(Objects.requireNonNull(modeloObject.get("forma")));
//                String forma = snapshot2.child("nome_forma").getValue(String.class)+" - "+snapshot2.child("b").getValue(String.class)+"x"+snapshot2.child("h").getValue(String.class);
//                String tipo = dataSnapshot.child("tipos_modelo").child(Objects.requireNonNull(modeloObject.get("tipo"))).child("tipo").getValue(String.class);
//                firebaseUsers.add(modeloObject.get("lastModifiedBy")); firebaseUsers.add(modeloObject.get("createdBy"));
//                modeloObject.put("obra", dataSnapshot.child("obras").child(obraUid).child("nome_obra").getValue(String.class));
//                modeloObject.put("forma", forma);
//                modeloObject.put("tipo", tipo);
//
//                System.out.println("modeloObject: " + modeloObject);
//                System.out.println("firebaseUsers: " + firebaseUsers);
//
//                for(DataSnapshot dataSnapshot1: dataSnapshot.child("pecas").child(obraUid).child(tag).getChildren()){
//                    if(!Objects.equals(dataSnapshot1.getKey(), "etapas") && !Objects.equals(dataSnapshot1.getKey(), "history")){
//                        String property = dataSnapshot1.getKey();
//                        String value = dataSnapshot1.getValue(String.class);
//                        pecaObject.put(property, value);
//                    }
//                }
//                firebaseUsers.add(pecaObject.get("lastModifiedBy"));
//                pecaObject.put("etapa_atual", etapasNameMap.get(pecaObject.get("etapa_atual")));
//                pecaObject.put("modelo", modeloObject.get("nome_modelo"));
//                pecaObject.put("obra", dataSnapshot.child("obras").child(obraUid).child("nome_obra").getValue(String.class));
//
//
//                for(DataSnapshot dataSnapshot1: dataSnapshot.child("pecas").child(obraUid).child(tag).child("etapas").getChildren()){
//                    String etapa = dataSnapshot1.getKey();
//                    HashMap <String, String> etapaMap = new HashMap<>();
//                    for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){
//                        String property = dataSnapshot2.getKey();
//                        String value = dataSnapshot2.getValue(String.class);
//                        if(Objects.equals(property, "galpao")) value = dataSnapshot.child("galpoes").child(Objects.requireNonNull(value)).child("nome_galpao").getValue(String.class);
//                        etapaMap.put(property, value);
//                        if(Objects.equals(property, "createdBy")){
//                            firebaseUsers.add(value);
//                        }
//                    }
//                    etapasMap.put(etapa, etapaMap);
//                }
//
//                getFirebaseUsers();
//                reference.removeEventListener (this);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                errorDialog.showError(PesquisarResult.this, "Erro! "+databaseError);
//                reference.removeEventListener (this);
//            }
//        });
//    }
//
//    private void getFirebaseUsers() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("usuarios");
//        reference.addListenerForSingleValueEvent (new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(String userUid : firebaseUsers){
//                    usersMap.put(userUid, dataSnapshot.child(userUid).child("nome").getValue(String.class));
//                }
//                modeloObject.put("lastModifiedBy", usersMap.get(modeloObject.get("lastModifiedBy")));
//                modeloObject.put("createdBy", usersMap.get(modeloObject.get("createdBy")));
//                pecaObject.put("lastModifiedBy", usersMap.get(pecaObject.get("lastModifiedBy")));
//                for(String etapa: etapasMap.keySet()){
//                    HashMap<String, String> etapaMap = etapasMap.get(etapa);
//                    if (etapaMap != null) {
//                        etapaMap.put("createdBy", usersMap.get(etapaMap.get("createdBy")));
//                    }
//                }
//
//
//                System.out.println("modeloObject: " + modeloObject);
//                System.out.println("pecaObject: " + pecaObject);
//                System.out.println("etapasMap: " + etapasMap);
//                reference.removeEventListener (this);
//                gerarRelatorioModelo();
//                gerarRelatorioPeca();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                errorDialog.showError(PesquisarResult.this, "Erro! "+databaseError);
//                reference.removeEventListener (this);
//            }
//        });
//    }
//
//    private void gerarRelatorioModelo() {
//        String[] modeloProperties = {"nome_modelo", "obra", "numMax", "numPecas", "tipo", "b", "h", "c", "fckdesf", "fckic", "volume", "peso", "taxaaco", "forma", "lastModifiedBy", "lastModifiedOn"};
//        for(String property: modeloProperties){
//            inflateItemOnLayout(property, modeloObject.get(property), listaLayout, R.layout.custom_3_list_item_start);
//        }
//    }
//
//    private void gerarRelatorioPeca() {
//        String[] PecaProperties = {"tag", "nome_peca", "obra", "modelo", "etapa_atual","lastModifiedBy", "lastModifiedOn"};
//        for(String property: PecaProperties){
//            inflateItemOnLayout(property, pecaObject.get(property), listaLayout2, R.layout.custom_3_list_item_start);
//        }
//
//        for(String etapa: orderEtapas){
//            HashMap <String,String> etapaMap = etapasMap.get(etapa);
//            if (etapaMap != null) {
//                inflateItemOnLayout(etapa + " CheckList", "", listaLayout2, R.layout.custom_3_list_item_start_header);
//                for(String property: etapaMap.keySet()){
//                    if(property.equals("createdBy") || property.equals("creation")) continue;
//                    inflateItemOnLayout(property, etapaMap.get(property), listaLayout2, R.layout.custom_3_list_item_start);
//                }
//                inflateItemOnLayout("createdBy", etapaMap.get("createdBy"), listaLayout2, R.layout.custom_3_list_item_start);
//                inflateItemOnLayout("creation", etapaMap.get("creation"), listaLayout2, R.layout.custom_3_list_item_start);
//
//            }
//        }
//    }
//
//    @Override
//    public void onBackPressed(){
//        super.onBackPressed();
//        if(doubleClick.detected()){
//            return;
//        }
//        finish();
//    }
//
//    private void inflateItemOnLayout(String propriedade, String valor, LinearLayout layout, int customLayout){
//        View obj = LayoutInflater.from(getApplicationContext()).inflate(customLayout , layout ,false);
//        TextView item1 = obj.findViewById(R.id.item1);
//        TextView item2 = obj.findViewById(R.id.item2);
//        ImageView item3 = obj.findViewById(R.id.item3);
//        item3.setVisibility(View.GONE);
//        if(propriedade!= null) {
//            switch (propriedade) {
//                case "b":
//                case "h":
//                    valor = valor + unidades.get("secao");
//                    break;
//                case "c":
//                    valor = valor + unidades.get("comprimento");
//                    break;
//                case "fckdesf":
//                case "fckic":
//                    valor = valor + unidades.get("tensao");
//                    break;
//                case "peso":
//                    valor = valor + unidades.get("massa");
//                    break;
//                case "volume":
//                    valor = valor + unidades.get("volume");
//                    break;
//                case "numMax":
//                    propriedade = "Peças Planejadas";
//                    break;
//                case "numPecas":
//                    propriedade = "Peças Cadastradas";
//                    break;
//                case "taxaaco":
//                    propriedade = "taxa de aço";
//                    valor = valor + unidades.get("massa");
//                    break;
//                case "createdBy":
//                case "creation":
//                case "lastModifiedOn":
//                case "lastModifiedBy":
//                    propriedade = "Ultima Modificação";
//                    break;
//            }
//        }
//        if (propriedade != null) {
//            propriedade = propriedade.substring(0, 1).toUpperCase() + propriedade.substring(1).toLowerCase();
//        }
//        item1.setText(propriedade);
//        item2.setText(valor);
//        layout.addView(obj);
//    }
//
//    private void fillEtapasCustomNamesMap(){
//        etapasNameMap.put("planejamento", "Planejamento");
//        etapasNameMap.put("cadastro", "Produção/Cadastro");
//        etapasNameMap.put("armacao", "Produção/Armacao");
//        etapasNameMap.put("forma", "Produção/Forma");
//        etapasNameMap.put("armacaoForma", "Produção/Armacao com Forma");
//        etapasNameMap.put("concretagem", "Produção/Concretagem");
//        etapasNameMap.put("liberacao", "Produção/Liberacao Final");
//        etapasNameMap.put("carga", "Transporte/Carga");
//        etapasNameMap.put("descarga", "Transporte/Descarga");
//        etapasNameMap.put("montagem", "Montagem");
//        orderEtapas.add("planejamento");
//        orderEtapas.add("cadastro");
//        orderEtapas.add("armacao");
//        orderEtapas.add("forma");
//        orderEtapas.add("armacaoForma");
//        orderEtapas.add("concretagem");
//        orderEtapas.add("liberacao");
//        orderEtapas.add("carga");
//        orderEtapas.add("descarga");
//        orderEtapas.add("montagem");
//    }
}
