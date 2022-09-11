package com.android.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;

public class FirebaseNetworkExceptionErros {

    public static Map<String, String> getError(){
        Map<String, String> response = new HashMap<>();
        response.put("errorCode", "Erro de Conexão");
        response.put("message", "Verifique a sua conexão com a Internet. Ocorreu um erro de rede (como tempo limite, conexão interrompida ou host inacessível).");
        return response;
    }
}