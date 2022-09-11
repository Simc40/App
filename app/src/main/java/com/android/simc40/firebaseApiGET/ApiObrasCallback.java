package com.android.simc40.firebaseApiGET;

import com.android.simc40.classes.Obra;

import java.util.TreeMap;

public interface ApiObrasCallback {
    void onCallback(TreeMap<String, Obra> response) throws Exception;
}
