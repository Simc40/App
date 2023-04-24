package com.simc.simc40.moduloQualidade;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.simc.simc40.MainActivity;
import com.simc.simc40.R;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.moduloQualidade.moduloProducao.Armacao;
import com.simc.simc40.moduloQualidade.moduloProducao.ArmacaoCForma;
import com.simc.simc40.moduloQualidade.moduloProducao.Cadastro;
import com.simc.simc40.moduloQualidade.moduloProducao.Concretagem;
import com.simc.simc40.moduloQualidade.moduloProducao.Forma;

public class ModuloDeProducao extends Fragment {


    DuploClique duploClique = new DuploClique();
    CardView card1, card2, card3, card4, card5, card6;
    TextView goBack;

    public ModuloDeProducao() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setViews(){
        card1 = requireView().findViewById(R.id.card1);
        card2 = requireView().findViewById(R.id.card2);
        card3 = requireView().findViewById(R.id.card3);
        card4 = requireView().findViewById(R.id.card4);
        card5 = requireView().findViewById(R.id.card5);
        card6 = requireView().findViewById(R.id.card6);
        goBack = requireView().findViewById(R.id.goBack);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mproducao_modulo_de_producao, container, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews();

        card1.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            ((MainActivity) getActivity()).replaceFragments(Cadastro.class);
        });
        card2.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            ((MainActivity) getActivity()).replaceFragments(Armacao.class);
        });
        card3.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            ((MainActivity) getActivity()).replaceFragments(Forma.class);
        });
        card4.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            ((MainActivity) getActivity()).replaceFragments(ArmacaoCForma.class);
        });
        card5.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            ((MainActivity) getActivity()).replaceFragments(Concretagem.class);
        });
        card6.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
//            startActivity(new Intent(this, Liberacao.class));
        });

        goBack.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            getActivity().onBackPressed();
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}