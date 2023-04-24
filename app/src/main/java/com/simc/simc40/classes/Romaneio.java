package com.simc.simc40.classes;

import java.io.Serializable;
import java.util.Collection;
import java.util.TreeMap;

public class Romaneio implements Serializable {
    public static final String statusPlanejamento = "PLANEJAMENTO";
    public static final String statusCarga = "CARGA";
    public static final String statusTransporte = "EM TRANSPORTE";
    public static final String statusFinalizado = "FINALIZADO";
    String uid;
    Integer numCarga;
    Integer pesoCarregamento;
    Integer volumeCarregamento;
    String dataPrevisao;
    String dataCarga;
    String dataTransporte;
    String dataDescarga;
    Motorista motorista;
    Obra obra;
    TreeMap <String, Peca> pecas;
    Transportadora transportadora;
    Veiculo veiculo;
    String creation;
    String createdBy;
    String lastModifiedBy;
    String lastModifiedOn;

    public Romaneio(String uid, String numCarga, String pesoCarregamento, String volumeCarregamento, String dataPrevisao, String dataCarga, String dataTransporte, String dataDescarga, Motorista motorista, Obra obra, TreeMap<String, Peca> pecas, Transportadora transportadora, Veiculo veiculo, String creation, String createdBy, String lastModifiedBy, String lastModifiedOn) {
        this.uid = uid;
        this.numCarga = Integer.parseInt(numCarga);
        this.pesoCarregamento = Integer.parseInt(pesoCarregamento);
        this.volumeCarregamento = Integer.parseInt(volumeCarregamento);
        this.dataPrevisao = dataPrevisao;
        this.dataCarga = dataCarga;
        this.dataTransporte = dataTransporte;
        this.dataDescarga = dataDescarga;
        this.motorista = motorista;
        this.obra = obra;
        this.pecas = pecas;
        this.transportadora = transportadora;
        this.veiculo = veiculo;
        this.creation = creation;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedOn = lastModifiedOn;
    }

    public String getUid() {return uid;}
    public Integer getNumCarga() {return numCarga;}
    public Integer getPesoCarregamento() {return pesoCarregamento;}
    public Integer getVolumeCarregamento() {return volumeCarregamento;}
    public String getDataPrevisao() {return dataPrevisao;}
    public String getDataCarga() {return dataCarga;}
    public String getDataDescarga() {return dataDescarga;}
    public Motorista getMotorista() {return motorista;}
    public Obra getObra() {return obra;}
    public Collection<Peca> getPecas() {return pecas.values();}
    public Transportadora getTransportadora() {return transportadora;}
    public Veiculo getVeiculo() {return veiculo;}
    public String getCreation() {return creation;}
    public String getCreatedBy() {return createdBy;}
    public String getLastModifiedBy() {return lastModifiedBy;}
    public String getLastModifiedOn() {return lastModifiedOn;}
    public String getStatus() {
        if(dataDescarga != null) return statusFinalizado;
        else if (dataTransporte != null) return statusTransporte;
        else if (dataCarga != null) return statusCarga;
        return statusPlanejamento;
    }

}
