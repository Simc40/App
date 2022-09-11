package com.android.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PermissionExceptonErrors {
    static Map<String, String[]> errorList = new HashMap<String, String[]>() {{
        put("ERROR_CAMERA_PERMISSION_DENIED", new String[]{"Permissão de Câmera Negada", "Para manejar imagens no aplicativo é necessário que essa permissão seja concedida. Tente novamente realizar a operação  e conceda a permissão."});
        put("ERROR_GALLERY_PERMISSION_DENIED", new String[]{"Permissão de Acesso a Galeria Negada", "Para manejar imagens no aplicativo é necessário que essa permissão seja concedida. Tente novamente realizar a operação  e conceda a permissão."});
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