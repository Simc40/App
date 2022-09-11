package com.android.simc40.firebaseApiGET;

import com.android.simc40.classes.TipoDePeca;

import java.util.HashMap;

public interface ApiTiposDePecaCallback {
    void onCallback(HashMap<String, TipoDePeca> response) throws Exception;
}
