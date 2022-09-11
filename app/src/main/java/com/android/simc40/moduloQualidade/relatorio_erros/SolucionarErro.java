package com.android.simc40.moduloQualidade.relatorio_erros;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.simc40.R;
import com.android.simc40.alerts.errorDialog;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SolucionarErro extends AppCompatActivity {

    Intent intent;
    TextView visualizarImagem;
    EditText comentarios;
    CardView adicionarImagem, submitForm;
    String obraUid, etapa, erroUid, userUid, database;
    boolean processing = false;
    DoubleClick doubleClick = new DoubleClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorioerros_solucionar_erro);

        intent = getIntent();
        obraUid = intent.getStringExtra("obra");
        etapa = intent.getStringExtra("etapa");
        erroUid = intent.getStringExtra("erro");

        visualizarImagem = findViewById(R.id.visualizarImagem);
        visualizarImagem.setVisibility(View.GONE);
        comentarios = findViewById(R.id.comentarios);
        adicionarImagem = findViewById(R.id.adicionarImagem);
        submitForm = findViewById(R.id.submitForm);

        userUid = sharedPrefsDatabase.getUserUid(SolucionarErro.this, MODE_PRIVATE);
        database = sharedPrefsDatabase.getDatabase(SolucionarErro.this, MODE_PRIVATE);

        submitForm.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            if(comentarios.getText().toString().trim().isEmpty()){
                errorDialog.showError(SolucionarErro.this, "Adicione algum comentário referente a Solução");
                return;
            }
            if (processing) {
                errorDialog.showError(SolucionarErro.this, "Aguarde o processo Finalizar");
                return;
            }
            processing = true;
            submitForm();

        });
    }

    private void submitForm() {
        DatabaseReference reference = FirebaseDatabase.getInstance(database).getReference().child("erros").child(obraUid).child(etapa).child(erroUid);
        @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastModifiedBy", userUid);
        updates.put("lastModifiedOn", data);
        updates.put("comentarios_solucao", comentarios.getText().toString().trim());
        updates.put("status", "fechado");
        reference.updateChildren(updates).addOnCompleteListener(task -> {
            Toast.makeText(SolucionarErro.this, "Inconformidade solucionada com sucesso!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult (SolucionarErro.RESULT_OK, intent);
            finish();
        }).addOnFailureListener(e -> {
            errorDialog.showError(SolucionarErro.this, e.toString());
            processing = false;
        });
    }
}