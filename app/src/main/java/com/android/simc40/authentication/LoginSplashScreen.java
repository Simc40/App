package com.android.simc40.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.android.simc40.R;
import com.android.simc40.accessLevel.AccessLevel;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class LoginSplashScreen extends AppCompatActivity implements FirebaseUserPaths, FirebaseClientePaths, DefaultErrorMessage, FirebaseDatabaseExceptionErrorList, AccessLevel {

    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    DoubleClick doubleClick = new DoubleClick(2000);
    String userUid;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_login_splash_screen);

        errorDialog = new ErrorDialog(LoginSplashScreen.this);

        errorDialog.getButton().setOnClickListener(null);
        errorDialog.getButton().setOnClickListener(buttonView1 -> {
            if(doubleClick.detected()) return;
            goToLoginPage();
        });

        loadingDialog = new LoadingDialog(LoginSplashScreen.this, errorDialog);
        loadingDialog.showLoadingDialog(4 + ApiSingleUser.ticks);


        //Check If exists User session on firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            goToLoginPage();
            return;
        }
        loadingDialog.tick(); // 1

        //if user is logged in --> Check Permission
        sessionManagement session = new sessionManagement(LoginSplashScreen.this);
        userUid = session.getSession();
        System.out.println(userUid);
        if(userUid == null || userUid.equals("-1")) {
            goToLoginPage();
            return;
        }

        loadingDialog.tick(); // 2

        try{
            ApiSingleUserCallback apiSingleUserCallback = response -> {
                user = response;
                if(!user.getAccessLevel().equals(accessLevelUser)){
                    goToHomePage();
                    return;
                }
                if(user.getAppPermission().equals("ativo")){
                    goToHomePage();
                }
            };
            new ApiSingleUser(LoginSplashScreen.this, this.getClass().getSimpleName(), loadingDialog, errorDialog, apiSingleUserCallback, userUid);
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, errorDialog);
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void goToLoginPage(){
        loadingDialog.tick(); // 3
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            loadingDialog.finalTick(); // 4
            startActivity(new Intent(LoginSplashScreen.this, Login.class));
            finish();
        }, 1500);
    }

    private void goToHomePage(){
        loadingDialog.tick(); // 3
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            loadingDialog.finalTick(); // 4
            startActivity(new Intent(LoginSplashScreen.this, Home.class));
            finish();
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        errorDialog.endErrorDialog();
        loadingDialog.endLoadingDialog();
    }
}