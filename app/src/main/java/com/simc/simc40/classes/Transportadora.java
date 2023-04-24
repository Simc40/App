package com.simc.simc40.classes;

import java.io.Serializable;
import java.util.TreeMap;

public class Transportadora implements Serializable {

    String uid;
    String bairro;
    String cep;
    String cidade;
    String cnpj;
    String email;
    String endereco;
    String nome;
    String status;
    String telefone;
    String uf;
    String creation;
    String createdBy;
    String lastModifiedOn;
    String lastModifiedBy;
    TreeMap<String, Motorista> motoristas;
    TreeMap<String, Veiculo> veiculos;

    public Transportadora(String uid, String bairro, String cep, String cidade, String cnpj, String email, String endereco, String nome, String status, String telefone, String uf, String creation, String createdBy, String lastModifiedOn, String lastModifiedBy, TreeMap<String, Motorista> motoristas, TreeMap<String, Veiculo> veiculos) {
        this.uid = uid;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
        this.cnpj = cnpj;
        this.email = email;
        this.endereco = endereco;
        this.nome = nome;
        this.status = status;
        this.telefone = telefone;
        this.uf = uf;
        this.creation = creation;
        this.createdBy = createdBy;
        this.lastModifiedOn = lastModifiedOn;
        this.lastModifiedBy = lastModifiedBy;
        this.motoristas = motoristas;
        this.veiculos = veiculos;
    }


    public String getBairro() {return (bairro != null) ? bairro : "";}
    public String getCep() {return (cep != null) ? cep : "";}
    public String getCidade() {return (cidade != null) ? cidade : "";}
    public String getCnpj() {return (cnpj != null) ? cnpj : "";}
    public String getEmail() {return (email != null) ? email : "";}
    public String getEndereco() {return (endereco != null) ? endereco : "";}
    public String getNome() {return (nome != null) ? nome : "";}
    public String getStatus() {return (status != null) ? status : "";}
    public String getTelefone() {return (telefone != null) ? telefone : "";}
    public String getUf() {return (uf != null) ? uf : "";}
    public String getUid() {return (uid != null) ? uid : "";}
    public TreeMap<String, Motorista> getMotoristas() {return (motoristas != null) ? motoristas : new TreeMap<>();}
    public TreeMap<String, Veiculo> getVeiculos() {return (veiculos != null) ? veiculos : new TreeMap<>();}

    @Override
    public String toString() {
        return "Transportadora{" +
                "uid='" + uid + '\'' +
                ", nome='" + nome + '\'' +
                ", motoristas=" + motoristas +
                ", veiculos=" + veiculos +
                '}';
    }
}
