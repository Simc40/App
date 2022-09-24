package com.android.simc40.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.simc40.R;
import com.android.simc40.accessLevel.AccessLevel;
import com.android.simc40.classes.Cliente;
import com.android.simc40.classes.User;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebaseApiGET.ApiSingleUser;
import com.android.simc40.firebaseApiGET.ApiSingleUserCallback;
import com.android.simc40.firebasePaths.FirebaseClientePaths;
import com.android.simc40.firebasePaths.FirebaseUserPaths;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.home.Home;
import com.android.simc40.selecaoListas.SelecaoListaClientes;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity implements FirebaseUserPaths, FirebaseClientePaths, DefaultErrorMessage, FirebaseDatabaseExceptionErrorList, AccessLevel {

    EditText emailForm;
    EditText passwordForm;
    String email, password, uid;
    FirebaseAuth firebaseAuth;
    TextView forgotPassword;
    CardView submitForm;
    ImageView passwordVisibility;
    DoubleClick doubleClick = new DoubleClick();
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_login);

        errorDialog = new ErrorDialog(Login.this);
        loadingDialog = new LoadingDialog(Login.this, errorDialog);

        emailForm = findViewById(R.id.emailForm);
        passwordForm = findViewById(R.id.passwordForm);
        passwordVisibility = findViewById(R.id.hideShowPassword);
        submitForm = findViewById(R.id.submitForm);
        forgotPassword = findViewById(R.id.forgotPassword);

        emailForm.setText("julioclopes32@gmail.com");
        passwordForm.setText("12345678");

        FirebaseAuth.getInstance().signOut();

        submitForm.setOnClickListener(view -> {
            if (doubleClick.detected()) return;
            email = emailForm.getText().toString().trim();
            password = passwordForm.getText().toString().trim();
            if (inputFieldsAreNotValid(email, password)) return;
            if (loadingDialog.isVisible) return;
            loadingDialog.showLoadingDialog(8 + ApiSingleUser.ticks);

            loadingDialog.tick(); // 1
            //Start Firebase Auth
            firebaseAuth = FirebaseAuth.getInstance();
            loadingDialog.tick(); // 2
            loginUser(email, password);
        });

        passwordVisibility.setOnClickListener(view -> {
            if (passwordForm.getTransformationMethod().getClass().getSimpleName().equals("PasswordTransformationMethod")) {
                passwordForm.setTransformationMethod(new SingleLineTransformationMethod());
                passwordVisibility.setImageResource(R.drawable.visibility_on);
            } else {
                passwordForm.setTransformationMethod(new PasswordTransformationMethod());
                passwordVisibility.setImageResource(R.drawable.visibility_off);
            }
            passwordForm.setSelection(passwordForm.getText().length());
        });

        forgotPassword.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(Login.this, ForgotPassword.class));
        });
    }

    private void loginUser(String email, String password) {
        try {
            loadingDialog.tick(); // 3
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    uid = FirebaseAuth.getInstance().getUid();
                    loadingDialog.tick(); // 4
                    checkUserPermissionAndAccess(uid);
                } else {
                    ErrorHandling.handleError(this.getClass().getSimpleName(), task.getException(), loadingDialog, errorDialog);
                }
            });
        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }
    }

    void checkUserPermissionAndAccess(String uid){
        try{
            loadingDialog.tick(); // 5
            ApiSingleUserCallback apiSingleUserCallback = response -> {
                loadingDialog.tick(); // 6
                user = response;
                if (user.getAccessLevel().equals(accessLevelAdmin)) {
                    loadingDialog.finalTick(); // 7
                    Intent intent = new Intent(this, SelecaoListaClientes.class);
                    selectClientFromList.launch(intent);
                }else if(user.getAccessLevel().equals(accessLevelResponsable) || user.getAppPermission().equals("ativo")){
                    sharedPrefsDatabase.SaveUserOnSharedPreferences(this, user, MODE_PRIVATE, loadingDialog, errorDialog);
                    loadingDialog.tick(); // 7
                    sessionManagement session = new sessionManagement(this);
                    session.saveSession(user);
                    loadingDialog.finalTick(); // 8
                    Toast.makeText(this, "Login realizado com Sucesso!", Toast.LENGTH_LONG).show();
                    goToHomePage();
                }else{
                    loadingDialog.finalTick(); // 7
                    throw new FirebaseAuthException("ERROR_ACCESS_DENIED", defaultErrorMessage);
                }
            };
            new ApiSingleUser(this, this.getClass().getSimpleName(), loadingDialog, errorDialog, apiSingleUserCallback, uid);
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, errorDialog);
            FirebaseAuth.getInstance().signOut();
        }
    }

    ActivityResultLauncher<Intent> selectClientFromList = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                try {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data == null) throw new Exception(defaultErrorMessage);
                        Cliente cliente = (Cliente) data.getSerializableExtra("result");

                        user.setCliente(cliente);

                        sharedPrefsDatabase.SaveUserOnSharedPreferences(this, user, MODE_PRIVATE, loadingDialog, errorDialog);
                        sessionManagement session = new sessionManagement(this);
                        session.saveSession(user);
                        Toast.makeText(this, "Login realizado com Sucesso!", Toast.LENGTH_LONG).show();
                        goToHomePage();
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(this, "É necessário selecionar o Cliente para fazer o Login!", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
                }
            });

    private void goToHomePage(){
        startActivity(new Intent(this, Home.class));
        finish();
    }

    private boolean inputFieldsAreNotValid(String email, String password){
        try {
            if (email.isEmpty()) {
                errorDialog.showError("Erro de Validação", "Preencha o Email");
                return true;
            } else if (password.isEmpty()) {
                errorDialog.showError("Erro de Validação", "Preencha a senha!");
                return true;
            } else if (password.length() < 8) {
                errorDialog.showError("Erro de Validação", "A senha deve ter ao menos 8 dígitos!");
                return true;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                errorDialog.showError("Erro de Validação", "Preencha um Email válido!");
                return true;
            }
        }catch (Exception e){
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
    }
}