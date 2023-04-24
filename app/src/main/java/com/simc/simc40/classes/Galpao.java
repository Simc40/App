package com.simc.simc40.classes;

import java.io.Serializable;
import java.util.TreeMap;

public class Galpao implements Serializable {
    public static final String disponivel = "Disponível";
    public static final String indisponivel = "Indisponível";
    String uid;
    String nome;
    String status;
    String creation;
    String createdBy;
    String lastModifiedOn;
    String lastModifiedBy;
    TreeMap <String, PecaGalpao> controle = new TreeMap<>();

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

    @Override
    public String toString() {
        return "Galpao{" +
                "uid='" + uid + '\'' +
                ", nome='" + nome + '\'' +
                ", status='" + status + '\'' +
                ", controle=" + controle +
                '}';
    }


    public String getUid() {return (uid != null) ? uid : "";}
    public String getNome() {return (nome != null) ? nome : "";}
    public String getStatus() {return (status != null) ? status : "";}
    public String getPrettyStatus() {return (status != null) ? (status.equals("ativo")) ? disponivel : indisponivel  : "";}
    public String getCreation() {return (creation != null) ? creation : "";}
    public String getCreatedBy() {return (createdBy != null) ? createdBy : "";}
    public String getLastModifiedOn() {return (lastModifiedOn != null) ? lastModifiedOn : "";}
    public String getLastModifiedBy() {return (lastModifiedBy != null) ? lastModifiedBy : "";}
    public void insertPecaGalpao(String rfid, String uidObra, String entrada, String saida){ controle.put(rfid, new PecaGalpao(rfid, uidObra, entrada, saida));}
    public TreeMap<String, PecaGalpao> getControle() {return controle;}
}
