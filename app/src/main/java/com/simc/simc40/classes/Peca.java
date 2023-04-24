package com.simc.simc40.classes;

import com.simc.simc40.checklist.Etapas;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

public class Peca implements Serializable, Etapas, LayoutExceptionErrorList {
    Obra obraObject;
    Elemento elementoObject;
    Galpao galpaoObject;
    String elemento;
    String etapa_atual;
    Map<String, Object> etapas;
    String lastModifiedBy;
    String lastModifiedOn;
    String nome_peca;
    String num;
    String obra;
    String qrCode;
    String tag;
    String uid;

    public Peca() {

    }

    public Peca(String etapa_atual) throws Exception {
        if(!Arrays.asList(allEtapas).contains(etapa_atual)) throw new Exception();
        this.etapa_atual = etapa_atual;
    }

    public Peca(String uid, String tag, String qrCode, String etapa_atual, Obra obraObject, Elemento elementoObject, String nome_peca) {
        this.uid = uid;
        this.tag = tag;
        this.qrCode = qrCode;
        this.etapa_atual = etapa_atual;
        this.obraObject = obraObject;
        this.elementoObject = elementoObject;
        this.nome_peca = nome_peca;
    }

    public void formatVariable(Peca new_peca) {
        this.tag = new_peca.getTag();
        this.etapa_atual = new_peca.getEtapa_atual();
        this.obraObject = new_peca.getObraObject();
        this.elementoObject = new_peca.getElementoObject();
        this.nome_peca = new_peca.getNome_peca();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peca peca = (Peca) o;
        return tag.equals(peca.getTag());
    }

    @Override
    public String toString() {
        return "Peca{" +
                "obraObject=" + obraObject +
                ", elementoObject=" + elementoObject +
                ", galpaoObject=" + galpaoObject +
                ", elemento='" + elemento + '\'' +
                ", etapa_atual='" + etapa_atual + '\'' +
                ", etapas=" + etapas +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedOn='" + lastModifiedOn + '\'' +
                ", nome_peca='" + nome_peca + '\'' +
                ", num='" + num + '\'' +
                ", obra='" + obra + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", tag='" + tag + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

    public String getNome_peca() {return nome_peca; }
    public String getTag() {return (tag != null) ? tag : "";}
    public String getEtapa_atual() {return etapa_atual;}
    public Obra getObraObject() {return obraObject;}
    public Elemento getElementoObject() {return elementoObject;}
    public Galpao getGalpaoObject() {return galpaoObject;}
    public String getUid() {return uid;}
    public String getQrCode() {return qrCode;}

    public void setObraObject(Obra obraObject) {this.obraObject = obraObject;}
    public void setTag(String tag) {this.tag = tag;}
    public void setElementoObject(Elemento elementoObject) {this.elementoObject = elementoObject;}
    public void setGalpaoObject(Galpao galpaoObject) {this.galpaoObject = galpaoObject;}
    public void setElemento(String elemento) {this.elemento = elemento;}
    public void setEtapa_atual(String etapa_atual) {this.etapa_atual = etapa_atual;}
    public void setEtapas(Map<String, Object> etapas) {this.etapas = etapas;}
    public void setLastModifiedBy(String lastModifiedBy) {this.lastModifiedBy = lastModifiedBy;}
    public void setLastModifiedOn(String lastModifiedOn) {this.lastModifiedOn = lastModifiedOn;}
    public void setNome_peca(String nome_peca) {this.nome_peca = nome_peca;}
    public void setNum(String num) {this.num = num;}
    public void setObra(String obra) {this.obra = obra;}
    public void setQrCode(String qrCode) {this.qrCode = qrCode;}
    public void setUid(String uid) {this.uid = uid;}


}


