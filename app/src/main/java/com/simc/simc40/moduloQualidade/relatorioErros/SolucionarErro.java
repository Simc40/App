package com.simc.simc40.moduloQualidade.relatorioErros;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.simc.simc40.Images.DownloadImage;
import com.simc.simc40.Images.ImageActivity;
import com.simc.simc40.R;
import com.simc.simc40.checklist.Etapas;
import com.simc.simc40.classes.Elemento;
import com.simc.simc40.classes.Erro;
import com.simc.simc40.classes.Image;
import com.simc.simc40.classes.Peca;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ImageDialog;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.dialogs.QuestionDialog;
import com.simc.simc40.dialogs.SuccessDialog;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiPOST.ImageUploadSuccessCallback;
import com.simc.simc40.firebaseApiPOST.UploadErroPicture;
import com.simc.simc40.firebasePaths.FirebaseErrosPaths;
import com.simc.simc40.moduloGerenciamento.GerenciamentoElementos;
import com.simc.simc40.sharedPreferences.LocalStorage;
import com.simc.simc40.touchListener.TouchListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class SolucionarErro extends AppCompatActivity implements SharedPrefsExceptionErrorList, Etapas, LayoutExceptionErrorList, FirebaseErrosPaths {

    Usuario usuario;
    String database;
    Intent intent;
    CardView informacoesProjeto, submitForm;
    TextView goBack, etapaTextView, obraTextView, elementoTextView, tag, nome, item, data, autor;
    EditText comentario;
    ImageView picture;
    CheckBox lockPeca;
    DuploClique duploClique = new DuploClique();
    Peca peca;
    Erro erro;
    Elemento elemento;
    ConstraintLayout tagLayout, nomeLayout;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    SuccessDialog successDialog;
    QuestionDialog questionDialog;
    ImageDialog imageDialog;
    DatabaseReference reference;
    Image image;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorioerros_solucionar_erro);

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);
        successDialog = new SuccessDialog(this);
        imageDialog = new ImageDialog(this);
        questionDialog = new QuestionDialog(this);

        intent = getIntent();
        Object[] dependency = (Object[]) intent.getSerializableExtra("dependency");
        erro = (Erro) dependency[0];
        if(erro.getEtapa_detectada().equals(planejamentoKey)) elemento = (Elemento) dependency[1];
        else peca = (Peca) dependency[1];

        try{
            usuario = LocalStorage.getUsuario(this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        goBack = findViewById(R.id.goBack);
        etapaTextView = findViewById(R.id.etapa);
        obraTextView = findViewById(R.id.obra);
        elementoTextView = findViewById(R.id.elemento);
        informacoesProjeto = findViewById(R.id.informacoesProjeto);
        tagLayout = findViewById(R.id.tagLayout);
        nomeLayout = findViewById(R.id.nomeLayout);
        tag = findViewById(R.id.tag);
        nome = findViewById(R.id.nome);
        item = findViewById(R.id.item);
        data = findViewById(R.id.data);
        autor = findViewById(R.id.autor);
        comentario = findViewById(R.id.comentario);
        picture = findViewById(R.id.picture);
        lockPeca = findViewById(R.id.lockPeca);
        submitForm = findViewById(R.id.submitForm);

        etapaTextView.setText(prettyEtapas.get(erro.getEtapa_detectada()));
        item.setText(erro.getItem());

        if(erro.getEtapa_detectada().equals(planejamentoKey)){
            tagLayout.setVisibility(View.GONE);
            nomeLayout.setVisibility(View.GONE);
            if(elemento == null) return;
            obraTextView.setText(elemento.getObra().getNome_obra());
            elementoTextView.setText(elemento.getNome());
        } else{
            if(peca == null) return;
            tag.setText(peca.getTag());
            nome.setText((peca.getNome_peca() != null) ? peca.getNome_peca() : "Tag não Cadastrada");
            obraTextView.setText(peca.getObraObject().getNome_obra());
            elementoTextView.setText(peca.getElementoObject().getNome());
        }

        lockPeca.setEnabled(false);
        lockPeca.setChecked((erro.getStatus_locked().equals(Erro.statusIsLocked)));
        data.setText(erro.getCreation());
        autor.setText(erro.getCreatedBy());

        informacoesProjeto.setOnTouchListener(TouchListener.getTouch());
        informacoesProjeto.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            try{
                Intent ipIntent = new Intent(this, GerenciamentoElementos.class);
                ipIntent.putExtra("dependency", (erro.getEtapa_detectada().equals(planejamentoKey)) ? elemento : peca.getElementoObject());
                startActivity(ipIntent);
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        });

        picture.setOnTouchListener(TouchListener.getSinlgeImageInActivityTouch());
        picture.setOnClickListener(view -> {
            System.out.println("CLICK");
            if(duploClique.detectado()) return;
            if(image == null) takePicture.launch(new Intent(this, ImageActivity.class));
            else imageDialog.showImageDialog(image.getPath());
        });

        imageDialog.getImageView().setOnClickListener(view -> {
            questionDialog.getButton1().setOnClickListener(view1 -> questionDialog.endQuestionDialog());
            questionDialog.getButton2().setOnClickListener(view1 -> {
                questionDialog.endQuestionDialog();
                takePicture.launch(new Intent(this, ImageActivity.class));
                imageDialog.endImageDialog();
            });
            questionDialog.showQuestion("Trocar de foto?", "Cancelar", "Sim");
        });

        submitForm.setOnTouchListener(TouchListener.getTouch());

        submitForm.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            if(modalDeCarregamento.estaVisivel) return;
            modalDeCarregamento.mostrarModalDeCarregamento(UploadErroPicture.solucaoTicks + 3);
            try{
                if(comentario.getText().toString().trim().isEmpty()){
                    ErrorHandling.handleError(modalDeCarregamento, modalAlertaDeErro, "Comentário Vazio!", "Adicione algum comentário referente a Solução");
                    return;
                }
                if(!ErrorHandling.deviceIsConnected(this)) throw new FirebaseNetworkException("message");
                if(image != null) uploadImageOnStorage(erro);
                else uploadDataOnDatabase(erro.getUid(), null);
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        });
    }

    private void uploadDataOnDatabase(String uuid, String imgUrl){
        if(uuid == null) uuid = UUID.randomUUID().toString();
        HashMap<String, Object> firebaseObj = new HashMap<>();
        @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        firebaseObj.put(firebaseErrosPathsComentariosSolucao, comentario.getText().toString().trim());
        firebaseObj.put(firebaseErrosPathsLastModifiedBy, usuario.getUid());
        firebaseObj.put(firebaseErrosPathsLastModifiedOn, data);
        if(imgUrl != null) firebaseObj.put(firebaseErrosPathsImgUrlSolucao, imgUrl);
        firebaseObj.put(firebaseErrosPathsStatus, Erro.statusFechado);
//        if(erro.getEtapa_detectada().equals(planejamentoKey)) reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseErrosPathsFirstKey).child(erro.getUidObra()).child(erro.getUidElemento()).child(uuid);
        reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseErrosPathsFirstKey).child(erro.getObra()).child(erro.getPeca()).child(uuid);
        modalDeCarregamento.avancarPasso(); // 2
        reference.updateChildren(firebaseObj)
        .addOnSuccessListener(unused -> {
            modalDeCarregamento.passoFinal(); // 3
            modalDeCarregamento.fecharModal();
            Intent intent = new Intent();
            setResult(SolucionarErro.RESULT_OK, intent);
            finish();
        }).addOnFailureListener(e -> ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro));
    }

    private void uploadImageOnStorage(Erro erro){
        String uuid = erro.getUid();
        ImageUploadSuccessCallback imageUploadSuccessCallback = response -> {
            modalDeCarregamento.avancarPasso(); // 1
            if(response.isEmpty()) return;
            uploadDataOnDatabase(uuid, response);
        };
        UploadErroPicture.uploadSolucaoPictureOnFirebaseStorage(uuid, usuario, image, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, imageUploadSuccessCallback);
    }

    ActivityResultLauncher<Intent> takePicture = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                image = (Image) data.getSerializableExtra("result");
                DownloadImage.fromPath(picture, image.getPath());
            } catch (Exception e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        }
    });
}