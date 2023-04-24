package com.simc.simc40.firebaseApiGET;

import com.simc.simc40.classes.TipoDePeca;

import java.util.HashMap;

public interface ApiTiposDePecaCallback {
    void onCallback(HashMap<String, TipoDePeca> response) throws Exception;
}
