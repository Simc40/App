package com.android.simc40.firebaseApiGET;

import com.android.simc40.classes.Galpao;

import java.util.TreeMap;

public interface ApiGalpoesCallback {
    void onCallback(TreeMap<String, Galpao> response) throws Exception;
}
