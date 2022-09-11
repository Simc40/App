package com.android.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseDatabaseExceptionErrors {
    static Map<String, String[]> errorList = new HashMap<String, String[]>() {{
        put("ERROR_NO_CLIENT_REGISTERED", new String[]{"Sem Registro de Empresa", "Não encontramos registro da sua empresa no Banco de Dados."});
        put("ERROR_NO_CLIENT_REGISTERED_ADMIN", new String[]{"Sem Registro de Empresa", "Não há registro de empresas no Banco de Dados."});
        put("ERROR_CLIENT_WITH_INACTIVE_STATUS", new String[]{"Status Inativo", "A Empresa selecionada possui status Inativo, Altere o status para ativo para poder acessar o sistema."});
        put("ERROR_TIME_LIMIT_REACHED", new String[]{"Tempo de Resposta Excedido", "O servidor demorou muito para retornar os dados, tente novamente em breve."});
        put("ERROR_USER_NOT_FOUND", new String[]{"Usuário não Encontrado", "Não foi possível encontrar registro de usuário no banco de dados, certifique-se de que seus administradoras realizaram o registro corretamente, se o erro persistir entre em contato com o suporte!"});
        put("ERROR_RETURNED_NULL_VALUE", new String[]{"Não foi possível resgatar os dados do servidor", "Houve um conflito no banco de dados, entre em contato com o suporte!"});

    }};

    public static Map<String, String> getError(String errorCode){
        Map<String, String> response = new HashMap<>();
        if(errorCode == null || errorList.get(errorCode) == null){
            response.put("errorCode", "A solicitação ao Servidor Falhou");
            response.put("message", "Não foi possível identificar a razão da falha na solicitação. Contate o suporte!");
            return response;
        }
        response.put("errorCode", Objects.requireNonNull(errorList.get(errorCode))[0]);
        response.put("message", Objects.requireNonNull(errorList.get(errorCode))[1]);
        return response;
    }
}

