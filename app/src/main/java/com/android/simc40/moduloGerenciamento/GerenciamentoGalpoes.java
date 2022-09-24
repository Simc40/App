package com.android.simc40.moduloGerenciamento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.simc40.R;
import com.android.simc40.activityStatus.ActivityStatus;
import com.android.simc40.alerts.errorDialog;
import com.android.simc40.classes.Galpao;
import com.android.simc40.classes.User;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.firebaseApiGET.ApiGalpoes;
import com.android.simc40.firebaseApiGET.ApiGalpoesCallback;
import com.android.simc40.selecaoListas.SelecaoListaGalpoes;
import com.android.simc40.selecaoListas.SelecaoListaObras;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GerenciamentoGalpoes extends AppCompatActivity implements FirebaseDatabaseExceptionErrorList, SharedPrefsExceptionErrorList {

    User user;
    LinearLayout header;
    LinearLayout listaLayout;
    DatabaseReference reference;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    String database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_galpoes);

        errorDialog = new ErrorDialog(this);
        loadingDialog = new LoadingDialog(this, errorDialog);
        loadingDialog.showLoadingDialog(3);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);

        try{
            user = sharedPrefsDatabase.getUser(this, MODE_PRIVATE, loadingDialog, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = user.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }

        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Galpão";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Disponibilidade";
        item2.setText(Text2);
        header.addView(headerXML);

        loadingDialog.tick(); // 1

        try{
            ApiGalpoesCallback apiCallback = response -> {
                loadingDialog.tick(); // 2
                if(this.isFinishing() || this.isDestroyed()) return;
                for(Galpao galpao: response.values()) {
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_3_list_item, listaLayout ,false);
                    TextView objItem1 = obj.findViewById(R.id.item1);
                    TextView objItem2 = obj.findViewById(R.id.item2);
                    ImageView objItem3 = obj.findViewById(R.id.item3);
                    objItem1.setText(galpao.getNome());
                    objItem2.setText(galpao.getPrettyStatus());
                    if(galpao.getPrettyStatus().equals(Galpao.disponivel)){
                        objItem3.setImageResource(R.drawable.checked);
                    }else{
                        objItem3.setImageResource(R.drawable.uncheck);
                    }
                    listaLayout.addView(obj);
                }
                loadingDialog.finalTick(); // 3
                loadingDialog.endLoadingDialog();
            };
            new ApiGalpoes(this, database, this.getClass().getSimpleName(), loadingDialog, errorDialog, apiCallback, null);
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }
    }
}