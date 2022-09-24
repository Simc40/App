package com.android.simc40.moduloQualidade.modulo_transporte;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.simc40.R;
import com.android.simc40.configuracaoLeitor.readerConnectorUHF1128;
import com.android.simc40.doubleClick.DoubleClick;

import java.util.HashMap;

public class Descarga extends AppCompatActivity {

    HashMap<String, String> etapasNameMap = new HashMap<>();
    HashMap<String, String> checklistOrder = new HashMap<>();
    HashMap<ImageView, String> checklistStatus = new HashMap<>();
    TextView goBack, textViewObra, textViewVeiculo, peca;
    String database, userUid, obraUid, obraNome, pecaNome, tag, etapa = "descarga", novaEtapa = "montagem";
    String transportadoraUid, veiculoUid, veiculoNome;
    HashMap<String, Object> firebaseObj;
    Button read, clear;
    CardView informacoesProjeto, reportarErro, submitForm;
    DoubleClick doubleClick = new DoubleClick();
    boolean processing = false;
    LinearLayout tagHeader, tagItem, checklistHeader, checkList;
    readerConnectorUHF1128 readerConnectorUHF1128;
    String contextException = "Descarga";
    Context context = Descarga.this;
    Activity activity = Descarga.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mtransporte_descarga);

//        textViewObra = findViewById(R.id.obra);
//        peca = findViewById(R.id.peca);
//        goBack = findViewById(R.id.goBack);
//        submitForm = findViewById(R.id.submitForm);
//        checklistHeader = findViewById(R.id.checklistHeader);
//        checkList = findViewById(R.id.checkList);
//        reportarErro = findViewById(R.id.reportarErro);
//        informacoesProjeto = findViewById(R.id.informacoesProjeto);
//        textViewVeiculo = findViewById(R.id.textViewVeiculo);
//        //ReaderConnectorItems
//        read = findViewById(R.id.read);
//        clear = findViewById(R.id.clear);
//        tagItem = findViewById(R.id.tagItem);
//        tagHeader = findViewById(R.id.tagItemHeader);
//
//        Intent intent = new Intent(context, SelecaoListaObras.class);
//        selectObraFromList.launch(intent);
//
//        fillEtapasCustomNamesMap();
//        userUid = sharedPrefsDatabase.getUserUid(context, MODE_PRIVATE);
//        database = sharedPrefsDatabase.getDatabase(context, MODE_PRIVATE);
//        if(database == null){
//            Log.e(contextException, "Null Database");
//        }
//        readerConnectorUHF1128 = new readerConnectorUHF1128(activity, tagItem, read, clear);
//        View tagHeaderXML = LayoutInflater.from(context).inflate(R.layout.custom_tag_layout_header, tagHeader, false);
//        tagHeader.addView(tagHeaderXML);
//
//        tagItem.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> detectChangesOnTagLayout());
//
//        View checklistHeaderXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_header, checklistHeader, false);
//        TextView item1 = checklistHeaderXML.findViewById(R.id.item1);
//        String Text1 = "CheckList";
//        item1.setText(Text1);
//        TextView item2 = checklistHeaderXML.findViewById(R.id.item2);
//        String Text2 = "Marcar Todos como: ";
//        item2.setText(Text2);
//        ImageView item3 = checklistHeaderXML.findViewById(R.id.item3);
//        item3.setImageResource(R.drawable.checked);
//        item3.setOnClickListener(view -> {
//            for (ImageView image : checklistStatus.keySet()) {
//                image.setImageResource(R.drawable.checked);
//                image.setTag(R.drawable.checked);
//            }
//        });
//        item2.setText(Text2);
//        checklistHeader.addView(checklistHeaderXML);
//
//        goBack.setOnClickListener(view -> {
//            if (doubleClick.detected()) return;
//            onBackPressed();
//        });
//
//        informacoesProjeto.setOnClickListener(view -> {
//            try {
//                if (doubleClick.detected()) return;
//                if (moreThanOneOrNoneTag()) return;
//                String tag = readerConnectorUHF1128.countRegisteredTags().get(0);
//                DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference().child("pecas").child(obraUid).child(tag);
//                reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (!dataSnapshot.exists()) {
//                            Toast.makeText(context, "A tag " + tag + " não possui registro na obra " + obraNome, Toast.LENGTH_LONG).show();
//                            return;
//                        }
//                        Intent intent1 = new Intent(context, SelecaoListaInformacoesDeProjeto.class);
//                        intent1.putExtra("tag", tag);
//                        intent1.putExtra("obra", obraUid);
//                        intent1.putExtra("obraNome", obraNome);
//                        intent1.putExtra("etapa", etapa);
//                        startActivity(intent1);
//
//                        reference.removeEventListener(this);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        errorDialog.showError(context, "Erro! " + databaseError);
//                        reference.removeEventListener(this);
//                    }
//                });
//            }catch (Exception e){
//                Log.e(contextException, e.getMessage());
//            }
//        });
//
//        reportarErro.setOnClickListener(view -> {
//            try {
//                if (doubleClick.detected()) return;
//                if (moreThanOneOrNoneTag()) return;
//                String tag = readerConnectorUHF1128.countRegisteredTags().get(0);
//                DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference().child("pecas").child(obraUid).child(tag);
//                reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (!dataSnapshot.exists()) {
//                            Toast.makeText(context, "A tag " + tag + " não possui registro na obra " + obraNome, Toast.LENGTH_LONG).show();
//                            return;
//                        }
//                        Intent intent4 = new Intent(context, RelatarErro.class);
//                        intent4.putExtra("tag", tag);
//                        intent4.putExtra("obra", obraUid);
//                        intent4.putExtra("obraNome", obraNome);
//                        intent4.putExtra("etapa", etapa);
//                        ArrayList<String> errorCheckList = new ArrayList<>();
//                        for (ImageView image : checklistStatus.keySet()) {
//                            if (image.getTag().equals(R.drawable.uncheck)) {
//                                errorCheckList.add(checklistStatus.get(image));
//                            }
//                        }
//                        intent4.putExtra("errorCheckList", errorCheckList);
//                        relatarErro.launch(intent4);
//
//                        reference.removeEventListener(this);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        errorDialog.showError(context, "Erro! " + databaseError);
//                        reference.removeEventListener(this);
//                    }
//                });
//            }catch (Exception e){
//                Log.e(contextException, e.getMessage());
//            }
//        });
//
//        getCheckList();
//
//        submitForm.setOnClickListener(view -> {
//            if (doubleClick.detected()) return;
//            if (processing) {
//                errorDialog.showError(context, "Aguarde o Processo Finalizar");
//                return;
//            }
//            if (moreThanOneOrNoneTag()) return;
//            if (tag == null) {
//                errorDialog.showError(context, "A tag não está na etapa " + etapasNameMap.get(etapa) + " ou não possui Registro");
//                return;
//            }
//            for (ImageView image : checklistStatus.keySet()) {
//                if (image.getTag().equals(R.drawable.uncheck) | image.getTag().equals(R.drawable.empty_box)) {
//                    errorDialog.showError(context, "É necessário marcar todos os itens do Checklist corretamente!");
//                    return;
//                }
//            }
//            if(veiculoUid == null || veiculoUid.isEmpty()){
//                errorDialog.showError(context, "Selecione um veículo");
//                return;
//            }
//            processing = true;
//            try {
//                firebaseObj = new HashMap<>();
//                @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
//                firebaseObj.put("etapa_atual", novaEtapa);
//                firebaseObj.put("lastModifiedBy", userUid);
//                firebaseObj.put("lastModifiedOn", data);
//                Map<String, Object> etapa_atual = new HashMap<>();
//                etapa_atual.put("creation", data);
//                etapa_atual.put("createdBy", userUid);
//                etapa_atual.put("transportadora", transportadoraUid);
//                etapa_atual.put("veiculo", veiculoUid);
//                for (String ordem : checklistOrder.keySet()) {
//                    etapa_atual.put(ordem, checklistOrder.get(ordem));
//                }
//                DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference();
//                String uuid = UUID.randomUUID().toString();
//                reference.child("database_version").setValue(uuid).addOnCompleteListener(task -> {
//                    reference.child("pecas").child(obraUid).child(tag).updateChildren(firebaseObj).addOnSuccessListener(unused -> Log.i(contextException, "SubmitForm step 1"));
//                    reference.child("pecas").child(obraUid).child(tag).child("etapas").child(etapa).updateChildren(etapa_atual).addOnSuccessListener(unused -> {
//                        Log.i(contextException, "SubmitForm finished successfully");
//                        Toast.makeText(context, "Processo de " + etapasNameMap.get(etapa) + " finalizado com Sucesso!", Toast.LENGTH_LONG).show();
//                        finish();
//                    });
//                });
//            }catch (Exception e){
//                Log.e(contextException, e.getMessage());
//                processing = false;
//            }
//        });
//    }
//
//    public void detectChangesOnTagLayout() {
//        System.out.println("DETECT CHANGES ON LAYOUT");
//        if (readerConnectorUHF1128.countRegisteredTags().size() == 0) {
//            tag = null;
//            String text = "Selecione uma Tag";
//            peca.setText(text);
//            return;
//        } else if (readerConnectorUHF1128.countRegisteredTags().size() > 1) {
//            tag = null;
//            String text = "Mais de uma Tag Selecionada";
//            peca.setText(text);
//            return;
//        }
//        try {
//            tag = readerConnectorUHF1128.countRegisteredTags().get(0);
//            DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference().child("pecas").child(obraUid).child(tag);
//            reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (!dataSnapshot.exists()) {
//                        String text = "Tag sem Registro";
//                        peca.setText(text);
//                        Toast.makeText(context, "A tag " + tag + " não possui registro na obra " + obraNome, Toast.LENGTH_LONG).show();
//                        tag = null;
//                        return;
//                    }
//                    String etapa_atual = dataSnapshot.child("etapa_atual").getValue(String.class);
//                    if (!Objects.equals(etapa_atual, etapa)) {
//                        Toast.makeText(context, "A tag " + tag + " está na etapa " + etapasNameMap.get(etapa_atual), Toast.LENGTH_LONG).show();
//                        tag = null;
//                        return;
//                    }
//                    pecaNome = dataSnapshot.child("nome_peca").getValue(String.class);
//                    peca.setText(pecaNome);
//                    transportadoraUid = dataSnapshot.child("etapas").child("carga").child("transportadora").getValue(String.class);
//                    veiculoUid = dataSnapshot.child("etapas").child("carga").child("veiculo").getValue(String.class);
//                    getNomeVeiculo();
//                    reference.removeEventListener(this);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    errorDialog.showError(context, "Erro! " + databaseError);
//                    reference.removeEventListener(this);
//                }
//            });
//        }catch (Exception e){
//            Log.e(contextException, e.getMessage());
//        }
    }
//
//    private void getNomeVeiculo() {
//        try {
//            DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference().child("transportadoras").child(transportadoraUid).child("veiculos").child(veiculoUid);
//            reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (!dataSnapshot.exists()) {
//                        String text = "Veiculo sem Registro";
//                        peca.setText(text);
//                        textViewVeiculo.setText(text);
//                        veiculoNome = null;
//                        errorDialog.showError(context, "O registro do veículo foi removido do Sistema");
//                        tag = null;
//                        return;
//                    }
//                    String placa = dataSnapshot.child("placa").getValue(String.class);
//                    String modelo = dataSnapshot.child("modelo").getValue(String.class);
//                    veiculoNome = placa + "\n" + modelo;
//                    textViewVeiculo.setText(veiculoNome);
//                    reference.removeEventListener(this);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    errorDialog.showError(context, "Erro! " + databaseError);
//                    reference.removeEventListener(this);
//                }
//            });
//        }catch (Exception e){
//            Log.e(contextException, e.getMessage());
//        }
//    }
//
//    private boolean moreThanOneOrNoneTag() {
//        if (readerConnectorUHF1128.countRegisteredTags().size() == 0) {
//            Toast.makeText(context, "Selecione uma tag para Continuar", Toast.LENGTH_LONG).show();
//            return true;
//        } else if (readerConnectorUHF1128.countRegisteredTags().size() > 1) {
//            Toast.makeText(context, "Selecione apenas uma tag para Continuar", Toast.LENGTH_LONG).show();
//            return true;
//        }
//        return false;
//    }
//
//    ActivityResultLauncher<Intent> selectObraFromList = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    Intent data = result.getData();
//                    assert data != null;
//                    obraUid = data.getStringExtra("result1");
//                    obraNome = data.getStringExtra("result2");
//                    textViewObra.setText(obraNome);
//                } else {
//                    finish();
//                }
//            });
//
//    ActivityResultLauncher<Intent> relatarErro = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    finish();
//                }
//            });
//
//
//    private void getCheckList() {
//        try {
//            DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference().child("checklist").child(etapa);
//            reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                        String ordem = dataSnapshot1.getKey();
//                        String checklistItem = dataSnapshot1.getValue(String.class);
//                        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_ordered, checkList, false);
//                        TextView item1 = obj.findViewById(R.id.item1);
//                        TextView item2 = obj.findViewById(R.id.item2);
//                        ImageView item3 = obj.findViewById(R.id.item3);
//                        item1.setText(ordem);
//                        item2.setText(checklistItem);
//                        item3.setImageResource(R.drawable.empty_box);
//                        item3.setTag(R.drawable.empty_box);
//                        checklistStatus.put(item3, checklistItem);
//                        checklistOrder.put(ordem, checklistItem);
//                        onClickListener(item3);
//                        checkList.addView(obj);
//                    }
//                    reference.removeEventListener(this);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    errorDialog.showError(context, "Erro! " + databaseError);
//                    reference.removeEventListener(this);
//                }
//            });
//        }catch (Exception e){
//            Log.e(contextException, e.getMessage());
//        }
//    }
//
//    public void onClickListener(final ImageView Image) {
//        View.OnClickListener onClick = v -> {
//            if ((Integer) Image.getTag() == R.drawable.empty_box) {
//                Image.setImageResource(R.drawable.checked);
//                Image.setTag(R.drawable.checked);
//            } else if ((Integer) Image.getTag() == R.drawable.checked) {
//                Image.setImageResource(R.drawable.uncheck);
//                Image.setTag(R.drawable.uncheck);
//            } else if ((Integer) Image.getTag() == R.drawable.uncheck) {
//                Image.setImageResource(R.drawable.checked);
//                Image.setTag(R.drawable.checked);
//            }
//        };
//        Image.setOnClickListener(onClick);
//    }
//
//    private void fillEtapasCustomNamesMap() {
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
//    }

}