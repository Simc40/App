package com.android.simc40.firebaseApiGET;

import com.android.simc40.classes.Veiculo;

import java.util.TreeMap;

public interface ApiVeiculosCallback {
    void onCallback(TreeMap<String, Veiculo> response) throws Exception;
}
