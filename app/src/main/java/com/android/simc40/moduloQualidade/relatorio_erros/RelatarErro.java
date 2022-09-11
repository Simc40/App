package com.android.simc40.moduloQualidade.relatorio_erros;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.simc40.R;
import com.android.simc40.alerts.errorDialog;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class RelatarErro extends AppCompatActivity {
    //Get CheckList From Intent
    Intent intent;
    ArrayList<String> errorCheckList = new ArrayList<>();
    
    //Informações de Projeto HashMaps
    HashMap<String, String> usersMap = new HashMap<>();
    HashMap<String, String> etapasNameMap = new HashMap<>();
    HashMap<String, String> unidades = new HashMap<>();
    HashMap<String, String> modeloObject = new HashMap<>();
    HashMap<String, String> pecaObject = new HashMap<>();
    Set<String> firebaseUsers = new HashSet<>();
    HashMap<String, HashMap<String, String>> etapasMap = new HashMap<>();

    String tag, obraUid, database, modeloUid, etapa, userUid;

    //Layout Elements for Informações de Projeto
    LinearLayout layoutInfoProjeto;

    //Layout Elements for Error CheckList:
    LinearLayout layoutCheckList, listaDeCheckList;
    Spinner spinnerErro, spinnerEtapa;
    EditText comentarios;

    //Neutral Layout Elements
    TextView goBack, visualizarImagem;
    CardView adicionarImagem, submitForm;

    //HashMaps for Error CheckList:
    HashMap<String, List<String>> checklistErros = new HashMap<>();
    HashMap<String, List<View>> errorListByEtapaMap = new HashMap<>();

    //Others
    HashMap<String, String> submitFormObj;
    boolean processing = false;
    int numEtapa = 0;
    DoubleClick doubleClick = new DoubleClick();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorioerros_relatar_erro);

        intent = getIntent();
        modeloUid = intent.getStringExtra("modelo");
        tag = intent.getStringExtra("tag");
        obraUid = intent.getStringExtra("obra");
        etapa = intent.getStringExtra("etapa");
        errorCheckList = intent.getStringArrayListExtra("errorCheckList");

        layoutInfoProjeto = findViewById(R.id.layoutInfoProjeto);
        layoutCheckList = findViewById(R.id.layoutCheckList);
        listaDeCheckList = findViewById(R.id.listaDeCheckList);
        goBack = findViewById(R.id.goBack);
        spinnerErro = findViewById(R.id.erro);
        spinnerEtapa = findViewById(R.id.etapa);
        comentarios = findViewById(R.id.comentarios);
        visualizarImagem = findViewById(R.id.visualizarImagem);
        visualizarImagem.setVisibility(View.GONE);
        adicionarImagem = findViewById(R.id.adicionarImagem);
        submitForm = findViewById(R.id.submitForm);

        database = sharedPrefsDatabase.getDatabase(RelatarErro.this, MODE_PRIVATE);
        userUid = sharedPrefsDatabase.getUserUid(RelatarErro.this, MODE_PRIVATE);

        fillEtapasHashMap(etapa);
        getModeloFromDatabase();

        ifReceivedCheckListAddToList();

        goBack.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            onBackPressed();
        });

        List<String> listaEtapas = new ArrayList<>();
        listaEtapas.add("Selecione a etapa");
        listaEtapas.addAll(etapasNameMap.values());

        ArrayAdapter<String> adapterEtapas = new ArrayAdapter<>(RelatarErro.this, android.R.layout.simple_list_item_1, listaEtapas);
        adapterEtapas.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerEtapa.setAdapter(adapterEtapas);

        spinnerEtapa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedEtapa = spinnerEtapa.getSelectedItem().toString();
                String raw_etapa = getRawEtapa(selectedEtapa);
                if(selectedEtapa.equals("Selecione a etapa")){
                    spinnerErro.setAdapter(null);
                    layoutCheckList.removeAllViews();
                    return;
                }else{
                    if(errorListByEtapaMap.get(raw_etapa) != null) {
                        layoutCheckList.removeAllViews();
                        for (View v : Objects.requireNonNull(errorListByEtapaMap.get(raw_etapa))) {
                            layoutCheckList.addView(v);
                        }
                    }
                }
                ArrayAdapter<String> adapterErros = new ArrayAdapter<>(RelatarErro.this, android.R.layout.simple_list_item_1, checklistErros.get(selectedEtapa));
                adapterErros.setDropDownViewResource(android.R.layout.simple_list_item_1);
                spinnerErro.setAdapter(adapterErros);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerErro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedEtapa = spinnerEtapa.getSelectedItem().toString();
                String raw_etapa = getRawEtapa(selectedEtapa);
                if(selectedEtapa.equals("Selecione a etapa")) return;
                String selectedError = spinnerErro.getSelectedItem().toString();
                if(selectedError.equals("Selecionar Inconformidade")) return;
                if(selectedErrorIsInList(selectedError)) {
                    spinnerErro.setSelection(0);
                    errorDialog.showError(RelatarErro.this, "A inconformidade " + selectedError + " Já está na lista!");
                    return;
                }
                inflateItemErroOnLayout(raw_etapa, selectedError, layoutCheckList, R.layout.custom_2_list_item);
                spinnerErro.setSelection(0);
                listaDeCheckList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submitForm.setOnClickListener(view -> {
            String selectedEtapa = spinnerEtapa.getSelectedItem().toString();
            String raw_etapa = getRawEtapa(selectedEtapa);
            if(doubleClick.detected()) return;
            if (selectedEtapa.equals("Selecione a etapa")){
                errorDialog.showError(RelatarErro.this, "Selecione uma Etapa");
                return;
            }
            if(errorListByEtapaMap.get(raw_etapa) == null || Objects.requireNonNull(errorListByEtapaMap.get(raw_etapa)).size() == 1){
                errorDialog.showError(RelatarErro.this, "O checkList de Inconformidades está vazio!");
                return;
            }
            if(comentarios.getText().toString().isEmpty()){
                errorDialog.showError(RelatarErro.this, "Não há comentários");
                return;
            }
            if(processing) {
                errorDialog.showError(RelatarErro.this, "Operação está em andamento");
                return;
            }
            processing = true;
            @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
            submitFormObj = new HashMap<>();
            submitFormObj.put("comentarios", comentarios.getText().toString());
            submitFormObj.put("status", "aberto");
            submitFormObj.put("etapa_detectada", etapa);
            submitFormObj.put("creation", data);
            submitFormObj.put("createdBy", userUid);
            submitFormObj.put("modelo", modeloUid);
            int i = 1;
            for(View v : Objects.requireNonNull(errorListByEtapaMap.get(raw_etapa))){
                TextView item1 = v.findViewById(R.id.item1);
                if(item1.getText().toString().equals("checklist")) continue;
                submitFormObj.put(String.valueOf(i), item1.getText().toString());
                i++;
            }
            DatabaseReference reference;
            String uuid = UUID.randomUUID().toString();
            submitFormObj.put("uid", uuid);
            if (!Objects.requireNonNull(raw_etapa).equals("planejamento")) {
                submitFormObj.put("tag", tag);
            }
            reference =  FirebaseDatabase.getInstance(database).getReference().child("erros").child(obraUid).child(raw_etapa).child(uuid);
            reference.setValue(submitFormObj).addOnSuccessListener(unused -> {
                Intent intent = new Intent();
                Toast.makeText(RelatarErro.this, "Erro relatado com Sucesso!", Toast.LENGTH_LONG).show();
                setResult(RelatarErro.RESULT_OK, intent);
                processing = false;
                finish();
            }).addOnFailureListener(e -> {
                processing = false;
                errorDialog.showError(RelatarErro.this, e.getMessage());
            });
        });
    }

    private void getModeloFromDatabase(){
        DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference();
        reference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.child("checklist").getChildren()){
                    if (!Objects.equals(dataSnapshot1.getKey(), "history")) {
                        String etapa = dataSnapshot1.getKey();
                        List<String> errosDeEtapa = new ArrayList<>();
                        errosDeEtapa.add("Selecionar Inconformidade");
                        for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){
                            errosDeEtapa.add(dataSnapshot2.getValue(String.class));
                        }
                        checklistErros.put(etapasNameMap.get(etapa), errosDeEtapa);
                    }
                }
                if(modeloUid == null){
                    modeloUid = dataSnapshot.child("pecas").child(obraUid).child(tag).child("modelo").getValue(String.class);
                }
                for(DataSnapshot dataSnapshot1: dataSnapshot.child("modelos").child(obraUid).child(modeloUid).child("unidades").getChildren()){
                    String unidade = dataSnapshot1.getKey();
                    String value = dataSnapshot1.getValue(String.class);
                    unidades.put(unidade, value);
                }
                for(DataSnapshot dataSnapshot1: dataSnapshot.child("modelos").child(obraUid).child(modeloUid).getChildren()){
                    if(!Objects.equals(dataSnapshot1.getKey(), "unidades") && !Objects.equals(dataSnapshot1.getKey(), "history")){
                        String property = dataSnapshot1.getKey();
                        String value = dataSnapshot1.getValue(String.class);
                        modeloObject.put(property, value);
                    }
                }
                DataSnapshot snapshot2 = dataSnapshot.child("formas").child(Objects.requireNonNull(modeloObject.get("forma")));
                String forma = snapshot2.child("nome_forma").getValue(String.class)+" - "+snapshot2.child("b").getValue(String.class)+"x"+snapshot2.child("h").getValue(String.class);
                String tipo = dataSnapshot.child("tipos_modelo").child(Objects.requireNonNull(modeloObject.get("tipo"))).child("tipo").getValue(String.class);
                firebaseUsers.add(modeloObject.get("lastModifiedBy")); firebaseUsers.add(modeloObject.get("createdBy"));
                modeloObject.put("obra", dataSnapshot.child("obras").child(obraUid).child("nome_obra").getValue(String.class));
                modeloObject.put("forma", forma);
                modeloObject.put("tipo", tipo);

                if(!etapa.equals("planejamento") && !etapa.equals("cadastro")) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child("pecas").child(obraUid).child(tag).getChildren()) {
                        if (!Objects.equals(dataSnapshot1.getKey(), "etapas") && !Objects.equals(dataSnapshot1.getKey(), "history")) {
                            String property = dataSnapshot1.getKey();
                            String value = dataSnapshot1.getValue(String.class);
                            pecaObject.put(property, value);
                        }
                    }
                    firebaseUsers.add(pecaObject.get("lastModifiedBy"));
                    pecaObject.put("etapa_atual", etapasNameMap.get(pecaObject.get("etapa_atual")));
                    pecaObject.put("modelo", modeloObject.get("nome_modelo"));
                    pecaObject.put("obra", dataSnapshot.child("obras").child(obraUid).child("nome_obra").getValue(String.class));

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child("pecas").child(obraUid).child(tag).child("etapas").getChildren()) {
                        String etapa = dataSnapshot1.getKey();
                        HashMap<String, String> etapaMap = new HashMap<>();
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            String property = dataSnapshot2.getKey();
                            String value = dataSnapshot2.getValue(String.class);
                            etapaMap.put(property, value);
                            if (Objects.equals(property, "createdBy")) {
                                firebaseUsers.add(value);
                            }
                        }
                        etapasMap.put(etapa, etapaMap);
                    }
                }

                getFirebaseUsers();
                reference.removeEventListener (this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                errorDialog.showError(RelatarErro.this, "Erro! "+databaseError);
                reference.removeEventListener (this);
            }
        });
    }

    private void getFirebaseUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("usuarios");
        reference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(String userUid : firebaseUsers){
                    usersMap.put(userUid, dataSnapshot.child(userUid).child("nome").getValue(String.class));
                }
                modeloObject.put("lastModifiedBy", usersMap.get(modeloObject.get("lastModifiedBy")));
                modeloObject.put("createdBy", usersMap.get(modeloObject.get("createdBy")));

                if(!etapa.equals("planejamento") && !etapa.equals("cadastro")) {
                    pecaObject.put("lastModifiedBy", usersMap.get(pecaObject.get("lastModifiedBy")));
                    for (String etapa : etapasMap.keySet()) {
                        HashMap<String, String> etapaMap = etapasMap.get(etapa);
                        if (etapaMap != null) {
                            etapaMap.put("createdBy", usersMap.get(etapaMap.get("createdBy")));
                        }
                    }
                }


                System.out.println("modeloObject: " + modeloObject);
                System.out.println("pecaObject: " + pecaObject);
                System.out.println("etapasMap: " + etapasMap);
                reference.removeEventListener (this);

                if(!etapa.equals("planejamento") && !etapa.equals("cadastro")) {
                    gerarRelatorioPeca();
                }else{
                    gerarRelatorioModelo();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                errorDialog.showError(RelatarErro.this, "Erro! "+databaseError);
                reference.removeEventListener (this);
            }
        });
    }

    private void gerarRelatorioModelo() {
        if(etapa.equals("cadastro")){
            inflateItemOnLayout("Tag", tag, layoutInfoProjeto, R.layout.custom_3_list_item_start);

        }
        String[] modeloProperties = {"nome_modelo", "obra", "numMax", "numPecas", "tipo", "b", "h", "c", "fckdesf", "fckic", "volume", "peso", "taxaaco", "forma", "lastModifiedBy", "lastModifiedOn"};
        for(String property: modeloProperties){
            inflateItemOnLayout(property, modeloObject.get(property), layoutInfoProjeto, R.layout.custom_3_list_item_start);
        }
    }

    private void gerarRelatorioPeca() {
        String[] PecaProperties = {"tag", "nome_peca", "etapa_atual"};
        for(String property: PecaProperties){
            inflateItemOnLayout(property, pecaObject.get(property), layoutInfoProjeto, R.layout.custom_3_list_item_start);
        }

        String[] modeloProperties = {"nome_modelo", "obra", "numMax", "numPecas", "tipo", "b", "h", "c", "fckdesf", "fckic", "volume", "peso", "taxaaco", "forma", "lastModifiedBy", "lastModifiedOn"};
        for(String property: modeloProperties){
            inflateItemOnLayout(property, modeloObject.get(property), layoutInfoProjeto, R.layout.custom_3_list_item_start);
        }
    }


    private void ifReceivedCheckListAddToList() {
        inflateHeadersErroOnLayout(layoutCheckList);
        if(errorCheckList != null) {
            for(String item : errorCheckList){
                inflateItemErroOnLayout(etapa, item, layoutCheckList, R.layout.custom_2_list_item);
            }
        }
    }

    private boolean selectedErrorIsInList(String selectedError) {
        List<View> errorList = errorListByEtapaMap.get(getRawEtapa(spinnerEtapa.getSelectedItem().toString()));
        if (errorList != null) {
            for(View v : errorList){
                TextView item1 = v.findViewById(R.id.item1);
                if(item1.getText().toString().equals(selectedError)) return true;
            }
        }
        return false;
    }

    private String getRawEtapa(String selectedEtapa) {
        for(String raw_etapa : etapasNameMap.keySet()){
            if(Objects.equals(etapasNameMap.get(raw_etapa), selectedEtapa)){
                return raw_etapa;
            }
        }
        return null;
    }

    private void fillEtapasHashMap(String etapa) {
        switch (etapa) {
            case "planejamento":
                numEtapa = 0;
                break;
            case "cadastro":
                numEtapa = 1;
                break;
            case "armacao":
                numEtapa = 2;
                break;
            case "forma":
                numEtapa = 3;
                break;
            case "armacaoForma":
                numEtapa = 4;
                break;
            case "concretagem":
                numEtapa = 5;
                break;
            case "liberacao":
                numEtapa = 6;
                break;
            case "carga":
                numEtapa = 7;
                break;
            case "descarga":
                numEtapa = 8;
                break;
            case "montagem":
                numEtapa = 9;
                break;
        }
        if(numEtapa > -1){
            etapasNameMap.put("planejamento", "Planejamento");
        }if(numEtapa > 0){
            etapasNameMap.put("cadastro", "Produção/Cadastro");
        }if(numEtapa > 1){
            etapasNameMap.put("armacao", "Produção/Armacao");
        }if(numEtapa > 2){
            etapasNameMap.put("forma", "Produção/Forma");
        }if(numEtapa > 3){
            etapasNameMap.put("armacaoForma", "Produção/Armacao com Forma");
        }if(numEtapa > 4){
            etapasNameMap.put("concretagem", "Produção/Concretagem");
        }if(numEtapa > 5){
            etapasNameMap.put("liberacao", "Produção/Liberacao Final");
        }if(numEtapa > 6){
            etapasNameMap.put("carga", "Transporte/Carga");
        }if(numEtapa > 7){
            etapasNameMap.put("descarga", "Transporte/Descarga");
        }if(numEtapa > 8){
            etapasNameMap.put("montagem", "Montagem");
        }
        for(String etapa_raw: etapasNameMap.keySet()){
            errorListByEtapaMap.put(etapa_raw, new ArrayList<>());
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(doubleClick.detected()){
            return;
        }
        finish();
    }

    private void inflateItemOnLayout(String propriedade, String valor, LinearLayout layout, int customLayout){
        View obj = LayoutInflater.from(getApplicationContext()).inflate(customLayout , layout ,false);
        TextView item1 = obj.findViewById(R.id.item1);
        TextView item2 = obj.findViewById(R.id.item2);
        ImageView item3 = obj.findViewById(R.id.item3);
        item3.setVisibility(View.GONE);
        if(propriedade!= null) {
            switch (propriedade) {
                case "b":
                case "h":
                    valor = valor + unidades.get("secao");
                    break;
                case "c":
                    valor = valor + unidades.get("comprimento");
                    break;
                case "fckdesf":
                case "fckic":
                    valor = valor + unidades.get("tensao");
                    break;
                case "peso":
                    valor = valor + unidades.get("massa");
                    break;
                case "volume":
                    valor = valor + unidades.get("volume");
                    break;
                case "numMax":
                    propriedade = "Peças Planejadas";
                    break;
                case "numPecas":
                    propriedade = "Peças Cadastradas";
                    break;
                case "taxaaco":
                    propriedade = "taxa de aço";
                    valor = valor + unidades.get("massa");
                    break;
                case "createdBy":
                case "creation":
                case "lastModifiedOn":
                case "lastModifiedBy":
                    propriedade = "Ultima Modificação";
                    break;
            }
        }
        if (propriedade != null) {
            propriedade = propriedade.substring(0, 1).toUpperCase() + propriedade.substring(1).toLowerCase();
        }
        item1.setText(propriedade);
        item2.setText(valor);
        layout.addView(obj);
    }

    private void inflateItemErroOnLayout(String raw_etapa, String item, LinearLayout layout, int customLayout){
        View obj = LayoutInflater.from(getApplicationContext()).inflate(customLayout , layout ,false);
        TextView item1 = obj.findViewById(R.id.item1);
        ImageView item2 = obj.findViewById(R.id.item2);
        if(customLayout != R.layout.custom_2_list_item_header) {
            item2.setImageResource(R.drawable.uncheck);
            item2.setOnClickListener(view -> {
                if (doubleClick.detected()) return;
                Objects.requireNonNull(errorListByEtapaMap.get(raw_etapa)).remove(obj);
                layout.removeView(obj);
            });
        }
        item1.setText(item);
        Objects.requireNonNull(errorListByEtapaMap.get(raw_etapa)).add(obj);
        layout.addView(obj);
    }

    private void inflateHeadersErroOnLayout(LinearLayout layout){
        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_header , layout ,false);
        TextView item1 = obj.findViewById(R.id.item1);
        String text = "checklist";
        item1.setText(text);
        for(List<View> listv : errorListByEtapaMap.values()){
            listv.add(obj);
        }
    }
}