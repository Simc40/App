package com.android.simc40.classes;

import com.android.simc40.checklist.Etapas;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Peca implements Serializable, Etapas, LayoutExceptionErrorList {
    Obra obra;
    Elemento elemento;
    String etapaAtual;
    String nomePeca;
    String tag;

    public Peca(String etapaAtual) throws Exception {
        if(!Arrays.asList(allEtapas).contains(etapaAtual)) throw new Exception();
    }

    public Peca(String tag, String etapaAtual, Obra obra, Elemento elemento, String nomePeca) {
        this.tag = tag;
        this.etapaAtual = etapaAtual;
        this.obra = obra;
        this.elemento = elemento;
        this.nomePeca = nomePeca;
    }

    @Override
    public String toString() {
        return "Peca{" +
                "obra=" + obra +
                ", elemento=" + elemento +
                ", etapaAtual='" + etapaAtual + '\'' +
                ", nomePeca='" + nomePeca + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }

    public Obra getObra() {return obra;}
    public Elemento getElemento() {return elemento;}
    public void setObra(Obra obra) {this.obra = obra;}
    public void setElemento(Elemento elemento) {this.elemento = elemento;}
}


