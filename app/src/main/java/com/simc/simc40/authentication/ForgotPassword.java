package com.simc.simc40.authentication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simc.simc40.R;
import com.simc.simc40.activityStatus.ActivityStatus;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.dialogs.SuccessDialog;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    DuploClique duploClique = new DuploClique();
    TextView emailTextView, goBack;
    CardView submitForm;
    String email;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    SuccessDialog successDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        modalAlertaDeErro = new ModalAlertaDeErro(ForgotPassword.this);
        modalDeCarregamento = new ModalDeCarregamento(ForgotPassword.this, modalAlertaDeErro);
        successDialog = new SuccessDialog(ForgotPassword.this);

        emailTextView = findViewById(R.id.emailForm);
        submitForm = findViewById(R.id.submitForm);
        goBack = findViewById(R.id.goBack);

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            finish();
        });

        submitForm.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            if(modalDeCarregamento.estaVisivel) return;
            modalDeCarregamento.mostrarModalDeCarregamento(3);
            email = emailTextView.getText().toString().trim();
            if(email.equals("") || email.isEmpty() || email == null) {
                modalDeCarregamento.fecharModal();
                modalAlertaDeErro.showError("Email inválido", "Escreva um email válido para prosseguir.");
                return;
            }
            modalDeCarregamento.avancarPasso(); // 1
            if(!ErrorHandling.deviceIsConnected(this)){
                modalDeCarregamento.fecharModal();
                modalAlertaDeErro.showError("Erro de Conexão", "Verifique a sua conexão com a Internet. Ocorreu um erro de rede (como tempo limite, conexão interrompida ou host inacessível).");
                return;
            }
            modalDeCarregamento.avancarPasso(); // 2
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(task -> {
                modalDeCarregamento.passoFinal(); // 3
                modalDeCarregamento.fecharModal();
                successDialog.showSuccess("Email de Confirmação Enviado!", "Um link foi enviado para o email " + email + " verifique a sua caixa de spam para se certificar que o email foi recebido!");
            }).addOnFailureListener(e -> {
                if(ActivityStatus.activityIsRunning(this)){
                    modalDeCarregamento.fecharModal();
                    modalAlertaDeErro.showError("Não foi possível enviar o email de Confirmação", e.getMessage());
                }
            });
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
        successDialog.endSuccessDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
        successDialog.endSuccessDialog();
    }
}