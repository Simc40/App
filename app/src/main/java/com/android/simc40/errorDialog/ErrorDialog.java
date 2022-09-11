package com.android.simc40.errorDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.simc40.R;

import java.util.Map;

public class ErrorDialog {
    AlertDialog dialog;
    TextView vTitle, vDescription;
    Button vButton;
    View view;

    @SuppressLint("InflateParams")
    public ErrorDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.error_dialog, null);
        this.view = v;

        TextView vTitle = v.findViewById(R.id.title);
        TextView vDescription = v.findViewById(R.id.description);
        Button vButton = v.findViewById(R.id.button);

        this.vTitle = vTitle;
        this.vDescription = vDescription;
        this.vButton = vButton;

        vButton.setOnClickListener(view -> endErrorDialog());
        builder.setView(v);

        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().getDecorView().setBackgroundResource(R.drawable.loading_page_background);
    }

    public void showError(String title, String description){
        vTitle.setText(title);
        vDescription.setText(description);
        dialog.show();
    }

    public void showError(Map<String, String> errorCodeAndMessage){
        vTitle.setText(errorCodeAndMessage.get("errorCode"));
        vDescription.setText(errorCodeAndMessage.get("message"));
        dialog. show();
    }

    public void endErrorDialog(){
        dialog.dismiss();
    }

    public Button getButton() { return vButton; }

    public View getView() { return view; }


}
