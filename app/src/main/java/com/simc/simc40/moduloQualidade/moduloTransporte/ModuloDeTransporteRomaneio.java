package com.simc.simc40.moduloQualidade.moduloTransporte;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simc.simc40.R;
import com.simc.simc40.checklist.Etapas;
import com.simc.simc40.classes.Peca;
import com.simc.simc40.classes.Romaneio;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.dialogs.QuestionDialog;
import com.simc.simc40.dialogs.SuccessDialog;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.errorHandling.QualidadeException;
import com.simc.simc40.errorHandling.QualidadeExceptionErrorList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseRomaneioPaths;
import com.simc.simc40.selecaoListas.SelecaoListaRomaneio;
import com.simc.simc40.sharedPreferences.LocalStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ModuloDeTransporteRomaneio extends AppCompatActivity implements Etapas, FirebaseRomaneioPaths, LayoutExceptionErrorList, QualidadeExceptionErrorList, SharedPrefsExceptionErrorList {

    Usuario usuario;
    String database;
    DuploClique duploClique = new DuploClique();
    CardView statusCard, status1, status2, status3;
    TextView textViewStatus, textViewNumCarga, textViewData, textViewObra, textViewTransportadora, textViewMotorista, textViewVeiculo;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    QuestionDialog questionDialog;
    SuccessDialog successDialog;
    LinearLayout header, listaLayout;
    TextView goBack;
    CardView card1, card2, card3;
    Romaneio romaneio;
    DatabaseReference reference;
    HashMap<String, Object> carga_romaneio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mtransporte_romaneio);

        goBack = findViewById(R.id.goBack);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);

        //Romaneio
        statusCard = findViewById(R.id.statusCard);
        status1 = findViewById(R.id.status1);
        status2 = findViewById(R.id.status2);
        status3 = findViewById(R.id.status3);
        textViewStatus = findViewById(R.id.status);
        textViewNumCarga = findViewById(R.id.numCarga);
        textViewData = findViewById(R.id.data);
        textViewObra = findViewById(R.id.obra);
        textViewTransportadora = findViewById(R.id.transportadora);
        textViewMotorista = findViewById(R.id.motorista);
        textViewVeiculo = findViewById(R.id.veiculo);

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);
        questionDialog = new QuestionDialog(this);
        successDialog = new SuccessDialog(this);
        successDialog.getButton1().setOnClickListener(view -> {
            successDialog.endSuccessDialog();
            reestartActivity();
        });

        try{
            usuario = LocalStorage.getUsuario(this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        card1.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            try{
                if(!romaneio.getStatus().equals(Romaneio.statusPlanejamento)) throw new QualidadeException(EXCEPTION_CARGA_PASSED);
                Intent cIntent = new Intent(this, Carga.class);
                cIntent.putExtra("dependency", romaneio);
                restartOnResult.launch(cIntent);
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        });

        card2.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            try{
                if(romaneio.getStatus().equals(Romaneio.statusPlanejamento)) throw new QualidadeException(EXCEPTION_CARGA_NOT_READY);
                if(romaneio.getStatus().equals(Romaneio.statusTransporte)) throw new QualidadeException(EXCEPTION_TRANSPORTE_PASSED);
                if(romaneio.getStatus().equals(Romaneio.statusFinalizado)) throw new QualidadeException(EXCEPTION_ROMANEIO_FINISHED);
                @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                questionDialog.getButton2().setOnClickListener(null);
                questionDialog.getButton2().setOnClickListener(view1 -> {
                    modalDeCarregamento.mostrarModalDeCarregamento(2);
                    carga_romaneio = new HashMap<>();
                    carga_romaneio.put(firebaseRomaneioPathDataTransporte, data);
                    carga_romaneio.put(firebaseRomaneioPathLastModifiedByKey, usuario.getUid());
                    carga_romaneio.put(firebaseRomaneioPathLastModifiedOnKey, data);
                    reference = FirebaseDatabase.getInstance(database).getReference();
                    modalDeCarregamento.avancarPasso(); // 1
                    reference.child(firebaseRomaneioPathFirstKey).child(romaneio.getUid()).updateChildren(carga_romaneio)
                            .addOnSuccessListener(unused -> {
                                modalDeCarregamento.passoFinal(); // 2
                                modalDeCarregamento.fecharModal();
                                successDialog.showSuccess("Status de Carga atualizado com sucesso!");
                            })
                            .addOnFailureListener(e -> ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro));
                });
                questionDialog.showQuestion("Confirmar Transporte de Carga?", "Não", "Sim");
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        });

        card3.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            try{
                if(romaneio.getStatus().equals(Romaneio.statusPlanejamento)) throw new QualidadeException(EXCEPTION_CARGA_NOT_READY);
                if(romaneio.getStatus().equals(Romaneio.statusCarga)) throw new QualidadeException(EXCEPTION_TRANSPORTE_NOT_READY);
                if(romaneio.getStatus().equals(Romaneio.statusFinalizado)) throw new QualidadeException(EXCEPTION_ROMANEIO_FINISHED);
                Intent dIntent = new Intent(this, Descarga.class);
                dIntent.putExtra("dependency", romaneio);
                restartOnResult.launch(dIntent);
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        });

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            onBackPressed();
        });

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item_header, header, false);
        TextView headerItem1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Destino";
        headerItem1.setText(Text1);
        TextView headerItem2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Etapa";
        headerItem2.setText(Text2);
        TextView headerItem3 = headerXML.findViewById(R.id.item3);
        String Text3 = "Nome";
        headerItem3.setText(Text3);
        TextView headerItem4 = headerXML.findViewById(R.id.item4);
        headerItem4.setVisibility(View.INVISIBLE);
        header.addView(headerXML);


        Intent intent = new Intent(this, SelecaoListaRomaneio.class);
        intent.putExtra("dependency", true);
        selectRomaneioFromList.launch(intent);

    }

    ActivityResultLauncher<Intent> selectRomaneioFromList = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                romaneio = (Romaneio) data.getSerializableExtra("result");
                setViews(romaneio);
            } catch (LayoutException e) {
                modalAlertaDeErro.getBotao().setOnClickListener(view -> {
                    modalAlertaDeErro.fecharModal();
                    onBackPressed();
                });
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        }else{
            finish();
        }
    });

    @SuppressLint("SetTextI18n")
    private void setViews(Romaneio romaneio){
        textViewStatus.setText(romaneio.getStatus());
        switch (romaneio.getStatus()) {
            case Romaneio.statusCarga:
                status1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                statusCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
                break;
            case Romaneio.statusTransporte:
                status1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                status2.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                statusCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.soft_orange));
                break;
            case Romaneio.statusFinalizado:
                status1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                status2.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                status3.setCardBackgroundColor(ContextCompat.getColor(this, R.color.greenButton));
                statusCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.soft_green));
                break;
        }
        textViewNumCarga.setText("Carga " + romaneio.getNumCarga());
        textViewData.setText(romaneio.getDataPrevisao());
        textViewObra.setText((romaneio.getObra() != null) ? romaneio.getObra().getNome_obra() : "");
        textViewTransportadora.setText((romaneio.getTransportadora() != null) ? romaneio.getTransportadora().getNome() : "");
        textViewMotorista.setText((romaneio.getMotorista() != null) ? romaneio.getMotorista().getNome() : "");
        textViewVeiculo.setText((romaneio.getVeiculo() != null) ? romaneio.getVeiculo().getModelo() + " - " + romaneio.getVeiculo().getPlaca() : "");

        for(Peca peca : romaneio.getPecas()){
            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item, listaLayout ,false);
            TextView item1 = obj.findViewById(R.id.item1);
            TextView item2 = obj.findViewById(R.id.item2);
            TextView item3 = obj.findViewById(R.id.item3);
            ImageView item4 = obj.findViewById(R.id.item4);
            item1.setText(peca.getObraObject().getNome_obra());
            item2.setText(prettyShortEtapas.get(peca.getEtapa_atual()));
            item3.setText(peca.getNome_peca());
            item4.setVisibility(View.INVISIBLE);
            item1.setMaxLines(1);
            item2.setMaxLines(1);
            item3.setMaxLines(1);
            listaLayout.addView(obj);
        }
    }

    ActivityResultLauncher<Intent> restartOnResult = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) reestartActivity();
    });

    public void reestartActivity(){
        this.recreate();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(duploClique.detectado()){
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
        successDialog.endSuccessDialog();
        questionDialog.endQuestionDialog();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
        successDialog.endSuccessDialog();
        questionDialog.endQuestionDialog();
    }
}