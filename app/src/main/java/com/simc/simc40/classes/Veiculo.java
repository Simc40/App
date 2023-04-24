package com.simc.simc40.classes;

import android.widget.TextView;

import java.io.Serializable;

public class Veiculo implements Serializable {
    String uid;
    String capacidadeCarga;
    String transportadora;
    String marca;
    String modelo;
    String numeroEixos;
    String peso;
    String placa;
    String status;
    String creation;
    String createdBy;
    String lastModifiedOn;
    String lastModifiedBy;

    public Veiculo(
        String uid,
        String capacidadeCarga,
        String transportadora,
        String marca,
        String modelo,
        String numeroEixos,
        String peso,
        String placa,
        String status,
        String creation,
        String createdBy,
        String lastModifiedOn,
        String lastModifiedBy
    ){
        this.uid = (uid != null) ? uid : "";
        this.capacidadeCarga = (capacidadeCarga != null) ? capacidadeCarga : "";
        this.transportadora = (transportadora != null) ? transportadora : "";
        this.marca = (marca != null) ? marca : "";
        this.modelo = (modelo != null) ? modelo : "";
        this.numeroEixos = (numeroEixos != null) ? numeroEixos : "";
        this.peso = (peso != null) ? peso : "";
        this.placa = (placa != null) ? placa : "";
        this.status = (status != null) ? status : "";
        this.creation = (creation != null) ? creation : "";
        this.createdBy = (createdBy != null) ? createdBy : "";
        this.lastModifiedOn = (lastModifiedOn != null) ? lastModifiedOn : "";
        this.lastModifiedBy = (lastModifiedBy != null) ? lastModifiedBy : "";
    }

    public String getUid() {return (uid != null) ? uid : "";}
    public String getTransportadora() {return (transportadora != null) ? transportadora : "";}
    public String getCapacidadeCarga() {return (capacidadeCarga != null) ? capacidadeCarga : "";}
    public String getMarca() {return (marca != null) ? marca : "";}
    public String getModelo() {return (modelo != null) ? modelo : "";}
    public String getNumeroEixos() {return (numeroEixos != null) ? numeroEixos : "";}
    public String getPeso() {return (peso != null) ? peso : "";}
    public String getPlaca() {return (placa != null) ? placa : "";}
    public String getStatus() {return (status != null) ? status : "";}
    public String getCreation() {return (creation != null) ? creation : "";}
    public String getCreatedBy() {return (createdBy != null) ? createdBy : "";}
    public String getLastModifiedOn() {return (lastModifiedOn != null) ? lastModifiedOn : "";}
    public String getLastModifiedBy() {return (lastModifiedBy != null) ? lastModifiedBy : "";}
    public String getNome() { return (modelo != null && numeroEixos != null && capacidadeCarga != null) ? ("Modelo: " + modelo + "\n" + "Capacidade Carga: " + capacidadeCarga + " Kg" + "\n" + "Numero de Eixos: " + numeroEixos) : "";}

    @Override
    public String toString() {
        return "Veiculo{" +
                "uid='" + uid + '\'' +
                ", placa='" + placa + '\'' +
                '}';
    }
}
