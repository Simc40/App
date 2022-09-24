package com.android.simc40.moduloQualidade.relatorioErros;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import com.android.simc40.R;
import com.android.simc40.checklist.Etapas;
import com.android.simc40.classes.Erro;
import com.android.simc40.classes.ErrorFilter;
import com.android.simc40.classes.Obra;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.selecaoListas.SelecaoListaObras;
import com.android.simc40.touchListener.TouchListener;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class RelatorioDeErrosFiltros extends AppCompatActivity implements Etapas, LayoutExceptionErrorList {

    CardView creationSubmitData, solutionSubmitData, applyFilters;
    TextView obraSpinner;
    Spinner etapasSpinner, statusSpinner;
    Button etapasArrow, statusArrow, obraArrow, creationArrow, solutionArrow;
    LinearLayout etapasSelected, statusSelected, obraSelected, creationSelected, solutionSelected;
    ConstraintLayout etapasAdd, statusAdd, obraAdd, creationAdd, solutionAdd, clearFilters;
    ErrorDialog errorDialog;
    CalendarPickerView creationCalendar, solutionCalendar;
    NestedScrollView scrollView;
    ArrayList<Button> arrows = new ArrayList<>();
//    DateFormat dateHourFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Intent intent;
    ErrorFilter errorFilter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorioerros_filtros);

        errorDialog = new ErrorDialog(this);
        scrollView = findViewById(R.id.scrollView);
        applyFilters = findViewById(R.id.applyFilters);
        clearFilters = findViewById(R.id.clearFilters);

        intent = getIntent();
        errorFilter = (ErrorFilter) intent.getSerializableExtra("dependency");

        Log.d("FILTROS", errorFilter.toString());

        //Etapas
        etapasArrow = findViewById(R.id.etapasArrow);
        etapasSelected = findViewById(R.id.etapasSelected);
        etapasSpinner = findViewById(R.id.etapasSpinner);
        etapasAdd = findViewById(R.id.etapasAdd);

        etapasArrow.setOnTouchListener(TouchListener.getTouch());

        etapasArrow.setOnClickListener(view -> {
            if(etapasArrow.getTag() == null || etapasArrow.getTag().equals("hide")){
                hideAllViews();
                etapasArrow.setTag("show");
                etapasArrow.setBackgroundResource(R.drawable.arrow_down);
                etapasSelected.setVisibility(View.VISIBLE);
                etapasAdd.setVisibility(View.VISIBLE);
                scrollView.scrollTo(view.getScrollX(), view.getScrollY());
            }else{
                etapasArrow.setTag("hide");
                etapasArrow.setBackgroundResource(R.drawable.arrow_up);
                etapasSelected.setVisibility(View.GONE);
                etapasAdd.setVisibility(View.GONE);
            }
        });

        ArrayAdapter<String> etapasFiltros = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(){{add("Selecionar Etapa");addAll(prettyEtapas.values());}});
        etapasFiltros.setDropDownViewResource(android.R.layout.simple_list_item_1);
        etapasSpinner.setAdapter(etapasFiltros);

        etapasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    String selected = etapasSpinner.getSelectedItem().toString();
                    if(selected.equals("Selecionar Etapa")) return;
                    for(int j = 0; j < etapasSelected.getChildCount(); j++) if(etapasSelected.getChildAt(j).getTag().equals(selected)) return;
                    generateView(selected, etapasSelected);
                } finally {
                    etapasSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        
        //Status
        statusArrow = findViewById(R.id.statusArrow);
        statusSelected = findViewById(R.id.statusSelected);
        statusSpinner = findViewById(R.id.statusSpinner);
        statusAdd = findViewById(R.id.statusAdd);

        statusArrow.setOnTouchListener(TouchListener.getTouch());

        statusArrow.setOnClickListener(view -> {
            if(statusArrow.getTag() == null || statusArrow.getTag().equals("hide")){
                hideAllViews();
                statusArrow.setTag("show");
                statusArrow.setBackgroundResource(R.drawable.arrow_down);
                statusSelected.setVisibility(View.VISIBLE);
                statusAdd.setVisibility(View.VISIBLE);
                scrollView.scrollTo(view.getScrollX(), view.getScrollY());
            }else{
                statusArrow.setTag("hide");
                statusArrow.setBackgroundResource(R.drawable.arrow_up);
                statusSelected.setVisibility(View.GONE);
                statusAdd.setVisibility(View.GONE);
            }
        });

        ArrayAdapter<String> statusFiltros = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(){{add("Selecionar Status");add(Erro.statusAberto);add(Erro.statusFechado);}});
        statusFiltros.setDropDownViewResource(android.R.layout.simple_list_item_1);
        statusSpinner.setAdapter(statusFiltros);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    String selected = statusSpinner.getSelectedItem().toString();
                    if(selected.equals("Selecionar Status")) return;
                    for(int j = 0; j < statusSelected.getChildCount(); j++) if(statusSelected.getChildAt(j).getTag().equals(selected)) return;
                    if(statusSelected.getChildCount() > 0) statusSelected.removeAllViews();
                    generateView(selected, statusSelected);
                } finally {
                    statusSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        
        //Obra
        obraArrow = findViewById(R.id.obraArrow);
        obraSelected = findViewById(R.id.obraSelected);
        obraSpinner = findViewById(R.id.obraSpinner);
        obraAdd = findViewById(R.id.obraAdd);

        obraArrow.setOnTouchListener(TouchListener.getTouch());

        obraArrow.setOnClickListener(view -> {
            if(obraArrow.getTag() == null || obraArrow.getTag().equals("hide")){
                hideAllViews();
                obraArrow.setTag("show");
                obraArrow.setBackgroundResource(R.drawable.arrow_down);
                obraSelected.setVisibility(View.VISIBLE);
                obraAdd.setVisibility(View.VISIBLE);
                scrollView.scrollTo(view.getScrollX(), view.getScrollY());
            }else{
                obraArrow.setTag("hide");
                obraArrow.setBackgroundResource(R.drawable.arrow_up);
                obraSelected.setVisibility(View.GONE);
                obraAdd.setVisibility(View.GONE);
            }
        });

        obraSpinner.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelecaoListaObras.class);
            selectObraFromList.launch(intent);
        });

        creationSubmitData = findViewById(R.id.creationSubmitData);
        creationArrow = findViewById(R.id.creationArrow);
        creationSelected = findViewById(R.id.creationSelected);
        creationAdd = findViewById(R.id.creationAdd);
        creationCalendar = findViewById(R.id.creationCalendar);
        
        creationArrow.setOnTouchListener(TouchListener.getTouch());
        creationSubmitData.setOnTouchListener(TouchListener.getTouch());

        if(!errorFilter.rangeRegistroIsEmpty()) {
            creationArrow.setOnClickListener(view -> {
                if (creationArrow.getTag() == null || creationArrow.getTag().equals("hide")) {
                    hideAllViews();
                    creationArrow.setTag("show");
                    creationArrow.setBackgroundResource(R.drawable.arrow_down);
                    creationSelected.setVisibility(View.VISIBLE);
                    creationAdd.setVisibility(View.VISIBLE);
                    scrollView.scrollTo(view.getScrollX(), view.getScrollY());
                } else {
                    creationArrow.setTag("hide");
                    creationArrow.setBackgroundResource(R.drawable.arrow_up);
                    creationSelected.setVisibility(View.GONE);
                    creationAdd.setVisibility(View.GONE);
                }
            });

            creationCalendar.init(errorFilter.getMinDataRegistro(), errorFilter.getMaxDataRegistro()).inMode(CalendarPickerView.SelectionMode.RANGE);

            creationSubmitData.setOnClickListener(view -> {
                List<Date> result = creationCalendar.getSelectedDates();
                if (result.size() == 0) return;
                if (creationSelected.getChildCount() > 0) creationSelected.removeAllViews();
                if (result.size() <= 2) generateView(result, creationSelected);
                else if (result.size() > 2) {
                    List<Date> finalResult = result;
                    result = new ArrayList<Date>() {{
                        add(finalResult.get(0));
                        add(finalResult.get(finalResult.size() - 1));
                    }};
                    generateView(result, creationSelected);
                }
            });
        }

        solutionSubmitData = findViewById(R.id.solutionSubmitData);
        solutionArrow = findViewById(R.id.solutionArrow);
        solutionSelected = findViewById(R.id.solutionSelected);
        solutionAdd = findViewById(R.id.solutionAdd);
        solutionCalendar = findViewById(R.id.solutionCalendar);

        solutionArrow.setOnTouchListener(TouchListener.getTouch());

        if(!errorFilter.rangeSolucaoIsEmpty()){
            solutionArrow.setOnClickListener(view -> {
                if(solutionArrow.getTag() == null || solutionArrow.getTag().equals("hide")){
                    hideAllViews();
                    solutionArrow.setTag("show");
                    solutionArrow.setBackgroundResource(R.drawable.arrow_down);
                    solutionSelected.setVisibility(View.VISIBLE);
                    solutionAdd.setVisibility(View.VISIBLE);
                    scrollView.scrollTo(view.getScrollX(), view.getScrollY());
                }else{
                    solutionArrow.setTag("hide");
                    solutionArrow.setBackgroundResource(R.drawable.arrow_up);
                    solutionSelected.setVisibility(View.GONE);
                    solutionAdd.setVisibility(View.GONE);
                }
            });

            solutionCalendar.init(errorFilter.getMinDataSolucao(), errorFilter.getMaxDataSolucao()).inMode(CalendarPickerView.SelectionMode.RANGE);

            solutionSubmitData.setOnClickListener(view -> {
                List<Date> result = solutionCalendar.getSelectedDates();
                if(result.size() == 0) return;
                if(solutionSelected.getChildCount() > 0) solutionSelected.removeAllViews();
                if(result.size() <= 2) generateView(result, solutionSelected);
                else if(result.size() > 2){
                    List<Date> finalResult = result;
                    result = new ArrayList<Date>(){{add(finalResult.get(0));add(finalResult.get(finalResult.size()-1));}};
                    generateView(result, solutionSelected);
                }
            });
        }

        arrows.add(etapasArrow); arrows.add(obraArrow); arrows.add(statusArrow); arrows.add(creationArrow); arrows.add(solutionArrow);

        applyFilters.setOnClickListener(view -> {
            Intent intent = new Intent();
            loadFilters(errorFilter);
            intent.putExtra("result", errorFilter);
            setResult (RelatorioDeErrosFiltros.RESULT_OK, intent);
            finish();
        });

        clearFilters.setOnClickListener(view -> {
            errorFilter.resetFilters();
            intent.putExtra("result", errorFilter);
            setResult (RelatorioDeErrosFiltros.RESULT_OK, intent);
            finish();
        });

        if(errorFilter.isEmpty()) return;
        if(errorFilter.getEtapas() != null) for(String etapa: errorFilter.getEtapas()) generateView(prettyEtapas.get(etapa), etapasSelected);
        if(errorFilter.getStatus() != null) generateView(errorFilter.getStatus(), statusSelected);
        if(errorFilter.getObras() != null) for(Obra obra : errorFilter.getObras()) generateView(obra, obraSelected);
        if(errorFilter.getDataRegistro() != null) generateView(errorFilter.getDataRegistro(), creationSelected);
        if(errorFilter.getDataSolucao() != null) generateView(errorFilter.getDataSolucao(), solutionSelected);
    }

    private void loadFilters(ErrorFilter errorFilter){
        errorFilter.resetFilters();
        for(int j = 0; j < etapasSelected.getChildCount(); j++) errorFilter.putFilterEtapa(prettyEtapasKeys.get(etapasSelected.getChildAt(j).getTag().toString()));
        if(statusSelected.getChildCount() > 0) errorFilter.setStatus(statusSelected.getChildAt(0).getTag().toString());
        for(int j = 0; j < obraSelected.getChildCount(); j++) {
            Obra obra = (Obra) obraSelected.getChildAt(j).getTag();
            errorFilter.putFilterObra(obra);
        }
        if(creationSelected.getChildCount() > 0) {
            List<?> dates = (List<?>) creationSelected.getChildAt(0).getTag();
            List<Date> new_dates = new ArrayList<>();
            for(Object date : dates){
                new_dates.add((Date)date);
            }
            errorFilter.setDataRegistro(new_dates);
        }
        if(solutionSelected.getChildCount() > 0) {
            List<?> dates = (List<?>) solutionSelected.getChildAt(0).getTag();
            List<Date> new_dates = new ArrayList<>();
            for(Object date : dates){
                new_dates.add((Date)date);
            }
            errorFilter.setDataSolucao(new_dates);
        }

        System.out.println(errorFilter);
    }
    
    private void hideAllViews(){
        for(Button arrow: arrows){
            if(arrow.getTag() != null && arrow.getTag().equals("show")) arrow.callOnClick();
        }
    }

    private void generateView(String item, LinearLayout layout) {
        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item, layout ,false);
        TextView item1 = obj.findViewById(R.id.item1);
        ImageView item2 = obj.findViewById(R.id.item2);
        item2.setImageResource(R.drawable.uncheck);
        item1.setText(item);
        obj.setTag(item);
        item2.setOnClickListener(view -> layout.removeView(obj));
        layout.addView(obj);
    }

    private void generateView(Obra obra, LinearLayout layout) {
        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item, layout ,false);
        TextView item1 = obj.findViewById(R.id.item1);
        ImageView item2 = obj.findViewById(R.id.item2);
        item2.setImageResource(R.drawable.uncheck);
        item1.setText(obra.getNomeObra());
        obj.setTag(obra);
        item2.setOnClickListener(view -> layout.removeView(obj));
        layout.addView(obj);
    }

    private void generateView(List<Date> dates, LinearLayout layout) {
        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item, layout ,false);
        TextView item1 = obj.findViewById(R.id.item1);
        ImageView item2 = obj.findViewById(R.id.item2);
        item2.setImageResource(R.drawable.uncheck);
        String date = (dates.size() == 1) ? dateFormat.format(dates.get(0)) : dateFormat.format(dates.get(0)) + " - " + dateFormat.format(dates.get(1));
        item1.setText(date);
        obj.setTag(dates);
        item2.setOnClickListener(view -> layout.removeView(obj));
        layout.addView(obj);
    }

    ActivityResultLauncher<Intent> selectObraFromList = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                Obra obra = (Obra) data.getSerializableExtra("result");
                for(int j = 0; j < obraSelected.getChildCount(); j++){
                    Obra tagObra = (Obra) obraSelected.getChildAt(j).getTag();
                    if(tagObra.getUid().equals(obra.getUid())) return;
                }
                generateView(obra, obraSelected);
            } catch (LayoutException e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, errorDialog);
            }
        }
    });

}