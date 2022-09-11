package com.android.simc40.classes;

import java.io.Serializable;

public class Transportadora implements Serializable {

    String uidTransportadora;
    String nome;
    String status;

    public Transportadora(){}

    public Transportadora(String uidTransportadora, String nome, String status){
        this.uidTransportadora = uidTransportadora;
        this.nome = nome;
        this.status = status;
    }

    public String getUidTransportadora() {return (uidTransportadora != null) ? uidTransportadora : "";}
    public String getNome() {return (nome != null) ? nome : "";}
    public String getStatus() {return (status != null) ? status : "";}
}
