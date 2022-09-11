package com.android.simc40.classes;

import android.annotation.SuppressLint;
import android.widget.TextView;

import java.io.Serializable;

public class Veiculo implements Serializable {
    String uid;
    Transportadora transportadora;
    String capacidadeCarga;
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

    TextView textViewTransportadora;
    TextView textViewCapacidadeCarga;
    TextView textViewMarca;
    TextView textViewModelo;
    TextView textViewNumeroEixos;
    TextView textViewPeso;
    TextView textViewPlaca;
    TextView textViewCreation;
    TextView textViewCreatedBy;
    TextView textViewLastModifiedOn;
    TextView textViewLastModifiedBy;

    public Veiculo(){}

    public Veiculo(
        String uid,
        Transportadora transportadora,
        String capacidadeCarga,
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
        this.transportadora = (transportadora != null) ? transportadora : new Transportadora();
        this.capacidadeCarga = (capacidadeCarga != null) ? capacidadeCarga : "";
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
    public Transportadora getTransportadora() {return (transportadora != null) ? transportadora : new Transportadora();}
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

    @Override
    public String toString() {
        return "Veiculo{" + "\n" + "uid='" + uid + '\'' + "\n" + ", transportadora=" + transportadora + "\n" + ", capacidadeCarga='" + capacidadeCarga + '\'' + "\n" + ", marca='" + marca + '\'' + "\n" + ", modelo='" + modelo + '\'' + "\n" + ", numeroEixos='" + numeroEixos + '\'' + "\n" + ", peso='" + peso + '\'' + "\n" + ", placa='" + placa + '\'' + "\n" + ", status='" + status + '\'' + "\n" + ", creation='" + creation + '\'' + "\n" + ", createdBy='" + createdBy + '\'' + "\n" + ", lastModifiedOn='" + lastModifiedOn + '\'' + "\n" + ", lastModifiedBy='" + lastModifiedBy + '\'' + "\n" + '}';
    }
}
