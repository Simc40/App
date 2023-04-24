package com.simc.simc40.classes;

import android.annotation.SuppressLint;

import com.simc.simc40.checklist.Etapas;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Erro implements Serializable, Etapas {
    public static final String statusFechado = "fechado";
    public static final String statusAberto = "aberto";
    public static final String statusIsLocked = "true";
    public static final String statusUnlocked = "false";
    String checklist;
    String comentarios;
    String comentarios_solucao;
    String createdBy;
    String creation;
    String etapa_detectada;
    String imgUrl_defeito;
    String imgUrl_solucao;
    String item;
    String lastModifiedBy;
    String lastModifiedOn;
    String obra;
    String peca;
    String elemento;
    String status;
    String status_locked;
    String uid;


    @SuppressLint("SimpleDateFormat")
    public Date getDataCreation(){
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(creation);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public Date getDataSolution(){
        try {
            if(lastModifiedOn == null || creation.equals(lastModifiedOn)) return null;
            return new SimpleDateFormat("dd/MM/yyyy").parse(lastModifiedOn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getComentarios_solucao() {
        return comentarios_solucao;
    }

    public void setComentarios_solucao(String comentarios_solucao) {
        this.comentarios_solucao = comentarios_solucao;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

    public String getEtapa_detectada() {
        return etapa_detectada;
    }

    public void setEtapa_detectada(String etapa_detectada) {
        this.etapa_detectada = etapa_detectada;
    }

    public String getImgUrl_defeito() {
        return imgUrl_defeito;
    }

    public void setImgUrl_defeito(String imgUrl_defeito) {
        this.imgUrl_defeito = imgUrl_defeito;
    }

    public String getImgUrl_solucao() {
        return imgUrl_solucao;
    }

    public void setImgUrl_solucao(String imgUrl_solucao) {
        this.imgUrl_solucao = imgUrl_solucao;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(String lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public String getObra() {
        return obra;
    }

    public void setObra(String obra) {
        this.obra = obra;
    }

    public String getPeca() {
        return peca;
    }

    public void setPeca(String peca) {
        this.peca = peca;
    }

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_locked() {
        return status_locked;
    }

    public void setStatus_locked(String status_locked) {
        this.status_locked = status_locked;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
