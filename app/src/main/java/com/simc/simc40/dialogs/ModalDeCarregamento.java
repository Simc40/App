package com.simc.simc40.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.simc.simc40.R;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;

public class ModalDeCarregamento implements FirebaseDatabaseExceptionErrorList {

    AlertDialog alertDialog;
    public Boolean estaVisivel = false;
    ModalAlertaDeErro modalAlertaDeErro;
    Activity atividade;
    View layout;
    TextView textoDePorcentagem;
    TextView textoDeCarregamento;
    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet;
    Animation animacao;
    int totalDePassos;
    float intervaloDeCarregamentoPorPasso;
    int passo;

    @SuppressLint("InflateParams")
    public ModalDeCarregamento(Activity atividade, ModalAlertaDeErro modalAlertaDeErro){
        this.atividade = atividade;
        this.modalAlertaDeErro = modalAlertaDeErro;
        AlertDialog.Builder builder = new AlertDialog.Builder(atividade);

        LayoutInflater inflater = atividade.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_loading, null);
        this.layout = view;

        textoDeCarregamento = view.findViewById(R.id.loadingText);

        animacao = new AlphaAnimation(0.0f, 1.0f);
        animacao.setDuration(2000); //You can manage the blinking time with this parameter
        animacao.setStartOffset(20);
        animacao.setRepeatMode(Animation.REVERSE);
        animacao.setRepeatCount(Animation.INFINITE);

        textoDePorcentagem = view.findViewById(R.id.percentage);

        definirPorcentagemDeCarregamento(20);

        builder.setView(view);
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.getWindow().getDecorView().setBackgroundResource(R.drawable.loading_page_background);
    }

    public void definirPorcentagemDeCarregamento(float porcentagem){
        if(porcentagem > 100 || porcentagem < 0) return;
        porcentagem = porcentagem/100;
        constraintLayout = layout.findViewById(R.id.loadingBarConstraint);
        constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.constrainPercentWidth(R.id.loadingBar, porcentagem);
        constraintSet.applyTo(constraintLayout);

        String stringDePorcentagem = (int) (porcentagem * 100) + "%";
        textoDePorcentagem.setText(stringDePorcentagem);
    }

    public void mostrarModalDeCarregamento(int totalDePassos){
        this.totalDePassos = totalDePassos;
        this.passo = 0;
        definirPorcentagemDeCarregamento(0);
        this.intervaloDeCarregamentoPorPasso = (float) 100/totalDePassos;
        textoDeCarregamento.startAnimation(animacao);
        alertDialog.show();
        estaVisivel = true;
    }

    public void continueLoadingDialog(){
        textoDeCarregamento.startAnimation(animacao);
        alertDialog.show();
        estaVisivel = true;
    }

    public void avancarPasso(){
        if(!estaVisivel) return;
        if(passo > totalDePassos) return;
        passo++;
        definirPorcentagemDeCarregamento(passo * intervaloDeCarregamentoPorPasso);
    }

    public void passoFinal(){
        if(!estaVisivel) return;
        definirPorcentagemDeCarregamento(100);
    }

    public void fecharModal(){
        textoDeCarregamento.clearAnimation();
        alertDialog.dismiss();
        estaVisivel = false;
    }
}
