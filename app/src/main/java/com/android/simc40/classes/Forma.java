package com.android.simc40.classes;

import java.io.Serializable;

public class Forma implements Serializable {
    String uid;
    String nome;
    String b;
    String h;
    String nome_forma;
    String status;
    String creation;
    String createdBy;
    String lastModifiedOn;
    String lastModifiedBy;

    public Forma(){}

    public Forma(
        String uid,
        String b,
        String h,
        String nome_forma,
        String status,
        String creation,
        String createdBy,
        String lastModifiedOn,
        String lastModifiedBy
    ){
        this.uid = (uid != null) ? uid : "";
        this.b = (b != null) ? b : "";
        this.h = (h != null) ? h : "";
        this.nome_forma = (nome_forma != null) ? nome_forma : "";
        this.status = (status != null) ? status : "";
        this.creation = (creation != null) ? creation : "";
        this.createdBy = (createdBy != null) ? createdBy : "";
        this.lastModifiedOn = (lastModifiedOn != null) ? lastModifiedOn : "";
        this.lastModifiedBy = (lastModifiedBy != null) ? lastModifiedBy : "";
        this.nome = nome_forma + " - " + getB() + "cm x " + getH() + "cm";
    }

    public String getUid() {return (uid != null) ? uid : "";}
    public String getB() {return (b != null) ? b : "";}
    public String getH() {return (h != null) ? h : "";}
    public String getNome() {return (nome != null) ? nome : "";}
    public String getStatus() {return (status != null) ? status : "";}
    public String getCreation() {return (creation != null) ? creation : "";}
    public String getCreatedBy() {return (createdBy != null) ? createdBy : "";}
    public String getLastModifiedOn() {return (lastModifiedOn != null) ? lastModifiedOn : "";}
    public String getLastModifiedBy() {return (lastModifiedBy != null) ? lastModifiedBy : "";}

    @Override
    public String toString() {
        return "Forma{" + "uid='" + uid + '\'' + ", nome='" + nome + '\'' + ", b='" + b + '\'' + ", h='" + h + '\'' + ", nome_forma='" + nome_forma + '\'' + ", status='" + status + '\'' + ", creation='" + creation + '\'' + ", createdBy='" + createdBy + '\'' + ", lastModifiedOn='" + lastModifiedOn + '\'' + ", lastModifiedBy='" + lastModifiedBy + '\'' + '}';
    }
}


