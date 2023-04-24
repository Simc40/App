package com.simc.simc40.moduloGerenciamento;

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
import com.simc.simc40.R;
import com.simc.simc40.accessLevel.AccessLevel;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.selecaoListas.SelecaoListaUsuarios;

public class GerenciamentoAcessos extends AppCompatActivity implements LayoutExceptionErrorList, AccessLevel {

    CardView permissions, submit;
    Usuario usuario;
    TextView goBack;
    TextView textViewNome, textViewEmail, textViewEmpresa, textViewAccessLevel, textViewMatricula, textViewTelefone;
    ImageView imageViewAppPermission, imageViewReportPermission, imageViewImgUrl;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    DuploClique duploClique = new DuploClique();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_acessos);

        goBack = findViewById(R.id.goBack);
        submit = findViewById(R.id.submitForm);
        permissions = findViewById(R.id.permissions);

        View.OnClickListener goBackListener = view ->  {
            if(duploClique.detectado()) return;
            finish();
        };

        goBack.setOnClickListener(goBackListener);

        modalAlertaDeErro = new ModalAlertaDeErro(GerenciamentoAcessos.this);
        modalDeCarregamento = new ModalDeCarregamento(GerenciamentoAcessos.this, modalAlertaDeErro);

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
                usuario = (Usuario) data.getSerializableExtra("result");
                fill_fields(usuario);
            } catch (LayoutException e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        }else{
            finish();
        }
    });

    void fill_fields(Usuario usuario) throws LayoutException {
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
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }
}