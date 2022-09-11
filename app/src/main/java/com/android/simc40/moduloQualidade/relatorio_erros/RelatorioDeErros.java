package com.android.simc40.moduloQualidade.relatorio_erros;

import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.simc40.R;
import com.android.simc40.alerts.errorDialog;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.selecaoListas.SelecaoListaObras;
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

public class RelatorioDeErros extends AppCompatActivity {
    //Firebase HashMaps
    HashMap<String, String> pecasNameMap = new HashMap<>();
    HashMap<String, String> modelosNameMap = new HashMap<>();
    HashMap<String, String> etapasNameMap = new HashMap<>();
    HashMap<String, String> obrasNameMap = new HashMap<>();
    HashMap<String, String> usersMap = new HashMap<>();

    //Lista de Filtros para Spinner Adapter
    List<String> filtros = Arrays.asList("Selecionar Filtro", "Obra", "Etapa do Processo", "Tag", "Modelo", "Status");
    List<String> status = Arrays.asList("Selecionar Status","aberto", "fechado");
    boolean filterStatusIsActive = false;
    String filterStatus;
    List<String> etapaDoProcesso = Arrays.asList("Selecionar Etapa do Processo", "armacao", "armacaoForma", "cadastro", "carga", "concretagem", "descarga", "forma", "liberacao", "montagem", "planejamento");
    HashMap<String, List<String>> filtrosMap = new HashMap<String, List<String>>() {{
        put("Etapa do Processo", etapaDoProcesso);
        put("Status", status);
    }};
    List<String> filterEtapas = new ArrayList<>();
    List<String> filterObras = new ArrayList<>();
    List<String> filterModelos = new ArrayList<>();
    List<String> filterTags = new ArrayList<>();

    //Lista para errorObjects
    HashMap<String, String> modelosOfErrors = new HashMap<>();
    HashMap<String, String> pecasOfErrors = new HashMap<>();
    List<errorObject> fullListOfErrors = new ArrayList<>();
    List<errorObject> filteredListOfErrors;

    //Elementos de Layout
    String database;
    RelativeLayout filterLayout, secondaryFilterLayout;
    Spinner spinnerFiltros, spinnerFiltros2;
    LinearLayout contentLayout, header, header2;
    LinearLayout listaLayout, listaLayout2;
    TextView textViewSecondaryFilter, title2, goBack;
    DoubleClick doubleClick = new DoubleClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqualidade_relatorio_de_erros);

        header = findViewById(R.id.header);
        header2 = findViewById(R.id.header2);
        contentLayout = findViewById(R.id.contentLayout);
        contentLayout.setVisibility(View.GONE);
        listaLayout = findViewById(R.id.listaLayout);
        listaLayout2 = findViewById(R.id.listaLayout2);
        spinnerFiltros = findViewById(R.id.spinnerFiltros);
        spinnerFiltros2 = findViewById(R.id.spinnerFiltros2);
        filterLayout = findViewById(R.id.filterLayout);
        secondaryFilterLayout = findViewById(R.id.secondaryFilterLayout);
        textViewSecondaryFilter = findViewById(R.id.textViewSecondaryFilter);
        goBack = findViewById(R.id.goBack);
        title2 = findViewById(R.id.title2);

        fillEtapasCustomNamesMap();
        listaLayout.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> detectChangesOnFilters());
        database = sharedPrefsDatabase.getDatabase(RelatorioDeErros.this, MODE_PRIVATE);

        getUserDatabase();

        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Filtros";
        item1.setText(Text1);
        header.addView(headerXML);

        View headerXML2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_header, header ,false);
        TextView item1_2 = headerXML2.findViewById(R.id.item1);
        String text1_2 = "Inconformidades";
        item1_2.setText(text1_2);
        header2.addView(headerXML2);

        ArrayAdapter<String> adapterFiltros = new ArrayAdapter<>(RelatorioDeErros.this, android.R.layout.simple_list_item_1, filtros);
        adapterFiltros.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerFiltros.setAdapter(adapterFiltros);

        spinnerFiltros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = spinnerFiltros.getSelectedItem().toString();
                if(selected.equals("Selecionar Filtro")) {
                    secondaryFilterLayout.setVisibility(View.GONE);
                    return;
                }
                if(selected.equals("Obra")) {
                    Intent intent = new Intent(RelatorioDeErros.this, SelecaoListaObras.class);
                    spinnerFiltros.setSelection(0);
                    selectObraFromList.launch(intent);
                    return;
                }
                secondaryFilterLayout.setVisibility(View.VISIBLE);
                String text = selected+":";
                textViewSecondaryFilter.setText(text);

                ArrayAdapter<String> adapterFiltrosSecondary = new ArrayAdapter<>(RelatorioDeErros.this, android.R.layout.simple_list_item_1, filtrosMap.get(selected));
                adapterFiltrosSecondary.setDropDownViewResource(android.R.layout.simple_list_item_1);
                spinnerFiltros2.setAdapter(adapterFiltrosSecondary);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerFiltros2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String filtro = spinnerFiltros.getSelectedItem().toString();
                String selected = spinnerFiltros2.getSelectedItem().toString();
                if (selected.equals("Selecionar " + filtro)) return;
                if(filtro.equals("Etapa do Processo") && filterEtapas.contains(selected)) {
                    errorDialog.showError(RelatorioDeErros.this, "A " + filtro +" " + selected + " Já está no filtro");
                    spinnerFiltros2.setSelection(0);
                    return;
                }else if(filtro.equals("Modelo") && filterModelos.contains(selected)) {
                    errorDialog.showError(RelatorioDeErros.this, "O " + filtro +" " + selected + " Já está no filtro");
                    spinnerFiltros2.setSelection(0);
                    return;
                }else if(filtro.equals("Tag") && filterTags.contains(selected)) {
                    errorDialog.showError(RelatorioDeErros.this, "A " + filtro +" " + selected + " Já está no filtro");
                    spinnerFiltros2.setSelection(0);
                    return;
                }else if(filtro.equals("Status") && filterStatusIsActive) {
                    errorDialog.showError(RelatorioDeErros.this, "O " + filtro +" " + selected + " Já está no filtro");
                    spinnerFiltros2.setSelection(0);
                    return;
                }
                inflateFilter(filtro, selected);
                spinnerFiltros.setSelection(0);
                secondaryFilterLayout.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        goBack.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            onBackPressed();
        });

    }

    private void detectChangesOnFilters() {
        listaLayout2.removeAllViews();
        filteredListOfErrors = new ArrayList<>(fullListOfErrors);
        if(filterObras.size()>0) {
            filteredListOfErrors.removeIf(errorObj -> !filterObras.contains(errorObj.getObraUid()));
        }
        if(filterEtapas.size()>0) {
            filteredListOfErrors.removeIf(errorObj -> !filterEtapas.contains(errorObj.getEtapa()));
        }
        if(filterModelos.size()>0) {
            filteredListOfErrors.removeIf(errorObj -> !filterModelos.contains(errorObj.getNome_modelo()));
        }
        if(filterStatus!=null) {
            filteredListOfErrors.removeIf(errorObj -> !filterStatus.equals(errorObj.getStatus()));
        }
        System.out.println("filterStatus: " + filterStatus);
        if(filterTags.size()>0) {
            filteredListOfErrors.removeIf(errorObj -> !filterTags.contains(errorObj.getTag()));
        }
        for(errorObject errorObj : filteredListOfErrors){
            listaLayout2.addView(errorObj.view);
        }
    }

    private void getUserDatabase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("usuarios");
        reference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String uid = dataSnapshot1.getKey();
                    String nome = dataSnapshot1.child("nome").getValue(String.class);
                    usersMap.put(uid, nome);
                }
                getErrors();
                reference.removeEventListener (this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                errorDialog.showError(RelatorioDeErros.this, "Erro! "+databaseError);
                reference.removeEventListener (this);
            }
        });
    }

    private void getErrors() {
        DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1: snapshot.child("obras").getChildren()) {
                    String nome_obra = dataSnapshot1.child("nome_obra").getValue(String.class);
                    obrasNameMap.put(dataSnapshot1.getKey(), nome_obra);
                }
                for(DataSnapshot dataSnapshot1: snapshot.child("erros").getChildren()) {
                    String obraUid = dataSnapshot1.getKey();
                    for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()) {
                        String etapa = dataSnapshot2.getKey();
                        for(DataSnapshot dataSnapshot3: dataSnapshot2.getChildren()) {
                            String erroUid = dataSnapshot3.getKey();
                            HashMap<String,String> properties = new HashMap<>();
                            for(DataSnapshot dataSnapshot4: dataSnapshot3.getChildren()) {
                                String property = dataSnapshot4.getKey();
                                String value = dataSnapshot4.getValue(String.class);
                                properties.put(property, value);
                            }
                            fullListOfErrors.add(new errorObject(obraUid, etapa, erroUid, properties));
                        }
                    }
                }
                reference.removeEventListener (this);
                updateErrorObjects();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorDialog.showError(RelatorioDeErros.this, "Erro! "+error.toString());
                reference.removeEventListener (this);
            }
        });
    }

    private void inflateFilter(String filtro, String selected) {
        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_start, listaLayout ,false);
        TextView item1 = obj.findViewById(R.id.item1);
        TextView item2 = obj.findViewById(R.id.item2);
        ImageView item3 = obj.findViewById(R.id.item3);
        item3.setImageResource(R.drawable.uncheck);
        item1.setText(filtro);
        item2.setText(selected);
        if(filtro.equals("Etapa do Processo")) {
            item3.setOnClickListener(view -> {
                filterEtapas.remove(selected);
                listaLayout.removeView(obj);
            });
            filterEtapas.add(selected);
        }else if(filtro.equals("Modelo")) {
            item3.setOnClickListener(view -> {
                filterModelos.remove(selected);
                listaLayout.removeView(obj);
            });
            filterModelos.add(selected);
        }if(filtro.equals("Tag")) {
            item3.setOnClickListener(view -> {
                filterTags.remove(selected);
                listaLayout.removeView(obj);
            });
            filterTags.add(selected);
        }if(filtro.equals("Status")) {
            item3.setOnClickListener(view -> {
                filterStatusIsActive = false;
                filterStatus = null;
                listaLayout.removeView(obj);
            });
            filterStatus = selected;
        }
        listaLayout.addView(obj);
    }

    ActivityResultLauncher<Intent> selectObraFromList = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    String obraUid = data.getStringExtra("result1");
                    String obraNome = data.getStringExtra("result2");
                    if(filterObras.contains(obraUid)){
                        errorDialog.showError(RelatorioDeErros.this, "A obra " + obraNome + " Já está no filtro!");
                        return;
                    }
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_start, listaLayout ,false);
                    TextView item1 = obj.findViewById(R.id.item1);
                    TextView item2 = obj.findViewById(R.id.item2);
                    ImageView item3 = obj.findViewById(R.id.item3);
                    item3.setImageResource(R.drawable.uncheck);
                    String text = "Obra";
                    item1.setText(text);
                    item2.setText(obraNome);
                    item3.setOnClickListener(view -> {
                        filterObras.remove(obraUid);
                        listaLayout.removeView(obj);
                    });
                    filterObras.add(obraUid);
                    listaLayout.addView(obj);
                }
            });

    ActivityResultLauncher<Intent> solucionarErro = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Activity act = RelatorioDeErros.this;
                    Intent intent=new Intent();
                    intent.setClass(act, act.getClass());
                    act.startActivity(intent);
                    act.finish();
                }
            });

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(doubleClick.detected()){
            return;
        }
        finish();
    }


    private void fillEtapasCustomNamesMap(){
        etapasNameMap.put("planejamento", "Planejamento");
        etapasNameMap.put("cadastro", "Produção/Cadastro");
        etapasNameMap.put("armacao", "Produção/Armacao");
        etapasNameMap.put("forma", "Produção/Forma");
        etapasNameMap.put("armacaoForma", "Produção/Armacao com Forma");
        etapasNameMap.put("concretagem", "Produção/Concretagem");
        etapasNameMap.put("liberacao", "Produção/Liberacao Final");
        etapasNameMap.put("carga", "Transporte/Carga");
        etapasNameMap.put("descarga", "Transporte/Descarga");
        etapasNameMap.put("montagem", "Montagem");
    }

    private void updateErrorObjects(){
        DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(String modeloUid: modelosOfErrors.keySet()){
                    String nome_modelo = snapshot.child("modelos").child(Objects.requireNonNull(modelosOfErrors.get(modeloUid))).child(modeloUid).child("nome_modelo").getValue(String.class);
                    modelosNameMap.put(modeloUid, nome_modelo);
                }
                setNomeModeloOfErrorObjects();
                for(String tag: pecasOfErrors.keySet()){
                    String nome_peca = snapshot.child("pecas").child(Objects.requireNonNull(pecasOfErrors.get(tag))).child(tag).child("nome_peca").getValue(String.class);
                    System.out.println(tag + " " + nome_peca);
                    pecasNameMap.put(tag, nome_peca);
                }
                reference.removeEventListener (this);
                setNomePecasOfErrorObjects();
                List<String> hashModelos = new ArrayList<>();
                hashModelos.add("Selecionar Modelo");
                hashModelos.addAll(modelosNameMap.values());
                List<String> HashTags = new ArrayList<>();
                HashTags.add("Selecionar Tag");
                HashTags.addAll(pecasOfErrors.keySet());
                filtrosMap.put("Modelo", hashModelos);
                filtrosMap.put("Tag", HashTags);
                contentLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorDialog.showError(RelatorioDeErros.this, "Erro! "+error.toString());
                reference.removeEventListener (this);
            }
        });
    }

    private void setNomeModeloOfErrorObjects() {
        for(errorObject errorObj : fullListOfErrors){
            errorObj.setModeloTextView(modelosNameMap.get(errorObj.getModeloUid()));
        }
    }
    private void setNomePecasOfErrorObjects() {
        for(errorObject errorObj : fullListOfErrors){
            if(!errorObj.getEtapa().equals("planejamento")) {
                errorObj.setNomePecaTextView(pecasNameMap.get(errorObj.getTag()));
            }
        }
    }

    class errorObject{
        //Filterable properties
        String obraUid;
        String etapa;
        String tag;
        String modeloUid;

        //Others
        String etapa_detectada;
        String status;
        String comentarios;
        String erroUid;

        //toBuild View
        View view;
        String createdBy;
        String data;
        String hora;
        String nome_modelo;
        String nome_peca;
        String lastModifiedBy;
        String lastModifiedOn;
        String comentarios_solucao;
        StringBuilder erros = new StringBuilder();

        public errorObject(String obraUid, String etapa, String erroUid, HashMap<String,String> properties) {
            this.erroUid = erroUid;
            this.obraUid = obraUid;
            this.etapa = etapa;
            for(String property : properties.keySet()){
                switch (property){
                    case "comentarios":
                        this.comentarios = properties.get(property);
                        break;
                    case "createdBy":
                        this.createdBy = properties.get(property);
                        break;
                    case "creation":
                        String[] creation = Objects.requireNonNull(properties.get(property)).split(" ");
                        this.data = creation[0];
                        this.hora = creation[1];
                        break;
                    case "etapa_detectada":
                        this.etapa_detectada = properties.get(property);
                        break;
                    case "modelo":
                        String modeloUid = properties.get(property);
                        this.modeloUid = modeloUid;
                        modelosOfErrors.put(modeloUid, obraUid);
                        break;
                    case "status":
                        this.status = properties.get(property);
                        break;
                    case "tag":
                        String tag = properties.get(property);
                        this.tag = tag;
                        pecasOfErrors.put(tag, obraUid);
                        break;
                    case "lastModifiedBy":
                        this.lastModifiedBy = properties.get(property);
                        break;
                    case "lastModifiedOn":
                        this.lastModifiedOn = properties.get(property);
                        break;
                    case "comentarios_solucao":
                        this.comentarios_solucao = properties.get(property);
                        break;
                    case "uid":
                        break;
                    default:
                        erros.append(properties.get(property)).append("\n");
                }
            }
            generateView();
        }

        private void generateView(){
            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_error_item, listaLayout2 ,false);
            CardView statusCard = obj.findViewById(R.id.statusCard);
            RelativeLayout layoutSolucao = obj.findViewById(R.id.layoutSolucao);
            if(this.status.equals("fechado")) {
                layoutSolucao.setVisibility(View.VISIBLE);
                statusCard.setCardBackgroundColor(getColor(R.color.soft_green));
                TextView lastModifiedBy = obj.findViewById(R.id.lastModifiedBy);
                lastModifiedBy.setText(usersMap.get(this.lastModifiedBy));
                TextView lastModifiedOn = obj.findViewById(R.id.lastModifiedOn);
                lastModifiedOn.setText(this.lastModifiedOn);
                TextView comentarios_solucao = obj.findViewById(R.id.comentarios_solucao);
                comentarios_solucao.setText(this.comentarios_solucao);
            }
            TextView status = obj.findViewById(R.id.status);
            TextView data = obj.findViewById(R.id.data);
            TextView hora = obj.findViewById(R.id.hora);
            TextView nome_obra = obj.findViewById(R.id.nome_obra);
            TextView etapa = obj.findViewById(R.id.etapa);
            TextView nome_peca = obj.findViewById(R.id.nome_peca);
            TextView margin_peca = obj.findViewById(R.id.margin_peca);
            TextView tag = obj.findViewById(R.id.tag);
            TextView createdBy = obj.findViewById(R.id.createdBy);
            TextView etapa_detectada = obj.findViewById(R.id.etapa_detectada);
            TextView erros = obj.findViewById(R.id.erros);
            TextView comentarios = obj.findViewById(R.id.comentarios);
            RelativeLayout tagLayout = obj.findViewById(R.id.tagLayout);
            status.setText(this.status.toUpperCase());
            data.setText(this.data);
            hora.setText(this.hora);
            nome_obra.setText(obrasNameMap.get(this.obraUid));
            etapa.setText(etapasNameMap.get(this.etapa));
            if(this.etapa.equals("planejamento")){
                tagLayout.setVisibility(View.GONE);
                nome_peca.setVisibility(View.GONE);
                margin_peca.setVisibility(View.GONE);
            }else if(this.etapa.equals("cadastro")){
                nome_peca.setVisibility(View.GONE);
                margin_peca.setVisibility(View.GONE);
            }else{
                tag.setText(this.tag);
            }
            createdBy.setText(usersMap.get(this.createdBy));
            etapa_detectada.setText(etapasNameMap.get(this.etapa_detectada));
            erros.setText(this.erros.toString().trim());
            comentarios.setText(this.comentarios);
            obj.setOnClickListener(view -> {
                if(this.status.equals("fechado")){
                    errorDialog.showError(RelatorioDeErros.this, "Selecione uma Inconformidade em Aberto!");
                    return;
                }
                Intent intent = new Intent(RelatorioDeErros.this, SolucionarErro.class);
                intent.putExtra("obra", this.obraUid);
                intent.putExtra("etapa", this.etapa);
                intent.putExtra("erro", this.erroUid);
                solucionarErro.launch(intent);
            });
            this.view = obj;
            listaLayout2.addView(obj);
        }

        public void setModeloTextView(String nome_modelo) {
            View v = this.view;
            TextView viewNomeModelo = v.findViewById(R.id.nome_modelo);
            viewNomeModelo.setText(nome_modelo);
            setNome_modelo(nome_modelo);
        }

        public void setNomePecaTextView(String nome_peca) {
            View v = this.view;
            TextView viewNomeModelo = v.findViewById(R.id.nome_peca);
            viewNomeModelo.setText(nome_peca);
            setNome_peca(nome_modelo);
        }

        public String getObraUid() {
            return obraUid;
        }

        public void setObraUid(String obraUid) {
            this.obraUid = obraUid;
        }

        public String getEtapa() {
            return etapa;
        }

        public void setEtapa(String etapa) {
            this.etapa = etapa;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getModeloUid() {
            return modeloUid;
        }

        public String getStatus() {
            return status;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getNome_modelo() {
            return nome_modelo;
        }

        public void setNome_modelo(String nome_modelo) {
            this.nome_modelo = nome_modelo;
        }

        public void setNome_peca(String nome_peca) {
            this.nome_peca = nome_peca;
        }
    }
}