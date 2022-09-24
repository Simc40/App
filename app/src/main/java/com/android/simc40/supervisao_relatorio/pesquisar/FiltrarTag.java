package com.android.simc40.supervisao_relatorio.pesquisar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FiltrarTag extends AppCompatActivity {

//    //Firebase HashMaps
//    HashMap<String, String> modelosNameMap = new HashMap<>();
//    HashMap<String, String> etapasNameMap = new HashMap<>();
//    HashMap<String, String> obrasNameMap = new HashMap<>();
//    HashMap<String, String> usersMap = new HashMap<>();
//
//    //Lista de Filtros para Spinner Adapter
//    List<String> filtros = Arrays.asList("Selecionar Filtro", "Obra", "Etapa do Processo", "Modelo");
//    HashMap<String, List<String>> filtrosMap = new HashMap<>();
//    List<String> filterEtapas = new ArrayList<>();
//    List<String> filterObras = new ArrayList<>();
//    List<String> filterModelos = new ArrayList<>();
//
//    //Lista para errorObjects
//    List<tagObject> fullListOfTags = new ArrayList<>();
//    List<tagObject> filteredListOfTags;
//
//    //Elementos de Layout
//    String database;
//    RelativeLayout filterLayout, secondaryFilterLayout;
//    Spinner spinnerFiltros, spinnerFiltros2;
//    LinearLayout header, header2;
//    LinearLayout listaLayout, listaLayout2;
//    TextView textViewSecondaryFilter, title2, goBack;
//    DoubleClick doubleClick = new DoubleClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supervisao_relatorio_filtrar_tag);

//        header = findViewById(R.id.header);
//        header2 = findViewById(R.id.header2);
//        listaLayout = findViewById(R.id.listaLayout);
//        listaLayout2 = findViewById(R.id.listaLayout2);
//        spinnerFiltros = findViewById(R.id.spinnerFiltros);
//        spinnerFiltros2 = findViewById(R.id.spinnerFiltros2);
//        filterLayout = findViewById(R.id.filterLayout);
//        filterLayout.setVisibility(View.GONE);
//        secondaryFilterLayout = findViewById(R.id.secondaryFilterLayout);
//        textViewSecondaryFilter = findViewById(R.id.textViewSecondaryFilter);
//        goBack = findViewById(R.id.goBack);
//        title2 = findViewById(R.id.title2);
//        title2.setVisibility(View.GONE);
//
//        listaLayout.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> detectChangesOnFilters());
//        database = sharedPrefsDatabase.getDatabase(FiltrarTag.this, MODE_PRIVATE);
//
//        fillEtapasCustomNamesMap();
//
//        getUserDatabase();
//
//        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_header, header ,false);
//        TextView item1 = headerXML.findViewById(R.id.item1);
//        String Text1 = "Filtros";
//        item1.setText(Text1);
//        header.addView(headerXML);
//
//        View headerXML2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_header, header ,false);
//        TextView item1_2 = headerXML2.findViewById(R.id.item1);
//        String text1_2 = "Inconformidades";
//        item1_2.setText(text1_2);
//        header2.addView(headerXML2);
//
//        ArrayAdapter<String> adapterFiltros = new ArrayAdapter<>(FiltrarTag.this, android.R.layout.simple_list_item_1, filtros);
//        adapterFiltros.setDropDownViewResource(android.R.layout.simple_list_item_1);
//        spinnerFiltros.setAdapter(adapterFiltros);
//
//        spinnerFiltros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String selected = spinnerFiltros.getSelectedItem().toString();
//                if(selected.equals("Selecionar Filtro")) {
//                    secondaryFilterLayout.setVisibility(View.GONE);
//                    return;
//                }
//                secondaryFilterLayout.setVisibility(View.VISIBLE);
//                String text = selected+":";
//                textViewSecondaryFilter.setText(text);
//
//                ArrayAdapter<String> adapterFiltrosSecondary = new ArrayAdapter<>(FiltrarTag.this, android.R.layout.simple_list_item_1, filtrosMap.get(selected));
//                adapterFiltrosSecondary.setDropDownViewResource(android.R.layout.simple_list_item_1);
//                spinnerFiltros2.setAdapter(adapterFiltrosSecondary);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        spinnerFiltros2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String filtro = spinnerFiltros.getSelectedItem().toString();
//                String selected = spinnerFiltros2.getSelectedItem().toString();
//                if (selected.equals("Selecionar " + filtro)) return;
//                if(filtro.equals("Etapa do Processo") && filterEtapas.contains(selected)) {
//                    errorDialog.showError(FiltrarTag.this, "A " + filtro +" " + selected + " Já está no filtro");
//                    spinnerFiltros2.setSelection(0);
//                    return;
//                }else if(filtro.equals("Modelo") && filterModelos.contains(selected)) {
//                    errorDialog.showError(FiltrarTag.this, "O " + filtro +" " + selected + " Já está no filtro");
//                    spinnerFiltros2.setSelection(0);
//                    return;
//                }else if(filtro.equals("Obra") && filterObras.contains(selected)) {
//                    errorDialog.showError(FiltrarTag.this, "A " + filtro +" " + selected + " Já está no filtro");
//                    spinnerFiltros2.setSelection(0);
//                    return;
//                }
//                inflateFilter(filtro, selected);
//                spinnerFiltros.setSelection(0);
//                secondaryFilterLayout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        goBack.setOnClickListener(view -> {
//            if(doubleClick.detected()) return;
//            onBackPressed();
//        });

    }

//    private void detectChangesOnFilters() {
//        listaLayout2.removeAllViews();
//        filteredListOfTags = new ArrayList<>(fullListOfTags);
//        if(filterObras.size()>0) {
//            filteredListOfTags.removeIf(errorObj -> !filterObras.contains(errorObj.getNome_obra()));
//        }
//        System.out.println("filterEtapas" + filterEtapas);
//        System.out.println("filterModelos" + filterModelos);
//        if(filterEtapas.size()>0) {
//            filteredListOfTags.removeIf(errorObj -> !filterEtapas.contains(errorObj.getNome_etapa()));
//        }
//        if(filterModelos.size()>0) {
//            filteredListOfTags.removeIf(errorObj -> !filterModelos.contains(errorObj.getNome_modelo()));
//        }
//        for(tagObject tagObj : filteredListOfTags){
//            listaLayout2.addView(tagObj.view);
//        }
//    }
//
//    private void getUserDatabase() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("usuarios");
//        reference.addListenerForSingleValueEvent (new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                    String uid = dataSnapshot1.getKey();
//                    String nome = dataSnapshot1.child("nome").getValue(String.class);
//                    usersMap.put(uid, nome);
//                }
//                getTags();
//                reference.removeEventListener (this);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                errorDialog.showError(FiltrarTag.this, "Erro! "+databaseError);
//                reference.removeEventListener (this);
//            }
//        });
//    }
//
//    private void getTags() {
//        DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference();
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot1: snapshot.child("obras").getChildren()) {
//                    String nome_obra = dataSnapshot1.child("nome_obra").getValue(String.class);
//                    obrasNameMap.put(dataSnapshot1.getKey(), nome_obra);
//                }
//                List<String> obras = new ArrayList<>();
//                obras.add("Selecionar Obra");
//                obras.addAll(obrasNameMap.values());
//                filtrosMap.put("Obra", obras);
//                for(DataSnapshot dataSnapshot1: snapshot.child("modelos").getChildren()) {
//                    for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()) {
//                        String nome_modelo = dataSnapshot2.child("nome_modelo").getValue(String.class);
//                        modelosNameMap.put(dataSnapshot2.getKey(), nome_modelo);
//                    }
//                }
//                List<String> modelo = new ArrayList<>();
//                modelo.add("Selecionar Modelo");
//                modelo.addAll(modelosNameMap.values());
//                filtrosMap.put("Modelo", modelo);
//                for(DataSnapshot dataSnapshot1: snapshot.child("pecas").getChildren()) {
//                    String obraUid = dataSnapshot1.getKey();
//                    for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()) {
//                        String tag = dataSnapshot2.getKey();
//                        HashMap<String,String> properties = new HashMap<>();
//                        for(DataSnapshot dataSnapshot3: dataSnapshot2.getChildren()) {
//                            if(!Objects.equals(dataSnapshot3.getKey(), "etapas") && !Objects.equals(dataSnapshot3.getKey(), "history")) {
//                                String property = dataSnapshot3.getKey();
//                                String value = dataSnapshot3.getValue(String.class);
//                                properties.put(property, value);
//                            }
//                        }
//                        fullListOfTags.add(new tagObject(tag, obraUid, properties));
//                    }
//                }
//                filterLayout.setVisibility(View.VISIBLE);
//                reference.removeEventListener (this);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                errorDialog.showError(FiltrarTag.this, "Erro! "+error.toString());
//                reference.removeEventListener (this);
//            }
//        });
//    }
//
//    private void inflateFilter(String filtro, String selected) {
//        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_start, listaLayout ,false);
//        TextView item1 = obj.findViewById(R.id.item1);
//        TextView item2 = obj.findViewById(R.id.item2);
//        ImageView item3 = obj.findViewById(R.id.item3);
//        item3.setImageResource(R.drawable.uncheck);
//        item1.setText(filtro);
//        item2.setText(selected);
//        switch (filtro) {
//            case "Etapa do Processo":
//                item3.setOnClickListener(view -> {
//                    filterEtapas.remove(selected);
//                    listaLayout.removeView(obj);
//                });
//                filterEtapas.add(selected);
//                break;
//            case "Modelo":
//                item3.setOnClickListener(view -> {
//                    filterModelos.remove(selected);
//                    listaLayout.removeView(obj);
//                });
//                filterModelos.add(selected);
//                break;
//            case "Obra":
//                item3.setOnClickListener(view -> {
//                    filterObras.remove(selected);
//                    listaLayout.removeView(obj);
//                });
//                filterObras.add(selected);
//                break;
//        }
//        listaLayout.addView(obj);
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
//
//    private void fillEtapasCustomNamesMap(){
//        List<String> etapas = new ArrayList<>();
//        etapas.add("Selecionar Etapa do Processo");
//        etapas.add("Produção/Armacao");
//        etapas.add("Produção/Forma");
//        etapas.add("Produção/Armacao com Forma");
//        etapas.add("Produção/Concretagem");
//        etapas.add("Produção/Liberacao Final");
//        etapas.add("Transporte/Carga");
//        etapas.add("Transporte/Descarga");
//        etapas.add("Montagem");
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
//        filtrosMap.put("Etapa do Processo", etapas);
//    }
//
//    class tagObject{
//        String tag;
//
//        //Filterable properties
//        String obraUid;
//        String etapa;
//        String modeloUid;
//
//        //toBuild View
//        View view;
//        String lastModifiedBy;
//        String lastModifiedOn;
//        String nome_obra;
//        String nome_modelo;
//        String nome_peca;
//        String nome_etapa;
//
//        public tagObject(String tag, String obraUid, HashMap<String,String> properties) {
//            this.tag = tag;
//            this.obraUid = obraUid;
//            this.nome_obra = obrasNameMap.get(obraUid);
//            for(String property : properties.keySet()){
//                switch (property){
//                    case "etapa_atual":
//                        String etapa = properties.get(property);
//                        this.etapa = etapa;
//                        this.nome_etapa = etapasNameMap.get(etapa);
//                        break;
//                    case "lastModifiedBy":
//                        this.lastModifiedBy = usersMap.get(properties.get(property));
//                        break;
//                    case "lastModifiedOn":
//                        this.lastModifiedOn = properties.get(property);
//                        break;
//                    case "modelo":
//                        String modeloUid = properties.get(property);
//                        this.modeloUid = modeloUid;
//                        this.nome_modelo = modelosNameMap.get(modeloUid);
//                        break;
//                    case "nome_peca":
//                        this.nome_peca = properties.get(property);
//                        break;
//                    case "obra":
//                    case "tag":
//                        break;
//                }
//            }
//            generateView();
//        }
//
//        private void generateView(){
//            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tag_item, listaLayout2 ,false);
//            //Find TextViews
//            TextView tag = obj.findViewById(R.id.tag);
//            TextView nome_obra = obj.findViewById(R.id.nome_obra);
//            TextView nome_etapa = obj.findViewById(R.id.nome_etapa);
//            TextView nome_modelo = obj.findViewById(R.id.nome_modelo);
//            TextView nome_peca = obj.findViewById(R.id.nome_peca);
//            TextView lastModifiedBy = obj.findViewById(R.id.lastModifiedBy);
//            TextView lastModifiedOn = obj.findViewById(R.id.lastModifiedOn);
//            //Set TextView Values
//            tag.setText(this.tag);
//            nome_obra.setText(this.nome_obra);
//            nome_etapa.setText(this.nome_etapa);
//            nome_modelo.setText(this.nome_modelo);
//            nome_peca.setText(this.nome_peca);
//            lastModifiedBy.setText(this.lastModifiedBy);
//            lastModifiedOn.setText(this.lastModifiedOn);
//            //Set Obj ClickListener
//            obj.setOnClickListener(view -> {
//                Intent intent = new Intent(FiltrarTag.this, PesquisarResult.class);
//                intent.putExtra("tag", this.tag);
//                intent.putExtra("obra", this.obraUid);
//                intent.putExtra("modelo", this.modeloUid);
//                Toast.makeText(FiltrarTag.this, "Tag identificada com sucesso!", Toast.LENGTH_LONG).show();
//                startActivity(intent);
//                finish();
//            });
//
//            //Associate View to Object and Inflate
//            this.view = obj;
//            listaLayout2.addView(obj);
//        }
//
//        public String getTag() {
//            return tag;
//        }
//
//        public void setTag(String tag) {
//            this.tag = tag;
//        }
//
//        public View getView() {
//            return view;
//        }
//
//        public void setView(View view) {
//            this.view = view;
//        }
//
//        public String getNome_obra() {
//            return nome_obra;
//        }
//
//        public String getNome_modelo() {
//            return nome_modelo;
//        }
//
//        public String getNome_etapa() {
//            return nome_etapa;
//        }
//    }
}