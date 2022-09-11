package com.android.simc40.firebaseApiGET;

import com.android.simc40.classes.Cliente;

import java.util.HashMap;

public interface ApiClientesCallback {
    void onCallback(HashMap<String, Cliente> response) throws Exception;
}
