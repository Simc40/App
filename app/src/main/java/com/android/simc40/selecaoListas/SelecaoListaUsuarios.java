package com.android.simc40.selecaoListas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.simc40.R;
import com.android.simc40.accessLevel.AccessLevel;
import com.android.simc40.classes.User;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.firebaseApiGET.ApiUsers;
import com.android.simc40.firebaseApiGET.ApiUsersCallback;
import com.android.simc40.firebasePaths.FirebaseObraPaths;
import com.android.simc40.firebasePaths.FirebaseUserPaths;
import com.android.simc40.loadingPage.LoadingPage;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class SelecaoListaUsuarios extends AppCompatActivity implements DefaultErrorMessage, SharedPrefsExceptionErrorList, FirebaseObraPaths, FirebaseUserPaths, AccessLevel {

    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    String contextException = "SelecaoListaUsuarios";
    DoubleClick doubleClick = new DoubleClick();
    LoadingPage loadingPage;
    ErrorDialog errorDialog;
    User user;
    String uidCliente, accessLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_usuarios);

        errorDialog = new ErrorDialog(SelecaoListaUsuarios.this);
        loadingPage = new LoadingPage(SelecaoListaUsuarios.this, errorDialog);
        loadingPage.showLoadingPage();

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        try{
            user = sharedPrefsDatabase.getUser(SelecaoListaUsuarios.this, MODE_PRIVATE, loadingPage, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            uidCliente = user.getCliente().getUid();
            accessLevel = user.getAccessLevel();
        } catch (Exception e){
            ErrorHandling.handleError(contextException, e, loadingPage, errorDialog);
        }

        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item_header, header, false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Matricula";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Nome";
        item2.setText(Text2);
        TextView item3 = headerXML.findViewById(R.id.item3);
        String Text3 = "Nível de\nAcesso";
        item3.setText(Text3);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if (doubleClick.detected()) return;
            onBackPressed();
        });

        try {
            ApiUsersCallback apiUsersCallback = response -> {
                for (User user : response.values()) {
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item, listaLayout, false);
                    TextView objItem1 = obj.findViewById(R.id.item1);
                    TextView objItem2 = obj.findViewById(R.id.item2);
                    TextView objItem3 = obj.findViewById(R.id.item3);
                    ImageView objItem4 = obj.findViewById(R.id.item4);
                    String[] words = user.getNome().split(" ");
                    String nomeUser = (words.length > 3) ? words[0] + " " + words[1] + " " + words[2] : user.getNome();
                    objItem1.setText(user.getMatricula());
                    objItem2.setText(nomeUser);
                    objItem3.setText(accessLevelMap.get(user.getAccessLevel()));
                    objItem4.setImageResource(R.drawable.forward);
                    objItem4.setTag(user);
                    objItem4.setOnClickListener(view -> {
                        if(doubleClick.detected()) return;
                        Intent intent = new Intent();
                        User selectedUser = (User) objItem4.getTag();
                        intent.putExtra("result", selectedUser);
                        setResult (SelecaoListaObras.RESULT_OK, intent);
                        finish();
                    });
                    listaLayout.addView(obj);
                }
                loadingPage.endLoadingPage();
            };
            new ApiUsers(SelecaoListaUsuarios.this, contextException, loadingPage, errorDialog, apiUsersCallback, uidCliente, accessLevel);
        } catch (Exception e) {
            ErrorHandling.handleError(contextException, e, errorDialog);
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (doubleClick.detected()) {
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingPage.endLoadingPage();
        errorDialog.endErrorDialog();
    }
}