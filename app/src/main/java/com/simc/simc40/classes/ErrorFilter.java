package com.simc.simc40.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorFilter implements Serializable {
    List<String> etapas;
    String status;
    List<Obra> obras;
    List<Date> dataRegistro;
    List<Date> dataSolucao;
    Date minDataRegistro;
    Date maxDataRegistro;
    Date minDataSolucao;
    Date maxDataSolucao;

    public ErrorFilter(){}

    public boolean isEmpty(){
        return (etapas == null && status == null && obras == null && dataRegistro == null && dataSolucao == null);
    }

    public boolean rangeRegistroIsEmpty(){
        return (minDataRegistro == null && maxDataRegistro == null);
    }

    public boolean rangeSolucaoIsEmpty(){
        return (minDataSolucao == null && maxDataSolucao == null);
    }

    public void resetFilters(){
        etapas = null;
        status = null;
        obras = null;
        dataRegistro = null;
        dataSolucao = null;
    }

    public void putFilterEtapa(String etapa){if(etapas == null) etapas = new ArrayList<>(); etapas.add(etapa);}
    public void putFilterObra(Obra obra){if(obras == null) obras = new ArrayList<>(); obras.add(obra);}

    public List<String> getEtapas() {return etapas;}
    public void setEtapas(List<String> etapas) { this.etapas = etapas;}
    public String getStatus() {return status;}
    public void setStatus(String status) { this.status = status; }
    public List<Obra> getObras() {return obras;}
    public void setObras(List<Obra> obras) { this.obras = obras; }
    public List<Date> getDataRegistro() {return dataRegistro;}
    public void setDataRegistro(List<Date> dataRegistro) { this.dataRegistro = dataRegistro; }
    public List<Date> getDataSolucao() {return dataSolucao;}
    public void setDataSolucao(List<Date> dataSolucao) { this.dataSolucao = dataSolucao; }
    public Date getMinDataRegistro() {return minDataRegistro;}
    public void setMinDataRegistro(Date minDataRegistro) { this.minDataRegistro = minDataRegistro; }
    public Date getMaxDataRegistro() {return maxDataRegistro;}
    public void setMaxDataRegistro(Date maxDataRegistro) { this.maxDataRegistro = maxDataRegistro; }
    public Date getMinDataSolucao() {return minDataSolucao;}
    public void setMinDataSolucao(Date minDataSolucao) { this.minDataSolucao = minDataSolucao; }
    public Date getMaxDataSolucao() {return maxDataSolucao;}
    public void setMaxDataSolucao(Date maxDataSolucao) { this.maxDataSolucao = maxDataSolucao; }

    @Override
    public String toString() {
        return "ErrorFilter{" +
                "etapas=" + etapas +
                ", status='" + status + '\'' +
                ", obras=" + obras +
                ", dataRegistro=" + dataRegistro +
                ", dataSolucao=" + dataSolucao +
                ", minDataRegistro=" + minDataRegistro +
                ", maxDataRegistro=" + maxDataRegistro +
                ", minDataSolucao=" + minDataSolucao +
                ", maxDataSolucao=" + maxDataSolucao +
                '}';
    }
}
