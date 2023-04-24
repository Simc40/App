package com.simc.simc40.errorHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QualidadeExceptionErrors implements QualidadeExceptionErrorList{
    static Map<String, String[]> errorList = new HashMap<String, String[]>() {{
        put(EXCEPTION_OBRA_NULL, new String[]{"A Obra não foi Selecionada", "Para dar continuidade a essa etapa é necessário selecionar a Obra."});
        put(EXCEPTION_ELEMENTO_NULL, new String[]{"O elemento não foi Selecionado", "Para dar continuidade a essa etapa é necessário selecionar o Elemento."});
        put(EXCEPTION_NO_REGISTERED_ELEMENTOS, new String[]{"Não há elementos registrados nessa obra", "Selecione uma obra que possua elementos para prosseguir."});
        put(EXCEPTION_NO_REGISTERED_PECAS, new String[]{"Não há peças registrados nessa obra", "Selecione uma obra que possua peças registradas para prosseguir."});
        put(EXCEPTION_NO_REGISTERED_PDF_IN_OBRA, new String[]{"Não há registro de PDF de Locação nessa Obra", "É necessário registrar um PDF no sistema. O registro é realizado na plataforma Web."});
        put(EXCEPTION_NO_REGISTERED_PDF_IN_ELEMENTO, new String[]{"Não há registro de PDF de Locação nessa Peça", "É necessário registrar um PDF no sistema. O registro é realizado na plataforma Web."});
        put(EXCEPTION_MULTIPLE_SELECTED_TAGS, new String[]{"Mais de uma tag Selecionada", "É necessário Que haja apenas uma Tag na lista para prosseguir."});
        put(EXCEPTION_INPUT_FIELDS_ARE_NOT_VALID, new String[]{"Valor preenchido inválido", "É necessário preencher os campos corretamente para prosseguir."});
        put(EXCEPTION_TAG_NOT_FOUND, new String[]{"Tag não encontrada.", "Não foi possível identificar essa tag nessa Obra, selecione a obra correta, ou certifique-se que a peça está cadastrada."});
        put(EXCEPTION_TAG_ALREADY_REGISTERED, new String[]{"Tag já registrada.", "A tag já está cadastrada em outra peça!"});
        put(EXCEPTION_NO_SELECTED_TAG_ON_SUBMIT, new String[]{"Nenhuma Tag Selecionada.", "É necessário escanear uma TAG para prosseguir"});
        put(EXCEPTION_MAX_POSSIBLE_READ_PECAS_REACHED, new String[]{"Número máximo de peças lidas atendido", "O número de Peças planejadas para o Elemento de "});
        put(EXCEPTION_MAX_REGISTERED_PECAS_REACHED, new String[]{"Número de Peças Planejadas Já atendido", "O número de Peças cadastradas para o Elemento já é igual ao número de peças planejadas."});
        put(EXCEPTION_REPORTAR_ERRO_TAG_NO_ITEM_REGISTERED, new String[]{"Sem registro de item do Checklist", "É necessário selecionar ao menos um item do checklist para a TAG."});
        put(EXCEPTION_REPORTAR_ERRO_ITEM_NO_COMENTARIO_REGISTERED, new String[]{"Comentário sem Registro", "É necessário comentar todos os itens do checklist."});
        put(EXCEPTION_CHECKLIST_NOT_FILLED, new String[]{"Checklist Incompleto", "É necessário selecionar todos os itens do checklist."});
        put(EXCEPTION_PRODUCAO_INTERRUPTED, new String[]{"Processo de Produção Interrompido", "Existe uma inconformidade grave em Aberto para a tag selecionada. Resolva a inconformidade para prosseguir"});
        put(EXCEPTION_GALPAO_NOT_FILLED, new String[]{"Nenhuma Galpão Selecionado.", "É necessário selecionar um Galpão para prosseguir"});
        put(EXCEPTION_CARGA_PASSED, new String[]{"Etapa de Carga já Completada.", "Prossiga para as próximas etapas do Romaneio"});
        put(EXCEPTION_CARGA_NOT_READY, new String[]{"O Romaneio não finalizou a etapa de Carga.", "Finalize o checklist de Carga de todas as peças para prosseguir."});
        put(EXCEPTION_TRANSPORTE_PASSED, new String[]{"Etapa de Transporte já Completada.", "Prossiga para as próximas etapas do Romaneio"});
        put(EXCEPTION_TRANSPORTE_NOT_READY, new String[]{"O Romaneio não iniciou a etapa de Transporte.", "Finalize o checklist de Carga de todas as peças e ative a opção 'TRANSPORTE' para prosseguir"});
        put(EXCEPTION_ROMANEIO_FINISHED, new String[]{"O Romaneio Já foi Finalizado.", ""});
    }};

    public static Map<String, String> getError(String errorCode){
        Map<String, String> response = new HashMap<>();
        if(errorCode != null && errorCode.startsWith(EXCEPTION_MAX_POSSIBLE_READ_PECAS_REACHED)){
            response.put("errorCode", Objects.requireNonNull(errorList.get(EXCEPTION_MAX_POSSIBLE_READ_PECAS_REACHED))[0]);
            response.put("message", Objects.requireNonNull(errorList.get(EXCEPTION_MAX_POSSIBLE_READ_PECAS_REACHED))[1] + errorCode.replace(EXCEPTION_MAX_POSSIBLE_READ_PECAS_REACHED, "") + " Peças já foi atendido.");
            return response;
        }else if(errorCode == null || errorList.get(errorCode) == null){
            response.put("errorCode", "Erro no gerenciamento do Módulo de Qualidade");
            response.put("message", "Houve um erro inesperado no gerenciamento do módulo de Qualidade. Por favor contate o suporte!");
            return response;
        }
        response.put("errorCode", Objects.requireNonNull(errorList.get(errorCode))[0]);
        response.put("message", Objects.requireNonNull(errorList.get(errorCode))[1]);
        return response;
    }
}
