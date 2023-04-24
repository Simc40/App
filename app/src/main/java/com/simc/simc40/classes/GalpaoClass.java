package com.simc.simc40.classes;

import java.util.Map;
import java.util.TreeMap;

public class GalpaoClass {

    public static final String disponivel = "Disponível";
    public static final String indisponivel = "Indisponível";
    String uid;
    String nome_galpao;
    String status;
    String creation;
    String createdBy;
    String lastModifiedOn;
    String lastModifiedBy;
    Map<String, Object> controle;
    Map<String, Object> history;
//    TreeMap<String, PecaGalpao> controle = new TreeMap<>();


    public GalpaoClass() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNome_galpao() {
        return nome_galpao;
    }

    public void setNome_galpao(String nome_galpao) {
        this.nome_galpao = nome_galpao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(String lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Map<String, Object> getControle() {
        return controle;
    }

    public void setControle(Map<String, Object> controle) {
        this.controle = controle;
    }

    public Map<String, Object> getHistory() {
        return history;
    }

    public void setHistory(Map<String, Object> history) {
        this.history = history;
    }

    @Override
    public String toString() {
        return "GalpaoClass{" +
                "uid='" + uid + '\'' +
                ", nome_galpao='" + nome_galpao + '\'' +
                ", status='" + status + '\'' +
                ", creation='" + creation + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", lastModifiedOn='" + lastModifiedOn + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", controle=" + controle +
                ", history=" + history +
                '}';
    }
}
