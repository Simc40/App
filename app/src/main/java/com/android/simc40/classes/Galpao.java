package com.android.simc40.classes;

import java.io.Serializable;

public class Galpao implements Serializable {
    String uid;
    String nome;
    String status;
    String creation;
    String createdBy;
    String lastModifiedOn;
    String lastModifiedBy;

    public Galpao(){}

    public Galpao(
            String uid,
            String nome,
            String status,
            String creation,
            String createdBy,
            String lastModifiedOn,
            String lastModifiedBy
    ){
        this.uid = (uid != null) ? uid : "";
        this.nome = (nome != null) ? nome : "";
        this.status = (status != null) ? status : "";
        this.creation = (creation != null) ? creation : "";
        this.createdBy = (createdBy != null) ? createdBy : "";
        this.lastModifiedOn = (lastModifiedOn != null) ? lastModifiedOn : "";
        this.lastModifiedBy = (lastModifiedBy != null) ? lastModifiedBy : "";
    }

    public String getUid() {return (uid != null) ? uid : "";}
    public String getNome() {return (nome != null) ? nome : "";}
    public String getStatus() {return (status != null) ? status : "";}
    public String getCreation() {return (creation != null) ? creation : "";}
    public String getCreatedBy() {return (createdBy != null) ? createdBy : "";}
    public String getLastModifiedOn() {return (lastModifiedOn != null) ? lastModifiedOn : "";}
    public String getLastModifiedBy() {return (lastModifiedBy != null) ? lastModifiedBy : "";}
}
