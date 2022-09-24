package com.android.simc40.authentication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.simc40.R;
import com.android.simc40.activityStatus.ActivityStatus;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.dialogs.SuccessDialog;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    DoubleClick doubleClick = new DoubleClick();
    TextView emailTextView, goBack;
    CardView submitForm;
    String email;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    SuccessDialog successDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        errorDialog = new ErrorDialog(ForgotPassword.this);
        loadingDialog = new LoadingDialog(ForgotPassword.this, errorDialog);
        successDialog = new SuccessDialog(ForgotPassword.this);

        emailTextView = findViewById(R.id.emailForm);
        submitForm = findViewById(R.id.submitForm);
        goBack = findViewById(R.id.goBack);

        goBack.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            finish();
        });

        submitForm.setOnClickListener(view -> {
            if (doubleClick.detected()) return;
            if(loadingDialog.isVisible) return;
            loadingDialog.showLoadingDialog(3);
            email = emailTextView.getText().toString().trim();
            if(email.equals("") || email.isEmpty() || email == null) {
                loadingDialog.endLoadingDialog();
                errorDialog.showError("Email inválido", "Escreva um email válido para prosseguir.");
                return;
            }
            loadingDialog.tick(); // 1
            if(!ErrorHandling.deviceIsConnected(this)){
                loadingDialog.endLoadingDialog();
                errorDialog.showError("Erro de Conexão", "Verifique a sua conexão com a Internet. Ocorreu um erro de rede (como tempo limite, conexão interrompida ou host inacessível).");
                return;
            }
            loadingDialog.tick(); // 2
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(task -> {
                loadingDialog.finalTick(); // 3
                loadingDialog.endLoadingDialog();
                successDialog.showSuccess("Email de Confirmação Enviado!", "Um link foi enviado para o email " + email + " verifique a sua caixa de spam para se certificar que o email foi recebido!");
            }).addOnFailureListener(e -> {
                if(ActivityStatus.activityIsRunning(this)){
                    loadingDialog.endLoadingDialog();
                    errorDialog.showError("Não foi possível enviar o email de Confirmação", e.getMessage());
                }
            });
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
    }
}