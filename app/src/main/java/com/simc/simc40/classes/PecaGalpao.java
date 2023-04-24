package com.simc.simc40.classes;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PecaGalpao implements Serializable {
    String rfid;
    String uidObra;
    String entrada;
    String saida;

    public PecaGalpao(String rfid, String uidObra, String entrada, String saida) {
        this.rfid = rfid;
        this.uidObra = uidObra;
        this.entrada = entrada;
        this.saida = saida;
    }

    public String getRfid() {return (rfid != null) ? rfid : "";}
    public String getUidObra() {return (uidObra != null) ? uidObra : "";}
    public String getEntrada() {return (entrada != null) ? entrada : "";}
    public String getSaida() {return (saida != null) ? saida : "";}
    public String getShortEntrada() {return (entrada != null) ? entrada.substring(0, 11) : "";}
    public String getShortSaida() {return (saida != null) ? saida.substring(0, 11) : "";}
    @SuppressLint("SimpleDateFormat")
    public Date getDateEntrada() throws ParseException {return (entrada != null && !entrada.isEmpty()) ? new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(entrada) : null;}
    @SuppressLint("SimpleDateFormat")
    public Date getDateSaida() throws ParseException {return (saida != null && !saida.isEmpty()) ? new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(saida) : null;}
}
