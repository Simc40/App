package com.android.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageExceptionErrors {
    static Map<String, String[]> errorList = new HashMap<String, String[]>() {{
        put("ERROR_INVALID_FORMAT", new String[]{"Formato de Imagem Inválido", "A imagem deve ser do tipo jpg, jpeg ou png."});
    }};

    public static Map<String, String> getError(String errorCode){
        Map<String, String> response = new HashMap<>();
        if(errorCode == null || errorList.get(errorCode) == null){
            response.put("errorCode", "Houve um erro no processamento da Imagem");
            response.put("message", "Não foi possível identificar a razão da falha. Contate o suporte!");
            return response;
        }
        response.put("errorCode", Objects.requireNonNull(errorList.get(errorCode))[0]);
        response.put("message", Objects.requireNonNull(errorList.get(errorCode))[1]);
        return response;
    }
}
