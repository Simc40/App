package com.android.simc40.classes;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Obra implements Serializable, LayoutExceptionErrorList {
    String uid;
    String nomeObra;
    String responsavel;
    String tipoConstrucao;
    String cep;
    String cidade;
    String bairro;
    String endereco;
    String uf;
    String pdfUrl;
    String pecasPlanejadas;
    String previsaoInicio;
    String previsaoFim;
    String status;
    String creation;
    String createdBy;
    String lastModifiedOn;
    String lastModifiedBy;

    public Obra(){}

    public Obra(
        String uid,
        String nomeObra,
        String responsavel,
        String tipoConstrucao,
        String cep,
        String cidade,
        String bairro,
        String endereco,
        String uf,
        String pecasPlanejadas,
        String previsaoInicio,
        String previsaoFim,
        String status,
        String creation,
        String createdBy,
        String lastModifiedOn,
        String lastModifiedBy,
        String pdfUrl
    ){
        this.uid = (uid != null) ? uid : "";
        this.nomeObra = (nomeObra != null) ? nomeObra : "";
        this.responsavel = (responsavel != null) ? responsavel : "";
        this.tipoConstrucao = (tipoConstrucao != null) ? tipoConstrucao : "";
        this.cep = (cep != null) ? cep : "";
        this.cidade = (cidade != null) ? cidade : "";
        this.bairro = (bairro != null) ? bairro : "";
        this.endereco = (endereco != null) ? endereco : "";
        this.uf = (uf != null) ? uf : "";
        this.pecasPlanejadas = (pecasPlanejadas != null) ? pecasPlanejadas : "";
        this.previsaoInicio = (previsaoInicio != null) ? previsaoInicio : "";
        this.previsaoFim = (previsaoFim != null) ? previsaoFim : "";
        this.status = (status != null) ? status : "";
        this.creation = (creation != null) ? creation : "";
        this.createdBy = (createdBy != null) ? createdBy : "";
        this.lastModifiedOn = (lastModifiedOn != null) ? lastModifiedOn : "";
        this.lastModifiedBy = (lastModifiedBy != null) ? lastModifiedBy : "";
        this.pdfUrl = (pdfUrl != null) ? pdfUrl : "";
    }

    @NonNull
    @Override
    public String toString() {
        return "Obra{" + "\n" +
                "uid='" + uid + '\'' + "\n" +
                ", nomeObra='" + nomeObra + '\'' + "\n" +
                ", responsavel='" + responsavel + '\'' + "\n" +
                ", tipoConstrucao='" + tipoConstrucao + '\'' + "\n" +
                ", cep='" + cep + '\'' + "\n" +
                ", cidade='" + cidade + '\'' + "\n" +
                ", bairro='" + bairro + '\'' + "\n" +
                ", endereco='" + endereco + '\'' + "\n" +
                ", uf='" + uf + '\'' + "\n" +
                ", pdfUrl='" + pdfUrl + '\'' + "\n" +
                ", pecasPlanejadas='" + pecasPlanejadas + '\'' + "\n" +
                ", previsaoInicio='" + previsaoInicio + '\'' + "\n" +
                ", previsaoFim='" + previsaoFim + '\'' + "\n" +
                ", creation='" + creation + '\'' + "\n" +
                ", createdBy='" + createdBy + '\'' + "\n" +
                ", lastModifiedOn='" + lastModifiedOn + '\'' + "\n" +
                ", lastModifiedBy='" + lastModifiedBy + '\'' + "\n" +
                '}';
    }

    public void setUid(String uid) throws LayoutException {
        if(uid == null) throw new LayoutException(EXCEPTION_NULL_UID);
        this.uid = uid;
    }

    public String getUid() {return (uid != null) ? uid : "";}
    public String getNomeObra() {return (nomeObra != null) ? nomeObra : "";}
    public String getResponsavel() {return (responsavel != null) ? responsavel : "";}
    public String getTipoConstrucao() {return (tipoConstrucao != null) ? tipoConstrucao : "";}
    public String getCep() {return (cep != null) ? cep : "";}
    public String getCidade() {return (cidade != null) ? cidade : "";}
    public String getBairro() {return (bairro != null) ? bairro : "";}
    public String getEndereco() {return (endereco != null) ? endereco : "";}
    public String getUf() {return (uf != null) ? uf : "";}
    public String getPecasPlanejadas() {return (pecasPlanejadas != null) ? pecasPlanejadas : "";}
    public String getPrevisaoInicio() {return (previsaoInicio != null) ? previsaoInicio : "";}
    public String getPrevisaoFim() {return (previsaoFim != null) ? previsaoFim : "";}
    public String getStatus() {return (status != null) ? status : "";}
    public String getCreation() {return (creation != null) ? creation : "";}
    public String getCreatedBy() {return (createdBy != null) ? createdBy : "";}
    public String getLastModifiedOn() {return (lastModifiedOn != null) ? lastModifiedOn : "";}
    public String getLastModifiedBy() {return (lastModifiedBy != null) ? lastModifiedBy : "";}
    public String getPdfUrl() {return (pdfUrl != null) ? pdfUrl : "";}
}
