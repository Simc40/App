package com.simc.simc40.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.simc.simc40.R;

import java.util.Map;

public class ModalAlertaDeErro {
    AlertDialog alertDialog;
    TextView titulo, descricao;
    Button botao;
    View layout;

    @SuppressLint("InflateParams")
    public ModalAlertaDeErro(Activity atividade){
        AlertDialog.Builder builder = new AlertDialog.Builder(atividade);

        LayoutInflater inflater = atividade.getLayoutInflater();
        View layoutDeAlertDialog = inflater.inflate(R.layout.error_dialog, null);
        this.layout = layoutDeAlertDialog;

        TextView titulo = layoutDeAlertDialog.findViewById(R.id.title);
        TextView descricao = layoutDeAlertDialog.findViewById(R.id.description);
        Button botao = layoutDeAlertDialog.findViewById(R.id.button);

        this.titulo = titulo;
        this.descricao = descricao;
        this.botao = botao;

        botao.setOnClickListener(view -> fecharModal());
        builder.setView(layoutDeAlertDialog);

        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.getWindow().getDecorView().setBackgroundResource(R.drawable.loading_page_background);
    }

    public void showError(String title, String description){
        titulo.setText(title);
        descricao.setText(description);
        alertDialog.show();
    }

    public void showError(Map<String, String> errorCodeAndMessage){
        titulo.setText(errorCodeAndMessage.get("errorCode"));
        descricao.setText(errorCodeAndMessage.get("message"));
        alertDialog. show();
    }

    public void fecharModal(){
        alertDialog.dismiss();
    }

    public Button getBotao() { return botao; }

}
