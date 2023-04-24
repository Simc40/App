package com.simc.simc40.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.simc.simc40.R;
import com.simc.simc40.MainActivity;
import com.simc.simc40.accessLevel.AccessLevel;
import com.simc.simc40.classes.Usuario;
//import com.simc.simc40.components.DaggerFirebaseAppUserComponent;
//import com.simc.simc40.components.FirebaseAppUserComponent;
//import com.simc.simc40.components.DaggerFirebaseAppUserComponent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class LoginSplashScreen extends AppCompatActivity implements FirebaseUserPaths, FirebaseClientePaths, DefaultErrorMessage, FirebaseDatabaseExceptionErrorList, AccessLevel {

    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    DuploClique duploClique = new DuploClique(2000);
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_login_splash_screen);

        FirebaseUser usuarioAutenticadoNoFirebase = FirebaseAuth.getInstance().getCurrentUser();

        modalAlertaDeErro = new ModalAlertaDeErro(this);

        modalAlertaDeErro.getBotao().setOnClickListener(null);
        modalAlertaDeErro.getBotao().setOnClickListener(layoutBotaoAlertaDeErro -> {
            if(duploClique.detectado()) return;
            irParaPaginaDeLogin();
        });

        modalDeCarregamento = new ModalDeCarregamento(LoginSplashScreen.this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(4 + ApiSingleUser.ticks);

        if (usuarioAutenticadoNoFirebase == null) {
            irParaPaginaDeLogin();
            return;
        }
        modalDeCarregamento.avancarPasso(); // 1

        //if user is logged in --> Check Permission
        Sessao sessao = new Sessao(LoginSplashScreen.this);
        if(!sessao.existeInstanciaNoLocalStorage()) {
            irParaPaginaDeLogin();
            return;
        }

        modalDeCarregamento.avancarPasso(); // 2

        try{
            ApiSingleUserCallback apiSingleUserCallback = response -> {
                usuario = response;
                if(!usuario.getAccessLevel().equals(accessLevelUser)){
                    irParaHomePage();
                    return;
                }
                if(usuario.getAppPermission().equals("ativo")){
                    irParaHomePage();
                }
            };
            new ApiSingleUser(LoginSplashScreen.this, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiSingleUserCallback, sessao.getUidUsuarioDaSessao());
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalAlertaDeErro);
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void irParaPaginaDeLogin(){
        modalDeCarregamento.avancarPasso(); // 3
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            modalDeCarregamento.passoFinal(); // 4
            startActivity(new Intent(LoginSplashScreen.this, Login.class));
            finish();
        }, 1500);
    }

    private void irParaHomePage(){
        modalDeCarregamento.avancarPasso(); // 3
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            modalDeCarregamento.passoFinal(); // 4
            startActivity(new Intent(LoginSplashScreen.this, MainActivity.class));
            finish();
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modalAlertaDeErro.fecharModal();
        modalDeCarregamento.fecharModal();
    }
}