package com.simc.simc40.classes;

import androidx.annotation.NonNull;

import com.simc.simc40.errorHandling.LayoutException;
import com.simc.simc40.errorHandling.LayoutExceptionErrorList;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Obra implements Serializable, LayoutExceptionErrorList {
    String acronimo;
    String bairro;
    String cep;
    String cidade;
    String cnpj;
    String createdBy;
    String creation;
    String endereco;
    Map<String, Object> history;
    String lastModifiedBy;
    String lastModifiedOn;
    String nome_obra;
    String previsao_entrega;
    String previsao_inicio;
    String quantidade_pecas;
    String responsavel;
    String status;
    String tipo_construcao;
    String uf;
    String uid;
    String pdfUrl;

    public Obra(){}

    @NonNull
    @Override
    public String toString() {
        return "Obra{" + "\n" +
                "uid='" + uid + '\'' + "\n" +
                ", nomeObra='" + nome_obra + '\'' + "\n" +
                '}';
    }

    public void setUid(String uid) throws LayoutException {
        if(uid == null) throw new LayoutException(EXCEPTION_NULL_UID);
        this.uid = uid;
    }

    public String getUid() {return (uid != null) ? uid : "";}
    public String getNome_obra() {return (nome_obra != null) ? nome_obra : "";}
    public String getResponsavel() {return (responsavel != null) ? responsavel : "";}
    public String getTipo_construcao() {return (tipo_construcao != null) ? tipo_construcao : "";}
    public String getCep() {return (cep != null) ? cep : "";}
    public String getCidade() {return (cidade != null) ? cidade : "";}
    public String getBairro() {return (bairro != null) ? bairro : "";}
    public String getEndereco() {return (endereco != null) ? endereco : "";}
    public String getUf() {return (uf != null) ? uf : "";}
    public Integer getQuantidade_pecas() {return (quantidade_pecas != null) ? Integer.parseInt(quantidade_pecas) : 0;}
    public String getPrevisao_inicio() {return (previsao_inicio != null) ? previsao_inicio : "";}
    public String getPrevisao_entrega() {return (previsao_entrega != null) ? previsao_entrega : "";}
    public String getStatus() {return (status != null) ? status : "";}
    public String getCreation() {return (creation != null) ? creation : "";}
    public String getCreatedBy() {return (createdBy != null) ? createdBy : "";}
    public String getLastModifiedOn() {return (lastModifiedOn != null) ? lastModifiedOn : "";}
    public String getLastModifiedBy() {return (lastModifiedBy != null) ? lastModifiedBy : "";}
    public String getPdfUrl() {return (pdfUrl != null) ? pdfUrl : "";}
    public String getAcronimo() {return (acronimo != null ? acronimo : "");}
    public String getCnpj() {return (cnpj != null ? cnpj : "");}
    public Map<String, Object> getHistory() {return (history != null ? history : new HashMap<>());}

    public void setAcronimo(String acronimo) {this.acronimo = acronimo;}
    public void setBairro(String bairro) {this.bairro = bairro;}
    public void setCep(String cep) {this.cep = cep;}
    public void setCidade(String cidade) {this.cidade = cidade;}
    public void setCnpj(String cnpj) {this.cnpj = cnpj;}
    public void setCreatedBy(String createdBy) {this.createdBy = createdBy;}
    public void setCreation(String creation) {this.creation = creation;}
    public void setEndereco(String endereco) {this.endereco = endereco;}
    public void setHistory(Map<String, Object> history) {this.history = history;}
    public void setLastModifiedBy(String lastModifiedBy) {this.lastModifiedBy = lastModifiedBy;}
    public void setLastModifiedOn(String lastModifiedOn) {this.lastModifiedOn = lastModifiedOn;}
    public void setNome_obra(String nome_obra) {this.nome_obra = nome_obra;}
    public void setPrevisao_entrega(String previsao_entrega) {this.previsao_entrega = previsao_entrega;}
    public void setPrevisao_inicio(String previsao_inicio) {this.previsao_inicio = previsao_inicio;}
    public void setQuantidade_pecas(String quantidade_pecas) {this.quantidade_pecas = quantidade_pecas;}
    public void setResponsavel(String responsavel) {this.responsavel = responsavel;}
    public void setStatus(String status) {this.status = status;}
    public void setTipo_construcao(String tipo_construcao) {this.tipo_construcao = tipo_construcao;}
    public void setUf(String uf) {this.uf = uf;}
    public void setPdfUrl(String pdfUrl) {this.pdfUrl = pdfUrl;}

    public void setUsers(HashMap<String, String> usersMap) {
        if(this.responsavel != null) this.responsavel = usersMap.get(responsavel);
        if(this.lastModifiedBy != null) this.lastModifiedBy = usersMap.get(lastModifiedBy);
    }
}
