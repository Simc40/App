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

import com.android.simc40.Images.DisplayLoadedImage;
import com.android.simc40.Images.DownloadImage;
import com.android.simc40.Images.ImageUploadSuccessCallback;
import com.android.simc40.Images.IntentSelector;
import com.android.simc40.Images.PermissionList;
import com.android.simc40.Images.UploadPicture;
import com.android.simc40.R;
import com.android.simc40.accessLevel.AccessLevel;
import com.android.simc40.authentication.Login;
import com.android.simc40.authentication.sessionManagement;
import com.android.simc40.classes.Image;
import com.android.simc40.classes.User;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.errorHandling.PermissionException;
import com.android.simc40.errorHandling.PermissionExceptionList;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.loadingPage.LoadingPage;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.android.simc40.successDialog.SuccessDialog;
import com.google.firebase.auth.FirebaseAuth;

public class ConfiguracaoUsuario extends AppCompatActivity implements LayoutExceptionErrorList, SharedPrefsExceptionErrorList, PermissionExceptionList, AccessLevel, PermissionList {

    CardView permissions, submit;
    User user;
    Image image;
    TextView goBack;
    TextView textViewNome, textViewEmail, textViewEmpresa, textViewAccessLevel, textViewMatricula, textViewTelefone;
    ImageView imageViewAppPermission, imageViewReportPermission, imageViewImgUrl;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    SuccessDialog successDialog;
    String contextException = "ConfiguracaoUsuario";
    DoubleClick doubleClick = new DoubleClick();
    IntentSelector intentSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracao_usuario);

        intentSelector = new IntentSelector(ConfiguracaoUsuario.this);
        successDialog = new SuccessDialog(ConfiguracaoUsuario.this);
        errorDialog = new ErrorDialog(ConfiguracaoUsuario.this);
        loadingPage = new LoadingPage(ConfiguracaoUsuario.this, errorDialog);

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
            if(PermissionsDenied()) return;
            loadPicture.launch(IntentSelector.getIntent());
            System.out.println(image);
        });

        try{
            loadingPage.showLoadingPage();
            user = sharedPrefsDatabase.getUser(ConfiguracaoUsuario.this, MODE_PRIVATE, loadingPage, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);

            fill_fields(user);
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }

    }

    boolean PermissionsDenied(){
        intentSelector.checkPermissions();
        if(intentSelector.cameraPermissionDenied || intentSelector.galleryPermissionDenied){
            requestPermissionLauncher.launch(permissionList);
            return true;
        }
        return false;
    }

    ActivityResultLauncher<String[]> requestPermissionLauncher =
    registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
        try{
            for(String permission : permissionList){
                if(permissions.get(permission) == null) throw new PermissionException();
                if (!permissions.get(permission) && permission.equals(cameraPermission)) throw new PermissionException(EXCEPTION_CAMERA_PERMISSION_DENIED);
                else if (!permissions.get(permission) && permission.equals(galleryPermission)) throw new PermissionException(EXCEPTION_GALLERY_PERMISSION_DENIED);
                else if(!permissions.get(permission)) throw new PermissionException();
            }
            successDialog.showPermissionSuccess();
        }catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }
    });


    ActivityResultLauncher<Intent> loadPicture = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                image = new Image(ConfiguracaoUsuario.this, data, contextException, errorDialog);
                Intent intent = new Intent(ConfiguracaoUsuario.this, DisplayLoadedImage.class);
                intent.putExtra("dependency", image);
                this.uploadPictureOnResult.launch(intent);
            } catch (Exception e) {
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }
        }
    });

    ActivityResultLauncher<Intent> uploadPictureOnResult = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try{
                User user = sharedPrefsDatabase.getUser(ConfiguracaoUsuario.this, MODE_PRIVATE, loadingPage, errorDialog);
                if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
                loadingPage.setTimeLimitMillisseconds(20000);
                loadingPage.showLoadingPage();
                ImageUploadSuccessCallback imageUploadSuccessCallback = response -> {
                    FirebaseAuth.getInstance().signOut();
                    sessionManagement session = new sessionManagement(ConfiguracaoUsuario.this);
                    session.removeSession();
                    Intent i = new Intent(ConfiguracaoUsuario.this, Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                };
                UploadPicture.uploadProfilePictureOnFirebaseStorage(user, image, contextException, successDialog, loadingPage, errorDialog, imageUploadSuccessCallback);

            }catch (Exception e){
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }

        }
    });

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
            loadingPage.endLoadingPage();
        }catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingPage.endLoadingPage();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingPage.endLoadingPage();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
    }
}