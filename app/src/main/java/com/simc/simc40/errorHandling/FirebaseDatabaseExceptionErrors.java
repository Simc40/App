package com.simc.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseDatabaseExceptionErrors implements FirebaseDatabaseExceptionErrorList{
    static Map<String, String[]> errorList = new HashMap<String, String[]>() {{
        put("ERROR_NO_CLIENT_REGISTERED", new String[]{"Sem Registro de Empresa", "Não encontramos registro da sua empresa no Banco de Dados."});
        put("ERROR_NO_CLIENT_REGISTERED_ADMIN", new String[]{"Sem Registro de Empresa", "Não há registro de empresas no Banco de Dados."});
        put("ERROR_CLIENT_WITH_INACTIVE_STATUS", new String[]{"Status Inativo", "A Empresa selecionada possui status Inativo, Altere o status para ativo para poder acessar o sistema."});
        put("ERROR_TIME_LIMIT_REACHED", new String[]{"Tempo de Resposta Excedido", "O servidor demorou muito para retornar os dados, tente novamente em breve."});
        put("ERROR_USER_NOT_FOUND", new String[]{"Usuário não Encontrado", "Não foi possível encontrar registro de usuário no banco de dados, certifique-se de que seus administradoras realizaram o registro corretamente, se o erro persistir entre em contato com o suporte!"});
        put("ERROR_RETURNED_NULL_VALUE", new String[]{"Não foi possível resgatar os dados do servidor", "Houve um conflito no banco de dados, entre em contato com o suporte!"});
        put(EXCEPTION_NULL_DATABASE_OBRAS, new String[]{"Não há registro de obras", "Realize novos registros de obras para prosseguir!."});
        put(EXCEPTION_NULL_DATABASE_ELEMENTOS, new String[]{"Não há registro de elementos", "Realize novos registros de elementos para prosseguir!."});
        put(EXCEPTION_NULL_DATABASE_GALPOES, new String[]{"Não há registro de Galpões", "Realize novos registros de Galpões para prosseguir!."});
        put(EXCEPTION_NULL_DATABASE_TRANSPORTADORAS, new String[]{"Não há registro de Transportadoras", "Realize novos registros de Transportadoras para prosseguir!."});
        put(EXCEPTION_NULL_DATABASE_VEICULOS, new String[]{"Não há registro de Veículos", "Realize novos registros de Veículos para prosseguir!."});
        put(EXCEPTION_NULL_DATABASE_FORMAS, new String[]{"Não há registro de Formas", "Realize novos registros de Formas para prosseguir!."});
        put(EXCEPTION_NULL_DATABASE_USERS, new String[]{"Não há registro de Usuários nesse Cliente", "Realize novos registros de Usuários para prosseguir!."});
        put(EXCEPTION_POST_FAILED, new String[]{"Não foi possível se Comunicar com o Banco de dados", "Verifique se o seu aparelho está conectado à internet. Caso o erro persista, comunique ao suporte"});
        put(EXCEPTION_TRANSPORTADORA_NULL_VEICULOS_AND_MOTORISTAS, new String[]{"Transportadora selecionada sem registro de Motoristas e Veículos", "Realize novos registros de Veiculos ou Motoristas para prosseguir!."});
        put(EXCEPTION_NULL_CONTROLE_GALPAO, new String[]{"Nenhum registro de Peça no Controle do Galpão", "Não houve registro de peças incluídas nesse galpão!."});
        put(EXCEPTION_NULL_DATABASE_ROMANEIOS, new String[]{"Não há registro de Romaneios", "Realize novos registros de Veículos para prosseguir!."});

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

