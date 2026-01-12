package com.ntt.gestao.financeira.dto.response;

import java.util.List;

public record CotacaoResponseDTO(
        String moeda,
        String data,
        List<CotacaoCambioDTO> cotacoes
) {}