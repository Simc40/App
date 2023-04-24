package com.simc.simc40.firebaseApiGET;

import com.simc.simc40.classes.Romaneio;

import java.util.TreeMap;

public interface ApiRomaneiosCallback {
    void onCallback(TreeMap<String, Romaneio> response) throws Exception;
}
