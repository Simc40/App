package com.simc.simc40.firebaseApiGET;

import com.simc.simc40.classes.Cliente;

import java.util.HashMap;

public interface ApiClientesCallback {
    void onCallback(HashMap<String, Cliente> response) throws Exception;
}
