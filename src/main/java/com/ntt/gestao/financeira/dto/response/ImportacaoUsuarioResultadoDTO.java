package com.ntt.gestao.financeira.dto.response;

import java.util.List;

public record ImportacaoUsuarioResultadoDTO(
        int totalLinhas,
        int sucesso,
        int erros,
        List<String> mensagensErro
) {}
