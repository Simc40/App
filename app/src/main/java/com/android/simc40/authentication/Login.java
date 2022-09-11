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
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.firebaseApiGET.ApiSingleUser;
import com.android.simc40.firebaseApiGET.ApiSingleUserCallback;
import com.android.simc40.firebasePaths.FirebaseClientePaths;
import com.android.simc40.firebasePaths.FirebaseUserPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.android.simc40.home.Home;
import com.android.simc40.selecaoListas.SelecaoListaClientes;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;


public class Login extends AppCompatActivity implements FirebaseUserPaths, FirebaseClientePaths, DefaultErrorMessage, FirebaseDatabaseExceptionErrorList, AccessLevel {

    EditText emailForm;
    EditText passwordForm;
    String email, password, uid;
    FirebaseAuth firebaseAuth;
    TextView register;
    CardView submitForm;
    ImageView passwordVisibility;
    String contextException = "Login";
    DoubleClick doubleClick = new DoubleClick();
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_login);

        errorDialog = new ErrorDialog(Login.this);
        loadingPage = new LoadingPage(Login.this, errorDialog);

        emailForm = findViewById(R.id.emailForm);
        passwordForm = findViewById(R.id.passwordForm);
        passwordVisibility = findViewById(R.id.hideShowPassword);
        submitForm = findViewById(R.id.submitForm);
        register = findViewById(R.id.register);

        emailForm.setText("julioclopes32@gmail.com");
        passwordForm.setText("12345678");

        FirebaseAuth.getInstance().signOut();

        submitForm.setOnClickListener(view -> {
            if (doubleClick.detected()) return;
            email = emailForm.getText().toString().trim();
            password = passwordForm.getText().toString().trim();
            if (inputFieldsAreNotValid(email, password)) {
                return;
            }
            if (loadingPage.isVisible) {
                return;
            }
            loadingPage.showLoadingPage();

            //Start Firebase Auth
            firebaseAuth = FirebaseAuth.getInstance();
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

        register.setOnClickListener(v -> {
            if (doubleClick.detected()) return;
            startActivity(new Intent(Login.this, Register.class));
        });
    }

    private void loginUser(String email, String password) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    uid = FirebaseAuth.getInstance().getUid();
                    checkUserPermissionAndAccess(uid);
                } else {
                    ErrorHandling.handleError(contextException, task.getException(), loadingPage, errorDialog);
                }
            });
        } catch (Exception e) {
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }
    }

    void checkUserPermissionAndAccess(String uid){
        try{
            ApiSingleUserCallback apiSingleUserCallback = response -> {
                user = response;
                if (user.getAccessLevel().equals(accessLevelAdmin)) {
                    Intent intent = new Intent(Login.this, SelecaoListaClientes.class);
                    selectClientFromList.launch(intent);
                }else if(user.getAccessLevel().equals(accessLevelResponsable) || user.getAppPermission().equals("ativo")){
                    sharedPrefsDatabase.SaveUserOnSharedPreferences(Login.this, user, MODE_PRIVATE, loadingPage, errorDialog);
                    sessionManagement session = new sessionManagement(Login.this);
                    session.saveSession(user);
                    Toast.makeText(Login.this, "Login realizado com Sucesso!", Toast.LENGTH_LONG).show();
                    goToHomePage();
                }else{
                    throw new FirebaseAuthException("ERROR_ACCESS_DENIED", defaultErrorMessage);
                }
            };
            new ApiSingleUser(Login.this, contextException, loadingPage, errorDialog, apiSingleUserCallback, uid);
        }catch (Exception e){
            ErrorHandling.handleError(contextException, e, errorDialog);
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

                        sharedPrefsDatabase.SaveUserOnSharedPreferences(Login.this, user, MODE_PRIVATE, loadingPage, errorDialog);
                        sessionManagement session = new sessionManagement(Login.this);
                        session.saveSession(user);
                        Toast.makeText(Login.this, "Login realizado com Sucesso!", Toast.LENGTH_LONG).show();
                        goToHomePage();
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(Login.this, "É necessário selecionar o Cliente para fazer o Login!", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
                }
            });

    private void goToHomePage(){
        startActivity(new Intent(Login.this, Home.class));
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
            Log.e(contextException, e.getMessage());
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingPage.endLoadingPage();
        errorDialog.endErrorDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingPage.endLoadingPage();
        errorDialog.endErrorDialog();
    }
}