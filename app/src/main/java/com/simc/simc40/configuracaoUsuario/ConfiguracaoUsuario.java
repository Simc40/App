package com.simc.simc40.configuracaoUsuario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simc.simc40.Images.DownloadImage;
import com.simc.simc40.Images.ImageActivity;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.firebaseApiPOST.ImageUploadSuccessCallback;
import com.simc.simc40.Images.IntentSelector;
import com.simc.simc40.Images.PermissionList;
import com.simc.simc40.firebaseApiPOST.UploadProfilePicture;
import com.simc.simc40.R;
import com.simc.simc40.accessLevel.AccessLevel;
import com.simc.simc40.authentication.Login;
import com.simc.simc40.authentication.Sessao;
import com.simc.simc40.classes.Image;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.errorHandling.PermissionExceptionList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.sharedPreferences.LocalStorage;
import com.simc.simc40.dialogs.SuccessDialog;
import com.google.firebase.auth.FirebaseAuth;

public class ConfiguracaoUsuario extends AppCompatActivity implements LayoutExceptionErrorList, SharedPrefsExceptionErrorList, PermissionExceptionList, AccessLevel, PermissionList {

    CardView permissions, submit;
    Usuario usuario;
    Image image;
    TextView goBack;
    TextView textViewNome, textViewEmail, textViewEmpresa, textViewAccessLevel, textViewMatricula, textViewTelefone;
    ImageView imageViewAppPermission, imageViewReportPermission, imageViewImgUrl;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    SuccessDialog successDialog;
    DuploClique duploClique = new DuploClique();
    IntentSelector intentSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracao_usuario);

        intentSelector = new IntentSelector(ConfiguracaoUsuario.this);
        successDialog = new SuccessDialog(ConfiguracaoUsuario.this);
        modalAlertaDeErro = new ModalAlertaDeErro(ConfiguracaoUsuario.this);
        modalDeCarregamento = new ModalDeCarregamento(ConfiguracaoUsuario.this, modalAlertaDeErro);

        goBack = findViewById(R.id.goBack);
        submit = findViewById(R.id.submitForm);
        permissions = findViewById(R.id.permissions);

        View.OnClickListener goBackListener = view ->  {
            if(duploClique.detectado()) return;
            finish();
        };

        goBack.setOnClickListener(goBackListener);

        textViewNome = findViewById(R.id.nome);
        textViewEmail = findViewById(R.id.email);
        textViewEmpresa = findViewById(R.id.empresa);
        textViewAccessLevel = findViewById(R.id.accessLevel);
        textViewMatricula = findViewById(R.id.matricula);
        textViewTelefone = findViewById(R.id.telefone);
        imageViewAppPermission = findViewById(R.id.appPermission);
        imageViewReportPermission = findViewById(R.id.reportPermission);
        imageViewImgUrl = findViewById(R.id.userImage);

        submit.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            takePicture.launch(new Intent(ConfiguracaoUsuario.this, ImageActivity.class));
        });

        try{
            usuario = LocalStorage.getUsuario(ConfiguracaoUsuario.this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            fill_fields(usuario);
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

    }

    ActivityResultLauncher<Intent> takePicture = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                image = (Image) data.getSerializableExtra("result");
                System.out.println(image);
                uploadImage(image);
            } catch (Exception e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        }
    });

    private void uploadImage(Image image){
        try {
            Usuario usuario = LocalStorage.getUsuario(ConfiguracaoUsuario.this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            modalDeCarregamento.mostrarModalDeCarregamento(3);
            ImageUploadSuccessCallback imageUploadSuccessCallback = response -> {
                FirebaseAuth.getInstance().signOut();
                Sessao session = new Sessao(ConfiguracaoUsuario.this);
                session.removerSessao();
                Intent i = new Intent(ConfiguracaoUsuario.this, Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            };
            UploadProfilePicture.uploadProfilePictureOnFirebaseStorage(usuario, image, this.getClass().getSimpleName(), successDialog, modalDeCarregamento, modalAlertaDeErro, imageUploadSuccessCallback);

        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    void fill_fields(Usuario usuario) {
        try{
            if(textViewNome != null && usuario.getNome() != null) textViewNome.setText(usuario.getNome());
            if(textViewEmail != null && usuario.getEmail() != null) textViewEmail.setText(usuario.getEmail());
            if(textViewEmpresa != null && usuario.getCliente() != null) textViewEmpresa.setText(usuario.getCliente().getNome());
            if(textViewAccessLevel != null && usuario.getAccessLevel() != null) textViewAccessLevel.setText(accessLevelMap.get(usuario.getAccessLevel()));
            if(textViewMatricula != null && usuario.getMatricula() != null) textViewMatricula.setText(usuario.getMatricula());
            if(textViewTelefone != null && usuario.getTelefone() != null) textViewTelefone.setText(usuario.getTelefone());

            if(usuario.getAccessLevel() != null && !usuario.getAccessLevel().equals(accessLevelUser)) permissions.setVisibility(View.GONE);
            if(imageViewAppPermission != null && usuario.getAppPermission() != null)
                if("ativo".equals(usuario.getAppPermission())) imageViewAppPermission.setImageResource(R.drawable.checked);
                else imageViewAppPermission.setImageResource(R.drawable.uncheck);

            if(imageViewReportPermission != null && usuario.getReportPermission() != null)
                if("ativo".equals(usuario.getReportPermission())) imageViewReportPermission.setImageResource(R.drawable.checked);
                else imageViewReportPermission.setImageResource(R.drawable.uncheck);

            if(imageViewImgUrl != null) DownloadImage.fromUrlLayoutGone(imageViewImgUrl, usuario.getImgUrl());
            modalDeCarregamento.fecharModal();
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
        successDialog.endSuccessDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
        successDialog.endSuccessDialog();
    }
}