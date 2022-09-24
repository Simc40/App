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
import com.android.simc40.classes.Obra;
import com.android.simc40.classes.User;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.firebaseApiGET.ApiObras;
import com.android.simc40.firebaseApiGET.ApiObrasCallback;
import com.android.simc40.firebasePaths.FirebaseObraPaths;
import com.android.simc40.firebasePaths.FirebaseUserPaths;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;

import java.util.TreeMap;

public class SelecaoListaObras extends AppCompatActivity implements DefaultErrorMessage, SharedPrefsExceptionErrorList, FirebaseObraPaths, FirebaseUserPaths {

    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack;
    String database;
    DoubleClick doubleClick = new DoubleClick();
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_obras);

        errorDialog = new ErrorDialog(SelecaoListaObras.this);
        errorDialog.getButton().setOnClickListener(null);
        errorDialog.getButton().setOnClickListener(view -> finish());
        loadingDialog = new LoadingDialog(SelecaoListaObras.this, errorDialog);
        loadingDialog.showLoadingDialog(3);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        goBack = findViewById(R.id.goBack);

        try{
            user = sharedPrefsDatabase.getUser(SelecaoListaObras.this, MODE_PRIVATE, loadingDialog, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = user.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }

        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Obra";
        item1.setText(Text1);
        TextView item2 = headerXML.findViewById(R.id.item2);
        String Text2 = "Responsável";
        item2.setText(Text2);
        TextView item3 = headerXML.findViewById(R.id.item3);
        String Text3 = "Tipo";
        item3.setText(Text3);
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            onBackPressed();
        });

        loadingDialog.tick(); // 1

        try{
            ApiObrasCallback apiCallback = response -> {
                loadingDialog.tick(); // 2;
                if(this.isFinishing() || this.isDestroyed()) return;
                TreeMap <String, Obra> sorted = new TreeMap<String, Obra>(){{
                    for(Obra obra: response.values()) put(toFirstLetterUpperCase(obra.getNomeObra()) + obra.getUid(), obra);
                }};
                for(Obra obra: sorted.values()) {
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item, listaLayout ,false);
                    TextView objItem1 = obj.findViewById(R.id.item1);
                    TextView objItem2 = obj.findViewById(R.id.item2);
                    TextView objItem3 = obj.findViewById(R.id.item3);
                    ImageView objItem4 = obj.findViewById(R.id.item4);
                    objItem1.setText(toFirstLetterUpperCase(obra.getNomeObra()));
                    objItem2.setText(obra.getResponsavel());
                    objItem3.setText(obra.getTipoConstrucao());
                    objItem4.setImageResource(R.drawable.forward);
                    objItem4.setTag(obra);
                    objItem4.setOnClickListener(view -> {
                        if(doubleClick.detected()) return;
                        Intent intent = new Intent();
                        Obra selectedObra = (Obra) objItem4.getTag();
                        intent.putExtra("result", selectedObra);
                        setResult (SelecaoListaObras.RESULT_OK, intent);
                        finish();
                    });
                    listaLayout.addView(obj);
                }
                loadingDialog.finalTick(); // 3
                loadingDialog.endLoadingDialog();
            };
            new ApiObras(SelecaoListaObras.this, database, this.getClass().getSimpleName(), loadingDialog, errorDialog, apiCallback);
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }

    }

    private String toFirstLetterUpperCase(String string){
        return (string == null || string.length() < 1) ? string : string.substring(0,1).toUpperCase() + string.substring(1);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(doubleClick.detected()){
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
    }
}