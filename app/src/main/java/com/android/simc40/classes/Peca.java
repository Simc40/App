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
        this.etapaAtual = etapaAtual;
    }

    public Peca(String tag, String etapaAtual, Obra obra, Elemento elemento, String nomePeca) {
        this.tag = tag;
        this.etapaAtual = etapaAtual;
        this.obra = obra;
        this.elemento = elemento;
        this.nomePeca = nomePeca;
    }

    public void formatVariable(Peca new_peca) {
        this.tag = new_peca.getTag();
        this.etapaAtual = new_peca.getEtapaAtual();
        this.obra = new_peca.getObra();
        this.elemento = new_peca.getElemento();
        this.nomePeca = new_peca.getNomePeca();
    }

    @Override
    public String toString() {
        return "Peca{" +
                "obra=" + obra.getNomeObra() +
                ", elemento=" + elemento.getNome() +
                ", etapaAtual='" + etapaAtual + '\'' +
                ", nomePeca='" + nomePeca + '\'' +
                ", tag='" + tag + '\'' +
                '}' + '\'';
    }

    public String getNomePeca() {return nomePeca; }
    public String getTag() {return tag;}
    public void setTag(String tag) {this.tag = tag;}
    public String getEtapaAtual() {return etapaAtual;}
    public Obra getObra() {return obra;}
    public Elemento getElemento() {return elemento;}
    public void setObra(Obra obra) {this.obra = obra;}
    public void setElemento(Elemento elemento) {this.elemento = elemento;}
    public void setNomePeca(int readed_tags) {this.nomePeca = elemento.getNome() + "-" + (Integer.parseInt(elemento.getPecasCadastradas()) + 1 + readed_tags);

    }
}


