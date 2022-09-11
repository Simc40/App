package com.android.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LayoutExceptionErrors {
    static Map<String, String[]> errorList = new HashMap<String, String[]>() {{
        //put("ERROR_USER_NULL", new String[]{"Erro na Memória Interna", "Não foi possível resgatar dados na memória interna do seu Dispositivo. Reinicie a Aplicação e realize novamente o Login. Se o erro Persistir, Vá em configurações -> Apps -> Gerenciar Apps -> SIMC4.0 -> Limpar Dados. Dessa forma a memória interna da aplicação será reiniciada."});
    }};

    public static Map<String, String> getError(String errorCode){
        Map<String, String> response = new HashMap<>();
        if(errorCode == null || errorList.get(errorCode) == null){
            response.put("errorCode", "Houve um erro na Renderização do Layout");
            response.put("message", "Não foi possível identificar a razão da falha na renderização. Contate o suporte!");
            return response;
        }
        response.put("errorCode", Objects.requireNonNull(errorList.get(errorCode))[0]);
        response.put("message", Objects.requireNonNull(errorList.get(errorCode))[1]);
        return response;
    }
}
