package com.simc.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReaderExceptionErrors implements  ReaderExceptionErrorList{
    static Map<String, String[]> errorList = new HashMap<String, String[]>() {{
        put(EXCEPTION_READER_NOT_CONFIGURED, new String[]{"Leitor Não Configurado", "O Leitor selecionado não foi configurado no sistema, selecione outro leitor para prosseguir."});
        put(EXCEPTION_API_VERSION_NOT_CONFIGURED, new String[]{"Versão de Android Não suportada", "O Leitor selecionado não está configurado para o andorid 13, selecione outro leitor para prosseguir."});
        put(EXCEPTION_UNREGISTERED_TAG, new String[]{"A tag não está cadastrada", "Não foi possível identificar a tag "});
        put(EXCEPTION_WRONG_ETAPA, new String[]{"Etapa Incorreta", "A tag "});
        put(EXCEPTION_TAG_FORMAT_INVALID, new String[]{"Tag com formato inválido", "A tag deve conter apenas letras e números"});
        put(EXCEPTION_READER_NOT_SELECTED, new String[]{"Leitor não Selecionado", "O leitor não foi escolhido. Na página Inicial acesse Configuração de Leitor."});
        put(EXCEPTION_TAG_ALREADY_READED, new String[]{"Tag Já lida", "A tag "});
        put(EXCEPTION_WRONG_READING_TYPE, new String[]{"Leitor RFID Desconectado", "É necessário utilizar um leitor RFID para cadastrar a peça"});
        put(EXCEPTION_TAG_WITH_NO_ROMANEIO, new String[]{"Sem Cadastro em Romaneio", "Não foi encontrado cadastro de romaneio com registro da peça."});
        put(EXCEPTION_TAG_NOT_REGISTED_IN_ROMANEIO, new String[]{"A peça não está cadastrada neste Romaneio", "Selecione o romaneio para esta peça para prosseguir."});

    }};

    public static Map<String, String> getError(String errorCode){
        Map<String, String> response = new HashMap<>();
        if(errorCode != null && errorCode.startsWith(EXCEPTION_TAG_FORMAT_INVALID)){
            response.put("errorCode", Objects.requireNonNull(errorList.get(EXCEPTION_TAG_FORMAT_INVALID))[0]);
            response.put("message", Objects.requireNonNull(errorList.get(EXCEPTION_TAG_FORMAT_INVALID))[1] + ". A tag: " + errorCode.replace(EXCEPTION_TAG_FORMAT_INVALID, "") + " não atende os critérios.");
            return response;
        }else if(errorCode != null && errorCode.startsWith(EXCEPTION_WRONG_ETAPA)){
            String etapa = errorCode.substring(errorCode.indexOf("etapa"));
            errorCode = errorCode.replace(etapa, "");
            etapa = etapa.replace("etapa", "");
            response.put("errorCode", Objects.requireNonNull(errorList.get(EXCEPTION_WRONG_ETAPA))[0]);
            response.put("message", Objects.requireNonNull(errorList.get(EXCEPTION_WRONG_ETAPA))[1] + errorCode.replace(EXCEPTION_WRONG_ETAPA, "") + " se encontra na etapa " + etapa);
            return response;
        }else if(errorCode != null && errorCode.startsWith(EXCEPTION_UNREGISTERED_TAG)){
            response.put("errorCode", Objects.requireNonNull(errorList.get(EXCEPTION_UNREGISTERED_TAG))[0]);
            response.put("message", Objects.requireNonNull(errorList.get(EXCEPTION_UNREGISTERED_TAG))[1]  + errorCode.replace(EXCEPTION_UNREGISTERED_TAG, "") + " nessa Obra, selecione a obra correta, ou certifique-se que a peça está cadastrada.");
            return response;
        }else if(errorCode != null && errorCode.startsWith(EXCEPTION_TAG_ALREADY_READED)){
            response.put("errorCode", Objects.requireNonNull(errorList.get(EXCEPTION_TAG_ALREADY_READED))[0]);
            response.put("message", Objects.requireNonNull(errorList.get(EXCEPTION_TAG_ALREADY_READED))[1] + errorCode.replace(EXCEPTION_TAG_ALREADY_READED, "") + " Já foi lida.");
            return response;
        }else if(errorCode == null || errorList.get(errorCode) == null){
            response.put("errorCode", "Houve um erro na Comunicação com Leitor");
            response.put("message", "Não foi possível identificar a razão da falha na comunicação com o leitor. Contate o suporte!");
            return response;
        }
        response.put("errorCode", Objects.requireNonNull(errorList.get(errorCode))[0]);
        response.put("message", Objects.requireNonNull(errorList.get(errorCode))[1]);
        return response;
    }
}
