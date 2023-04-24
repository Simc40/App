package com.simc.simc40.firebaseApiGET;

import com.simc.simc40.classes.Peca;

import java.util.TreeMap;

public interface ApiPecasCallback {
    void onCallback(TreeMap<String, Peca> response) throws Exception;
}
