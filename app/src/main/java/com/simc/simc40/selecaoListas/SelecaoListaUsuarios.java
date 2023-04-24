package com.simc.simc40.selecaoListas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.simc.simc40.R;
import com.simc.simc40.accessLevel.AccessLevel;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiUsers;
import com.simc.simc40.firebaseApiGET.ApiUsersCallback;
import com.simc.simc40.firebasePaths.FirebaseObraPaths;
import com.simc.simc40.firebasePaths.FirebaseUserPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.sharedPreferences.LocalStorage;
import com.google.firebase.auth.FirebaseAuth;

public class SelecaoListaUsuarios extends AppCompatActivity implements DefaultErrorMessage, SharedPrefsExceptionErrorList, FirebaseObraPaths, FirebaseUserPaths, AccessLevel {

    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    DuploClique duploClique = new DuploClique();
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    Usuario usuario;
    String uidCliente, accessLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_usuarios);

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);
        modalDeCarregamento.mostrarModalDeCarregamento(4 + ApiUsers.ticks);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        try{
            usuario = LocalStorage.getUsuario(this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            uidCliente = usuario.getCliente().getUid();
            accessLevel = usuario.getAccessLevel();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        modalDeCarregamento.avancarPasso(); // 1

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
            if (duploClique.detectado()) return;
            onBackPressed();
        });

        try {
            modalDeCarregamento.avancarPasso(); // 2
            ApiUsersCallback apiUsersCallback = response -> {
                modalDeCarregamento.avancarPasso(); // 3
                for (Usuario user : response.values()) {
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
                        if(duploClique.detectado()) return;
                        Intent intent = new Intent();
                        Usuario selectedUsuario = (Usuario) objItem4.getTag();
                        intent.putExtra("result", selectedUsuario);
                        setResult (RESULT_OK, intent);
                        finish();
                    });
                    listaLayout.addView(obj);
                }
                modalDeCarregamento.passoFinal(); // 4
                modalDeCarregamento.fecharModal();
            };
            new ApiUsers(SelecaoListaUsuarios.this, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiUsersCallback, uidCliente, accessLevel);
        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalAlertaDeErro);
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (duploClique.detectado()) {
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
    }
}