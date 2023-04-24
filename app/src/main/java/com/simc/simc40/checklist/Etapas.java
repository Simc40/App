package com.simc.simc40.checklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public interface Etapas {
    String planejamentoKey = "planejamento";
    String cadastroKey = "cadastro";
    String armacaoKey = "armacao";
    String formaKey = "forma";
    String armacaoFormaKey = "armacaoForma";
    String concretagemKey = "concretagem";
    String liberacaoKey = "liberacao";
    String cargaKey = "carga";
    String descargaKey = "descarga";
    String montagemKey = "montagem";
    String completoKey = "completo";

    Integer numAllEtapas = 11;
    String[] allEtapas = {
        planejamentoKey,
        cadastroKey,
        armacaoKey,
        formaKey,
        armacaoFormaKey,
        concretagemKey,
        liberacaoKey,
        cargaKey,
        descargaKey,
        montagemKey,
        completoKey
    };

    Integer numFirebaseCheckLists = 10;
    String[] firebaseCheckLists = {
            planejamentoKey,
            cadastroKey,
            armacaoKey,
            formaKey,
            armacaoFormaKey,
            concretagemKey,
            liberacaoKey,
            cargaKey,
            descargaKey,
            montagemKey
    };

    Integer numEtapasDePeca = 8;
    ArrayList<String> etapasDePeca = new ArrayList<String>(){{
        add(armacaoKey);
        add(formaKey);
        add(armacaoFormaKey);
        add(concretagemKey);
        add(liberacaoKey);
        add(cargaKey);
        add(descargaKey);
        add(montagemKey);
        add(completoKey);
    }};

    LinkedHashMap<String, String> prettyEtapas = new LinkedHashMap<String, String>() {{
        put(planejamentoKey, "Planejamento");
        put(cadastroKey, "Cadastro de Peça");
        put(armacaoKey, "Produção/Armação");
        put(formaKey, "Produção/Forma");
        put(armacaoFormaKey, "Produção/Armacao com Forma");
        put(concretagemKey, "Produção/Concretagem");
        put(liberacaoKey, "Produção/Liberacao");
        put(cargaKey, "Transporte/Carga");
        put(descargaKey, "Transporte/Descarga");
        put(montagemKey, "Montagem");
        put(completoKey, "Completo");
    }};

    LinkedHashMap<String, String> prettyEtapasKeys = new LinkedHashMap<String, String>() {{
        put("Planejamento", planejamentoKey);
        put("Cadastro de Peça", cadastroKey);
        put("Produção/Armação", armacaoKey);
        put("Produção/Forma", formaKey);
        put("Produção/Armacao com Forma", armacaoFormaKey);
        put("Produção/Concretagem", concretagemKey);
        put("Produção/Liberacao", liberacaoKey);
        put("Transporte/Carga", cargaKey);
        put("Transporte/Descarga", descargaKey);
        put("Montagem", montagemKey);
        put("Completo", completoKey);
    }};

    LinkedHashMap<String, String> prettyShortEtapas = new LinkedHashMap<String, String>() {{
        put(planejamentoKey, "Planejamento");
        put(cadastroKey, "Cadastro");
        put(armacaoKey, "Armação");
        put(formaKey, "Forma");
        put(armacaoFormaKey, "Armacao com Forma");
        put(concretagemKey, "Concretagem");
        put(liberacaoKey, "Liberacao");
        put(cargaKey, "Carga");
        put(descargaKey, "Descarga");
        put(montagemKey, "Montagem");
        put(completoKey, "Completo");
    }};
}
