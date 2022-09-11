package com.android.simc40.checklist;

import java.util.HashMap;

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
    String[] etapasDePeca = {
        armacaoKey,
        formaKey,
        armacaoFormaKey,
        concretagemKey,
        liberacaoKey,
        cargaKey,
        descargaKey,
        montagemKey,
    };

    HashMap<String, String> prettyEtapas = new HashMap<String, String>() {{
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
}
