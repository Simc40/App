package com.simc.simc40.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simc.simc40.R;
import com.simc.simc40.MainActivity;
import com.simc.simc40.accessLevel.AccessLevel;
import com.simc.simc40.classes.Cliente;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiSingleUser;
import com.simc.simc40.firebaseApiGET.ApiSingleUserCallback;
import com.simc.simc40.firebasePaths.FirebaseClientePaths;
import com.simc.simc40.firebasePaths.FirebaseUserPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.selecaoListas.SelecaoListaClientes;
import com.simc.simc40.sharedPreferences.LocalStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;


public class Login extends AppCompatActivity implements FirebaseUserPaths, FirebaseClientePaths, DefaultErrorMessage, FirebaseDatabaseExceptionErrorList, AccessLevel {

    EditText emailForm;
    EditText passwordForm;
    String email, password, uid;
    FirebaseAuth firebaseAuth;
    TextView forgotPassword;
    CardView submitForm;
    ImageView passwordVisibility;
    DuploClique duploClique = new DuploClique();
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    Usuario usuario;
    private MenuItem mConnectMenuItem;
    private MenuItem mDisconnectMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_login);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//        setSupportActionBar(toolbar);

        modalAlertaDeErro = new ModalAlertaDeErro(Login.this);
        modalDeCarregamento = new ModalDeCarregamento(Login.this, modalAlertaDeErro);

        emailForm = findViewById(R.id.emailForm);
        passwordForm = findViewById(R.id.passwordForm);
        passwordVisibility = findViewById(R.id.hideShowPassword);
        submitForm = findViewById(R.id.submitForm);
        forgotPassword = findViewById(R.id.forgotPassword);

        emailForm.setText("julioclopes32@gmail.com");
        passwordForm.setText("12345678");

        FirebaseAuth.getInstance().signOut();

        submitForm.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            email = emailForm.getText().toString().trim();
            password = passwordForm.getText().toString().trim();
            if (inputFieldsAreNotValid(email, password)) return;
            if (modalDeCarregamento.estaVisivel) return;
            modalDeCarregamento.mostrarModalDeCarregamento(8 + ApiSingleUser.ticks);

            modalDeCarregamento.avancarPasso(); // 1
            //Start Firebase Auth
            firebaseAuth = FirebaseAuth.getInstance();
            modalDeCarregamento.avancarPasso(); // 2
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
            if(duploClique.detectado()) return;
            startActivity(new Intent(Login.this, ForgotPassword.class));
        });
    }

    private void loginUser(String email, String password) {
        try {
            modalDeCarregamento.avancarPasso(); // 3
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    uid = FirebaseAuth.getInstance().getUid();
                    modalDeCarregamento.avancarPasso(); // 4
                    checkUserPermissionAndAccess(uid);
                } else {
                    ErrorHandling.handleError(this.getClass().getSimpleName(), task.getException(), modalDeCarregamento, modalAlertaDeErro);
                }
            });
        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    void checkUserPermissionAndAccess(String uid){
        try{
            modalDeCarregamento.avancarPasso(); // 5
            ApiSingleUserCallback apiSingleUserCallback = response -> {
                modalDeCarregamento.avancarPasso(); // 6
                usuario = response;
                if (usuario.getAccessLevel().equals(accessLevelAdmin)) {
                    modalDeCarregamento.passoFinal(); // 7
                    Intent intent = new Intent(this, SelecaoListaClientes.class);
                    selectClientFromList.launch(intent);
                }else if(usuario.getAccessLevel().equals(accessLevelResponsable) || usuario.getAppPermission().equals("ativo")){
                    LocalStorage.salvarUsuario(this, usuario, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
                    modalDeCarregamento.avancarPasso(); // 7
                    Sessao session = new Sessao(this);
                    session.salvarSessao(usuario);
                    modalDeCarregamento.passoFinal(); // 8
                    Toast.makeText(this, "Login realizado com Sucesso!", Toast.LENGTH_LONG).show();
                    goToHomePage();
                }else{
                    modalDeCarregamento.passoFinal(); // 7
                    throw new FirebaseAuthException("ERROR_ACCESS_DENIED", defaultErrorMessage);
                }
            };
            new ApiSingleUser(this, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiSingleUserCallback, uid);
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalAlertaDeErro);
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

                        usuario.setCliente(cliente);

                        LocalStorage.salvarUsuario(this, usuario, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
                        Sessao session = new Sessao(this);
                        session.salvarSessao(usuario);
                        Toast.makeText(this, "Login realizado com Sucesso!", Toast.LENGTH_LONG).show();
                        goToHomePage();
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(this, "É necessário selecionar o Cliente para fazer o Login!", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                }
            });

    private void goToHomePage(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private boolean inputFieldsAreNotValid(String email, String password){
        try {
            if (email.isEmpty()) {
                modalAlertaDeErro.showError("Erro de Validação", "Preencha o Email");
                return true;
            } else if (password.isEmpty()) {
                modalAlertaDeErro.showError("Erro de Validação", "Preencha a senha!");
                return true;
            } else if (password.length() < 8) {
                modalAlertaDeErro.showError("Erro de Validação", "A senha deve ter ao menos 8 dígitos!");
                return true;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                modalAlertaDeErro.showError("Erro de Validação", "Preencha um Email válido!");
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
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
    }

    @Override
    protected void onStop() {
        super.onStop();
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reader_manager, menu);

        mConnectMenuItem = menu.findItem(R.id.connect_reader_menu_item);
        mDisconnectMenuItem= menu.findItem(R.id.disconnect_reader_menu_item);

        return true;
    }
}