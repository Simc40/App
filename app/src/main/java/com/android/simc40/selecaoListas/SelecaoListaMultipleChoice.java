package com.android.simc40.selecaoListas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.simc40.R;
import com.android.simc40.doubleClick.DoubleClick;

import java.util.ArrayList;
import java.util.List;

public class SelecaoListaMultipleChoice extends AppCompatActivity {

    Intent intent;
    LinearLayout header;
    LinearLayout listaLayout;
    TextView goBack, title;
    DoubleClick doubleClick = new DoubleClick();
    List<String> strs;
    ArrayList<String> result = new ArrayList<>();
    CardView submitForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selecao_lista_pecas);

        intent = getIntent();
        strs = intent.getStringArrayListExtra("dependency");

        submitForm = findViewById(R.id.submitForm);
        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        title = findViewById(R.id.title);
        goBack = findViewById(R.id.goBack);
        
        if(intent.getStringExtra("title") != null) title.setText(intent.getStringExtra("title"));
        
        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_header, header, false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String Text1 = "Peças";
        item1.setText(Text1);
        if(intent.getStringExtra("description") != null) item1.setText(intent.getStringExtra("description"));
        header.addView(headerXML);

        goBack.setOnClickListener(view -> {
            if (doubleClick.detected()) return;
            onBackPressed();
        });

        for (String str : strs){
            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_checkbox, listaLayout, false);
            TextView objItem1 = obj.findViewById(R.id.item1);
            objItem1.setText(str);
            CheckBox item2 = obj.findViewById(R.id.item2);
            item2.setOnClickListener(view -> {
                if(item2.isChecked()) obj.setTag(str);
                else obj.setTag(null);
            });
            listaLayout.addView(obj);
        }

        submitForm.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            for(int i = 0 ; i < listaLayout.getChildCount(); i++){
                String tag = (listaLayout.getChildAt(i).getTag() == null) ? null : String.valueOf(listaLayout.getChildAt(i).getTag());
                if(tag != null) result.add(tag);
            }
            if(result.isEmpty()) {
                Toast.makeText(SelecaoListaMultipleChoice.this, "Selecione ao menos um Item da Lista!", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra("result", result);
            setResult (SelecaoListaMultipleChoice.RESULT_OK, intent);
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (doubleClick.detected()) {
            return;
        }
        finish();
    }

}