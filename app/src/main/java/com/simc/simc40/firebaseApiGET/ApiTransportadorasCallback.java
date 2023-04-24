package com.simc.simc40.firebaseApiGET;

import com.simc.simc40.classes.Transportadora;

import java.util.TreeMap;

public interface ApiTransportadorasCallback {
    void onCallback(TreeMap<String, Transportadora> response) throws Exception;
}
