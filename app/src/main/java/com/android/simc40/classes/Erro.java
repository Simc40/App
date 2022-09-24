package com.android.simc40.classes;

import android.annotation.SuppressLint;

import com.android.simc40.checklist.Etapas;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Erro implements Serializable, Etapas {
    public static final String statusFechado = "fechado";
    public static final String statusAberto = "aberto";
    public static final String statusIsLocked = "true";
    public static final String statusUnlocked = "false";
    String uidObra;
    String uidElemento;
    String tag;
    String item;
    Checklist checklist;
    String comentarios;
    String comentariosSolucao;
    String createdBy;
    String creation;
    String etapaDetectada;
    String imgUrlDefeito;
    String imgUrlSolucao;
    String lastModifiedOn;
    String lastModifiedBy;
    String status;
    String statusLocked;
    String uid;

    public Erro(String item, String uidObra, String elementoOrTag, Checklist checklist, String comentarios, String comentariosSolucao, String createdBy, String creation, String etapaDetectada, String imgUrlDefeito, String imgUrlSolucao, String lastModifiedOn, String lastModifiedBy, String status, String statusLocked, String uid) {
        this.item = item;
        this.uidObra = uidObra;
        this.uidElemento = (etapaDetectada.equals(planejamentoKey)) ? elementoOrTag : null;
        this.tag = (!etapaDetectada.equals(planejamentoKey)) ? elementoOrTag : null;
        this.checklist = checklist;
        this.comentarios = comentarios;
        this.comentariosSolucao = comentariosSolucao;
        this.createdBy = createdBy;
        this.creation = creation;
        this.etapaDetectada = etapaDetectada;
        this.imgUrlDefeito = imgUrlDefeito;
        this.imgUrlSolucao = imgUrlSolucao;
        this.lastModifiedOn = lastModifiedOn;
        this.lastModifiedBy = lastModifiedBy;
        this.status = status;
        this.statusLocked = statusLocked;
        this.uid = uid;
    }

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

    public String getUidObra() {return uidObra;}
    public String getUidElemento() {return uidElemento;}
    public String getTag() {return tag;}
    public String getItem() {return item;}
    public Checklist getChecklist() {return checklist;}
    public String getComentarios() {return comentarios;}
    public String getComentariosSolucao() {return comentariosSolucao;}
    public String getCreatedBy() {return createdBy;}
    public String getCreation() {return creation;}
    public String getEtapaDetectada() {return etapaDetectada;}
    public String getImgUrlDefeito() {return imgUrlDefeito;}
    public String getImgUrlSolucao() {return imgUrlSolucao;}
    public String getLastModifiedOn() {return lastModifiedOn;}
    public String getLastModifiedBy() {return lastModifiedBy;}
    public String getStatus() {return status;}
    public String getStatusLocked() {return statusLocked;}
    public String getUid() {return uid;}
}
