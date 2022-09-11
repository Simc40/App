package com.android.simc40.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.simc40.R;
import com.android.simc40.alerts.errorDialog;
import com.android.simc40.classes.User;
import com.android.simc40.doubleClick.DoubleClick;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    EditText nameForm, emailForm, passwordForm, phoneForm, registrationForm;
    String imgUrl = "";
    CardView addImage, submitForm;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    DoubleClick doubleClick = new DoubleClick();
    boolean formRunning = false;
    TextView goBack;
    ImageView eye;
    User user;
    String contextException = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_register);

//        eye = findViewById(R.id.hideShowPassword);
//        goBack = findViewById(R.id.goBack);
//        nameForm = findViewById(R.id.nameText);
//        emailForm = findViewById(R.id.emailText);
//        passwordForm = findViewById(R.id.passwordText);
//        phoneForm = findViewById(R.id.phoneText);
//        registrationForm = findViewById(R.id.registrationText);
//        submitForm = findViewById(R.id.submitForm);
//        addImage = findViewById(R.id.addImage);
//        progressBar = findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.INVISIBLE);
//
//        nameForm.setHint("Nome Completo");
//        emailForm.setHint("E-mail");
//        passwordForm.setHint("Senha");
//        phoneForm.setHint("Telefone");
//        registrationForm.setHint("Matrícula");
//
//        eye.setOnClickListener(view -> new showHidePassword(passwordForm));
//        goBack.setOnClickListener(view -> {
//            if(doubleClick.detected()) return;
//            onBackPressed();
//        });
//
//        submitForm.setOnClickListener(view -> {
//            if(doubleClick.detected()) return;
//            user = new user(
//                    emailForm.getText().toString().trim(),
//                    imgUrl,
//                    registrationForm.getText().toString().trim(),
//                    nameForm.getText().toString().trim(),
//                    phoneForm.getText().toString().trim(),
//                    passwordForm.getText().toString().trim());
//            if(inputFieldsAreNotValid(
//                    user.getNome(),
//                    user.getEmail(),
//                    user.getPassword(),
//                    user.getTelefone(),
//                    user.getMatricula())){
//                return;
//            }
//            if(formRunning){
//                return;
//            }
//            formRunning = true;
//            progressBar.setVisibility(View.VISIBLE);
//            fAuth = FirebaseAuth.getInstance();
//            try {
//                fAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                        assert firebaseUser != null;
//                        String uid = firebaseUser.getUid();
//                        user.setUid(uid);
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid);
//                        reference.setValue(user.getUsermap()).addOnCompleteListener(task1 -> goToLoginPage()).addOnFailureListener(e -> errorDialog.showError(Register.this, e.getMessage()));
//                        //myclass.UploadImageToFirebase("Usuário Criado com sucesso!", "Usuário Criado com sucesso!\nMas não foi possível carregar a imagem", true);
//                    }
//                }).addOnFailureListener(e -> {
//                    errorDialog.showError(Register.this, e.getMessage());
//                    formRunning = false;
//                    progressBar.setVisibility(View.INVISIBLE);
//                });
//            }catch (Exception e){
//                Log.d(contextException, e.getMessage());
//            }
//            formRunning = false;
//            progressBar.setVisibility(View.INVISIBLE);
//        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(doubleClick.detected()){
            return;
        }
        startActivity(new Intent(Register.this, Login.class));
        finish();
    }


    private void goToLoginPage(){
        startActivity(new Intent(Register.this, Login.class));
        finish();
    }

    private boolean inputFieldsAreNotValid(String name, String email, String password, String phone, String registration){
        if(name.isEmpty()){
            errorDialog.showError(this, "Preencha o nome!");
            return true;
        }else if(email.isEmpty()){
            errorDialog.showError(this, "Preencha a Email!");
            return true;
        }else if(password.isEmpty()){
            errorDialog.showError(this, "Preencha a senha!");
            return true;
        }else if(phone.isEmpty()){
            errorDialog.showError(this, "Preencha o telefone!");
            return true;
        }else if(registration.isEmpty()){
            errorDialog.showError(this, "Preencha a matrícula!");
            return true;
        }else if(password.length()<8){
            errorDialog.showError(this, "A senha deve ter ao menos 8 dígitos!");
            return true;
        }else if(name.length()<3){
            errorDialog.showError(this, "Preencha um nome válido!");
            return true;
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            errorDialog.showError(this, "Preencha um Email válido!");
            return true;
        }
        return false;
    }
}