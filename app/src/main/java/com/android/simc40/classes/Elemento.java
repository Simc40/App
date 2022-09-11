package com.android.simc40.classes;

import android.annotation.SuppressLint;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.simc40.Images.DownloadImage;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Elemento implements Serializable, LayoutExceptionErrorList {
    Obra obra;
    Forma forma;
    TipoDePeca tipoDePeca;
    String uid;
    String nome;
    String b;
    String h;
    String c;
    String pecasPlanejadas;
    String pecasCadastradas;
    String pdfUrl;
    String fckDesf;
    String fckIc;
    String volume;
    String peso;
    String pesoAco;
    String status;
    String creation;
    String createdBy;
    String lastModifiedOn;
    String lastModifiedBy;

    ImageView imageViewImagemTipo;
    TextView textViewNomeObra;
    TextView textViewNome;
    TextView textViewB;
    TextView textViewH;
    TextView textViewC;
    TextView textViewTipoPeca;
    TextView textViewPecasPlanejadas;
    TextView textViewPecasCadastradas;
    TextView textViewForma;
    TextView textViewFckDesf;
    TextView textViewFckIc;
    TextView textViewVolume;
    TextView textViewPeso;
    TextView textViewPesoAco;
    TextView textViewCreation;
    TextView textViewCreatedBy;
    TextView textViewLastModifiedOn;
    TextView textViewLastModifiedBy;

    public Elemento(){}

    public Elemento(
        Obra obra,
        Forma forma,
        TipoDePeca tipoDePeca,
        String uid,
        String nome,
        String b,
        String h,
        String c,
        String pecasPlanejadas,
        String pecasCadastradas,
        String fckDesf,
        String fckIc,
        String volume,
        String peso,
        String pesoAco,
        String status,
        String creation,
        String createdBy,
        String lastModifiedOn,
        String lastModifiedBy,
        String pdfUrl
    ){
        this.obra = (obra != null) ? obra : new Obra();
        this.forma = (forma != null) ? forma : new Forma();
        this.tipoDePeca = (tipoDePeca != null) ? tipoDePeca : new TipoDePeca();
        this.uid = (uid != null) ? uid : "";
        this.nome = (nome != null) ? nome : "";
        this.b = (b != null) ? b : "";
        this.h = (h != null) ? h : "";
        this.c = (c != null) ? c : "";
        this.pecasPlanejadas = (pecasPlanejadas != null) ? pecasPlanejadas : "";
        this.pecasCadastradas = (pecasCadastradas != null) ? pecasCadastradas : "";
        this.fckDesf = (fckDesf != null) ? fckDesf : "";
        this.fckIc = (fckIc != null) ? fckIc : "";
        this.volume = (volume != null) ? volume : "";
        this.peso = (peso != null) ? peso : "";
        this.pesoAco = (pesoAco != null) ? pesoAco : "";
        this.status = (status != null) ? status : "";
        this.creation = (creation != null) ? creation : "";
        this.createdBy = (createdBy != null) ? createdBy : "";
        this.lastModifiedOn = (lastModifiedOn != null) ? lastModifiedOn : "";
        this.lastModifiedBy = (lastModifiedBy != null) ? lastModifiedBy : "";
        this.pdfUrl = (pdfUrl != null) ? pdfUrl : "";
    }

    @Override
    public String toString() {
        return "Elemento{" + "\n" + "obra=" + obra + "\n" + ", forma=" + forma + "\n" + ", tipoDePeca=" + tipoDePeca + "\n" + ", uid='" + uid + '\'' + "\n" + ", nome='" + nome + '\'' + "\n" + ", b='" + b + '\'' + "\n" + ", h='" + h + '\'' + "\n" + ", c='" + c + '\'' + "\n" + ", pecasPlanejadas='" + pecasPlanejadas + '\'' + "\n" + ", pecasCadastradas='" + pecasCadastradas + '\'' + "\n" + ", fckDesf='" + fckDesf + '\'' + "\n" + ", fckIc='" + fckIc + '\'' + "\n" + ", volume='" + volume + '\'' + "\n" + ", peso='" + peso + '\'' + "\n" + ", pesoAco='" + pesoAco + '\'' + "\n" + ", status='" + status + '\'' + "\n" + ", creation='" + creation + '\'' + "\n" + ", createdBy='" + createdBy + '\'' + "\n" + ", lastModifiedOn='" + lastModifiedOn + '\'' + "\n" + ", lastModifiedBy='" + lastModifiedBy + '\'' + "\n" + '}';
    }

    public Obra getObra() {return (obra != null) ? obra : new Obra();}
    public Forma getForma() {return (forma != null) ? forma : new Forma();}
    public TipoDePeca getTipoDePeca() {return (tipoDePeca != null) ? tipoDePeca : new TipoDePeca();}
    public String getUid() {return (uid != null) ? uid : "";}
    public String getNome() {return (nome != null) ? nome : "";}
    public String getB() {return (b != null) ? b : "";}
    public String getH() {return (h != null) ? h : "";}
    public String getC() {return (c != null) ? c : "";}
    public String getPecasPlanejadas() {return (pecasPlanejadas != null) ? pecasPlanejadas : "";}
    public String getPecasCadastradas() {return (pecasCadastradas != null) ? pecasCadastradas : "";}
    public String getFckDesf() {return (fckDesf != null) ? fckDesf : "";}
    public String getFckIc() {return (fckIc != null) ? fckIc : "";}
    public String getVolume() {return (volume != null) ? volume : "";}
    public String getPeso() {return (peso != null) ? peso : "";}
    public String getPesoAco() {return (pesoAco != null) ? pesoAco : "";}
    public String getStatus() {return (status != null) ? status : "";}
    public String getCreation() {return (creation != null) ? creation : "";}
    public String getCreatedBy() {return (createdBy != null) ? createdBy : "";}
    public String getLastModifiedOn() {return (lastModifiedOn != null) ? lastModifiedOn : "";}
    public String getLastModifiedBy() {return (lastModifiedBy != null) ? lastModifiedBy : "";}
    public String getPdfUrl() {return (pdfUrl != null) ? pdfUrl : "";}
}
