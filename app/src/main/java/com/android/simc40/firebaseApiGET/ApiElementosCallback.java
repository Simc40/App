package com.android.simc40.firebaseApiGET;

import com.android.simc40.classes.Elemento;

import java.util.TreeMap;

public interface ApiElementosCallback {
    void onCallback(TreeMap<String, Elemento> response) throws Exception;
}
