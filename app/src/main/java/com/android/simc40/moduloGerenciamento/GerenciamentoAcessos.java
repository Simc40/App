package com.android.simc40.moduloGerenciamento;

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
import com.android.simc40.R;
import com.android.simc40.accessLevel.AccessLevel;
import com.android.simc40.classes.User;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.loadingPage.LoadingPage;
import com.android.simc40.selecaoListas.SelecaoListaUsuarios;

public class GerenciamentoAcessos extends AppCompatActivity implements LayoutExceptionErrorList, AccessLevel {

    CardView permissions, submit;
    User user;
    TextView goBack;
    TextView textViewNome, textViewEmail, textViewEmpresa, textViewAccessLevel, textViewMatricula, textViewTelefone;
    ImageView imageViewAppPermission, imageViewReportPermission, imageViewImgUrl;
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    String contextException = "GerenciamentoAcessos";
    DoubleClick doubleClick = new DoubleClick();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_acessos);

        goBack = findViewById(R.id.goBack);
        submit = findViewById(R.id.submitForm);
        permissions = findViewById(R.id.permissions);

        View.OnClickListener goBackListener = view ->  {
            if(doubleClick.detected()) return;
            finish();
        };

        goBack.setOnClickListener(goBackListener);

        errorDialog = new ErrorDialog(GerenciamentoAcessos.this);
        loadingPage = new LoadingPage(GerenciamentoAcessos.this, errorDialog);

        textViewNome = findViewById(R.id.nome);
        textViewEmail = findViewById(R.id.email);
        textViewEmpresa = findViewById(R.id.empresa);
        textViewAccessLevel = findViewById(R.id.accessLevel);
        textViewMatricula = findViewById(R.id.matricula);
        textViewTelefone = findViewById(R.id.telefone);
        imageViewAppPermission = findViewById(R.id.appPermission);
        imageViewReportPermission = findViewById(R.id.reportPermission);
        imageViewImgUrl = findViewById(R.id.userImage);

        Intent intent = new Intent(GerenciamentoAcessos.this, SelecaoListaUsuarios.class);
        selectUserFromList.launch(intent);
    }

    ActivityResultLauncher<Intent> selectUserFromList = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                user = (User) data.getSerializableExtra("result");
                fill_fields(user);
            } catch (LayoutException e) {
                ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
            }
        }else{
            finish();
        }
    });

    void fill_fields(User user) throws LayoutException {
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
        }catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }
    }
}