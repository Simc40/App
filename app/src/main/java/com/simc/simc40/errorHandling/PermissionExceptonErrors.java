package com.simc.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PermissionExceptonErrors implements  PermissionExceptionList{
    static Map<String, String[]> errorList = new HashMap<String, String[]>() {{
        put("ERROR_CAMERA_PERMISSION_DENIED", new String[]{"Permissão de Câmera Negada", "Para manejar imagens no aplicativo é necessário que essa permissão seja concedida. Tente novamente realizar a operação  e conceda a permissão ou acesse as configurações e conceda a permissão."});
        put("ERROR_GALLERY_PERMISSION_DENIED", new String[]{"Permissão de Acesso a Galeria Negada", "Para manejar imagens no aplicativo é necessário que essa permissão seja concedida. Tente novamente realizar a operação  e conceda a permissão ou acesse as configurações e conceda a permissão."});
        put(EXCEPTION_CAMERA_FILE_NULL, new String[]{"Houve um erro no Processamento da Câmera", "Houve um erro de comunicação com a Cãmera, contate o suporte!"});

    }};

    public static Map<String, String> getError(String errorCode){
        Map<String, String> response = new HashMap<>();
        if(errorCode == null || errorList.get(errorCode) == null){
            response.put("errorCode", "Erro no gerenciamento de Permissões");
            response.put("message", "Não foram fornecidas todas as permissões necessárias para prosseguir com a atividade!");
            return response;
        }
        response.put("errorCode", Objects.requireNonNull(errorList.get(errorCode))[0]);
        response.put("message", Objects.requireNonNull(errorList.get(errorCode))[1]);
        return response;
    }
}