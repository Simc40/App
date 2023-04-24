package com.simc.simc40.firebaseApiGET;

import com.simc.simc40.classes.Obra;

import java.util.TreeMap;

public interface ApiObrasCallback {
    void onCallback(TreeMap<String, Obra> response) throws Exception;
}
