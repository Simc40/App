package com.simc.simc40.moduloGerenciamento;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simc.simc40.R;
import com.simc.simc40.classes.Motorista;
import com.simc.simc40.classes.Transportadora;
import com.simc.simc40.classes.Veiculo;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;
import com.simc.simc40.selecaoListas.SelecaoListaTransportadoras;
import com.simc.simc40.touchListener.TouchListener;

import java.util.TreeMap;

public class GerenciamentoTransportadoras extends AppCompatActivity implements LayoutExceptionErrorList {

    LinearLayout header;
    LinearLayout listaLayout;
    CardView cardViewVeiculos, cardViewMotoristas;
    TextView goBack, title;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    DuploClique duploClique = new DuploClique();
    Transportadora transportadora;
    TreeMap <String, Motorista> motoristasMap;
    TreeMap <String, Veiculo> veiculosMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenciamento_transportadoras);

        header = findViewById(R.id.header);
        listaLayout = findViewById(R.id.listaLayout);
        cardViewMotoristas = findViewById(R.id.cardViewMotoristas);
        cardViewVeiculos = findViewById(R.id.cardViewVeiculos);

        title = findViewById(R.id.title);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(view -> {
            if (duploClique.detectado()) return;
            finish();
        });

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);

        Intent intent = new Intent(this, SelecaoListaTransportadoras.class);
        selectTransportadoraFromList.launch(intent);
    }

    ActivityResultLauncher<Intent> selectTransportadoraFromList = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        Intent data = result.getData();
                        if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                        transportadora = (Transportadora) data.getSerializableExtra("result");
                        fill_fields(transportadora);
                    } catch (LayoutException e) {
                        ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                    }
                } else {
                    finish();
                }
            });

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    void fill_fields(Transportadora transportadora) throws LayoutException {
        try {
            title.setText(transportadora.getNome());
            motoristasMap = transportadora.getMotoristas();
            veiculosMap = transportadora.getVeiculos();

            cardViewMotoristas.setOnTouchListener(TouchListener.getTouch());
            cardViewMotoristas.setOnClickListener(view -> {
                header.removeAllViews();
                listaLayout.removeAllViews();
                if(motoristasMap == null || motoristasMap.isEmpty()){
                    View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_header, header ,false);
                    TextView item1 = headerXML.findViewById(R.id.item1);
                    String Text1 = "Nenhum Registro de Motorista Encontrado.";
                    item1.setText(Text1);
                    header.addView(headerXML);
                }else{
                    View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item_header, header ,false);
                    TextView item1 = headerXML.findViewById(R.id.item1);
                    String Text1 = "Telefone";
                    item1.setText(Text1);
                    TextView item2 = headerXML.findViewById(R.id.item2);
                    String Text2 = "Motoristas";
                    item2.setText(Text2);
                    TextView item3 = headerXML.findViewById(R.id.item3);
                    String Text3 = "Status";
                    item3.setText(Text3);
                    header.addView(headerXML);

                    for(Motorista motorista : motoristasMap.values()){
                        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_4_list_item, listaLayout, false);
                        TextView objItem1 = obj.findViewById(R.id.item1);
                        TextView objItem2 = obj.findViewById(R.id.item2);
                        TextView objItem3 = obj.findViewById(R.id.item3);
                        ImageView objItem4 = obj.findViewById(R.id.item4);
                        objItem1.setText(motorista.getTelefone());
                        objItem2.setText(motorista.getNome());
                        objItem3.setText(motorista.getStatus());
                        objItem4.setImageResource(R.drawable.forward);
                        objItem4.setTag(motorista);
                        objItem4.setOnClickListener(view1 -> {
                            try {
                                Motorista selectedMotorista = (Motorista) objItem4.getTag();
                                Intent intent = new Intent(this, GerenciamentoMotoristas.class);
                                intent.putExtra("dependency", new Object[]{transportadora, selectedMotorista});
                                startActivity (intent);
                            } catch (Exception e) {
                                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                            }
                        });
                        listaLayout.addView(obj);
                    }
                }
            });
            cardViewVeiculos.setOnTouchListener(TouchListener.getTouch());
            cardViewVeiculos.setOnClickListener(view -> {
                header.removeAllViews();
                listaLayout.removeAllViews();
                if(veiculosMap == null || veiculosMap.isEmpty()){
                    View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_2_list_item_header, header ,false);
                    TextView item1 = headerXML.findViewById(R.id.item1);
                    String Text1 = "Nenhum Registro de Veículo Encontrado.";
                    item1.setText(Text1);
                    header.addView(headerXML);
                }else{
                    View headerXML = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_5_list_item_header, header ,false);
                    TextView item1 = headerXML.findViewById(R.id.item1);
                    String Text1 = "Placa";
                    item1.setText(Text1);
                    TextView item2 = headerXML.findViewById(R.id.item2);
                    String Text2 = "Marca";
                    item2.setText(Text2);
                    TextView item3 = headerXML.findViewById(R.id.item3);
                    String Text3 = "Capacidade";
                    item3.setText(Text3);
                    item3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                    TextView item4 = headerXML.findViewById(R.id.item4);
                    String Text4 = "Status";
                    item4.setText(Text4);
                    header.addView(headerXML);

                    for(Veiculo veiculo : veiculosMap.values()){
                        View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_5_list_item, listaLayout, false);
                        TextView objItem1 = obj.findViewById(R.id.item1);
                        TextView objItem2 = obj.findViewById(R.id.item2);
                        TextView objItem3 = obj.findViewById(R.id.item3);
                        TextView objItem4 = obj.findViewById(R.id.item4);
                        ImageView objItem5 = obj.findViewById(R.id.item5);
                        objItem1.setText(veiculo.getPlaca());
                        objItem2.setText(veiculo.getMarca());
                        objItem3.setText(veiculo.getCapacidadeCarga() + "Kg");
                        objItem4.setText(veiculo.getStatus());
                        objItem5.setImageResource(R.drawable.forward);
                        objItem5.setTag(veiculo);
                        objItem5.setOnClickListener(view1 -> {
                            try {
                                Veiculo selectedVeiculo = (Veiculo) objItem5.getTag();
                                Intent intent = new Intent(this, GerenciamentoVeiculos.class);
                                intent.putExtra("dependency", new Object[]{transportadora, selectedVeiculo});
                                startActivity (intent);
                            } catch (Exception e) {
                                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                            }
                        });
                        listaLayout.addView(obj);
                    }
                }
            });

        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }
    }
}