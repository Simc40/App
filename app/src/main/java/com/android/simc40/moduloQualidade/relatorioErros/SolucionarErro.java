package com.android.simc40.moduloQualidade.relatorioErros;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.simc40.Images.DownloadImage;
import com.android.simc40.Images.ImageActivity;
import com.android.simc40.R;
import com.android.simc40.alerts.errorDialog;
import com.android.simc40.checklist.Etapas;
import com.android.simc40.classes.Checklist;
import com.android.simc40.classes.Cliente;
import com.android.simc40.classes.Elemento;
import com.android.simc40.classes.Erro;
import com.android.simc40.classes.Image;
import com.android.simc40.classes.Peca;
import com.android.simc40.classes.User;
import com.android.simc40.configuracaoUsuario.ConfiguracaoUsuario;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.dialogs.ImageDialog;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.dialogs.QuestionDialog;
import com.android.simc40.dialogs.SuccessDialog;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.errorHandling.QualidadeException;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.firebaseApiPOST.ImageUploadSuccessCallback;
import com.android.simc40.firebaseApiPOST.UploadErroPicture;
import com.android.simc40.firebasePaths.FirebaseErrosPaths;
import com.android.simc40.moduloGerenciamento.GerenciamentoElementos;
import com.android.simc40.selecaoListas.SelecaoListaClientes;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.android.simc40.touchListener.TouchListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SolucionarErro extends AppCompatActivity implements SharedPrefsExceptionErrorList, Etapas, LayoutExceptionErrorList, FirebaseErrosPaths {

    User user;
    String database;
    Intent intent;
    CardView informacoesProjeto, submitForm;
    TextView goBack, etapaTextView, obraTextView, elementoTextView, tag, nome, item, data, autor;
    EditText comentario;
    ImageView picture;
    CheckBox lockPeca;
    DoubleClick doubleClick = new DoubleClick();
    Peca peca;
    Erro erro;
    Elemento elemento;
    ConstraintLayout tagLayout, nomeLayout;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
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

        errorDialog = new ErrorDialog(this);
        loadingDialog = new LoadingDialog(this, errorDialog);
        successDialog = new SuccessDialog(this);
        imageDialog = new ImageDialog(this);
        questionDialog = new QuestionDialog(this);

        intent = getIntent();
        Object[] dependency = (Object[]) intent.getSerializableExtra("dependency");
        erro = (Erro) dependency[0];
        if(erro.getEtapaDetectada().equals(planejamentoKey)) elemento = (Elemento) dependency[1];
        else peca = (Peca) dependency[1];

        try{
            user = sharedPrefsDatabase.getUser(this, MODE_PRIVATE, loadingDialog, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = user.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
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

        etapaTextView.setText(prettyEtapas.get(erro.getEtapaDetectada()));
        item.setText(erro.getItem());

        if(erro.getEtapaDetectada().equals(planejamentoKey)){
            tagLayout.setVisibility(View.GONE);
            nomeLayout.setVisibility(View.GONE);
            if(elemento == null) return;
            obraTextView.setText(elemento.getObra().getNomeObra());
            elementoTextView.setText(elemento.getNome());
        } else{
            if(peca == null) return;
            tag.setText(peca.getTag());
            nome.setText((peca.getNomePeca() != null) ? peca.getNomePeca() : "Tag não Cadastrada");
            obraTextView.setText(peca.getObra().getNomeObra());
            elementoTextView.setText(peca.getElemento().getNome());
        }

        lockPeca.setEnabled(false);
        lockPeca.setChecked((erro.getStatusLocked().equals(Erro.statusIsLocked)));
        data.setText(erro.getCreation());
        autor.setText(erro.getCreatedBy());

        informacoesProjeto.setOnTouchListener(TouchListener.getTouch());
        informacoesProjeto.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            try{
                Intent ipIntent = new Intent(this, GerenciamentoElementos.class);
                ipIntent.putExtra("dependency", (erro.getEtapaDetectada().equals(planejamentoKey)) ? elemento : peca.getElemento());
                startActivity(ipIntent);
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        });

        picture.setOnTouchListener(TouchListener.getSinlgeImageInActivityTouch());
        picture.setOnClickListener(view -> {
            System.out.println("CLICK");
            if(doubleClick.detected()) return;
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
            if(doubleClick.detected()) return;
            if(loadingDialog.isVisible) return;
            loadingDialog.showLoadingDialog(UploadErroPicture.solucaoTicks + 3);
            try{
                if(comentario.getText().toString().trim().isEmpty()){
                    ErrorHandling.handleError(loadingDialog, errorDialog, "Comentário Vazio!", "Adicione algum comentário referente a Solução");
                    return;
                }
                if(!ErrorHandling.deviceIsConnected(this)) throw new FirebaseNetworkException("message");
                if(image != null) uploadImageOnStorage(erro);
                else uploadDataOnDatabase(erro.getUid(), null);
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        });
    }

    private void uploadDataOnDatabase(String uuid, String imgUrl){
        if(uuid == null) uuid = UUID.randomUUID().toString();
        HashMap<String, Object> firebaseObj = new HashMap<>();
        @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        firebaseObj.put(firebaseErrosPathsComentariosSolucao, comentario.getText().toString().trim());
        firebaseObj.put(firebaseErrosPathsLastModifiedBy, user.getUid());
        firebaseObj.put(firebaseErrosPathsLastModifiedOn, data);
        if(imgUrl != null) firebaseObj.put(firebaseErrosPathsImgUrlSolucao, imgUrl);
        firebaseObj.put(firebaseErrosPathsStatus, Erro.statusFechado);
        if(erro.getEtapaDetectada().equals(planejamentoKey)) reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseErrosPathsFirstKey).child(erro.getUidObra()).child(erro.getUidElemento()).child(uuid);
        else reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseErrosPathsFirstKey).child(erro.getUidObra()).child(erro.getTag()).child(uuid);
        loadingDialog.tick(); // 2
        reference.updateChildren(firebaseObj)
        .addOnSuccessListener(unused -> {
            loadingDialog.finalTick(); // 3
            loadingDialog.endLoadingDialog();
            Intent intent = new Intent();
            setResult(SolucionarErro.RESULT_OK, intent);
            finish();
        }).addOnFailureListener(e -> ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog));
    }

    private void uploadImageOnStorage(Erro erro){
        String uuid = erro.getUid();
        ImageUploadSuccessCallback imageUploadSuccessCallback = response -> {
            loadingDialog.tick(); // 1
            if(response.isEmpty()) return;
            uploadDataOnDatabase(uuid, response);
        };
        UploadErroPicture.uploadSolucaoPictureOnFirebaseStorage(uuid, user, image, this.getClass().getSimpleName(), loadingDialog, errorDialog, imageUploadSuccessCallback);
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
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        }
    });
}