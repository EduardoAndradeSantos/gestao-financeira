package com.ntt.gestao.financeira.dto.response;

import java.util.List;

public record CambioResponseDTO(
        String moeda,
        String data,
        List<CotacaoDTO> cotacoes
) {}
