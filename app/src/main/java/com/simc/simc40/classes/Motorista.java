package com.simc.simc40.classes;

import java.io.Serializable;

public class Motorista implements Serializable {
    String cnh;
    String email;
    String uidTransportadora;
    String imgUrl;
    String nome;
    String status;
    String telefone;
    String uid;
    String creation;
    String createdBy;
    String lastModifiedOn;
    String lastModifiedBy;

    public Motorista(String uid, String cnh, String email, String uidTransportadora, String imgUrl, String nome, String status, String telefone, String creation, String createdBy, String lastModifiedOn, String lastModifiedBy) {
        this.uid = uid;
        this.cnh = cnh;
        this.email = email;
        this.uidTransportadora = uidTransportadora;
        this.imgUrl = imgUrl;
        this.nome = nome;
        this.status = status;
        this.telefone = telefone;
        this.creation = creation;
        this.createdBy = createdBy;
        this.lastModifiedOn = lastModifiedOn;
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getCnh() {return cnh;}
    public String getEmail() {return (email != null) ? email : "";}
    public String getNome() {return (nome != null) ? nome : "";}
    public String getStatus() {return (status != null) ? status : "";}
    public String getTelefone() {return (telefone != null) ? telefone : "";}
    public String getUid() {return (uid != null) ? uid : "";}
    public String getCreation() {return (creation != null) ? creation : "";}
    public String getCreatedBy() {return (createdBy != null) ? createdBy : "";}
    public String getLastModifiedOn() {return (lastModifiedOn != null) ? lastModifiedOn : "";}
    public String getLastModifiedBy() {return (lastModifiedBy != null) ? lastModifiedBy : "";}

    @Override
    public String toString() {
        return "Motorista{" +
                "uid='" + uid + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}
