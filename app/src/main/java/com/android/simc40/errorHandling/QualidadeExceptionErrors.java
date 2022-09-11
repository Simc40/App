package com.android.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QualidadeExceptionErrors implements QualidadeExceptionErrorList{
    static Map<String, String[]> errorList = new HashMap<String, String[]>() {{
        put(EXCEPTION_OBRA_NULL, new String[]{"A Obra não foi Selecionada", "Para dar continuidade a essa etapa é necessário selecionar a Obra."});
        put(EXCEPTION_ELEMENTO_NULL, new String[]{"O elemento não foi Selecionado", "Para dar continuidade a essa etapa é necessário selecionar o Elemento."});
        put(EXCEPTION_NO_REGISTERED_ELEMENTOS, new String[]{"Não há elementos registrados nessa obra", "Selecione uma obra que possua elementos para prosseguir."});
        put(EXCEPTION_NO_REGISTERED_PDF_IN_OBRA, new String[]{"Não há registro de PDF de Locação nessa Obra", "É necessário registrar um PDF no sistema. O registro é realizado na plataforma Web."});
        put(EXCEPTION_NO_REGISTERED_PDF_IN_ELEMENTO, new String[]{"Não há registro de PDF de Locação nessa Peça", "É necessário registrar um PDF no sistema. O registro é realizado na plataforma Web."});
        put(EXCEPTION_MULTIPLE_SELECTED_TAGS, new String[]{"Mais de uma tag Selecionada", "É necessário Que haja apenas uma Tag na lista para prosseguir."});
        put(EXCEPTION_INPUT_FIELDS_ARE_NOT_VALID, new String[]{"Valor preenchido inválido", "É necessário preencher os campos corretamente para prosseguir."});
        put(EXCEPTION_TAG_NOT_FOUND, new String[]{"Tag não encontrada.", "Não foi possível identificar essa tag nessa Obra, selecione a obra correta, ou certifique-se que a peça está cadastrada."});
    }};

    public static Map<String, String> getError(String errorCode){
        Map<String, String> response = new HashMap<>();
        if(errorCode == null || errorList.get(errorCode) == null){
            response.put("errorCode", "Erro no gerenciamento do Módulo de Qualidade");
            response.put("message", "Houve um erro inesperado no gerenciamento do módulo de Qualidade. Por favor contate o suporte!");
            return response;
        }
        response.put("errorCode", Objects.requireNonNull(errorList.get(errorCode))[0]);
        response.put("message", Objects.requireNonNull(errorList.get(errorCode))[1]);
        return response;
    }
}
