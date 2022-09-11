package com.android.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReaderExceptionErrors implements  ReaderExceptionErrorList{
    static Map<String, String[]> errorList = new HashMap<String, String[]>() {{
        put(EXCEPTION_READER_NOT_CONFIGURED, new String[]{"Leitor Não Configurado", "O Leitor selecionado não foi configurado no sistema, selecione outro leitor para prosseguir."});
    }};

    public static Map<String, String> getError(String errorCode){
        Map<String, String> response = new HashMap<>();
        if(errorCode == null || errorList.get(errorCode) == null){
            response.put("errorCode", "Houve um erro na Comunicação com Leitor");
            response.put("message", "Não foi possível identificar a razão da falha na comunicação com o leitor. Contate o suporte!");
            return response;
        }
        response.put("errorCode", Objects.requireNonNull(errorList.get(errorCode))[0]);
        response.put("message", Objects.requireNonNull(errorList.get(errorCode))[1]);
        return response;
    }
}
