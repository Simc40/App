package com.simc.simc40.doubleClick;

import android.os.SystemClock;

public class DuploClique {
    long tempoDeSistemaDoUltimoClique = 0;
    long intervaloDeDuploClique = 1000;

    public DuploClique(){}

    public DuploClique(long intervaloDeDuploClique){
        this.intervaloDeDuploClique = intervaloDeDuploClique;
    }

    public boolean detectado(){
        if (SystemClock.elapsedRealtime() - tempoDeSistemaDoUltimoClique < intervaloDeDuploClique){
            return true;
        }
        tempoDeSistemaDoUltimoClique = SystemClock.elapsedRealtime();
        return false;
    }
}
