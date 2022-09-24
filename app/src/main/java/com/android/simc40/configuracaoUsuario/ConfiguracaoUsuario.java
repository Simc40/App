package com.android.simc40.configuracaoUsuario;

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

import com.android.simc40.Images.DownloadImage;
import com.android.simc40.Images.ImageActivity;
import com.android.simc40.firebaseApiPOST.ImageUploadSuccessCallback;
import com.android.simc40.Images.IntentSelector;
import com.android.simc40.Images.PermissionList;
import com.android.simc40.firebaseApiPOST.UploadProfilePicture;
import com.android.simc40.R;
import com.android.simc40.accessLevel.AccessLevel;
import com.android.simc40.authentication.Login;
import com.android.simc40.authentication.sessionManagement;
import com.android.simc40.classes.Image;
import com.android.simc40.classes.User;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.errorHandling.PermissionExceptionList;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.android.simc40.dialogs.SuccessDialog;
import com.google.firebase.auth.FirebaseAuth;

public class ConfiguracaoUsuario extends AppCompatActivity implements LayoutExceptionErrorList, SharedPrefsExceptionErrorList, PermissionExceptionList, AccessLevel, PermissionList {

    CardView permissions, submit;
    User user;
    Image image;
    TextView goBack;
    TextView textViewNome, textViewEmail, textViewEmpresa, textViewAccessLevel, textViewMatricula, textViewTelefone;
    ImageView imageViewAppPermission, imageViewReportPermission, imageViewImgUrl;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    SuccessDialog successDialog;
    DoubleClick doubleClick = new DoubleClick();
    IntentSelector intentSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracao_usuario);

        intentSelector = new IntentSelector(ConfiguracaoUsuario.this);
        successDialog = new SuccessDialog(ConfiguracaoUsuario.this);
        errorDialog = new ErrorDialog(ConfiguracaoUsuario.this);
        loadingDialog = new LoadingDialog(ConfiguracaoUsuario.this, errorDialog);

        goBack = findViewById(R.id.goBack);
        submit = findViewById(R.id.submitForm);
        permissions = findViewById(R.id.permissions);

        View.OnClickListener goBackListener = view ->  {
            if(doubleClick.detected()) return;
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
            if(doubleClick.detected()) return;
            takePicture.launch(new Intent(ConfiguracaoUsuario.this, ImageActivity.class));
        });

        try{
            user = sharedPrefsDatabase.getUser(ConfiguracaoUsuario.this, MODE_PRIVATE, loadingDialog, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            fill_fields(user);
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
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
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        }
    });

    private void uploadImage(Image image){
        try {
            User user = sharedPrefsDatabase.getUser(ConfiguracaoUsuario.this, MODE_PRIVATE, loadingDialog, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            loadingDialog.showLoadingDialog(3);
            ImageUploadSuccessCallback imageUploadSuccessCallback = response -> {
                FirebaseAuth.getInstance().signOut();
                sessionManagement session = new sessionManagement(ConfiguracaoUsuario.this);
                session.removeSession();
                Intent i = new Intent(ConfiguracaoUsuario.this, Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            };
            UploadProfilePicture.uploadProfilePictureOnFirebaseStorage(user, image, this.getClass().getSimpleName(), successDialog, loadingDialog, errorDialog, imageUploadSuccessCallback);

        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }
    }

    void fill_fields(User user) {
        try{
            if(textViewNome != null && user.getNome() != null) textViewNome.setText(user.getNome());
            if(textViewEmail != null && user.getEmail() != null) textViewEmail.setText(user.getEmail());
            if(textViewEmpresa != null && user.getCliente() != null) textViewEmpresa.setText(user.getCliente().getNome());
            if(textViewAccessLevel != null && user.getAccessLevel() != null) textViewAccessLevel.setText(accessLevelMap.get(user.getAccessLevel()));
            if(textViewMatricula != null && user.getMatricula() != null) textViewMatricula.setText(user.getMatricula());
            if(textViewTelefone != null && user.getTelefone() != null) textViewTelefone.setText(user.getTelefone());

            if(user.getAccessLevel() != null && !user.getAccessLevel().equals(accessLevelUser)) permissions.setVisibility(View.GONE);
            if(imageViewAppPermission != null && user.getAppPermission() != null)
                if("ativo".equals(user.getAppPermission())) imageViewAppPermission.setImageResource(R.drawable.checked);
                else imageViewAppPermission.setImageResource(R.drawable.uncheck);

            if(imageViewReportPermission != null && user.getReportPermission() != null)
                if("ativo".equals(user.getReportPermission())) imageViewReportPermission.setImageResource(R.drawable.checked);
                else imageViewReportPermission.setImageResource(R.drawable.uncheck);

            if(imageViewImgUrl != null) DownloadImage.fromUrlLayoutGone(imageViewImgUrl, user.getImgUrl());
            loadingDialog.endLoadingDialog();
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
    }
}