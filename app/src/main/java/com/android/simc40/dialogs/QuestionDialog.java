package com.android.simc40.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.simc40.R;

public class QuestionDialog {
    AlertDialog dialog;
    TextView vTitle, vDescription;
    Button vButton, vButton2;
    View view;
    AlertDialog.Builder builder;

    @SuppressLint("InflateParams")
    public QuestionDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_question, null);
        this.view = v;

        TextView vTitle = v.findViewById(R.id.title);
        TextView vDescription = v.findViewById(R.id.description);
        Button vButton = v.findViewById(R.id.button);
        Button vButton2 = v.findViewById(R.id.button2);

        this.vTitle = vTitle;
        this.vDescription = vDescription;
        this.vButton = vButton;
        this.vButton2 = vButton2;

        vButton.setOnClickListener(view -> endQuestionDialog());
        builder.setView(v);

        dialog = builder.create();
        dialog.getWindow().getDecorView().setBackgroundResource(R.drawable.loading_page_background);
    }

    public void showQuestion(String title, String description){
        vTitle.setText(title);
        vDescription.setText(description);
        dialog.show();
    }

    public void showQuestion(String title){
        vTitle.setText(title);
        vDescription.setVisibility(View.GONE);
        dialog.show();
    }

    public void showQuestion(String title, String b1, String b2){
        vTitle.setText(title);
        vDescription.setVisibility(View.GONE);
        dialog.show();
        vButton.setText(b1);
        vButton2.setText(b2);
    }

    public void endQuestionDialog(){
        dialog.dismiss();
    }

    public Button getButton1() { return vButton; }
    public Button getButton2() { return vButton2; }

    public View getView() { return view; }
    public AlertDialog.Builder getBuilder() {return builder;}
}
