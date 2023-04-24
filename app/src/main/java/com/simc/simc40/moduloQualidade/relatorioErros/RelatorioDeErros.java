package com.simc.simc40.moduloQualidade.relatorioErros;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simc.simc40.R;
import com.simc.simc40.checklist.Etapas;
import com.simc.simc40.classes.Elemento;
import com.simc.simc40.classes.Erro;
import com.simc.simc40.classes.ErrorFilter;
import com.simc.simc40.classes.Obra;
import com.simc.simc40.classes.Peca;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebaseApiGET.ApiErros;
import com.simc.simc40.firebaseApiGET.ApiErrosCallback;
import com.simc.simc40.firebaseApiGET.ApiPecas;
import com.simc.simc40.firebaseApiGET.ApiPecasCallback;
import com.simc.simc40.firebaseApiGET.ApiPecasUid;
import com.simc.simc40.sharedPreferences.LocalStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class RelatorioDeErros extends AppCompatActivity implements LayoutExceptionErrorList, SharedPrefsExceptionErrorList, Etapas {

    Usuario usuario;
    CardView applyFilters;
    ErrorFilter errorFilter = new ErrorFilter();
    TreeMap<String, Erro> errosMap;
    TreeMap<String, Obra> obrasMap;
    TreeMap<String, Elemento> elementosMap;
    TreeMap<String, Peca> pecasMap;
    ApiPecasUid apiPecas;
    ApiErros apiErros;
    TextView goBack;
    String database;
    LinearLayout  header;
    LinearLayout listaLayout;
    DuploClique duploClique = new DuploClique();
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    boolean responseApiErros = false, responseApiPecas = false;
    Date minDataRegistro, maxDataRegistro, minDataSolucao, maxDataSolucao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqualidade_relatorio_de_erros);

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);

        modalDeCarregamento.mostrarModalDeCarregamento(ApiPecas.ticks + ApiErros.ticks + 5);

        goBack = findViewById(R.id.goBack);
        applyFilters = findViewById(R.id.filtros);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);

        goBack.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            onBackPressed();
        });

        applyFilters.setOnClickListener(view -> {
            Intent filterIntent = new Intent(this, RelatorioDeErrosFiltros.class);
            filterIntent.putExtra("dependency", errorFilter);
            setFilters.launch(filterIntent);
        });

        try{
            usuario = LocalStorage.getUsuario(this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = usuario.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_header, header ,false);
        TextView item1 = headerXML.findViewById(R.id.item1);
        String text1 = "Inconformidades";
        item1.setText(text1);
        header.addView(headerXML);

        modalDeCarregamento.avancarPasso(); // 1

        ApiPecasCallback apiCallback = response -> {
            try{
                pecasMap = response;
                obrasMap = apiPecas.getObras();
                elementosMap = apiPecas.getElementos();
                responseApiPecas = true;
                checkApiResponse();
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        };
        apiPecas = new ApiPecasUid(this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiCallback, null);

        ApiErrosCallback apiErrosCallback = response -> {
            if(this.isFinishing() || this.isDestroyed()) return;
            try{
                errosMap = response;
                responseApiErros = true;
                checkApiResponse();
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        };
        apiErros = new ApiErros(this, database, this.getClass().getSimpleName(), modalDeCarregamento, modalAlertaDeErro, apiErrosCallback, false);
    }

    private void checkApiResponse(){
        modalDeCarregamento.avancarPasso(); // 2 e 3
        if(responseApiPecas && responseApiErros) generateViews(errosMap);
    }

    private void loadFilters(ErrorFilter errorFilter) {
        for(int i = 0; i < listaLayout.getChildCount(); i++) listaLayout.getChildAt(i).setVisibility(View.VISIBLE);
        if(errorFilter.isEmpty()) return;
        modalDeCarregamento.mostrarModalDeCarregamento(6);

        ArrayList<View> views = new ArrayList<>();
        for(int i = 0; i < listaLayout.getChildCount(); i++) views.add(listaLayout.getChildAt(i));
        ArrayList<View> filteredViews = new ArrayList<>(views);

        modalDeCarregamento.avancarPasso(); // 1.1

        if(errorFilter.getEtapas() != null) filteredViews.removeIf(view -> !errorFilter.getEtapas().contains(getErroFromTag(view.getTag()).getEtapa_detectada()));
        modalDeCarregamento.avancarPasso(); // 1.2
        if(errorFilter.getStatus() != null) filteredViews.removeIf(view -> !errorFilter.getStatus().equals(getErroFromTag(view.getTag()).getStatus()));
        modalDeCarregamento.avancarPasso(); // 1.3
        if(errorFilter.getObras() != null) filteredViews.removeIf(view -> !errorFilter.getObras().stream().map(Obra::getUid).collect(Collectors.toList()).contains(getErroFromTag(view.getTag()).getObra()));
        modalDeCarregamento.avancarPasso(); // 1.4
        if(errorFilter.getDataRegistro() != null) {
            if (errorFilter.getDataRegistro().size() == 1) filteredViews.removeIf(view -> errorFilter.getDataRegistro().get(0).compareTo(getErroFromTag(view.getTag()).getDataCreation()) != 0);
            else filteredViews.removeIf(view -> errorFilter.getDataRegistro().get(0).compareTo(getErroFromTag(view.getTag()).getDataCreation()) > 0 || errorFilter.getDataRegistro().get(1).compareTo(getErroFromTag(view.getTag()).getDataCreation()) < 0);
        }
        modalDeCarregamento.avancarPasso(); // 1.5
        if(errorFilter.getDataSolucao() != null) {
            filteredViews.removeIf(view -> getErroFromTag(view.getTag()).getStatus().equals(Erro.statusAberto));
            if (errorFilter.getDataSolucao().size() == 1) filteredViews.removeIf(view -> errorFilter.getDataSolucao().get(0).compareTo(getErroFromTag(view.getTag()).getDataSolution()) != 0);
            else filteredViews.removeIf(view -> errorFilter.getDataSolucao().get(0).compareTo(getErroFromTag(view.getTag()).getDataSolution()) > 0 || errorFilter.getDataSolucao().get(1).compareTo(getErroFromTag(view.getTag()).getDataSolution()) < 0);
        }
        views.removeAll(filteredViews);

        for(View v : views) v.setVisibility(View.GONE);
        modalDeCarregamento.passoFinal(); // 1.6
        modalDeCarregamento.fecharModal();
    }

    private Erro getErroFromTag(Object object){
        return (Erro) object;
    }

    private void generateViews(TreeMap<String, Erro> errosMap){
        modalDeCarregamento.avancarPasso(); // 4
        for(Erro erro : errosMap.values()) {
            generateView(erro, pecasMap, elementosMap, obrasMap);
            if(maxDataRegistro == null){
                maxDataRegistro = erro.getDataCreation();
                minDataRegistro = erro.getDataCreation();
            }else{
                if(minDataRegistro.compareTo(erro.getDataCreation()) > 0) minDataRegistro = erro.getDataCreation();
                if(maxDataRegistro.compareTo(erro.getDataCreation()) < 0) maxDataRegistro = erro.getDataCreation();
            }
            if(erro.getDataSolution() == null) continue;
            if(maxDataSolucao == null){
                maxDataSolucao = erro.getDataSolution();
                minDataSolucao = erro.getDataSolution();
            }else{
                if(minDataSolucao.compareTo(erro.getDataSolution()) > 0) minDataSolucao = erro.getDataSolution();
                if(maxDataSolucao.compareTo(erro.getDataSolution()) < 0) maxDataSolucao = erro.getDataSolution();
            }
        }

        errorFilter.setMaxDataRegistro((maxDataRegistro != null) ? new Date(maxDataRegistro.getTime() + 24*60*60*1000) : null); // + 1 day
        errorFilter.setMinDataRegistro(minDataRegistro); // - 1 day
        errorFilter.setMaxDataSolucao((maxDataSolucao != null) ? new Date(maxDataSolucao.getTime() + 24*60*60*1000) : null); // + 1 day
        errorFilter.setMinDataSolucao(minDataSolucao); // - 1 day

        modalDeCarregamento.passoFinal(); // 5
        modalDeCarregamento.fecharModal();
    }

    private void generateView(Erro erro, TreeMap<String, Peca> pecasMap, TreeMap<String, Elemento> elementosMap, TreeMap<String, Obra> obrasMap) {
        try{
            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_error_item, listaLayout ,false);
            CardView statusCard = obj.findViewById(R.id.statusCard);
            RelativeLayout layoutSolucao = obj.findViewById(R.id.layoutSolucao);
            TextView creationData = obj.findViewById(R.id.creationData);
            TextView creationHora = obj.findViewById(R.id.creationHora);
            String[] creation = Objects.requireNonNull(erro.getCreation()).split(" ");
            creationData.setText(creation[0]);
            creationHora.setText(creation[1]);
            TextView solutionData = obj.findViewById(R.id.solutionData);
            TextView solutionHora = obj.findViewById(R.id.solutionHora);
            TextView traceData = obj.findViewById(R.id.traceData);
            if(erro.getStatus().equals(Erro.statusFechado)) {
                layoutSolucao.setVisibility(View.VISIBLE);
                statusCard.setCardBackgroundColor(getColor(R.color.soft_green));
                TextView lastModifiedBy = obj.findViewById(R.id.lastModifiedBy);
                lastModifiedBy.setText(erro.getLastModifiedBy());
                lastModifiedBy.setText(erro.getLastModifiedBy() != null && apiErros.getUsersMap() != null ? apiErros.getUsersMap().get(erro.getLastModifiedBy()) : "");
                TextView comentarios_solucao = obj.findViewById(R.id.comentarios_solucao);
                comentarios_solucao.setText(erro.getComentarios_solucao());
                solutionData.setVisibility(View.VISIBLE);
                solutionHora.setVisibility(View.VISIBLE);
                traceData.setVisibility(View.VISIBLE);
                String[] lastModifiedOn = Objects.requireNonNull(erro.getLastModifiedOn()).split(" ");
                solutionData.setText(lastModifiedOn[0]);
                solutionHora.setText(lastModifiedOn[1]);
            }
            TextView status = obj.findViewById(R.id.status);

            TextView nome_obra = obj.findViewById(R.id.nome_obra);
            TextView etapa = obj.findViewById(R.id.etapa);
            TextView elemento = obj.findViewById(R.id.nome_elemento);
            TextView nome_peca = obj.findViewById(R.id.nome_peca);
            TextView margin_peca = obj.findViewById(R.id.margin_peca);
            TextView tag = obj.findViewById(R.id.tag);
            TextView createdBy = obj.findViewById(R.id.createdBy);
            TextView etapa_detectada = obj.findViewById(R.id.etapa_detectada);
            TextView erros = obj.findViewById(R.id.erros);
            TextView comentarios = obj.findViewById(R.id.comentarios);
            RelativeLayout tagLayout = obj.findViewById(R.id.tagLayout);
            status.setText(erro.getStatus().toUpperCase());

            nome_obra.setText((obrasMap.get(erro.getObra()) != null) ? obrasMap.get(erro.getObra()).getNome_obra() : "");
            etapa.setText(prettyShortEtapas.get(erro.getEtapa_detectada()));
            if(erro.getEtapa_detectada().equals(planejamentoKey)){
                tagLayout.setVisibility(View.GONE);
                nome_peca.setVisibility(View.GONE);
                margin_peca.setVisibility(View.GONE);
                elemento.setText((elementosMap.get(erro.getElemento()) != null) ? elementosMap.get(erro.getElemento()).getNome() : "");
            }else if(erro.getEtapa_detectada().equals(cadastroKey)){
                nome_peca.setVisibility(View.GONE);
                margin_peca.setVisibility(View.GONE);
                tag.setText(pecasMap.get(erro.getPeca()) != null ? pecasMap.get(erro.getPeca()).getQrCode() : "");
                Peca peca = (pecasMap.get(erro.getPeca()) != null) ? pecasMap.get(erro.getPeca()) : null;
                nome_peca.setText((peca != null && peca.getNome_peca() != null) ? peca.getNome_peca() : "Tag não Cadastrada");
                elemento.setText((peca != null && peca.getElementoObject() != null) ? peca.getElementoObject().getNome() : "Tag não Cadastrada");
            }else{
                Peca peca = (pecasMap.get(erro.getPeca()) != null) ? pecasMap.get(erro.getPeca()) : null;
                tag.setText(peca != null ? peca.getQrCode() : "");
                nome_peca.setText((peca != null && peca.getNome_peca() != null) ? peca.getNome_peca() : "");
                elemento.setText((peca != null && peca.getElementoObject() != null) ? peca.getElementoObject().getNome() : "");
            }
            createdBy.setText(erro.getCreatedBy() != null && apiErros.getUsersMap() != null ? apiErros.getUsersMap().get(erro.getCreatedBy()) : "");
            etapa_detectada.setText(erro.getEtapa_detectada());
            erros.setText(erro.getItem());
            comentarios.setText(erro.getComentarios());
            obj.setOnClickListener(view -> {
                if(erro.getStatus().equals(Erro.statusFechado)){
                    Toast.makeText(this, "Essa inconformidade já foi selecionada!", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(this, SolucionarErro.class);
                Erro tagErro = (Erro) obj.getTag();
                intent.putExtra("dependency", new Object[]{tagErro, (tagErro.getEtapa_detectada().equals(planejamentoKey)) ? elementosMap.get(tagErro.getElemento()) : pecasMap.get(tagErro.getPeca())});
                solucionarErro.launch(intent);
            });
            obj.setTag(erro);
            listaLayout.addView(obj);
        }
        catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    ActivityResultLauncher<Intent> setFilters = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try{
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                errorFilter = (ErrorFilter) data.getSerializableExtra("result");
                Log.d("RELATORIO", errorFilter.toString());
                loadFilters(errorFilter);
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        }
    });

    ActivityResultLauncher<Intent> solucionarErro = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Activity act = RelatorioDeErros.this;
            Intent intent=new Intent();
            intent.setClass(act, act.getClass());
            act.startActivity(intent);
            act.finish();
        }
    });

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(duploClique.detectado()){
            return;
        }
        finish();
    }
}