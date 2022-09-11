package com.android.simc40.successDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.simc40.R;

public class SuccessDialog {
    String permissionTitle = "Permissão Concedida com Sucesso!";
    String permissionDescription = "As permissões foram concedidas, tente capturar a imagem novamente. Se outras permissões foram necessárias, conceda para prosseguir.";
    String imageUploadTitle = "Upload de Imagem Realizado com Sucesso!";
    String imageUploadDescription = "É necessário realizar novamente o login para aplicar as mudanças. O seu LogOut será realizado.";
    AlertDialog dialog;
    TextView vTitle, vDescription;
    Button vButton;
    View view;
    AlertDialog.Builder builder;

    @SuppressLint("InflateParams")
    public SuccessDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.success_dialog, null);
        this.view = v;

        TextView vTitle = v.findViewById(R.id.title);
        TextView vDescription = v.findViewById(R.id.description);
        Button vButton = v.findViewById(R.id.button);

        this.vTitle = vTitle;
        this.vDescription = vDescription;
        this.vButton = vButton;

        vButton.setOnClickListener(view -> endSuccessDialog());
        builder.setView(v);
        builder.setCancelable(false);


        dialog = builder.create();
        dialog.getWindow().getDecorView().setBackgroundResource(R.drawable.loading_page_background);
    }

    public void showSuccess(String title, String description){
        vTitle.setText(title);
        vDescription.setText(description);
        dialog.show();
    }

    public void showImageUploadSuccess(){
        vTitle.setText(imageUploadTitle);
        vDescription.setText(imageUploadDescription);
        dialog.show();
    }

    public void showPermissionSuccess(){
        vTitle.setText(permissionTitle);
        vDescription.setText(permissionDescription);
        dialog.show();
    }

    public void endSuccessDialog(){
        dialog.dismiss();
    }

    public Button getButton() { return vButton; }
    public View getView() { return view; }
    public AlertDialog.Builder getBuilder() {return builder;}
}
