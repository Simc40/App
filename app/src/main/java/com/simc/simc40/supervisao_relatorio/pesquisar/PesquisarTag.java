package com.simc.simc40.supervisao_relatorio.pesquisar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.simc.simc40.R;

public class PesquisarTag extends AppCompatActivity {

//    TextView goBack;
//    DatabaseReference reference;
//    Button read, clear, confirm;
//    String database, tag, obraUid, modeloUid;
//    DoubleClick doubleClick = new DoubleClick();
//    boolean processing = false;
//    LinearLayout tagHeader, tagItem;
//    readerConnectorUHF1128 readerConnectorUHF1128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supervisao_relatorio_pesquisar_tag);

//        goBack = findViewById(R.id.goBack);
//        confirm = findViewById(R.id.confirm);
//        //ReaderConnectorItems
//        read = findViewById(R.id.read);
//        clear = findViewById(R.id.clear);
//        tagItem = findViewById(R.id.tagItem);
//        tagHeader = findViewById(R.id.tagItemHeader);
//
//        database = sharedPrefsDatabase.getDatabase(PesquisarTag.this, MODE_PRIVATE);
//
//        readerConnectorUHF1128 = new readerConnectorUHF1128(PesquisarTag.this, tagItem, read, clear);
//        View headerXML = LayoutInflater.from(PesquisarTag.this).inflate(R.layout.custom_tag_layout_header, tagHeader ,false);
//        tagHeader.addView(headerXML);
//
//        confirm.setOnClickListener(view -> {
//            if(doubleClick.detected()) return;
//            if(moreThanOneOrNoneTag()) return;
//            if(processing) {
//                errorDialog.showError(PesquisarTag.this, "Aguarde o Processo Finalizar!");
//                return;
//            }
//            processing = true;
//            lookForTagOnDatabase();
//        });
//
//        goBack.setOnClickListener(view -> {
//            if(doubleClick.detected()) return;
//            onBackPressed();
//        });

    }

//    private void lookForTagOnDatabase(){
//        reference = FirebaseDatabase.getInstance(database).getReference().child("pecas");
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                findTag:
//                for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
//                    obraUid = snapshot.getKey();
//                    for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){
//                        if(dataSnapshot2.getKey().equals(readerConnectorUHF1128.countRegisteredTags().get(0))) {
//                            tag = dataSnapshot2.getKey();
//                            modeloUid = dataSnapshot2.child("modelo").getValue(String.class);
//                            break findTag;
//                        }
//                    }
//                }
//                if (tag == null){
//                    errorDialog.showError(PesquisarTag.this, "A tag não foi cadastrada no Sistema!");
//                    processing = false;
//                }else{
//                    Intent intent = new Intent(PesquisarTag.this, PesquisarResult.class);
//                    intent.putExtra("tag", tag);
//                    intent.putExtra("obra", obraUid);
//                    intent.putExtra("modelo", modeloUid);
//                    Toast.makeText(PesquisarTag.this, "Tag identificada com sucesso!", Toast.LENGTH_LONG).show();
//                    startActivity(intent);
//                    finish();
//                }
//
//                reference.removeEventListener (this);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                errorDialog.showError(PesquisarTag.this, "Erro! "+error.toString());
//                reference.removeEventListener (this);
//            }
//        });
//    }
//
//    private boolean moreThanOneOrNoneTag() {
//        if(readerConnectorUHF1128.countRegisteredTags().size() == 0){
//            Toast.makeText(PesquisarTag.this, "Selecione uma tag para continuar", Toast.LENGTH_LONG).show();
//            return true;
//        }
//        else if(readerConnectorUHF1128.countRegisteredTags().size() > 1){
//            Toast.makeText(PesquisarTag.this, "Selecione apenas uma tag para continuar", Toast.LENGTH_LONG).show();
//            return true;
//        }
//        return false;
//    }

}