package com.simc.simc40.firebaseApiGET;

import com.simc.simc40.classes.Erro;

import java.util.TreeMap;

public interface ApiErrosCallback {
    void onCallback(TreeMap<String, Erro> response) throws Exception;
}
