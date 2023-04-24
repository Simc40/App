package com.simc.simc40.home;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.simc.simc40.moduloQualidade.ModuloDeMontagem;
import com.simc.simc40.moduloQualidade.ModuloDeProducao;
import com.simc.simc40.moduloQualidade.ModuloDeTransporte;
import com.simc.simc40.moduloQualidade.relatorioErros.RelatorioDeErros;

public class ModuloDeQualidade extends Fragment {


    DuploClique duploClique = new DuploClique();
    CardView card1, card2, card3, card4;
    TextView goBack;

    public ModuloDeQualidade() {}

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
        goBack = requireView().findViewById(R.id.goBack);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_modulo_de_qualidade, container, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews();

        card1.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            ((MainActivity) getActivity()).replaceFragments(ModuloDeProducao.class);
        });
        card2.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(this.getActivity(), ModuloDeTransporte.class));
        });
        card3.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(this.getActivity(), ModuloDeMontagem.class));
        });
        card4.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(this.getActivity(), RelatorioDeErros.class));
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