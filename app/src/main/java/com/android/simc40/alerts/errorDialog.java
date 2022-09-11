package com.android.simc40.alerts;

import android.app.AlertDialog;
import android.content.Context;

public class  errorDialog {

    public static void showError(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Erro!");
        builder.setMessage(message);
        builder.setPositiveButton("ok", (dialog, which) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
